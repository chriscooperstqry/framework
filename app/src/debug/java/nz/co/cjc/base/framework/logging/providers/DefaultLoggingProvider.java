package nz.co.cjc.base.framework.logging.providers;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import nz.co.cjc.base.framework.logging.providers.contracts.LoggingProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 *
 * Implementation of the debug variant of the logging provider.
 */
public class DefaultLoggingProvider implements LoggingProvider {
    static{
        Logger.init().methodOffset(1);
    }
    @Override
    public void d(@NonNull String message) {
        Logger.d(message);
    }

    @Override
    public void i(@NonNull String message) {
        Logger.i(message);
    }

    @Override
    public void e(@NonNull String message) {
        Logger.e(message);
    }

    public void e(@NonNull Exception e) {
        Logger.e(e, e.getMessage());
    }
}
