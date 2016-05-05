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

/**
 * Created by Chris Cooper on 5/05/16.
 * <p/>
 * Adapter for displaying the categories in the list view
 */
public class CategoriesAdapter extends BaseAdapter {

    private List<CategoryData> mCategoryItems;

    public CategoriesAdapter() {
        mCategoryItems = new ArrayList<>();
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

    private static class ViewHolder {

        public TextView categoryName;

        public ViewHolder(View view) {
            categoryName = ButterKnife.findById(view, R.id.name);
        }
    }
}
