package nz.co.cjc.base.features.categoriesandlistings.logic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

import nz.co.cjc.base.R;
import nz.co.cjc.base.features.categoriesandlistings.events.CategoryEvent;
import nz.co.cjc.base.features.categoriesandlistings.events.ListingsEvent;
import nz.co.cjc.base.features.categoriesandlistings.models.CategoryData;
import nz.co.cjc.base.features.categoriesandlistings.ui.CategoriesFragment;
import nz.co.cjc.base.features.categoriesandlistings.ui.ListingsFragment;
import nz.co.cjc.base.features.listingsstack.providers.DefaultListingStackProvider;
import nz.co.cjc.base.features.listingsstack.providers.contract.ListingsStackProvider;
import nz.co.cjc.base.framework.core.logic.BaseViewLogic;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusProvider;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusSubscriber;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p>
 * view logic for the categories and listings activity
 */
public class CategoriesAndListingsViewLogic extends BaseViewLogic<CategoriesAndListingsViewLogic.ViewLogicDelegate> implements EventBusSubscriber {

    private final EventBusProvider mEventBusProvider;
    private final ListingsStackProvider mListingsStackProvider;

    @Inject
    public CategoriesAndListingsViewLogic(@NonNull StringsProvider stringsProvider,
                                          @NonNull EventBusProvider eventBusProvider,
                                          @NonNull ListingsStackProvider listingsStackProvider) {
        super(ViewLogicDelegate.class, stringsProvider);
        mEventBusProvider = eventBusProvider;
        mListingsStackProvider = listingsStackProvider;

    }

    public void initViewLogic(@Nullable ViewLogicDelegate delegate) {
        setDelegate(delegate);

        mListingsStackProvider.addListing("0");
        CategoriesFragment categoriesFragment = CategoriesFragment.newInstance(new Bundle());
        mDelegate.presentFragment(categoriesFragment, R.id.categories_container, false);

        Fragment listingsFragment = ListingsFragment.newInstance();
        mDelegate.presentFragment(listingsFragment, R.id.listings_container, false);

    }

    public void screenResumed() {
        subscribeToEventBus();
    }

    public void screenPaused() {
        unsubscribeFromEventBus();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(@NonNull CategoryEvent event) {
        switch (event.getEventType()) {
            case CategorySelected:

                ArrayList<CategoryData> subcategories = event.getBundle().getParcelableArrayList(CategoriesViewLogic.SUBCATEGORIES);
                String categoryNumber = event.getBundle().getString(CategoriesViewLogic.CATEGORY_NUMBER);

                mListingsStackProvider.addListing(categoryNumber);
                mEventBusProvider.postEvent(new ListingsEvent(null, ListingsEvent.EventType.UpdateListings, categoryNumber));

                if (subcategories != null && !subcategories.isEmpty()) {

                    Fragment categoriesFragment = CategoriesFragment.newInstance(event.getBundle());
                    mDelegate.presentFragment(categoriesFragment, R.id.categories_container, true);

                } else {
                    mDelegate.closeSlidingPanel();
                    mListingsStackProvider.addListing(DefaultListingStackProvider.END_OF_CATEGORY);
                }
                break;
            case CategoryLayoutReady:
                mDelegate.setSlidingPanelScrollableView();
                break;
        }
    }

    @Override
    public void subscribeToEventBus() {
        mEventBusProvider.subscribe(this);
    }

    @Override
    public void unsubscribeFromEventBus() {
        mEventBusProvider.unsubscribe(this);
    }

    /**
     * If the back button is pressed, find out if the
     * the current category is at the end of the sub category chain
     * and indicate that we have handled this case ourselves
     * i.e (don't bubble up to the parent).
     *
     * @return false if we have handled the back press
     * ourselves and no further action should be taken.
     */
    public boolean onBackPressed() {
        boolean bubbleUp = true;

        if (mListingsStackProvider.isEndOfListing()) {
            mListingsStackProvider.removeListing();
            mEventBusProvider.postEvent(new CategoryEvent(null, CategoryEvent.EventType.ClearCategorySelection, null));
            bubbleUp = false;
        }

        mListingsStackProvider.removeListing();

        if (!mListingsStackProvider.isListingsEmpty()) {
            mEventBusProvider.postEvent(new ListingsEvent(null, ListingsEvent.EventType.UpdateListings, mListingsStackProvider.getTopListing()));
        }

        mDelegate.setSlidingPanelScrollableView();

        return bubbleUp;
    }

    public interface ViewLogicDelegate {

        /**
         * Insert a fragment into the given container reference
         *
         * @param fragment            The fragment to present
         * @param fragmentContainerId The view container id of where to put the fragment
         * @param addToBackStack      Whether we want to add this fragment to the back stack or not
         */
        void presentFragment(@NonNull Fragment fragment, int fragmentContainerId, boolean addToBackStack);

        /**
         * Check whether we are using the larger layout xml (we are on a tablet), to see if the listings container
         * is available for us to insert a fragment
         *
         * @return True if so, false otherwise
         */
        boolean isListingsContainerAvailable();

        /**
         * Sets the sliding panel layouts scrollable view to the
         * current fragments list view
         */
        void setSlidingPanelScrollableView();

        /**
         * Close the sliding panel so it is hidden
         */
        void closeSlidingPanel();

    }
}
