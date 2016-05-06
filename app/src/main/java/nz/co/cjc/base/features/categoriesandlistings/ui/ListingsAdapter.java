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
import nz.co.cjc.base.features.categoriesandlistings.models.ListingData;
import nz.co.cjc.base.framework.application.MainApp;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;

/**
 * Created by Chris Cooper on 6/05/16.
 * <p>
 * Adapter for displaying the listings in the list view.
 */
public class ListingsAdapter extends BaseAdapter {

    private List<ListingData> mListingItems;
    private final StringsProvider mStringsProvider;

    public ListingsAdapter() {
        mListingItems = new ArrayList<>();
        mStringsProvider = MainApp.getDagger().getStringsProvider();
    }

    public void setListingItems(@NonNull List<ListingData> categoryItems) {
        mListingItems = categoryItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mListingItems.size();
    }

    @Override
    public ListingData getItem(int position) {
        return mListingItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_listing, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ListingData item = getItem(position);

        viewHolder.listingTitle.setText(item.getTitle());

        return convertView;
    }

    private static class ViewHolder {
        public TextView listingTitle;

        public ViewHolder(View view) {
            listingTitle = ButterKnife.findById(view, R.id.title);
        }
    }

}
