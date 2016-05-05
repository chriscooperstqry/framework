package nz.co.cjc.base.features.categoriesandlistings.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import nz.co.cjc.base.framework.utils.StringUtils;

/**
 * Created by Chris Cooper on 5/05/16.
 * <p/>
 * Model class representing a category returned from the api
 */
public class CategoryData {

    @SerializedName("Name")
    private String mName;

    @SerializedName("Number")
    private String mNumber;

    @SerializedName("Path")
    private String mPath;

    @SerializedName("Subcategories")
    private List<CategoryData> mSubCategories;

    @NonNull
    public String getName() {
        return StringUtils.emptyIfNull(mName);
    }

    @NonNull
    public String getNumber() {
        return StringUtils.emptyIfNull(mNumber);
    }

    @NonNull
    public String getPath() {
        return StringUtils.emptyIfNull(mPath);
    }

    @NonNull
    public List<CategoryData> getSubCategories() {
        if (mSubCategories == null) {
            return new ArrayList<>();
        }
        return mSubCategories;
    }
}
