package nz.co.cjc.base.features.categoriesandlistings.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import nz.co.cjc.base.R;
import nz.co.cjc.base.features.categoriesandlistings.logic.CategoriesViewLogic;
import nz.co.cjc.base.features.categoriesandlistings.models.CategoryData;
import nz.co.cjc.base.framework.application.MainApp;
import nz.co.cjc.base.framework.constants.AppConstants;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p>
 * Fragment to display the categories received from the api
 *
 * Will either be displayed inside a sliding panel on small devices, or
 * just next to the listings fragment on larger devices.
 *
 * Contains a simple list view for displaying the categories
 */
public class CategoriesFragment extends Fragment {

    private ListView mListView;
    private ProgressBar mProgressBar;
    private TextView mErrorView;
    private CategoriesAdapter mAdapter;

    @Inject
    CategoriesViewLogic mViewLogic;

    // region public
    @NonNull
    public static CategoriesFragment newInstance(@NonNull Bundle arguments) {
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        categoriesFragment.setArguments(arguments);
        return categoriesFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        MainApp.getDagger().inject(this);
        mViewLogic.initViewLogic(mViewLogicDelegate, getArguments(), savedInstanceState);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.categories_fragment, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListView = null;
        mErrorView = null;
        mProgressBar = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mViewLogic != null) {
            mViewLogic.disconnect();
            mViewLogic = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mViewLogic != null) {
            mViewLogic.onSaveInstanceState(outState);
        }
    }

    @Nullable
    public ListView getListView() {
        return mListView;
    }
    //end region

    //region private
    private void initUI(View view) {
        mListView = ButterKnife.findById(view, R.id.list_view);
        mErrorView = ButterKnife.findById(view, R.id.error_view);
        mProgressBar = ButterKnife.findById(view, R.id.progress_bar);
        mAdapter = new CategoriesAdapter();
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mViewLogic != null) {
                            mViewLogic.listItemSelected(position);
                        }
                    }
                }, AppConstants.RIPPLE_DELAY);

            }
        });

        mErrorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewLogic != null) {
                    mViewLogic.onErrorViewClick();
                }
            }
        });

    }
    //end region

    private CategoriesViewLogic.ViewLogicDelegate mViewLogicDelegate = new CategoriesViewLogic.ViewLogicDelegate() {

        @Override
        public void populateScreen(@NonNull List<CategoryData> categories) {
            mAdapter.setCategoryItems(categories);
        }

        @Override
        public void setSelectedItem(int position) {
            mAdapter.setSelectedItem(position);
        }

        @Override
        public void hideProgressBar() {
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void hideErrorView() {
            mErrorView.setVisibility(View.GONE);
        }

        @Override
        public void showErrorView() {
            mErrorView.setVisibility(View.VISIBLE);
        }

        @Override
        public void showProgressBar() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

    };

}
