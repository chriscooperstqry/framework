package nz.co.cjc.base.features.categoriesandlistings.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
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
 * Activity to let the user browse the categories and potentially the listings also
 * if the user is using a tablet
 */
public class CategoriesAndListingsActivity extends CoreActivity {

    private FrameLayout mCategoriesContainer;
    private FrameLayout mListingsContainer;
    private SlidingUpPanelLayout mSlidingPanelLayout;

    private CategoriesAndListingsViewLogic mViewLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
        mViewLogic = MainApp.getDagger().createCategoriesAndListingsViewLogic();
        mViewLogic.initViewLogic(mViewLogicDelegate);
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
        mCategoriesContainer = ButterKnife.findById(this, R.id.categories_container);
        mListingsContainer = ButterKnife.findById(this, R.id.listings_container);
        mSlidingPanelLayout = ButterKnife.findById(this, R.id.sliding_layout);
    }

    private CategoriesAndListingsViewLogic.ViewLogicDelegate mViewLogicDelegate = new CategoriesAndListingsViewLogic.ViewLogicDelegate() {
        @Override
        public void presentFragment(@NonNull Fragment fragment, int fragmentContainerId, boolean addToBackStack) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(fragmentContainerId, fragment);
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
            fragmentManager.executePendingTransactions();
        }

        @Override
        public boolean isListingsContainerAvailable() {
            return mListingsContainer != null;
        }

        @Override
        public void setSlidingPanelScrollableView() {
            if (mSlidingPanelLayout != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                CategoriesFragment fragment = (CategoriesFragment) fragmentManager.findFragmentById(R.id.categories_container);
                if (fragment != null && fragment.getListView() != null) {
                    MainApp.getDagger().getLoggingProvider().d("Setting scrollable view");
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
