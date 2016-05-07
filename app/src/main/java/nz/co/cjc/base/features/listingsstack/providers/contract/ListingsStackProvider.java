package nz.co.cjc.base.features.listingsstack.providers.contract;

import android.support.annotation.NonNull;

import nz.co.cjc.base.features.categoriesandlistings.models.CategoryData;

/**
 * Created by Chris Cooper on 6/05/16.
 * <p>
 * Provider for keeping track of the listings/category we have viewed
 */
public interface ListingsStackProvider {

    /**
     * Add a listing that we're viewing to the stack
     *
     * @param listing to add
     */
    void addListing(@NonNull CategoryData listing);

    /**
     * Get the listing on the top of the stack
     *
     * @return The model
     */
    @NonNull
    CategoryData getTopListing();

    /**
     * Remove the listing at the top of the stack
     */
    void removeListing();

    /**
     * Check if there are any listing in the stack
     *
     * @return True if the stack is empty, false otherwise
     */
    boolean isListingsEmpty();

    /**
     * A way of telling us that the current listing has no more
     * sub categories left to view
     *
     * @return True if this is the last sub category in the chain
     */
    boolean isEndOfSubcategory();

    /**
     * Get the size of the listings stack
     *
     * @return the size
     */
    int size();

    /**
     * Check if we are displaying a current category that has no subcategories and
     * we are not on the root listing view (home page)
     * @return True if so, false otherwise
     */
    boolean isViewingNonRootEmptySubcategory();
}
