package nz.co.cjc.base.features.categoriesandlistings.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.ButterKnife;
import nz.co.cjc.base.R;
import nz.co.cjc.base.features.categoriesandlistings.logic.CategoriesAndListingsViewLogic;
import nz.co.cjc.base.features.core.ui.CoreActivity;
import nz.co.cjc.base.framework.application.MainApp;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p>
 * Activity to let the user browse the categories and listings
 *
 * Involves 2 fragments, one containing the categories, one containing the listings
 * Both will always be on the screen
 *
 * When on a small device, categories fragment will appear in a sliding drawer down the bottom
 * over top of the listings
 *
 * On larger devices they will appear side by side
 */
public class CategoriesAndListingsActivity extends CoreActivity {

    private SlidingUpPanelLayout mSlidingPanelLayout;

    private CategoriesAndListingsViewLogic mViewLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
        mViewLogic = MainApp.getDagger().createCategoriesAndListingsViewLogic();
        mViewLogic.initViewLogic(mViewLogicDelegate, savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mViewLogic != null) {
            mViewLogic.screenResumed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mViewLogic != null) {
            mViewLogic.screenPaused();
        }
    }

    @Override
    public void onBackPressed() {

        boolean bubbleUp = true;
        if (mViewLogic != null) {
            bubbleUp = mViewLogic.onBackPressed();
        }

        if (bubbleUp) {
            super.onBackPressed();
        }
    }

    private void initUI() {
        setContentView(R.layout.categories_and_listings_activity);
        setupToolbar();
        mSlidingPanelLayout = ButterKnife.findById(this, R.id.sliding_layout);
    }

    private CategoriesAndListingsViewLogic.ViewLogicDelegate mViewLogicDelegate = new CategoriesAndListingsViewLogic.ViewLogicDelegate() {
        @Override
        public void presentFragment(@NonNull Fragment fragment, int fragmentContainerId, boolean addToBackStack) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(fragmentContainerId, fragment);
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
            fragmentManager.executePendingTransactions();
        }

        @Override
        public void setSlidingPanelScrollableView() {
            if (mSlidingPanelLayout != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                CategoriesFragment fragment = (CategoriesFragment) fragmentManager.findFragmentById(R.id.categories_container);
                if (fragment != null && fragment.getListView() != null) {
                    mSlidingPanelLayout.setScrollableView(fragment.getListView());
                }
            }
        }

        @Override
        public void closeSlidingPanel() {
            if (mSlidingPanelLayout != null) {
                mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        }

        @Override
        public void updateToolbarText(@NonNull String text) {
            TextView title = ButterKnife.findById(mToolbar, R.id.toolbar_title);
            title.setText(text);
        }

        @Override
        @NonNull
        public String getToolbarTitle() {
            TextView title = ButterKnife.findById(mToolbar, R.id.toolbar_title);
            return title.getText().toString();
        }

    };
}
