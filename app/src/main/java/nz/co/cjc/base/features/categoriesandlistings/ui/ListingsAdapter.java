package nz.co.cjc.base.features.categoriesandlistings.ui;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_listing_2, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ListingData item = getItem(position);

        viewHolder.title.setText(item.getTitle());
        viewHolder.subTitle.setText(item.getPriceDisplay());
        viewHolder.chevron.getDrawable().setColorFilter(MainApp.getDagger().getApplicationContext().getResources().getColor(R.color.primary_text),android.graphics.PorterDuff.Mode.MULTIPLY);

        DrawableRequestBuilder<String> request = Glide.with(viewHolder.image.getContext())
                .load(item.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                });

        request.into(viewHolder.image);

        return convertView;
    }


    private static class ViewHolder {
        public TextView title;
        public TextView subTitle;
        public ImageView image;
        public ImageView chevron;

        public ViewHolder(View view) {
            title = ButterKnife.findById(view, R.id.title);
            subTitle = ButterKnife.findById(view, R.id.subtitle);
            image = ButterKnife.findById(view, R.id.image);
            chevron = ButterKnife.findById(view, R.id.chevron);

        }
    }

}
