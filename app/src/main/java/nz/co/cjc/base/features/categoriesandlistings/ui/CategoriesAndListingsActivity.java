package nz.co.cjc.base.features.categoriesandlistings.ui;

import android.os.Bundle;
import android.widget.FrameLayout;

import butterknife.BindView;
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

    @BindView(R.id.categories_container)
    FrameLayout mCategoriesContainer;

    @BindView(R.id.listings_container)
    FrameLayout mListingsContainer;

    private CategoriesAndListingsViewLogic mViewLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
        mViewLogic = MainApp.getDagger().createCategoriesAndListingsViewLogic();
        mViewLogic.setDelegate(mViewLogicDelegate);
    }

    private void initUI() {
        setContentView(R.layout.categories_and_listings_activity);

    }

    private CategoriesAndListingsViewLogic.ViewLogicDelegate mViewLogicDelegate = new CategoriesAndListingsViewLogic.ViewLogicDelegate() {
    };
}
