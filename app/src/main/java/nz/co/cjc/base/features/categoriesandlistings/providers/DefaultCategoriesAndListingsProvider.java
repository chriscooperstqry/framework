package nz.co.cjc.base.features.categoriesandlistings.providers;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import nz.co.cjc.base.R;
import nz.co.cjc.base.features.categoriesandlistings.models.CategoryData;
import nz.co.cjc.base.features.categoriesandlistings.models.ListingData;
import nz.co.cjc.base.features.categoriesandlistings.providers.contract.CategoriesAndListingsProvider;
import nz.co.cjc.base.framework.network.models.NetworkRequestProperties;
import nz.co.cjc.base.framework.network.providers.contracts.NetworkRequestDelegate;
import nz.co.cjc.base.framework.network.providers.contracts.NetworkRequestProvider;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;
import nz.co.cjc.base.framework.threading.providers.contracts.ThreadUtilsProvider;
import nz.co.cjc.base.framework.utils.StringUtils;

/**
 * Created by Chris Cooper on 5/05/16.
 * <p>
 * Implementation of the categories and listings provider
 */
public class DefaultCategoriesAndListingsProvider implements CategoriesAndListingsProvider {

    private final NetworkRequestProvider mNetworkRequestProvider;
    private final StringsProvider mStringsProvider;
    private final ThreadUtilsProvider mThreadUtilsProvider;
    private List<CategoryData> mCategoriesData;

    public DefaultCategoriesAndListingsProvider(@NonNull StringsProvider stringsProvider,
                                                @NonNull NetworkRequestProvider networkRequestProvider,
                                                @NonNull ThreadUtilsProvider threadUtilsProvider) {
        mNetworkRequestProvider = networkRequestProvider;
        mStringsProvider = stringsProvider;
        mThreadUtilsProvider = threadUtilsProvider;
        mCategoriesData = new ArrayList<>();
    }

    @Override
    public void getCategoriesData(@Nullable String categoryNumber, @NonNull final CategoriesRequestDelegate requestDelegate) {

        //As this data is unlikely to change very frequently, to save parsing the data each time the fragment is recreated on orientation change,
        // save the data for quick return
        if (!mCategoriesData.isEmpty()) {
            requestDelegate.requestSuccess(mCategoriesData);
            return;
        }

        if (StringUtils.isEmpty(categoryNumber)) {
            categoryNumber = "0";
        }
        String url = mNetworkRequestProvider.getBaseUrl()
                + mStringsProvider.get(R.string.categories_url)
                + categoryNumber
                + mNetworkRequestProvider.getFileFormat();

        NetworkRequestProperties networkRequestProperties = NetworkRequestProperties.create().url(url).respondOnMainThread(false);

        mNetworkRequestProvider.startRequest(networkRequestProperties, new NetworkRequestDelegate() {
            @Override
            public void onRequestComplete(int statusCode, @NonNull String response) {
                final List<CategoryData> result = parseCategories(response);

                if (result == null) {
                    mThreadUtilsProvider.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            requestDelegate.requestFailed();
                        }
                    });
                    return;
                }

                mCategoriesData = result;

                mThreadUtilsProvider.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        requestDelegate.requestSuccess(result);
                    }
                });
            }

            @Override
            public void onRequestFailed(int statusCode, @NonNull String response) {
                requestDelegate.requestFailed();
            }

            @Override
            public void onConnectionFailed() {
                requestDelegate.requestFailed();
            }
        });
    }

    @Override
    public void getListingsData(@Nullable String categoryNumber, @NonNull final ListingsRequestDelegate requestDelegate) {

        if (StringUtils.isEmpty(categoryNumber)) {
            categoryNumber = "0";
        }
        String url = mNetworkRequestProvider.getBaseUrl()
                + mStringsProvider.get(R.string.listings_url)
                + mNetworkRequestProvider.getFileFormat()
                + new Uri.Builder()
                .appendQueryParameter("category", categoryNumber)
                .appendQueryParameter("photo_size", "Medium")
                .build()
                .toString();


        NetworkRequestProperties networkRequestProperties = NetworkRequestProperties.create().url(url).respondOnMainThread(false);

        mNetworkRequestProvider.startRequest(networkRequestProperties, new NetworkRequestDelegate() {
            @Override
            public void onRequestComplete(int statusCode, @NonNull String response) {
                final List<ListingData> result = parseListings(response);

                if (result == null) {
                    mThreadUtilsProvider.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            requestDelegate.requestFailed();
                        }
                    });
                    return;
                }

                mThreadUtilsProvider.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        requestDelegate.requestSuccess(result);
                    }
                });
            }

            @Override
            public void onRequestFailed(int statusCode, @NonNull String response) {
                requestDelegate.requestFailed();
            }

            @Override
            public void onConnectionFailed() {
                requestDelegate.requestFailed();
            }
        });
    }

    private List<ListingData> parseListings(String input) {
        if (StringUtils.isEmpty(input)) {
            return null;
        }

        try {
            JsonObject rootObject = new JsonParser().parse(input).getAsJsonObject();
            JsonArray listings = rootObject.getAsJsonArray(LIST);

            return new Gson().fromJson(listings, new TypeToken<List<ListingData>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<CategoryData> parseCategories(String input) {
        if (StringUtils.isEmpty(input)) {
            return null;
        }

        try {
            JsonObject rootObject = new JsonParser().parse(input).getAsJsonObject();
            JsonArray subcategories = rootObject.getAsJsonArray(SUBCATEGORIES);

            return new Gson().fromJson(subcategories, new TypeToken<List<CategoryData>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
