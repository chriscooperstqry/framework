package nz.co.cjc.base.features.categoriesandlistings.dagger;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nz.co.cjc.base.features.categoriesandlistings.providers.DefaultCategoriesAndListingsProvider;
import nz.co.cjc.base.features.categoriesandlistings.providers.contract.CategoriesAndListingsProvider;
import nz.co.cjc.base.framework.network.providers.contracts.NetworkRequestProvider;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;
import nz.co.cjc.base.framework.threading.providers.contracts.ThreadUtilsProvider;

/**
 * Created by Chris Cooper on 5/05/16.
 * Dagger module for categories and listings provider
 */
@Module
public class DaggerModuleCategoriesAndListings {

    @Singleton
    @Provides
    @NonNull
    public CategoriesAndListingsProvider provideCategoriesAndListingsProvider(@NonNull StringsProvider stringsProvider,
                                                                              @NonNull NetworkRequestProvider networkRequestProvider,
                                                                              @NonNull ThreadUtilsProvider threadUtilsProvider) {
        return new DefaultCategoriesAndListingsProvider(stringsProvider, networkRequestProvider, threadUtilsProvider);
    }
}
