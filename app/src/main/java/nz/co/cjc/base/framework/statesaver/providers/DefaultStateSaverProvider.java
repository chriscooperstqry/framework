package nz.co.cjc.base.framework.statesaver.providers;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import nz.co.cjc.base.framework.statesaver.providers.contract.StateSaverProvider;

/**
 * Created by Chris Cooper on 7/05/16.
 * <p>
 * Default implementation of the state saver provider
 */
public class DefaultStateSaverProvider implements StateSaverProvider {

    /**
     * Save the given array list data into the given bundle
     *
     * @param key    The key to the data
     * @param value  The data
     * @param bundle The bundle to save it to
     */
    @Override
    public void saveParcelableArrayList(@NonNull String key, @NonNull ArrayList<? extends Parcelable> value, @NonNull Bundle bundle) {
        bundle.putParcelableArrayList(key, value);
    }

    /**
     * Get the saved array list data associated with the given key
     *
     * @param key    To lookup
     * @param bundle To look in
     * @return The array list if found, null otherwise
     */
    @Nullable
    @Override
    public <T extends Parcelable> ArrayList<T> getParcelableArrayList(@NonNull String key, @NonNull Bundle bundle) {
        return bundle.getParcelableArrayList(key);
    }
}
