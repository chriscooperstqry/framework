package nz.co.cjc.base.features.categoriesandlistings.logic;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import nz.co.cjc.base.features.categoriesandlistings.events.ListingsEvent;
import nz.co.cjc.base.features.categoriesandlistings.models.ListingData;
import nz.co.cjc.base.features.categoriesandlistings.providers.contract.CategoriesAndListingsProvider;
import nz.co.cjc.base.framework.core.logic.BaseViewLogic;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusProvider;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusSubscriber;
import nz.co.cjc.base.framework.statesaver.providers.contract.StateSaverProvider;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;
import nz.co.cjc.base.framework.utils.StringUtils;

/**
 * Created by Chris Cooper on 5/05/16.
 * <p/>
 * View logic for the listings fragment
 */
public class ListingsViewLogic extends BaseViewLogic<ListingsViewLogic.ViewLogicDelegate> implements EventBusSubscriber {

    private final StringsProvider mStringsProvider;
    private final CategoriesAndListingsProvider mCategoriesAndListingsProvider;
    private final EventBusProvider mEventBusProvider;
    private final StateSaverProvider mStateSaverProvider;
    private List<ListingData> mListingItems;

    @Inject
    public ListingsViewLogic(@NonNull StringsProvider stringsProvider,
                             @NonNull CategoriesAndListingsProvider categoriesAndListingsProvider,
                             @NonNull EventBusProvider eventBusProvider,
                             @NonNull StateSaverProvider stateSaverProvider) {
        super(ViewLogicDelegate.class, stringsProvider);
        mStringsProvider = stringsProvider;
        mCategoriesAndListingsProvider = categoriesAndListingsProvider;
        mEventBusProvider = eventBusProvider;
        mStateSaverProvider = stateSaverProvider;
    }

    //region public
    public void initViewLogic(@Nullable ViewLogicDelegate delegate, @Nullable Bundle savedInstanceState) {
        setDelegate(delegate);

        mListingItems = new ArrayList<>();

        //Get the saved listings data on device rotation
        if (savedInstanceState != null && mStateSaverProvider.getParcelableArrayList(StateSaverProvider.STATE_PARCELABLE_ARRAY_LIST, savedInstanceState) != null) {
            mListingItems = mStateSaverProvider.getParcelableArrayList(StateSaverProvider.STATE_PARCELABLE_ARRAY_LIST, savedInstanceState);
            mDelegate.populateScreen(mListingItems);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(@NonNull ListingsEvent event) {
        switch (event.getEventType()) {
            //Told by hosting activity to update the listings,
            //Or if no category number specified, clear the list items (on home page)
            case UpdateListings:
                if (StringUtils.isEmpty(event.getCategoryNumber())) {
                    mListingItems.clear();
                    mDelegate.showMessage();
                    mDelegate.populateScreen(mListingItems);
                } else {
                    mDelegate.hideMessage();
                    getListings(event.getCategoryNumber());
                }
                break;
        }
    }

    public void screenResumed() {
        subscribeToEventBus();
    }

    public void screenPaused() {
        unsubscribeFromEventBus();
    }

    @Override
    public void subscribeToEventBus() {
        mEventBusProvider.subscribe(this);
    }

    @Override
    public void unsubscribeFromEventBus() {
        mEventBusProvider.unsubscribe(this);
    }

    //Store the listings data
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mStateSaverProvider.saveParcelableArrayList(StateSaverProvider.STATE_PARCELABLE_ARRAY_LIST, (ArrayList<? extends Parcelable>) mListingItems, outState);
    }
    //end region

    //region private
    private void getListings(String categoryNumber) {

        mCategoriesAndListingsProvider.getListingsData(categoryNumber, new CategoriesAndListingsProvider.ListingsRequestDelegate() {
            @Override
            public void requestSuccess(@NonNull List<ListingData> listings) {
                mListingItems = listings;
                mDelegate.populateScreen(listings);
            }

            @Override
            public void requestFailed() {
                //TODO present error
            }
        });
    }
    //end region

    public interface ViewLogicDelegate {
        /**
         * Populate the given list of listings to the user
         *
         * @param listings to present
         */
        void populateScreen(@NonNull List<ListingData> listings);

        /**
         * Show text to begin
         */
        void showMessage();

        /**
         * Hide text to begin
         */
        void hideMessage();
    }
}
