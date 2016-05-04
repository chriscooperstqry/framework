package nz.co.cjc.base.framework.strings.providers;

import android.content.Context;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.util.UUID;

import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 *
 * Implementation of the strings provider for resolving
 * application strings and arguments.
 */
public class DefaultStringsProvider implements StringsProvider {
    private final Context mApplicationContext;

    public DefaultStringsProvider(@NonNull Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @NonNull
    @Override
    public String get(@StringRes int resourceId) {
        return mApplicationContext.getString(resourceId);
    }

    @NonNull
    @Override
    public String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    @NonNull
    @Override
    public String[] getStringArray(@ArrayRes int resourceId) {
        return mApplicationContext.getResources().getStringArray(resourceId);
    }
}
