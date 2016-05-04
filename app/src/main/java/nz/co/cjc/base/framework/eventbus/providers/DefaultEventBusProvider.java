package nz.co.cjc.base.framework.eventbus.providers;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusEvent;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusProvider;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusSubscriber;
import nz.co.cjc.base.framework.threading.providers.contracts.ThreadUtilsProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p/>
 * Default implementation of the event bus system,
 * <p/>
 * The operations in the event bus implementation
 * always perform their actions on the main thread
 */
public final class DefaultEventBusProvider implements EventBusProvider {
    private final ThreadUtilsProvider mThreadUtilsProvider;
    private final EventBus mEventBus;

    public DefaultEventBusProvider(@NonNull ThreadUtilsProvider threadUtilsProvider) {
        mThreadUtilsProvider = threadUtilsProvider;
        mEventBus = EventBus.builder().logNoSubscriberMessages(false).sendNoSubscriberEvent(false).build();
    }

    @Override
    public void subscribe(@NonNull final EventBusSubscriber subscriber) {
        mThreadUtilsProvider.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mEventBus.register(subscriber);
            }
        });
    }

    @Override
    public void unsubscribe(@NonNull final EventBusSubscriber subscriber) {
        mThreadUtilsProvider.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mEventBus.unregister(subscriber);
            }
        });
    }

    @Override
    public void postEvent(@NonNull final EventBusEvent event) {
        mThreadUtilsProvider.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mEventBus.post(event);
            }
        });
    }
}