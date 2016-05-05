package nz.co.cjc.base.features.categoriesandlistings.logic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import nz.co.cjc.base.R;
import nz.co.cjc.base.features.categoriesandlistings.events.CategoryEvent;
import nz.co.cjc.base.features.categoriesandlistings.ui.CategoriesFragment;
import nz.co.cjc.base.features.categoriesandlistings.ui.ListingsFragment;
import nz.co.cjc.base.framework.core.logic.BaseViewLogic;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusProvider;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusSubscriber;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p/>
 * view logic for the categories and listings activity
 */
public class CategoriesAndListingsViewLogic extends BaseViewLogic<CategoriesAndListingsViewLogic.ViewLogicDelegate> implements EventBusSubscriber {

    private final EventBusProvider mEventBusProvider;

    @Inject
    public CategoriesAndListingsViewLogic(@NonNull StringsProvider stringsProvider,
                                          @NonNull EventBusProvider eventBusProvider) {
        super(ViewLogicDelegate.class, stringsProvider);
        mEventBusProvider = eventBusProvider;
    }

    public void initViewLogic(@Nullable ViewLogicDelegate delegate) {
        setDelegate(delegate);

        Fragment categoriesFragment = CategoriesFragment.newInstance(new Bundle());
        mDelegate.presentFragment(categoriesFragment, R.id.categories_container, false);

        if (mDelegate.isListingsContainerAvailable()) {
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
                Fragment categoriesFragment = CategoriesFragment.newInstance(event.getBundle());
                mDelegate.presentFragment(categoriesFragment, R.id.categories_container, true);
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

    public interface ViewLogicDelegate {

        /**
         * Insert a fragment into the given container reference
         *
         * @param fragment            The fragment to present
         * @param fragmentContainerId The view container id of where to put the fragment
         * @param addToBackStack Whether we want to add this fragment to the back stack or not
         */
        void presentFragment(@NonNull Fragment fragment, int fragmentContainerId, boolean addToBackStack);

        /**
         * Check whether we are using the larger layout xml (we are on a tablet), to see if the listings container
         * is available for us to insert a fragment
         *
         * @return True if so, false otherwise
         */
        boolean isListingsContainerAvailable();
    }
}
