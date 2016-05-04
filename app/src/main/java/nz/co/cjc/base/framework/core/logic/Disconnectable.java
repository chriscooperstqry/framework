package nz.co.cjc.base.framework.core.logic;

/**
 * Created by Chris Cooper on 4/05/16.
 *
 * Contract for disconnecting in base view logic
 */
public interface Disconnectable {

    /**
     * Disconnect from the implementing object, the
     * meaning of 'disconnect' being up to the implementation.
     */
    void disconnect();
}
