package nz.co.cjc.base.features.categoriesandlistings.logic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import javax.inject.Inject;

import nz.co.cjc.base.R;
import nz.co.cjc.base.features.categoriesandlistings.events.CategoryEvent;
import nz.co.cjc.base.features.categoriesandlistings.events.ListingsEvent;
import nz.co.cjc.base.features.categoriesandlistings.models.CategoryData;
import nz.co.cjc.base.features.categoriesandlistings.ui.CategoriesFragment;
import nz.co.cjc.base.features.categoriesandlistings.ui.ListingsFragment;
import nz.co.cjc.base.features.listingsstack.providers.contract.ListingsStackProvider;
import nz.co.cjc.base.framework.core.logic.BaseViewLogic;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusProvider;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusSubscriber;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;
import nz.co.cjc.base.framework.utils.StringUtils;

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

    public void initViewLogic(@Nullable ViewLogicDelegate delegate, @Nullable Bundle savedInstanceState) {
        setDelegate(delegate);

        if (savedInstanceState == null) {
            mListingsStackProvider.addListing(new CategoryData());
            CategoriesFragment categoriesFragment = CategoriesFragment.newInstance(new Bundle());
            mDelegate.presentFragment(categoriesFragment, R.id.categories_container, false);

            Fragment listingsFragment = ListingsFragment.newInstance();
            mDelegate.presentFragment(listingsFragment, R.id.listings_container, false);
        }

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

                if (event.getBundle() == null || event.getBundle().getParcelable(CategoriesViewLogic.CATEGORY_DATA) == null) {
                    throw new IllegalArgumentException("Must provide category data");
                }

                CategoryData categoryData = event.getBundle().getParcelable(CategoriesViewLogic.CATEGORY_DATA);
                List<CategoryData> datasSubcategories = categoryData.getSubCategories();
                String categoryNumber = categoryData.getNumber();

                //If there are more other sub categories to drill into
                if (!datasSubcategories.isEmpty()) {
                    addToolbarText(categoryData.getName());

                    mListingsStackProvider.addListing(categoryData);
                    mEventBusProvider.postEvent(new ListingsEvent(null, ListingsEvent.EventType.UpdateListings, categoryNumber));

                    Fragment categoriesFragment = CategoriesFragment.newInstance(event.getBundle());
                    mDelegate.presentFragment(categoriesFragment, R.id.categories_container, true);

                }
                //If this category doesn't have any more subcategories, and we're not clicking on the same category that is already displaying
                else if (datasSubcategories.isEmpty() && !mListingsStackProvider.getTopListing().getNumber().equals(categoryNumber)) {

                    //When we're clicking on a different option that is in the same list of the end category list
                    //We need to remove the current viewing from the stack and title, as
                    //really we're just replacing the current top state, not adding an additional item on.
                    //Also, don't do this if we are on the root home page
                    if (mListingsStackProvider.size() != 1 && mListingsStackProvider.getTopListing().getSubCategories().isEmpty()) {
                        mListingsStackProvider.removeListing();
                        removeToolbarText();
                    }

                    addToolbarText(categoryData.getName());
                    mListingsStackProvider.addListing(categoryData);
                    mEventBusProvider.postEvent(new ListingsEvent(null, ListingsEvent.EventType.UpdateListings, categoryNumber));
                    mDelegate.closeSlidingPanel();
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

        if (!StringUtils.isEmpty(mListingsStackProvider.getTopListing().getName()) && mListingsStackProvider.isEndOfSubcategory()) {
            mEventBusProvider.postEvent(new CategoryEvent(null, CategoryEvent.EventType.ClearCategorySelection, null));
            bubbleUp = false;
        }

        mListingsStackProvider.removeListing();

        if (!mListingsStackProvider.isListingsEmpty()) {
            removeToolbarText();
            mEventBusProvider.postEvent(new ListingsEvent(null, ListingsEvent.EventType.UpdateListings, mListingsStackProvider.getTopListing().getNumber()));
        }

        mDelegate.setSlidingPanelScrollableView();

        return bubbleUp;
    }

    private void addToolbarText(String text) {
        String current = mDelegate.getToolbarTitle();
        mDelegate.updateToolbarText(current + " > " + text);
    }

    private void removeToolbarText() {
        String current = mDelegate.getToolbarTitle();
        current = current.substring(0, current.lastIndexOf(" > "));
        mDelegate.updateToolbarText(current);
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
         * Sets the sliding panel layouts scrollable view to the
         * current fragments list view
         */
        void setSlidingPanelScrollableView();

        /**
         * Close the sliding panel so it is hidden
         */
        void closeSlidingPanel();

        /**
         * Set the toolbar text
         *
         * @param text to set
         */
        void updateToolbarText(@NonNull String text);

        /**
         * Get the toolbars current text
         *
         * @return The text
         */
        @NonNull
        String getToolbarTitle();
    }
}
