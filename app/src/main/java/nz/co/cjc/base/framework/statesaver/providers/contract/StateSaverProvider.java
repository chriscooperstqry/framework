package nz.co.cjc.base.framework.statesaver.providers.contract;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by Chris Cooper on 7/05/16.
 * <p>
 * Provider to help with saving state of fragments and activities
 */
public interface StateSaverProvider {

    String STATE_PARCELABLE_ARRAY_LIST = "State.Parcelable.Array.List";
    String STATE_INT = "State.Int";

    /**
     * Save the given array list data into the given bundle
     *
     * @param key    The key to the data
     * @param value  The data
     * @param bundle The bundle to save it to
     */
    void saveParcelableArrayList(@NonNull String key, @NonNull ArrayList<? extends Parcelable> value, @NonNull Bundle bundle);

    /**
     * Get the saved array list data associated with the given key
     *
     * @param key    To lookup
     * @param bundle To look in
     * @return The array list if found, null otherwise
     */
    @Nullable
    <T extends Parcelable> ArrayList<T> getParcelableArrayList(@NonNull String key, @NonNull Bundle bundle);

    /**
     * Save the given int into the given bundle
     *
     * @param key    The key to the data
     * @param value  The data
     * @param bundle To save data to
     */
    void saveInt(@NonNull String key, int value, @NonNull Bundle bundle);

    /**
     * Get the saved int from the given bundle
     *
     * @param key          To look up
     * @param bundle       To look in
     * @param defaultValue to return if value associated with key not found
     * @return Value associated with key, or default value provided if not
     */
    int getInt(@NonNull String key, @NonNull Bundle bundle, int defaultValue);

}
