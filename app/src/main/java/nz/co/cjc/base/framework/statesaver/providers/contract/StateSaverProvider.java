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

    public static final String STATE_ITEMS = "State.Items";

    void saveParcelableArrayList(@NonNull String key, @NonNull ArrayList<? extends Parcelable> value, @NonNull Bundle bundle);

    @Nullable
    <T extends Parcelable> ArrayList<T> getParcelableArrayList(@NonNull String key, @NonNull Bundle bundle);

}
