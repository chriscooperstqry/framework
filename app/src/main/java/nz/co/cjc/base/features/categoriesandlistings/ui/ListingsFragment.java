package nz.co.cjc.base.features.categoriesandlistings.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import nz.co.cjc.base.R;
import nz.co.cjc.base.features.categoriesandlistings.logic.ListingsViewLogic;
import nz.co.cjc.base.features.categoriesandlistings.models.ListingData;
import nz.co.cjc.base.framework.application.MainApp;

/**
 * Created by Chris Cooper on 5/05/16.
 * <p/>
 * Fragment to display the listings related to a category received from the api
 */
public class ListingsFragment extends Fragment {

    private ListView mListView;
    private ListingsAdapter mAdapter;

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
        return inflater.inflate(R.layout.listings_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mViewLogic != null) {
            mViewLogic.screenResumed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mViewLogic != null) {
            mViewLogic.screenPaused();
        }
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
        mListView = ButterKnife.findById(view, R.id.list_view);
        mAdapter = new ListingsAdapter();

        mListView.setAdapter(mAdapter);
    }

    private ListingsViewLogic.ViewLogicDelegate mViewLogicDelegate = new ListingsViewLogic.ViewLogicDelegate() {

        @Override
        public void populateScreen(@NonNull List<ListingData> listings) {
            mAdapter.setListingItems(listings);
        }
    };

}
