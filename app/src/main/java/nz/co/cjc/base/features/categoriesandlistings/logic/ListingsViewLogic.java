package nz.co.cjc.base.features.categoriesandlistings.logic;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import nz.co.cjc.base.framework.core.logic.BaseViewLogic;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;

/**
 * Created by Chris Cooper on 5/05/16.
 *
 * View logic for the listings fragment
 */
public class ListingsViewLogic extends BaseViewLogic<ListingsViewLogic.ViewLogicDelegate>{

    @Inject
    public ListingsViewLogic(@NonNull StringsProvider stringsProvider) {
        super(ViewLogicDelegate.class, stringsProvider);
    }

    public void initViewLogic(@Nullable ViewLogicDelegate delegate) {
        setDelegate(delegate);
    }

    public interface ViewLogicDelegate{

    }
}
