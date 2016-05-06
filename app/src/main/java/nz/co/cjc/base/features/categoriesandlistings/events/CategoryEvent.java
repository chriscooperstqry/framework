package nz.co.cjc.base.features.categoriesandlistings.events;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusEvent;

/**
 * Created by Chris Cooper on 5/05/16.
 * <p/>
 * Event notifying of a category event
 */
public class CategoryEvent extends EventBusEvent {

    private final Bundle mBundle;
    private final EventType mEventType;

    public enum EventType {
        CategorySelected,
        CategoryLayoutReady
    }

    /**
     * Create a new event bus event with the given
     * 'owner id'.
     *
     * @param ownerId as a reference to who create this.
     */
    public CategoryEvent(@Nullable String ownerId, @NonNull EventType eventType, @Nullable Bundle bundle) {
        super(ownerId);
        mBundle = bundle;
        mEventType = eventType;
    }

    /**
     * Get the events bundle
     *
     * @return The bundle
     */
    public Bundle getBundle() {
        return mBundle;
    }

    /**
     * Get the event type
     *
     * @return The event type
     */
    public EventType getEventType() {
        return mEventType;
    }
}
