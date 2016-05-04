package nz.co.cjc.base.framework.application;

import android.app.Application;
import android.support.annotation.NonNull;

import nz.co.cjc.base.framework.dagger.components.DaggerLibraryComponent;
import nz.co.cjc.base.framework.dagger.components.LibraryComponent;
import nz.co.cjc.base.framework.dagger.modules.DaggerModuleLibrary;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p/>
 * Main application class to init framework
 */
public class MainApp extends Application {

    private static MainApp sMainAppSelf;
    private LibraryComponent mLibraryComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        sMainAppSelf = this;

        mLibraryComponent = buildDaggerLibraryComponent();
        mLibraryComponent.inject(this);

    }

    /**
     * Build the Dagger dependency component. This
     * method can be overridden in child classes
     * of the MainApp class, to provide different
     * implementations of elements in the Dagger
     * mapping.
     * @return constructed Dagger component.
     */
    @NonNull
    protected LibraryComponent buildDaggerLibraryComponent() {
        return DaggerLibraryComponent
                .builder()
                .daggerModuleLibrary(new DaggerModuleLibrary(this))
                .build();
    }


    @NonNull
    public static LibraryComponent getDagger() {
        return sMainAppSelf.mLibraryComponent;
    }
}
