package nz.co.cjc.base.framework.network.providers.contracts;

import android.support.annotation.NonNull;

/**
 * Created by Chris Cooper on 4/05/16.
 *
 * Request callback delegate to return successful or failed
 * network request attempts. Is used for each request attempt.
 *
 * IMPORTANT NOTE: Any callback delegate methods are NOT executed
 * on the main thread. It is the caller's responsibility to make
 * any code after the callback invocation run on the main thread if
 * required.
 *
 * This design decision was deliberate - because it will allow the
 * receiver of the callback to continue to run in the worker thread
 * to complete any parsing of the response data etc, and therefore
 * get some async benefit for free.
 */
public interface NetworkRequestDelegate {
    /**
     * If the request reports to have completed successfully, this method
     * will be called with the resulting status code and raw string response.
     *
     * @param statusCode of the network operation.
     * @param response the raw string response text received.
     */
    void onRequestComplete(int statusCode, @NonNull String response);

    /**
     * If the request reports to have failed, this method will be called with
     * an error code, which will typically be an error code we know about.
     *
     * @param statusCode the error code describing the type of failure.
     * @param response the server response content.
     */
    void onRequestFailed(int statusCode, @NonNull String response);

    /**
     * If the request could not reach the server or desired endpoint this
     * method will be called.
     */
    void onConnectionFailed();
}