package com.odoo.work.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

public class OdooSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = OdooSyncAdapter.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.work.sync";

    public OdooSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {

    }
}
