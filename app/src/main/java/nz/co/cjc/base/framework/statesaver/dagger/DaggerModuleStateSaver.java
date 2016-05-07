package nz.co.cjc.base.framework.statesaver.dagger;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nz.co.cjc.base.framework.statesaver.providers.DefaultStateSaverProvider;
import nz.co.cjc.base.framework.statesaver.providers.contract.StateSaverProvider;

/**
 * Created by Chris Cooper on 7/05/16.
 * <p>
 * Dagger module for the state saver provider
 */
@Module
public class DaggerModuleStateSaver {

    @Singleton
    @Provides
    @NonNull
    public StateSaverProvider provideStateSaverProvider() {
        return new DefaultStateSaverProvider();
    }
}
