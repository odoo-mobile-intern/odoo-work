package com.odoo.work.orm.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.odoo.work.R;
import com.odoo.work.orm.OModel;

public class OSyncUtils {


    private Context context;
    private OModel model;

    private OSyncUtils(Context context, OModel model) {
        this.context = context;
        this.model = model;
    }

    public static OSyncUtils get(Context context, OModel model) {
        return new OSyncUtils(context, model);
    }

    public void sync(Bundle data) {
        AccountManager manager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts = manager.getAccountsByType(context.getString(R.string.auth_type));
        if (accounts.length > 0) {
            Bundle settings = new Bundle();
            settings.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            settings.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            if (data != null)
                settings.putAll(data);
            ContentResolver.requestSync(accounts[0], model.getAuthority(), settings);
        }
    }


}
