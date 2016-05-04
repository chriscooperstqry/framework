package nz.co.cjc.base.framework.threading.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nz.co.cjc.base.framework.threading.providers.DefaultThreadUtilsProvider;
import nz.co.cjc.base.framework.threading.providers.contracts.ThreadUtilsProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 * Dagger module for thread utils
 */
@Module
public class DaggerModuleThreadUtils {

    @Provides
    @Singleton
    @NonNull
    public ThreadUtilsProvider provideThreadUtilsProvider(@NonNull Context applicationContext) {
        return new DefaultThreadUtilsProvider(applicationContext);
    }
}
