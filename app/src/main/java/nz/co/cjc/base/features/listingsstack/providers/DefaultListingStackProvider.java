package nz.co.cjc.base.features.listingsstack.providers;

import android.support.annotation.NonNull;

import java.util.Stack;

import nz.co.cjc.base.features.listingsstack.providers.contract.ListingsStackProvider;

/**
 * Created by Chris Cooper on 6/05/16.
 * <p>
 * Implementation of the listings stack provider
 */
public class DefaultListingStackProvider implements ListingsStackProvider {

    public static final String END_OF_CATEGORY ="End.Of.Category";

    private final Stack<String> mStack;

    public DefaultListingStackProvider() {
        mStack = new Stack<>();
    }

    @Override
    public void addListing(@NonNull String categoryNumber) {
        mStack.add(categoryNumber);
    }

    @NonNull
    @Override
    public String getTopListing() {
        return mStack.peek();
    }

    @Override
    public void removeListing() {
        mStack.pop();
    }

    @Override
    public boolean isListingsEmpty(){
        return mStack.isEmpty();
    }

    @Override
    public boolean isEndOfListing() {
        return END_OF_CATEGORY.equals(mStack.peek());
    }


}
