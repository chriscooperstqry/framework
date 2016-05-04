package nz.co.cjc.base.framework.threading.providers.contracts;

import android.support.annotation.NonNull;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p/>
 * A way to make a runnable execute on the main
 * thread. This provider is to give a quick and
 * easy way to do this without littering lots
 * of duplicated boilerplate main thread code.
 * <p/>
 * It also allows for calling a runnable on the
 * main thread if you are not running within
 * an Activity context etc.
 */
public interface ThreadUtilsProvider {
    /**
     * Run the given runnable on the main thread.
     *
     * @param runnable to execute on the main thread.
     */
    void runOnMainThread(@NonNull Runnable runnable);

    /**
     * Run the given runnable on the main thread.
     *
     * @param runnable to execute on the main thread.
     * @param delay    delay time in milliseconds
     */
    void runOnMainThread(@NonNull Runnable runnable, int delay);

    /**
     * Run the given runnable on the main thread.
     *
     * @param runnable to execute on the main thread.
     */
//    void runOnBackgroundThread(@NonNull Runnable runnable);
}
