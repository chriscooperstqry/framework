package nz.co.cjc.base.features.categoriesandlistings.logic;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import nz.co.cjc.base.features.categoriesandlistings.events.CategoryEvent;
import nz.co.cjc.base.features.categoriesandlistings.models.ListingData;
import nz.co.cjc.base.features.categoriesandlistings.providers.contract.CategoriesAndListingsProvider;
import nz.co.cjc.base.framework.application.MainApp;
import nz.co.cjc.base.framework.core.logic.BaseViewLogic;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusProvider;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusSubscriber;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;

/**
 * Created by Chris Cooper on 5/05/16.
 * <p>
 * View logic for the listings fragment
 */
public class ListingsViewLogic extends BaseViewLogic<ListingsViewLogic.ViewLogicDelegate> implements EventBusSubscriber {


    private final StringsProvider mStringsProvider;
    private final CategoriesAndListingsProvider mCategoriesAndListingsProvider;
    private final EventBusProvider mEventBusProvider;
    private List<ListingData> mListingItems;

    @Inject
    public ListingsViewLogic(@NonNull StringsProvider stringsProvider,
                             @NonNull CategoriesAndListingsProvider categoriesAndListingsProvider,
                             @NonNull EventBusProvider eventBusProvider) {
        super(ViewLogicDelegate.class, stringsProvider);
        mStringsProvider = stringsProvider;
        mCategoriesAndListingsProvider = categoriesAndListingsProvider;
        mEventBusProvider = eventBusProvider;
    }

    public void initViewLogic(@Nullable ViewLogicDelegate delegate) {
        setDelegate(delegate);

        mListingItems = new ArrayList<>();
    }

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
                MainApp.getDagger().getLoggingProvider().d("failed listings");
            }
        });
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(@NonNull CategoryEvent event) {
        switch (event.getEventType()) {
            case CategorySelected:
                getListings(event.getBundle().getString(CategoriesViewLogic.CATEGORY_NUMBER));
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

    public interface ViewLogicDelegate {
        /**
         * Populate the given list of listings to the user
         *
         * @param listings to present
         */
        void populateScreen(@NonNull List<ListingData> listings);
    }
}
