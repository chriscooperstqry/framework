package nz.co.cjc.base.framework.network.providers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import nz.co.cjc.base.R;
import nz.co.cjc.base.framework.buildconfig.providers.contracts.BuildConfigProvider;
import nz.co.cjc.base.framework.logging.providers.contracts.LoggingProvider;
import nz.co.cjc.base.framework.network.models.NetworkRequestProperties;
import nz.co.cjc.base.framework.network.providers.contracts.NetworkRequestDelegate;
import nz.co.cjc.base.framework.network.providers.contracts.NetworkRequestProvider;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;
import nz.co.cjc.base.framework.threading.providers.contracts.ThreadUtilsProvider;
import nz.co.cjc.base.framework.utils.StringUtils;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p/>
 * Implementation of the network request provider
 * based on the OkHttp library.
 * Refer to http://docs.stqry.com/api/#introduction
 */
public class DefaultNetworkRequestProvider implements NetworkRequestProvider {
    private static final String TAG_IGNORE_AUTH = "!IGNORE_AUTH!";

    private static final String RESPONSE_CACHE_DIRECTORY = "okhttp_response_cache";
    private static final long RESPONSE_CACHE_SIZE_MEGABYTES = 50L * 1024L * 1024L; // 50 megabytes

    // It pains me to do this but we currently need a very big timeout to
    // cope with the huge lag time for the server to respond. We should change
    // this once the server performance issues are ironed out.
    private static final long REQUEST_TIMEOUT_SECONDS = 10L;

    protected final Context mApplicationContext;
    private final OkHttpClient mOkHttpClient;

    private final ConnectivityManager mConnectivityManager;
    private final StringsProvider mStringsProvider;
    private final BuildConfigProvider mBuildConfigProvider;
    private final ThreadUtilsProvider mThreadUtilsProvider;
    private final LoggingProvider mLoggingProvider;

    public DefaultNetworkRequestProvider(@NonNull Context context,
                                         @NonNull StringsProvider stringsProvider,
                                         @NonNull BuildConfigProvider buildConfigProvider,
                                         @NonNull ThreadUtilsProvider threadUtilsProvider,
                                         @NonNull LoggingProvider loggingProvider) {

        mApplicationContext = context;
        mStringsProvider = stringsProvider;
        mBuildConfigProvider = buildConfigProvider;
        mThreadUtilsProvider = threadUtilsProvider;
        mLoggingProvider = loggingProvider;

        mConnectivityManager = (ConnectivityManager) mApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        //Use signpost library to set the authorization needed
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(mStringsProvider.get(R.string.consumer_key), mStringsProvider.get(R.string.consumer_secret));

        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS).addInterceptor(new SigningInterceptor(consumer));

