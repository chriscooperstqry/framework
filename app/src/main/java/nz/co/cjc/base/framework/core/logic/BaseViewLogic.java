package nz.co.cjc.base.framework.core.logic;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import nz.co.cjc.base.framework.core.weakwrapper.WeakWrapper;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusEvent;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 *  **Architecture code**
 *
 * Base class for view logic classes
 */
public abstract class BaseViewLogic<T> implements Disconnectable {

    /**
     * This field can be accessed by the child class and
     * have commands sent via it.
     */
    protected T mDelegate;

    private final Class<T> mClazz;

    private String mInstanceId;

    /**
     * Initialise the instance with the given class type
     * that represents the logic delegate.
     *
     * @param clazz type of the delegate contract.
     */
    public BaseViewLogic(@NonNull Class<T> clazz, @NonNull StringsProvider stringsProvider) {
        mClazz = clazz;
        resetDelegate();
        mInstanceId = stringsProvider.generateUniqueId();
    }

    /**
     * Connect the logic delegate to the given delegate
     * instance, via the weak wrapper so it won't hold
     * a strong reference to it and so it also won't
     * through NPE when calling methods on the delegate
     * that is not connected to anything.
     *
     * @param delegate to assign as the logic delegate.
     */
    public void setDelegate(@Nullable T delegate) {
        mDelegate = WeakWrapper.wrap(delegate, mClazz);
    }

    @Override
    public void disconnect() {
        resetDelegate();
    }

    /**
     * Retrieve the unique identifier for this instance
     * which can be used as a key to associate with
     * other data structures to indicate they 'belong'
     * to this instance.
     *
     * @return unique instance id of this object.
     */
    @NonNull
    protected String getInstanceId() {
        return mInstanceId;
    }

    /**
     * Determine whether the given event bus event is 'owned'
     * by us - which basically is determined by checking if
     * the 'owner id' inside the event matches our own instance
     * id. This would indicate that we had originally created
     * the object that is now emitting the event and therefore
     * it 'belongs' to us.
     *
     * @param event to check.
     *
     * @return true if the given event has the same 'owner id'
     * as our 'instance id'.
     */
    protected boolean ownsEvent(@NonNull EventBusEvent event) {
        return getInstanceId().equals(event.getOwnerId());
    }

    /**
     * Resetting the delegate basically just wraps it
     * with a weak wrapper with null as the target.
     */
    private void resetDelegate() {
        mDelegate = WeakWrapper.wrapEmpty(mClazz);
    }
}
