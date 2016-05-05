package nz.co.cjc.base.features.core.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import nz.co.cjc.base.features.core.logic.CoreViewLogic;
import nz.co.cjc.base.framework.application.MainApp;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p/>
 * Base activity class that all others will extend for common use
 */
public class CoreActivity extends AppCompatActivity {

    private CoreViewLogic mViewLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Here we use dagger to create an instance of the view logic rather than inject
        //as injecting from parent classes causes issues with dagger being reliant on strongly typed classes
        mViewLogic = MainApp.getDagger().createCoreViewLogic();
        mViewLogic.initViewLogic(mViewLogicDelegate);

//        NetworkRequestProperties properties = NetworkRequestProperties.create().url("https://api.tmsandbox.co.nz/v1/Listings/3.json").respondOnMainThread(false);
//        MainApp.getDagger().getNetworkRequestProvider().startRequest(properties, new NetworkRequestDelegate() {
//            @Override
//            public void onRequestComplete(int statusCode, @NonNull String response) {
//                MainApp.getDagger().getLoggingProvider().d("response " + response);
//            }
//
//            @Override
//            public void onRequestFailed(int statusCode, @NonNull String response) {
//                MainApp.getDagger().getLoggingProvider().d("failed " + response);
//
//            }
//
//            @Override
//            public void onConnectionFailed() {
//                MainApp.getDagger().getLoggingProvider().d("connect failed ");
//
//            }
//        });
    }

    private CoreViewLogic.ViewLogicDelegate mViewLogicDelegate = new CoreViewLogic.ViewLogicDelegate() {
        //Empty at the moment
    };


}
