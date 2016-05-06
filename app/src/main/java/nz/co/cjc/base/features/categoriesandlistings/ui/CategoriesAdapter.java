package nz.co.cjc.base.features.categoriesandlistings.ui;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import nz.co.cjc.base.R;
import nz.co.cjc.base.features.categoriesandlistings.models.CategoryData;
import nz.co.cjc.base.framework.application.MainApp;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;

/**
 * Created by Chris Cooper on 5/05/16.
 * <p/>
 * Adapter for displaying the categories in the list view.
 * Also implements sticky headers for showing the category alphabetically
 */
public class CategoriesAdapter extends BaseAdapter {

    private List<CategoryData> mCategoryItems;
    private final StringsProvider mStringsProvider;

    public CategoriesAdapter() {
        mCategoryItems = new ArrayList<>();
        mStringsProvider = MainApp.getDagger().getStringsProvider();
    }

    public void setCategoryItems(@NonNull List<CategoryData> categoryItems) {
        mCategoryItems = categoryItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCategoryItems.size();
    }

    @Override
    public CategoryData getItem(int position) {
        return mCategoryItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CategoryData item = getItem(position);

        viewHolder.categoryName.setText(item.getName());

        return convertView;
    }

//    @Override
//    public View getHeaderView(int position, View convertView, ViewGroup parent) {
//        HeaderViewHolder headerViewHolder;
//        if (convertView == null) {
//            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category_sticky_header, parent, false);
//            headerViewHolder = new HeaderViewHolder(convertView);
//            convertView.setTag(headerViewHolder);
//        } else {
//            headerViewHolder = (HeaderViewHolder) convertView.getTag();
//        }
//
//        //Categorise the trade me items differently
//        if (mCategoryItems.get(position).getName().startsWith(mStringsProvider.get(R.string.sticky_header_trade_me_suffix))) {
//            headerViewHolder.headerName.setText(mStringsProvider.get(R.string.sticky_header_trade_me_category));
//        } else {
//            headerViewHolder.headerName.setText(new StringBuilder().append(mCategoryItems.get(position).getName().charAt(0)));
//        }
//
//        return convertView;
//    }
//
//    @Override
//    public long getHeaderId(int position) {
//        //Categorise the trade me items differently
//        if (mCategoryItems.get(position).getName().startsWith(mStringsProvider.get(R.string.sticky_header_trade_me_suffix))) {
//            return -1;
//        }
//        return mCategoryItems.get(position).getName().charAt(0);
//    }

    private static class ViewHolder {
        public TextView categoryName;
        public ViewHolder(View view) {
            categoryName = ButterKnife.findById(view, R.id.name);
        }
    }

    private static class HeaderViewHolder {
        public TextView headerName;
        public HeaderViewHolder(View view) {
            headerName = ButterKnife.findById(view, R.id.header);
        }
    }
}
