package nz.co.cjc.base.framework.network.providers.contracts;

import android.support.annotation.NonNull;

import nz.co.cjc.base.framework.network.models.NetworkRequestProperties;
import okhttp3.Call;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p>
 * Network request contract to provide access to network
 * resources.
 */
public interface NetworkRequestProvider {
    /**
     * Get the base url for the current environment that is being pointed at
     * by the application (eg dev / prod)
     *
     * @return the base url that can be used for forming urls.
     */
    @NonNull
    String getBaseUrl();

    /**
     * Get the file format we wish the api response to be returned as
     *
     * @return the file format, eg json, xml
     */
    @NonNull
    String getFileFormat();

    /**
     * Determine whether the host environment currently has networking
     * connectivity.
     *
     * @return true if there is a network connection detected.
     */
    boolean hasNetworkConnectivity();

    /**
     * Initiate a new request with the given network request properties.
     * <p>
     * The result of the request will be returned via the given callback.
     *
     * @param networkRequestProperties containing the configuration for
     *                                 the given request.
     * @param networkRequestDelegate   the callback to respond with the result
     *                                 of the network request.
     */
    Call startRequest(@NonNull NetworkRequestProperties networkRequestProperties, @NonNull NetworkRequestDelegate networkRequestDelegate);

    /**
     * Cancel a network request with the given tag if it is currently in flight.
     *
     * @param call to cancel.
     */
    void cancelRequest(@NonNull Call call);

}
