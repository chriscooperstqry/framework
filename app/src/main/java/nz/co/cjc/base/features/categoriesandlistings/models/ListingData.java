package nz.co.cjc.base.features.categoriesandlistings.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import nz.co.cjc.base.framework.utils.StringUtils;

/**
 * Created by Chris Cooper on 6/05/16.
 * <p/>
 * Model class representing a listing returned from the api
 */
public class ListingData {

    @SerializedName("ListingId")
    private String mListingId;

    @SerializedName("Title")
    private String mTitle;

    @SerializedName("PictureHref")
    private String mImageUrl;

    @SerializedName("PriceDisplay")
    private String mPriceDisplay;

    @NonNull
    public String getListingId() {
        return StringUtils.emptyIfNull(mListingId);
    }
    @NonNull
    public String getTitle() {
        return StringUtils.emptyIfNull(mTitle);
    }

    @NonNull
    public String getImageUrl() {
        return StringUtils.emptyIfNull(mImageUrl);
    }

    @NonNull
    public String getPriceDisplay() {
        return StringUtils.emptyIfNull(mPriceDisplay);
    }
}
