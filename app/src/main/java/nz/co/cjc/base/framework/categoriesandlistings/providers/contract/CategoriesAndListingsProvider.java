package nz.co.cjc.base.framework.categoriesandlistings.providers.contract;

import android.support.annotation.NonNull;

import java.util.List;

import nz.co.cjc.base.framework.categoriesandlistings.models.CategoryData;

/**
 * Created by Chris Cooper on 5/05/16.
 * <p/>
 * Provider to retrieve categories and listings data from the api
 */
public interface CategoriesAndListingsProvider {

    /**
     * Retrieve the categories from the api
     *
     * @param requestDelegate to callback to
     */
    void getCategoriesData(@NonNull final CategoriesRequestDelegate requestDelegate);

    /**
     * Callback for the request to retrieve the categories
     */
    interface CategoriesRequestDelegate {
        /**
         * Succeeded callback
         *
         * @param categories retrieved from the server.
         */
        void requestSuccess(@NonNull List<CategoryData> categories);

        /**
         * The request for the filtered entities failed.
         */
        void requestFailed();
    }
}
