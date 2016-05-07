package nz.co.cjc.base.features.categoriesandlistings.logic;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import nz.co.cjc.base.features.categoriesandlistings.events.CategoryEvent;
import nz.co.cjc.base.features.categoriesandlistings.models.CategoryData;
import nz.co.cjc.base.features.categoriesandlistings.providers.contract.CategoriesAndListingsProvider;
import nz.co.cjc.base.framework.application.MainApp;
import nz.co.cjc.base.framework.core.logic.BaseViewLogic;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusProvider;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusSubscriber;
import nz.co.cjc.base.framework.statesaver.providers.contract.StateSaverProvider;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p>
 * View logic for the categories fragment
 */
public class CategoriesViewLogic extends BaseViewLogic<CategoriesViewLogic.ViewLogicDelegate> implements EventBusSubscriber {

    public static final String CATEGORY_DATA = "Category.Data";

    private final StringsProvider mStringsProvider;
    private final CategoriesAndListingsProvider mCategoriesAndListingsProvider;
    private final EventBusProvider mEventBusProvider;
    private final StateSaverProvider mStateSaverProvider;
    private List<CategoryData> mCategoryItems;

    @Inject
    public CategoriesViewLogic(@NonNull StringsProvider stringsProvider,
                               @NonNull CategoriesAndListingsProvider categoriesAndListingsProvider,
                               @NonNull EventBusProvider eventBusProvider,
                               @NonNull StateSaverProvider stateSaverProvider) {
        super(ViewLogicDelegate.class, stringsProvider);
        mStringsProvider = stringsProvider;
        mCategoriesAndListingsProvider = categoriesAndListingsProvider;
        mEventBusProvider = eventBusProvider;
        mStateSaverProvider = stateSaverProvider;
    }

    public void initViewLogic(@Nullable ViewLogicDelegate delegate, @NonNull Bundle arguments, @Nullable Bundle savedInstanceState) {
        setDelegate(delegate);

        CategoryData categoryData = arguments.getParcelable(CATEGORY_DATA);
        mCategoryItems = new ArrayList<>();

        if (savedInstanceState != null) {
            mCategoryItems = mStateSaverProvider.getParcelableArrayList(StateSaverProvider.STATE_ITEMS, savedInstanceState);
            mDelegate.populateScreen(mCategoryItems);
            return;
        }

        if (categoryData != null && !categoryData.getSubCategories().isEmpty()) {
            mCategoryItems = categoryData.getSubCategories();
            mDelegate.populateScreen(mCategoryItems);
        } else {
            mCategoriesAndListingsProvider.getCategoriesData(null, new CategoriesAndListingsProvider.CategoriesRequestDelegate() {
                @Override
                public void requestSuccess(@NonNull List<CategoryData> categories) {
                    mCategoryItems = categories;
                    mDelegate.populateScreen(categories);
                }

                @Override
                public void requestFailed() {
                    //TODO present error
                    MainApp.getDagger().getLoggingProvider().d("failed");
                }
            });

        }

    }

    public void listItemSelected(int position) {
        CategoryData item = mCategoryItems.get(position);

        Bundle bundle = new Bundle();
        bundle.putParcelable(CATEGORY_DATA, item);

        mEventBusProvider.postEvent(new CategoryEvent(null, CategoryEvent.EventType.CategorySelected, bundle));

        if (item.getSubCategories().isEmpty()) {
            mDelegate.setSelectedItem(position);
        }
    }

    public void screenResumed() {
        subscribeToEventBus();
        mEventBusProvider.postEvent(new CategoryEvent(null, CategoryEvent.EventType.CategoryLayoutReady, null));
    }

    @Override
    public void subscribeToEventBus() {
        mEventBusProvider.subscribe(this);
    }

    @Override
    public void unsubscribeFromEventBus() {
        mEventBusProvider.unsubscribe(this);
    }

    public void screenPaused() {
        unsubscribeFromEventBus();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(@NonNull CategoryEvent event) {
        switch (event.getEventType()) {
            case ClearCategorySelection:
                mDelegate.setSelectedItem(-1);
                break;
        }
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        mStateSaverProvider.saveParcelableArrayList(StateSaverProvider.STATE_ITEMS, (ArrayList<? extends Parcelable>) mCategoryItems, outState);
    }

    public interface ViewLogicDelegate {

        /**
         * Populate the given list of categories to the user
         *
         * @param categories to present
         */
        void populateScreen(@NonNull List<CategoryData> categories);

        /**
         * Remember the users selection so we can highlight it
         *
         * @param position in the adapter
         */
        void setSelectedItem(int position);
    }
}
