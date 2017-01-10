package com.odoo.core.rpc.listeners;

import com.odoo.core.support.OUser;

public abstract class OdooSignUpCallback {

    public abstract void onSignUpSuccess(OUser user);

    public void onSignUpFail(OdooError error) {
        // override when needed
    }
}
