package nz.co.cjc.base.framework.dagger.components;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import nz.co.cjc.base.features.categoriesandlistings.logic.CategoriesAndListingsViewLogic;
import nz.co.cjc.base.features.core.logic.CoreViewLogic;
import nz.co.cjc.base.framework.application.MainApp;
import nz.co.cjc.base.framework.buildconfig.dagger.DaggerModuleBuildConfig;
import nz.co.cjc.base.framework.dagger.modules.DaggerModuleLibrary;
import nz.co.cjc.base.framework.eventbus.dagger.DaggerModuleEventBus;
import nz.co.cjc.base.framework.logging.dagger.DaggerModuleLogging;
import nz.co.cjc.base.framework.logging.providers.contracts.LoggingProvider;
import nz.co.cjc.base.framework.network.dagger.DaggerModuleNetwork;
import nz.co.cjc.base.framework.network.providers.contracts.NetworkRequestProvider;
import nz.co.cjc.base.framework.strings.dagger.DaggerModuleStrings;
import nz.co.cjc.base.framework.threading.dagger.DaggerModuleThreadUtils;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p/>
 * Registration of all dagger classes
 */

@Singleton
@Component(modules = {
        DaggerModuleLibrary.class,
        DaggerModuleStrings.class,
        DaggerModuleEventBus.class,
        DaggerModuleThreadUtils.class,
        DaggerModuleLogging.class,
        DaggerModuleNetwork.class,
        DaggerModuleBuildConfig.class
})
public interface LibraryComponent {
    //region Providers

    /**
     * Resolve the application context.
     *
     * @return application context.
     */
    Context getApplicationContext();

    /**
     * Resolve the network request provider.
     *
     * @return resolved network request provider.
     */
    NetworkRequestProvider getNetworkRequestProvider();

    /**
     * Resolve the logging provider
     *
     * @return resolved logging provider
     */
    LoggingProvider getLoggingProvider();
    //end region

    //region injection

    /**
     * Injection for the main app.
     *
     * @param mainApp to inject.
     */
    void inject(MainApp mainApp);

    /**
     * Create an instance of the core view logic.
     *
     * @return core view logic.
     */
    CoreViewLogic createCoreViewLogic();

    /**
     * Create an instance of the categories and listings view logic
     *
     * @return The view logic
     */
    CategoriesAndListingsViewLogic createCategoriesAndListingsViewLogic();
    //end region
}
