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
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p>
 * View logic for the categories fragment
 */
public class CategoriesViewLogic extends BaseViewLogic<CategoriesViewLogic.ViewLogicDelegate> implements EventBusSubscriber {

    public static final String SUBCATEGORIES = "Subcategories";
    public static final String CATEGORY_NUMBER = "Category.Number";

    private final StringsProvider mStringsProvider;
    private final CategoriesAndListingsProvider mCategoriesAndListingsProvider;
    private final EventBusProvider mEventBusProvider;
    private List<CategoryData> mCategoryItems;

    @Inject
    public CategoriesViewLogic(@NonNull StringsProvider stringsProvider,
                               @NonNull CategoriesAndListingsProvider categoriesAndListingsProvider,
                               @NonNull EventBusProvider eventBusProvider) {
        super(ViewLogicDelegate.class, stringsProvider);
        mStringsProvider = stringsProvider;
        mCategoriesAndListingsProvider = categoriesAndListingsProvider;
        mEventBusProvider = eventBusProvider;
    }

    public void initViewLogic(@Nullable ViewLogicDelegate delegate, @NonNull Bundle arguments) {
        setDelegate(delegate);

        mCategoryItems = new ArrayList<>();
        ArrayList<CategoryData> bundleSubcategories = arguments.getParcelableArrayList(SUBCATEGORIES);

        if (bundleSubcategories != null) {
            mCategoryItems = bundleSubcategories;
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

        bundle.putString(CATEGORY_NUMBER, item.getNumber());
        bundle.putParcelableArrayList(SUBCATEGORIES, (ArrayList<? extends Parcelable>) item.getSubCategories());
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
