package nz.co.cjc.base.features.core.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import nz.co.cjc.base.R;
import nz.co.cjc.base.features.core.logic.CoreViewLogic;
import nz.co.cjc.base.framework.application.MainApp;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p>
 * Base activity class that all others will extend for common use
 */
public class CoreActivity extends AppCompatActivity {

    private CoreViewLogic mViewLogic;
    public Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Here we use dagger to create an instance of the view logic rather than inject
        //as injecting from parent classes causes issues with dagger being reliant on strongly typed classes
        mViewLogic = MainApp.getDagger().createCoreViewLogic();
        mViewLogic.initViewLogic(mViewLogicDelegate);


    }

    public void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private CoreViewLogic.ViewLogicDelegate mViewLogicDelegate = new CoreViewLogic.ViewLogicDelegate() {
        //Empty at the moment
    };


}
