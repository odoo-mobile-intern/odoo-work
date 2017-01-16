package com.odoo.work.core.support.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.widget.Toast;

import com.odoo.core.support.OUser;
import com.odoo.work.R;

public class DeviceAccountUtils {

    private Context context;
    private AccountManager accountManager;

    public static DeviceAccountUtils get(Context context) {
        return new DeviceAccountUtils(context);
    }

    private DeviceAccountUtils(Context context) {
        this.context = context;
        accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
    }


    public boolean createAccount(OUser user) {
        String accountName = user.getAndroidName();
        if (!hasAccount(accountName)) {
            Account account = new Account(accountName, getAuthType());
            return accountManager.addAccountExplicitly(account, "N/A", user.getAsBundle());
        } else {
            Toast.makeText(context, R.string.toast_account_already_created,
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean hasAccount(String name) {
        for (Account account : getAccounts()) {
            if (account.name.equals(name))
                return true;
        }
        return false;
    }

    public String getAuthType() {
        return context.getString(R.string.auth_type);
    }

    @SuppressWarnings("MissingPermission")
    public Account[] getAccounts() {
        return accountManager.getAccountsByType(getAuthType());
    }

    public boolean hasAnyAccount() {
        return getAccounts().length > 0;
    }
}
