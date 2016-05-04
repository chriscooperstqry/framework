package nz.co.cjc.base.framework.eventbus.dagger;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nz.co.cjc.base.framework.eventbus.providers.DefaultEventBusProvider;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusProvider;
import nz.co.cjc.base.framework.threading.providers.contracts.ThreadUtilsProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 *
 * Dagger module for the event bus system.
 */
@Module
public class DaggerModuleEventBus {
    @Provides
    @Singleton
    @NonNull
    public EventBusProvider provideEventBusProvider(@NonNull ThreadUtilsProvider threadUtilsProvider) {
        return new DefaultEventBusProvider(threadUtilsProvider);
    }
}
