package nz.co.cjc.base.framework.strings.providers.contracts;

import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Created by Chris Cooper on 4/05/16.
 *
 * Provider to resolve application strings.
 */
public interface StringsProvider {
    /**
     * Retrieve a string with the given resource id from
     * the app string resources.
     *
     * @param resourceId to retrieve.
     *
     * @return the string for the given resource id.
     */
    @NonNull String get(@StringRes int resourceId);

    /**
     * Generate a unique string id that can be used as a key for something.
     *
     * @return unique string - typically some form of UUID.
     */
    @NonNull String generateUniqueId();

    /**
     * Retrieve a string array with the given resource id from
     *
     * @param resourceId to retrieve.
     *
     * @return the string array for the given resource id.
     */
    @NonNull String[] getStringArray(@ArrayRes int resourceId);
}
