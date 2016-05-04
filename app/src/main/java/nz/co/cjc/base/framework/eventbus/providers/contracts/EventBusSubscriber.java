package nz.co.cjc.base.framework.eventbus.providers.contracts;

/**
 * Created by Chris Cooper on 4/05/16.
 *
 * Basic contract identifying an object as a
 * qualified event bus subscriber, forcing them
 * to implement the two required behaviours of an
 * event bus subscriber (subscribe/unsubscribe).
 *
 */
public interface EventBusSubscriber {
    /**
     * Implementation of this method should subscribe
     * the implementor to the framework event bus.
     */
    void subscribeToEventBus();

    /**
     * Implementation of this method should un subscribe
     * the implementor from the framework event bus.
     */
    void unsubscribeFromEventBus();
}
