package nz.co.cjc.base.features.categoriesandlistings.providers;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import nz.co.cjc.base.R;
import nz.co.cjc.base.features.categoriesandlistings.models.CategoryData;
import nz.co.cjc.base.features.categoriesandlistings.providers.contract.CategoriesAndListingsProvider;
import nz.co.cjc.base.framework.network.models.NetworkRequestProperties;
import nz.co.cjc.base.framework.network.providers.contracts.NetworkRequestDelegate;
import nz.co.cjc.base.framework.network.providers.contracts.NetworkRequestProvider;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;
import nz.co.cjc.base.framework.threading.providers.contracts.ThreadUtilsProvider;
import nz.co.cjc.base.framework.utils.StringUtils;

/**
 * Created by Chris Cooper on 5/05/16.
 * <p/>
 * Implementation of the categories and listings provider
 */
public class DefaultCategoriesAndListingsProvider implements CategoriesAndListingsProvider {

    private static final String SUBCATEGORIES = "Subcategories";

    private final NetworkRequestProvider mNetworkRequestProvider;
    private final StringsProvider mStringsProvider;
    private final ThreadUtilsProvider mThreadUtilsProvider;

    public DefaultCategoriesAndListingsProvider(@NonNull StringsProvider stringsProvider,
                                                @NonNull NetworkRequestProvider networkRequestProvider,
                                                @NonNull ThreadUtilsProvider threadUtilsProvider) {
        mNetworkRequestProvider = networkRequestProvider;
        mStringsProvider = stringsProvider;
        mThreadUtilsProvider = threadUtilsProvider;
    }

    @Override
    public void getCategoriesData(@NonNull final CategoriesRequestDelegate requestDelegate) {

        String url = mNetworkRequestProvider.getBaseUrl() + mStringsProvider.get(R.string.categories_url);

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
