package nz.co.cjc.base.features.listingsstack.providers;

import android.support.annotation.NonNull;

import java.util.Stack;

import nz.co.cjc.base.features.categoriesandlistings.models.CategoryData;
import nz.co.cjc.base.features.listingsstack.providers.contract.ListingsStackProvider;

/**
 * Created by Chris Cooper on 6/05/16.
 * <p>
 * Implementation of the listings stack provider
 */
public class DefaultListingStackProvider implements ListingsStackProvider {

    private final Stack<CategoryData> mStack;

    public DefaultListingStackProvider() {
        mStack = new Stack<>();
    }

    @Override
    public void addListing(@NonNull CategoryData model) {
        mStack.add(model);
    }

    @NonNull
    @Override
    public CategoryData getTopListing() {
        return mStack.peek();
    }

    @Override
    public void removeListing() {
        mStack.pop();
    }

    @Override
    public boolean isListingsEmpty() {
        return mStack.isEmpty();
    }

    @Override
    public boolean isEndOfSubcategory() {
        return mStack.peek().getSubCategories().isEmpty();
    }

    @Override
    public int size() {
        return mStack.size();
    }

    @Override
    public boolean isViewingEmptyRootSubcategory() {
        return mStack.size() != 1 && mStack.peek().getSubCategories().isEmpty();
    }


}
