package nz.co.cjc.base.framework.network.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p/>
 * Model describing the properties to include for
 * network requests made via the network request
 * provider.
 * <p/>
 * The default instance uses response caching and
 * performs a Get.
 */
public class NetworkRequestProperties {
    private static final int DEFAULT_MAXIMUM_CACHE_AGE_SECONDS = 60 * 30; // 30 minutes

    /**
     * Definition of a request method.
     */
    public enum Method {
        Get,
        Post,
    }

    private Method mMethod;
    private String mUrl;
    private int mMaximumCacheAgeInSeconds;
    private Map<String, String> mHeaders;
    private List<Pair<String, String>> mPostParameters;
    private String mRawBody;
    private boolean mRespondOnMainThread;
    private boolean mIgnoreAuthentication;

    // By default we do not want to allow any HTTP requests - only HTTPS.
    // Override this if you want an HTTP request to be processed, however
    // this should not be used in a production build if possible.
    private boolean mAllowHttp;

    // For the special case of sending a request to the ticketing api in which it does
    // not want the access token to be included, only the api key.
    // Otherwise all other requests should have access token included, if we have one.
    private boolean mIncludeAccessToken = true;

    /**
     * The default instance is set to use a Get request method, with a
     * response cache time of 5 minutes.
     */
    private NetworkRequestProperties() {
        mMethod = Method.Get;
        mMaximumCacheAgeInSeconds = DEFAULT_MAXIMUM_CACHE_AGE_SECONDS;
        mUrl = "";
        mRespondOnMainThread = true;
    }

    //region Factory construction method
    @NonNull
    public static NetworkRequestProperties create() {
        return new NetworkRequestProperties();
    }
    //endregion

    //region Setters (builder)

    /**
     * Set the request method for this request.
     *
     * @param method (eg Get, Post, Head etc).
     * @return self.
     */
    @NonNull
    public NetworkRequestProperties method(@NonNull Method method) {
        mMethod = method;
        return this;
    }

    /**
     * Set the target URL for this request.
     *
     * @param url to set the request to.
     * @return self.
     */
    @NonNull
    public NetworkRequestProperties url(@NonNull String url) {
        mUrl = url;
        return this;
    }

    /**
     * Set the maximum length of time that the request should be
     * retrieved from the response cache. If you don't want to use
     * the default response cache time, specify your own time with
     * this method. Supplying a value of 0 will skip the caching
     * and force the request to resolve to the server.
     *
     * @param maximumCacheAgeInSeconds to allow this request to be retrieved
     *                                 from the cache instead of hitting the
     *                                 server. Pass 0 to prevent a cached
     *                                 request.
     * @return self.
     */
    @NonNull
    public NetworkRequestProperties maximumCacheAgeInSeconds(int maximumCacheAgeInSeconds) {
        mMaximumCacheAgeInSeconds = maximumCacheAgeInSeconds;
        return this;
    }

    /**
     * Set a collection of headers to attach to the request.
     *
     * @param headers to attach to the request.
     * @return self.
     */
    @NonNull
    public NetworkRequestProperties headers(@Nullable Map<String, String> headers) {
        mHeaders = headers;
        return this;
    }

    /**
     * Set a list of Post parameters, intended to be sent through a Post request.
     * Note that unless the 'method' is set to 'Post', these parameters will not be used.
     * If we use this method, mRawBody will be cleared
     *
     * @param postParameters to send as the parameters of the Post request.
     * @return self.
     */
    @NonNull
    public NetworkRequestProperties postParameters(@Nullable List<Pair<String, String>> postParameters) {
        mPostParameters = postParameters;
        mRawBody = null;
        return this;
    }

    /**
     * Set the raw request body
     * Note that unless the 'method' is set to 'Post', these parameters will not be used.
     * If we use this method, postParameters will be cleared
     *
     * @param rawBody
     * @return
     */
    public NetworkRequestProperties rawBody(@NonNull String rawBody) {
        mRawBody = rawBody;
        mPostParameters = null;
        return this;
    }

