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


    @Override
    public void saveParcelableArrayList(@NonNull String key, @NonNull ArrayList<? extends Parcelable> value, @NonNull Bundle bundle) {
        bundle.putParcelableArrayList(key, value);
    }

    @Nullable
    @Override
    public <T extends Parcelable> ArrayList<T> getParcelableArrayList(@NonNull String key, @NonNull Bundle bundle) {
        return bundle.getParcelableArrayList(key);
    }

    @Override
    public void saveInt(@NonNull String key, int value, @NonNull Bundle bundle) {
        bundle.putInt(key, value);
    }

    @Override
    public int getInt(@NonNull String key, @NonNull Bundle bundle, int defaultValue) {
        return bundle.getInt(key, defaultValue);
    }
}
