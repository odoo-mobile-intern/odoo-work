package com.odoo.work.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.rpc.helper.OdooFields;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.support.OUser;
import com.odoo.work.orm.OModel;

public class OdooSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = OdooSyncAdapter.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.work.sync";
    private Odoo odoo;
    private OUser mUser;
    private Context mContext;
    private OModel syncModel;
    private AccountManager accountManager;

    public OdooSyncAdapter(Context context, boolean autoInitialize, OModel model) {
        super(context, autoInitialize);
        mContext = context;
        this.syncModel = model;
        accountManager = (AccountManager) mContext.getSystemService(Context.ACCOUNT_SERVICE);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {

        mUser = getUser(account);
        try {
            odoo = Odoo.createWithUser(mContext, mUser);
            if (syncModel != null) {
                syncData(syncModel, null, syncResult);
            }
        } catch (OdooVersionException e) {
            e.printStackTrace();
        }
    }

    private void syncData(OModel syncModel, ODomain syncDomain, SyncResult syncResult) {

        OdooFields fields = new OdooFields();
        fields.addAll(syncModel.getServerColumns());

        ODomain domain = new ODomain();

        OdooResult result = odoo.searchRead(syncModel.getModelName(), fields, domain, 0, 0, null);
        if (result != null) {
            Log.e(">>>>>>", result + "");
        }
    }

    public OUser getUser(Account account) {
        OUser user = new OUser();
        user.setHost(accountManager.getUserData(account, "host"));
        user.setUsername(accountManager.getUserData(account, "username"));
        user.setDatabase(accountManager.getUserData(account, "database"));
        user.setSession_id(accountManager.getUserData(account, "session_id"));
        return user;
    }
}