    /**
     * Specify whether or not to allow Http requests to be processed. The default is to
     * deny any request that is not to an Https link. Normal use case should be to not
     * allow Http requests. Use cases where Http would be allowed might be to point at
     * a development environment that doesn't have an SSL certificate and therefore can't
     * be reached via Https - but for a proper production build we should not allow Http.
     *
     * @param allowHttp set to true to allow this request to be sent via Http instead of
     *                  it being denied by our networking system.
     * @return self.
     */
    @NonNull
    public NetworkRequestProperties allowHttp(boolean allowHttp) {
        mAllowHttp = allowHttp;
        return this;
    }

    @NonNull
    public NetworkRequestProperties excludeAccessToken() {
        mIncludeAccessToken = false;
        return this;
    }

    @NonNull
    public NetworkRequestProperties respondOnMainThread(boolean respondOnMainThread) {
        mRespondOnMainThread = respondOnMainThread;
        return this;
    }

    @NonNull
    public NetworkRequestProperties ignoreAuthentication(boolean ignoreAuthentication) {
        mIgnoreAuthentication = ignoreAuthentication;
        return this;
    }
    //endregion

    //region Getters

    /**
     * The method of the request (eg Get, Post, Head etc).
     *
     * @return the request method.
     */
    @NonNull
    public Method getMethod() {
        return mMethod;
    }

    /**
     * The Url of the request to load from server or cache.
     *
     * @return the Url to load.
     */
    @NonNull
    public String getUrl() {
        return mUrl;
    }

    /**
     * The permitted maximum age of a cached response for the
     * request, specified in seconds.
     *
     * @return the number of seconds to accept a cached response.
     */
    public int getMaximumCacheAgeInSeconds() {
        return mMaximumCacheAgeInSeconds;
    }

    /**
     * The collection of headers to send with the request if they exist.
     *
     * @return the collection of headers, or null if there are no headers.
     */
    @Nullable
    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    /**
     * The collection of Post parameters to send with a Post request if
     * they exist.
     *
     * @return the collection of Post parameters, or null if there are no headers.
     */
    @Nullable
    public List<Pair<String, String>> getPostParameters() {
        return mPostParameters;
    }

    @Nullable
    public String getRawBody() {
        return mRawBody;
    }

    /**
     * Whether or not this request has been marked to allow Http requests.
     *
     * @return true if Http requests are permitted for this instance.
     */
    public boolean isHttpAllowed() {
        return mAllowHttp;
    }

    /**
     * Whether or not to include the access token, this should default to yes for all requests,
     * apart from the special case ticketing api
     *
     * @return true to include the access token, false to leave it out.
     */
    public boolean shouldIncludeAccessToken() {
        return mIncludeAccessToken;
    }

    /**
     * Whether or not the network response should be delivered
     * on the main thread or not. The default is true - all
     * responses will be delivered on the main thread.
     * <p/>
     * Disabling this setting means the caller will receive the
     * network response on the same background thread that the
     * request itself was made on, which can provide some 'free'
     * threading capability to do some extra processing such as
     * parsing etc.
     * <p/>
     * Just be sure that the caller handles invoking any UI code
     * on the main thread manually if this option is set to false.
     *
     * @return true if network responses should be delivered on
     * the main thread, or false to deliver responses on the same
     * background thread that the request was running on.
     */
    public boolean shouldRespondOnMainThread() {
        return mRespondOnMainThread;
    }

    /**
     * If you want a request to ignore any access token checking
     * then set this property to true.
     *
     * @return true if the request should ignore authentication,
     * this value is false by default.
     */
    public boolean shouldIgnoreAuthentication() {
        return mIgnoreAuthentication;
    }
    //endregion


    @Override
    public String toString() {
        return "url=" + mUrl + ", method=" + mMethod + ", body=" + mRawBody + ", header=" + mHeaders;
    }
}
