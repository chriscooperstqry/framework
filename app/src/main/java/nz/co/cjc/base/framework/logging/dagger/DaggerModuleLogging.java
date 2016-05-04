package nz.co.cjc.base.framework.logging.dagger;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nz.co.cjc.base.framework.logging.providers.DefaultLoggingProvider;
import nz.co.cjc.base.framework.logging.providers.contracts.LoggingProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p/>
 * Dagger mapping for the logging provider.
 */
@Module
public class DaggerModuleLogging {
    @Provides
    @Singleton
    @NonNull
    public LoggingProvider provideLoggingProvider() {
        return new DefaultLoggingProvider();
    }
}
