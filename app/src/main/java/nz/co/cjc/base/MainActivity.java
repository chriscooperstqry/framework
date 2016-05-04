package nz.co.cjc.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import nz.co.cjc.base.framework.application.MainApp;
import nz.co.cjc.base.framework.network.models.NetworkRequestProperties;
import nz.co.cjc.base.framework.network.providers.contracts.NetworkRequestDelegate;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        NetworkRequestProperties properties = NetworkRequestProperties.create().url("https://api.tmsandbox.co.nz/v1/Listings/1234567890.json").respondOnMainThread(false);
        MainApp.getDagger().getNetworkRequestProvider().startRequest(properties, new NetworkRequestDelegate() {
            @Override
            public void onRequestComplete(int statusCode, @NonNull String response) {
              MainApp.getDagger().getLoggingProvider().d("response " +response);
            }

            @Override
            public void onRequestFailed(int statusCode, @NonNull String response) {
                MainApp.getDagger().getLoggingProvider().d("failed " +response);

            }

            @Override
            public void onConnectionFailed() {
                MainApp.getDagger().getLoggingProvider().d("connect failed ");

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
