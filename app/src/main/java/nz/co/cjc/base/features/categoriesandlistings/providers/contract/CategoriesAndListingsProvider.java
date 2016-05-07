package nz.co.cjc.base.features.categoriesandlistings.providers.contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import nz.co.cjc.base.features.categoriesandlistings.models.CategoryData;
import nz.co.cjc.base.features.categoriesandlistings.models.ListingData;

/**
 * Created by Chris Cooper on 5/05/16.
 * <p>
 * Provider to retrieve categories and listings data from the api
 */
public interface CategoriesAndListingsProvider {
    String SUBCATEGORIES = "Subcategories";
    String LIST = "List";
    String QUERY_PARAM_CATEGORY ="category";
    String QUERY_PARAM_PHOTO_SIZE ="photo_size";
    String QUERY_PARAM_ROWS ="rows";

    /**
     * Retrieve the categories from the api
     *
     * @param categoryNumber  Number corresponding to the category you wish to retrieve categories for.
     *                        If not supplied, will return all categories.
     * @param requestDelegate to callback to
     */
    void getCategoriesData(@Nullable String categoryNumber, @NonNull final CategoriesRequestDelegate requestDelegate);

    /**
     * Retrieve the listings from the api
     *
     * @param categoryNumber  Number corresponding to the category you wish to retrieve listings for.
     *                        If not supplied, will return all listings.
     * @param requestDelegate to callback to
     */
    void getListingsData(@Nullable String categoryNumber, @NonNull final ListingsRequestDelegate requestDelegate);

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
         * The request for the categories failed.
         */
        void requestFailed();
    }

    /**
     * Callback for the request to retrieve the listings
     */
    interface ListingsRequestDelegate {
        /**
         * Succeeded callback
         *
         * @param listings retrieved from the server.
         */
        void requestSuccess(@NonNull List<ListingData> listings);

        /**
         * The request for the listings failed.
         */
        void requestFailed();
    }
}
