package nz.co.cjc.base.features.categoriesandlistings.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import nz.co.cjc.base.R;
import nz.co.cjc.base.features.categoriesandlistings.logic.CategoriesViewLogic;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p>
 * Fragment to display the categories received from the api
 */
public class CategoriesFragment extends Fragment {

    @BindView(R.id.list_view) ListView mListView;

    @Inject
    CategoriesViewLogic mViewLogic;

    @NonNull
    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        mViewLogic.initViewLogic(mViewLogicDelegate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.categories_fragment, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mViewLogic != null) {
            mViewLogic.disconnect();
            mViewLogic = null;
        }
    }

    private void initUI(View view) {
        ButterKnife.bind(this, view);
    }

    private CategoriesViewLogic.ViewLogicDelegate mViewLogicDelegate = new CategoriesViewLogic.ViewLogicDelegate() {

    };

}
