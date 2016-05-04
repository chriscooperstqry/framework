package nz.co.cjc.base.framework.strings.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nz.co.cjc.base.framework.strings.providers.DefaultStringsProvider;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;


/**
 * Created by Chris Cooper on 4/05/16.
 *
 * Dagger dependency mapping for the strings provider.
 */
@Module
public class DaggerModuleStrings {
    @Provides
    @Singleton
    @NonNull
    public StringsProvider provideStringsProvider(@NonNull Context applicationContext) {
        return new DefaultStringsProvider(applicationContext);
    }
}
