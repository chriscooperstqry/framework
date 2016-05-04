package nz.co.cjc.base.framework.buildconfig.dagger;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nz.co.cjc.base.framework.buildconfig.providers.DefaultBuildConfigProvider;
import nz.co.cjc.base.framework.buildconfig.providers.contracts.BuildConfigProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 *
 * Dagger mapping for the build config provider.
 */
@Module
public class DaggerModuleBuildConfig {
    @Provides
    @Singleton
    @NonNull
    public BuildConfigProvider provideBuildConfigProvider() {
        return new DefaultBuildConfigProvider();
    }
}