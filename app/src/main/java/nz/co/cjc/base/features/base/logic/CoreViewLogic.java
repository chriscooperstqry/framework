package nz.co.cjc.base.features.base.logic;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import nz.co.cjc.base.framework.core.logic.BaseViewLogic;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p/>
 * View logic for the core activity
 */
public class CoreViewLogic extends BaseViewLogic<CoreViewLogic.ViewLogicDelegate> {

    @Inject
    public CoreViewLogic(@NonNull StringsProvider stringsProvider) {
        super(ViewLogicDelegate.class, stringsProvider);
    }

    public void initViewLogic(@Nullable ViewLogicDelegate delegate) {
        setDelegate(delegate);
    }

    public interface ViewLogicDelegate {
    //Empty at the moment
    }

}
