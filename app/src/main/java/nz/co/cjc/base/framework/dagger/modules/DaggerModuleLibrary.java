package nz.co.cjc.base.framework.dagger.modules;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p/>
 * Core Dagger module to cover the framework level injection mapping.
 */
@Module
public final class DaggerModuleLibrary {

    private final Context mApplicationContext;
    private final Application mApplication;

    public DaggerModuleLibrary(@NonNull Application application) {
        mApplication = application;
        mApplicationContext = application.getApplicationContext();
    }

    //region Singleton providers
    @Provides
    @Singleton
    @NonNull
    public Context provideApplicationContext() {
        return mApplicationContext;
    }

    @Provides
    @Singleton
    @NonNull
    public Application provideApplication() {
        return mApplication;
    }

    //endregion
}
