package nz.co.cjc.base.robolectric;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.multidex.ShadowMultiDex;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p/>
 * Test runner for running our unit tests
 */
@Config(shadows = ShadowMultiDex.class)
public class RobolectricApplicationTestRunner extends RobolectricGradleTestRunner {
    public RobolectricApplicationTestRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }
}