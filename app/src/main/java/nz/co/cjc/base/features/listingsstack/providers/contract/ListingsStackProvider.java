package nz.co.cjc.base.features.listingsstack.providers.contract;

import android.support.annotation.NonNull;

/**
 * Created by Chris Cooper on 6/05/16.
 * <p>
 * Provider for keeping track of the listings we have viewed
 */
public interface ListingsStackProvider {

    /**
     * Add a listing that we're viewing to the stack
     *
     * @param categoryNumber to add
     */
    void addListing(@NonNull String categoryNumber);

    /**
     * Get the listing on the top of the stack
     *
     * @return The category number of the listings
     */
    @NonNull
    String getTopListing();

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
     * A not so good way of telling us that the current listing has no more
     * sub categories left to view
     *
     * @return True if this is the last sub category in the chain of listings
     */
    boolean isEndOfListing();
}
