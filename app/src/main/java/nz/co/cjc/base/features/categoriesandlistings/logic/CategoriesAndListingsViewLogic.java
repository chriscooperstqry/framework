package nz.co.cjc.base.features.categoriesandlistings.logic;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import javax.inject.Inject;

import nz.co.cjc.base.R;
import nz.co.cjc.base.features.categoriesandlistings.ui.CategoriesFragment;
import nz.co.cjc.base.features.categoriesandlistings.ui.ListingsFragment;
import nz.co.cjc.base.framework.core.logic.BaseViewLogic;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p/>
 * view logic for the categories and listings activity
 */
public class CategoriesAndListingsViewLogic extends BaseViewLogic<CategoriesAndListingsViewLogic.ViewLogicDelegate> {

    @Inject
    public CategoriesAndListingsViewLogic(@NonNull StringsProvider stringsProvider) {
        super(ViewLogicDelegate.class, stringsProvider);
    }

    public void initViewLogic(@Nullable ViewLogicDelegate delegate) {
        setDelegate(delegate);

        Fragment categoriesFragment = CategoriesFragment.newInstance();
        mDelegate.presentFragment(categoriesFragment, R.id.categories_container);

        if (mDelegate.isListingsContainerAvailable()) {
            Fragment listingsFragment = ListingsFragment.newInstance();
            mDelegate.presentFragment(listingsFragment, R.id.listings_container);
        }
    }

    public interface ViewLogicDelegate {

        /**
         * Insert a fragment into the given container reference
         *
         * @param fragment            The fragment to present
         * @param fragmentContainerId The view container id of where to put the fragment
         */
        void presentFragment(@NonNull Fragment fragment, int fragmentContainerId);

        /**
         * Check whether we are using the larger layout xml (we are on a tablet), to see if the listings container
         * is available for us to insert a fragment
         *
         * @return True if so, false otherwise
         */
        boolean isListingsContainerAvailable();
    }
}
