package nz.co.cjc.base.framework.logging.providers.contracts;

import android.support.annotation.NonNull;

/**
 * Created by Chris Cooper on 4/05/16.
 */
public interface LoggingProvider {
    /**
     * Print a debug log to logcat, which is for debug purpose only and should be removed before committing.
     *
     * @param message to emit.
     */
    void d(@NonNull String message);

    /**
     * Print a info log to logcat, which is for import information, such as app status change, service status change
     *
     * @param message to emit.
     */
    void i(@NonNull String message);

    /**
     * Print error log to logcat, which is used for log something unexpected, such as network failure, invalid input
     *
     * @param message to emit.
     */
    void e(@NonNull String message);

    /**
     * Print stack trace for associated exception
     *
     * @param e exception
     */
    void e(@NonNull Exception e);
}
