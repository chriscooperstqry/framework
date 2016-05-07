package nz.co.cjc.base.features.categoriesandlistings.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import nz.co.cjc.base.framework.utils.StringUtils;

/**
 * Created by Chris Cooper on 6/05/16.
 * <p/>
 * Model class representing a listing returned from the api
 */
public class ListingData implements Parcelable {

    @SerializedName("ListingId")
    private String mListingId;

    @SerializedName("Title")
    private String mTitle;

    @SerializedName("PictureHref")
    private String mImageUrl;

    @SerializedName("PriceDisplay")
    private String mPriceDisplay;

    protected ListingData(Parcel in) {
        mListingId = in.readString();
        mTitle = in.readString();
        mImageUrl = in.readString();
        mPriceDisplay = in.readString();
    }

    public static final Creator<ListingData> CREATOR = new Creator<ListingData>() {
        @Override
        public ListingData createFromParcel(Parcel in) {
            return new ListingData(in);
        }

        @Override
        public ListingData[] newArray(int size) {
            return new ListingData[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mListingId);
        dest.writeString(mTitle);
        dest.writeString(mImageUrl);
        dest.writeString(mPriceDisplay);
    }
}
