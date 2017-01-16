package com.odoo.work;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;

import com.odoo.config.AppConfig;
import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.rpc.listeners.IOdooLoginCallback;
import com.odoo.core.rpc.listeners.OdooConnectionListener;
import com.odoo.core.rpc.listeners.OdooError;
import com.odoo.core.support.OUser;
import com.odoo.work.core.support.OdooActivity;
import com.odoo.work.core.support.account.DeviceAccountUtils;


public class LoginActivity extends OdooActivity implements View.OnClickListener,
        IOdooLoginCallback {
    public static final int REQUEST_NEW_SIGNUP = 123;
    private EditText editEmail, editPassword;
    private ProgressDialog progressDialog;

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
                startActivityForResult(new Intent(this, SignUpActivity.class), REQUEST_NEW_SIGNUP);
                break;
            case R.id.btnSignin:
                if (isValid()) {
                    try {
                        progressDialog = new ProgressDialog(this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage(getString(R.string.msg_authenticating));
                        progressDialog.show();
                        Odoo odoo = Odoo.createInstance(this, AppConfig.HOST_URL);
                        odoo.setOnConnect(new OdooConnectionListener() {
                            @Override
                            public void onConnect(Odoo odoo) {
                                odoo.authenticate(editEmail.getText().toString(), editPassword.getText().toString(),
                                        AppConfig.HOST_DB, LoginActivity.this);
                            }

                            @Override
                            public void onError(OdooError error) {
                                super.onError(error);
                                progressDialog.dismiss();
                                Snackbar.make(getContentView(), error.getMessage(), Snackbar.LENGTH_SHORT)
                                        .show();
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
        progressDialog.dismiss();
        if (DeviceAccountUtils.get(this).createAccount(user)) {
            startSplashScreen();
        }
    }

    @Override
    public void onLoginFail(OdooError error) {
        progressDialog.dismiss();
        Snackbar.make(getContentView(), R.string.error_login_failed, BaseTransientBottomBar.LENGTH_LONG)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_NEW_SIGNUP) {
            startSplashScreen();
        }
    }

    private void startSplashScreen() {
        startActivity(new Intent(this, SplashScreen.class));
        finish();
    }
}
