package nz.co.cjc.base.framework.threading.providers;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import nz.co.cjc.base.framework.threading.providers.contracts.ThreadUtilsProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p/>
 * Convenience implementation to run code on the main looper thread,
 * regardless of where the calling code is scoped (ie, doesn't have
 * to be inside an Android component etc).
 */
public class DefaultThreadUtilsProvider implements ThreadUtilsProvider {
    private final Context mApplicationContext;

    public DefaultThreadUtilsProvider(@NonNull Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @Override
    public void runOnMainThread(final @NonNull Runnable runnable) {
        runOnMainThread(runnable, 0);
    }

    @Override
    public void runOnMainThread(@NonNull Runnable runnable, int delay) {
        new Handler(mApplicationContext.getMainLooper()).postDelayed(runnable, delay);
    }

//    @Override
//    public void runOnBackgroundThread(@NonNull final Runnable runnable) {
//        Task.callInBackground(new Callable<Void>() {
//            @Override
//            public Void call() throws Exception {
//                runnable.run();
//                return null;
//            }
//        });
//    }
}