        // Configure a response cache for OkHttp so we get some free bandwidth / battery preservation.
        try {
            File responseCacheDirectory = new File(mApplicationContext.getCacheDir(), RESPONSE_CACHE_DIRECTORY);
            builder.cache(new Cache(responseCacheDirectory, RESPONSE_CACHE_SIZE_MEGABYTES));
        } catch (Exception e) {
            // This event should not occur but if so, then there will be no response caching...
            mLoggingProvider.e("Unable to create OkHttp response cache...");
        }
        mOkHttpClient = builder.build();
    }


    @NonNull
    @Override
    public String getBaseUrl() {
        // If we are compiling in a non release build, then use whatever
        // base url is currently set in the strings resource (this should be
        // extracted into another pattern though).
//        if (mBuildConfigProvider.allowNonProductionBaseUrl()) {
//            return mStringsProvider.get(R.string.http_request_base_url_dev);
//        }

        // If our build config reports that we are not allowed to point at
        // any environments except production, then force production as the url.
        return mStringsProvider.get(R.string.http_request_base_url);
    }

    @Override
    public synchronized boolean hasNetworkConnectivity() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public Call startRequest(@NonNull final NetworkRequestProperties properties, @NonNull final NetworkRequestDelegate networkRequestDelegate) {
        if (StringUtils.isEmpty(properties.getUrl())) {
            networkRequestDelegate.onConnectionFailed();
            return null;
        }
        Request request = createRequest(properties);
//        if (shouldBlockRequest(request, properties)) {
//            networkRequestDelegate.onConnectionFailed();
//            return null;
//        }
        // Start the actual request.
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                /**
                 * If the exception was detected as a socket exception
                 * we are assuming it was caused by a cancellation of
                 * the request, therefore we don't really want to call
                 * back on the delegate.
                 */
                mLoggingProvider.e("Request cancelled");
                if (e instanceof SocketException) {
                    return;
                }
                mLoggingProvider.e("Request connection failure");

                if (properties.shouldRespondOnMainThread()) {
                    mThreadUtilsProvider.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            networkRequestDelegate.onConnectionFailed();
                        }
                    });
                } else {
                    networkRequestDelegate.onConnectionFailed();
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final int statusCode = response.code();
                final String responseBody;
                if (response.body() != null) {
                    responseBody = response.body().string();
                    response.body().close();
                } else {
                    responseBody = "";
                }
                if (!properties.shouldRespondOnMainThread()) {
                    if (response.isSuccessful()) {
                        networkRequestDelegate.onRequestComplete(statusCode, responseBody);
                    } else {
                        networkRequestDelegate.onRequestFailed(statusCode, responseBody);
                    }
                    return;
                }
                mThreadUtilsProvider.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            networkRequestDelegate.onRequestComplete(statusCode, responseBody);
                        } else {
                            networkRequestDelegate.onRequestFailed(statusCode, responseBody);
                        }
                    }
                });
            }
        });
        return call;
    }

    @Override
    public void cancelRequest(@NonNull Call call) {
        if (call != null) {
            call.cancel();
        }
    }

    private Request createRequest(NetworkRequestProperties properties) {
        Request.Builder requestBuilder = new Request.Builder().url(properties.getUrl());
        if (properties.shouldIgnoreAuthentication()) {
            requestBuilder.tag(TAG_IGNORE_AUTH);
        }
        // Assign the correct request method.
        switch (properties.getMethod()) {
            case Get:
                requestBuilder.get();

                // Check to see if we should use response caching and if so, apply it. A value of 0 for
                // the maximum cache age will result in no caching being applied. Only Get requests are
                // permitted to use cached responses.
                int cacheAge = properties.getMaximumCacheAgeInSeconds();
                if (cacheAge > 0) {
                    CacheControl cacheControl = new CacheControl.Builder().maxAge(cacheAge, TimeUnit.SECONDS)
                            .maxStale(cacheAge, TimeUnit.SECONDS).build();
                    requestBuilder.cacheControl(cacheControl);
                }
                break;
            case Post:
                List<Pair<String, String>> postParameters = properties.getPostParameters();
                if (postParameters != null) {
                    FormBody.Builder parameterBuilder = new FormBody.Builder();
                    // Add all the given Post parameters
                    for (Pair<String, String> parameter : postParameters) {
                        parameterBuilder.add(parameter.first, parameter.second);
                    }
                    requestBuilder.post(parameterBuilder.build());
                    break;
                }
                String rawBody = properties.getRawBody();
                if (!TextUtils.isEmpty(rawBody)) {
                    requestBuilder.post(RequestBody.create(MediaType.parse("application/json"), properties.getRawBody()));
                }
                break;
        }
        // Get the collection of default headers to apply to every request.
        Map<String, String> defaultHeaders = getDefaultHeaders(properties.shouldIncludeAccessToken());

        // Add all the default headers to the request builder.
        for (String headerKey : defaultHeaders.keySet()) {
            requestBuilder.header(headerKey, defaultHeaders.get(headerKey));
        }

        // Grab any user defined headers that were in the request properties.
        Map<String, String> userHeaders = properties.getHeaders();

        // If there were any, add them all to the request builder.
        if (userHeaders != null) {
            for (String key : userHeaders.keySet()) {
                requestBuilder.header(key, userHeaders.get(key));
            }
        }

        // Construct the request object itself with all of the configuration.
        return requestBuilder.build();
    }

    /**
     * Construct a default set of headers to be included in each request.
     *
     * @return map of headers.
     */
    private Map<String, String> getDefaultHeaders(boolean shouldIncludeAccessToken) {
        Map<String, String> headers = new HashMap<>();

        //headers.put("Accept", "application/vnd.stqry.*+json;version=2.1");

        headers.put("Accept-Language", Locale.getDefault().getLanguage());

//        String accessToken = getAccessToken();
//
//        // Check to see if this request specifically wants to not include the access token or not (ticketing api)
//        if (shouldIncludeAccessToken && !StringUtils.isEmpty(accessToken)) {
//            headers.put("Authorization", HttpRequestConstants.GRANT_BEARER + " " + accessToken);
//        } else {
//            headers.put("Authorization", "Basic " + getEncodedApiKey());
//        }


        return headers;
    }

//    private String getEncodedApiKey() {
//        String appApiKey = mStringsProvider.get(R.string.app_api_key);
//        return Base64.encodeToString((appApiKey).getBytes(), Base64.NO_WRAP);
//    }
}
