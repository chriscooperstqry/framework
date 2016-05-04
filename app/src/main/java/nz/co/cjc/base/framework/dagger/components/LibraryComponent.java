package nz.co.cjc.base.framework.dagger.components;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import nz.co.cjc.base.framework.application.MainApp;
import nz.co.cjc.base.framework.dagger.modules.DaggerModuleLibrary;

/**
 * Created by Chris Cooper on 4/05/16.
 *
 * Registration of all dagger classes
 */

@Singleton
@Component(modules = {
        DaggerModuleLibrary.class,
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
     * Injection for the main app.
     *
     * @param mainApp to inject.
     */
    void inject(MainApp mainApp);
}
