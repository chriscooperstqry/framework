package nz.co.cjc.base.framework.dagger.components;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import nz.co.cjc.base.framework.application.MainApp;
import nz.co.cjc.base.framework.dagger.modules.DaggerModuleLibrary;
import nz.co.cjc.base.framework.eventbus.dagger.DaggerModuleEventBus;
import nz.co.cjc.base.framework.logging.dagger.DaggerModuleLogging;
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
        DaggerModuleLogging.class
})
public interface LibraryComponent {
    //region Providers

    /**
     * Resolve the application context.
     *
     * @return application context.
     */
    Context getApplicationContext();
    //end region

    //region injection

    /**
     * Injection for the main app.
     *
     * @param mainApp to inject.
     */
    void inject(MainApp mainApp);
    //end region
}
