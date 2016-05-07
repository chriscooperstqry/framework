package nz.co.cjc.base.features.categoriesandlistings.ui;

import android.graphics.Typeface;
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
 * <p>
 * Adapter for displaying the categories in the list view.
 * Also implements sticky headers for showing the category alphabetically
 */
public class CategoriesAdapter extends BaseAdapter {

    private int mSelectedPosition;
    private List<CategoryData> mCategoryItems;
    private final StringsProvider mStringsProvider;

    public CategoriesAdapter() {
        mCategoryItems = new ArrayList<>();
        mStringsProvider = MainApp.getDagger().getStringsProvider();
        mSelectedPosition = -1;
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

        if (mSelectedPosition == position) {
            viewHolder.categoryName.setTypeface(null, Typeface.BOLD);
            viewHolder.categoryName.setTextColor(MainApp.getDagger().getApplicationContext().getResources().getColor(R.color.accent));
        } else {
            viewHolder.categoryName.setTypeface(null, Typeface.NORMAL);
            viewHolder.categoryName.setTextColor((MainApp.getDagger().getApplicationContext().getResources().getColor(R.color.primary_text)));
        }

        return convertView;
    }

    public void setSelectedItem(int position) {
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        public TextView categoryName;

        public ViewHolder(View view) {
            categoryName = ButterKnife.findById(view, R.id.name);
        }
    }

}
