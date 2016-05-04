package nz.co.cjc.base.framework.network.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nz.co.cjc.base.framework.buildconfig.providers.contracts.BuildConfigProvider;
import nz.co.cjc.base.framework.logging.providers.contracts.LoggingProvider;
import nz.co.cjc.base.framework.network.providers.DefaultNetworkRequestProvider;
import nz.co.cjc.base.framework.network.providers.contracts.NetworkRequestProvider;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;
import nz.co.cjc.base.framework.threading.providers.contracts.ThreadUtilsProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 *
 * This is the main network request provider
 * Dagger mapping module to construct a network
 * request provider when needed.
 */
@Module
public class DaggerModuleNetwork {
    @Provides
    @Singleton
    @NonNull
    public NetworkRequestProvider provideNetworkRequestProvider(@NonNull Context applicationContext,
                                                                @NonNull StringsProvider stringsProvider,
                                                                @NonNull BuildConfigProvider buildConfigProvider,
                                                                @NonNull ThreadUtilsProvider threadUtilsProvider,
                                                                @NonNull LoggingProvider loggingProvider) {

        return new DefaultNetworkRequestProvider(
                applicationContext,
                stringsProvider,
                buildConfigProvider,
                threadUtilsProvider,
                loggingProvider
                );
    }
}