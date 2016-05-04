package nz.co.cjc.base.framework.eventbus.providers.contracts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Chris Cooper on 4/05/16.
 *
 * Base event bus event contract which should be
 * subclassed by all event bus events.
 */
public abstract class EventBusEvent {
    private String mOwnerId;

    /**
     * Create a new event bus event with the given
     * 'owner id'.
     *
     * @param ownerId as a reference to who create this.
     */
    public EventBusEvent(@Nullable String ownerId) {
        mOwnerId = ownerId == null ? "" : ownerId;
    }

    /**
     * Retrieve the 'owner id' which may be used
     * to assist in identifying who created this
     * event (if needed).
     *
     * @return the owner id who created this event.
     */
    @NonNull
    public String getOwnerId() {
        return mOwnerId;
    }
}
