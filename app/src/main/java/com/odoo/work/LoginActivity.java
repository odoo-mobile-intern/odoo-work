package com.odoo.work;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;

import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.rpc.listeners.IOdooLoginCallback;
import com.odoo.core.rpc.listeners.OdooConnectionListener;
import com.odoo.core.rpc.listeners.OdooError;
import com.odoo.core.support.OUser;
import com.odoo.work.core.support.OdooActivity;


public class LoginActivity extends OdooActivity implements View.OnClickListener,
        IOdooLoginCallback {
    private EditText editEmail, editPassword;
    private Odoo odoo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_login_activity);
        // click listeners
        editEmail = (EditText) findViewById(R.id.edit_email);
        editPassword = (EditText) findViewById(R.id.edit_password);

        findViewById(R.id.btnSignup).setOnClickListener(this);
        findViewById(R.id.btnSignin).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignup:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.btnSignin:
                if (isValid()) {
                    try {
                        odoo = Odoo.createInstance(this, "http://192.168.199.101:8069");
                        odoo.setOnConnect(new OdooConnectionListener() {
                            @Override
                            public void onConnect(Odoo odoo) {
                                odoo.authenticate(editEmail.toString(), editPassword.toString(), "odoo-work", LoginActivity.this);
                            }
                        });
                    } catch (OdooVersionException e) {
                        e.printStackTrace();
                    }
                    break;

                }
        }
    }

    private boolean isValid() {

        editEmail.setError(null);
        if (editEmail.getText().toString().trim().isEmpty()) {
            editEmail.setError(getString(R.string.error_enter_email));
            editEmail.setFocusable(true);
            return false;
        }

        editPassword.setError(null);
        if (editPassword.getText().toString().trim().isEmpty()) {
            editPassword.setError(getString(R.string.error_enter_password));
            editPassword.setFocusable(true);
            return false;
        }

        return true;
    }

    @Override
    public void onLoginSuccess(Odoo odoo, OUser user) {
        //TODO: Create account.
    }

    @Override
    public void onLoginFail(OdooError error) {
        Snackbar.make(getContentView(), R.string.error_login_failed, BaseTransientBottomBar.LENGTH_LONG)
                .show();
    }
}
