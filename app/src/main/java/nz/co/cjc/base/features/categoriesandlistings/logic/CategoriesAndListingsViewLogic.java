package nz.co.cjc.base.features.categoriesandlistings.logic;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import nz.co.cjc.base.framework.core.logic.BaseViewLogic;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;

/**
 * Created by Chris Cooper on 4/05/16.
 *
 * view logic for the categories and listings activity
 */
public class CategoriesAndListingsViewLogic extends BaseViewLogic<CategoriesAndListingsViewLogic.ViewLogicDelegate> {

    @Inject
    public CategoriesAndListingsViewLogic(@NonNull StringsProvider stringsProvider) {
        super(ViewLogicDelegate.class, stringsProvider);
    }

    public void initViewLogic(@Nullable ViewLogicDelegate delegate) {
        setDelegate(delegate);
    }

    public interface ViewLogicDelegate{

    }
}
