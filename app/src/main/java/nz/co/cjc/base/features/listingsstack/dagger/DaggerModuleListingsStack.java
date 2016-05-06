package nz.co.cjc.base.features.listingsstack.dagger;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nz.co.cjc.base.features.listingsstack.providers.DefaultListingStackProvider;
import nz.co.cjc.base.features.listingsstack.providers.contract.ListingsStackProvider;

/**
 * Created by Chris Cooper on 6/05/16.
 * <p>
 * Dagger module for the listings stack
 */
@Module
public class DaggerModuleListingsStack {

    @Singleton
    @NonNull
    @Provides
    public ListingsStackProvider provideListingsStackProvider() {
        return new DefaultListingStackProvider();
    }
}
