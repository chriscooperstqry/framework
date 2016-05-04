package nz.co.cjc.base.framework.eventbus.providers.contracts;

import android.support.annotation.NonNull;

/**
 * Created by Marcel Braghetto on 15/07/15.
 *
 * Provider to control the application event bus
 * to allow for decoupled communication at an
 * application level.
 *
 * Note: A subscriber who registers MUST unregister, typically
 * in line with the Android lifecycle of the host component.
 *
 *
 */
public interface EventBusProvider {
    /**
     * A subscriber can register itself to the event
     * bus in order to listen for broadcast events.
     *
     * @param subscriber who wants to receive broadcast event bus events.
     */
    void subscribe(@NonNull EventBusSubscriber subscriber);

    /**
     * A subscriber can (and should) unregister itself from
     * the event bus to stop listening for broadcast events.
     *
     * @param subscriber to stop receiving broadcast event bus events.
     */
    void unsubscribe(@NonNull EventBusSubscriber subscriber);

    /**
     * Broadcast the given event bus event to any subscribers
     * who are listening for the event.
     *
     * @param event to broadcast.
     */
    void postEvent(@NonNull EventBusEvent event);
}