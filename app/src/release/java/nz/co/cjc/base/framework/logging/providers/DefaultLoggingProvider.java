package nz.co.cjc.base.framework.logging.providers;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import nz.co.cjc.base.framework.logging.providers.contracts.LoggingProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 *
 * Logging provider implementation for the release variant, that
 * emits no logging output.
 */
public class DefaultLoggingProvider implements LoggingProvider {
    static{
        Logger.init().methodOffset(1);
    }
    @Override
    public void d(@NonNull String message) {
    }

    @Override
    public void i(@NonNull String message) {
    }

    @Override
    public void e(@NonNull String message) {
    }

    public void e(@NonNull Exception e) {
    }
}
