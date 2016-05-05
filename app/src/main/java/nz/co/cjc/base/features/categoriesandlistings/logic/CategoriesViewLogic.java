package nz.co.cjc.base.features.categoriesandlistings.logic;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;

import nz.co.cjc.base.features.categoriesandlistings.models.CategoryData;
import nz.co.cjc.base.features.categoriesandlistings.providers.contract.CategoriesAndListingsProvider;
import nz.co.cjc.base.framework.application.MainApp;
import nz.co.cjc.base.framework.core.logic.BaseViewLogic;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p/>
 * View logic for the categories fragment
 */
public class CategoriesViewLogic extends BaseViewLogic<CategoriesViewLogic.ViewLogicDelegate> {

    private final StringsProvider mStringsProvider;
    private final CategoriesAndListingsProvider mCategoriesAndListingsProvider;

    @Inject
    public CategoriesViewLogic(@NonNull StringsProvider stringsProvider,
                               @NonNull CategoriesAndListingsProvider categoriesAndListingsProvider) {
        super(ViewLogicDelegate.class, stringsProvider);
        mStringsProvider = stringsProvider;
        mCategoriesAndListingsProvider = categoriesAndListingsProvider;
    }

    public void initViewLogic(@Nullable ViewLogicDelegate delegate) {
        setDelegate(delegate);

        mCategoriesAndListingsProvider.getCategoriesData(new CategoriesAndListingsProvider.CategoriesRequestDelegate() {
            @Override
            public void requestSuccess(@NonNull List<CategoryData> categories) {
                mDelegate.populateScreen(categories);
            }

            @Override
            public void requestFailed() {
                MainApp.getDagger().getLoggingProvider().d("failed");
            }
        });

    }

    public interface ViewLogicDelegate {

        /**
         * Populate the given list of categories to the user
         *
         * @param categories to present
         */
        void populateScreen(@NonNull List<CategoryData> categories);
    }
}
