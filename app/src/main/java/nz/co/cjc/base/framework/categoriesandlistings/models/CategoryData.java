package nz.co.cjc.base.framework.categoriesandlistings.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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
}
