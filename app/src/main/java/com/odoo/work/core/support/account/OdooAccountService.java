package com.odoo.work.core.support.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class OdooAccountService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new OdooAccountAuthenticator(getApplicationContext()).getIBinder();
    }
}
