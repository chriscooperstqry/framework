package nz.co.cjc.base.features.categoriesandlistings.events;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusEvent;

/**
 * Created by Chris Cooper on 6/05/16.
 * <p/>
 * Event notifying of a listings event
 */
public class ListingsEvent extends EventBusEvent {

    private final EventType mEventType;
    private final String mCategoryNumber;

    public enum EventType {
        UpdateListings
    }

    /**
     * Create a new event bus event with the given
     * 'owner id'.
     *
     * @param ownerId as a reference to who create this.
     */
    public ListingsEvent(@Nullable String ownerId, @NonNull EventType eventType, @NonNull String categoryNumber) {
        super(ownerId);
        mEventType = eventType;
        mCategoryNumber = categoryNumber;
    }

    public String getCategoryNumber() {
        return mCategoryNumber;
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
