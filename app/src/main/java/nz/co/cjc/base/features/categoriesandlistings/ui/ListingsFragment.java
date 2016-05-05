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
import nz.co.cjc.base.features.categoriesandlistings.logic.ListingsViewLogic;
import nz.co.cjc.base.framework.application.MainApp;

/**
 * Created by Chris Cooper on 5/05/16.
 * <p/>
 * Fragment to display the listings related to a category received from the api
 */
public class ListingsFragment extends Fragment {

    @BindView(R.id.list_view) ListView mListView;

    @Inject
    ListingsViewLogic mViewLogic;

    @NonNull
    public static ListingsFragment newInstance() {
        return new ListingsFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        MainApp.getDagger().inject(this);
        mViewLogic.initViewLogic(mViewLogicDelegate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listings_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
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

    private ListingsViewLogic.ViewLogicDelegate mViewLogicDelegate = new ListingsViewLogic.ViewLogicDelegate() {

    };

}
