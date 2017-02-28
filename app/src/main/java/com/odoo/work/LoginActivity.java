package com.odoo.work;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SyncResult;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.odoo.config.AppConfig;
import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.rpc.helper.OArguments;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.rpc.listeners.IOdooLoginCallback;
import com.odoo.core.rpc.listeners.OdooConnectionListener;
import com.odoo.core.rpc.listeners.OdooError;
import com.odoo.core.support.OUser;
import com.odoo.work.addons.project.models.ProjectTeams;
import com.odoo.work.core.support.OdooActivity;
import com.odoo.work.core.support.account.DeviceAccountUtils;
import com.odoo.work.orm.sync.SyncAdapter;

import java.io.IOException;
import java.util.HashMap;


public class LoginActivity extends OdooActivity implements View.OnClickListener,
        IOdooLoginCallback {
    public static final int REQUEST_NEW_SIGNUP = 123;
    private EditText editEmail, editPassword;
    private ProgressDialog progressDialog;
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
                startActivityForResult(new Intent(this, SignUpActivity.class), REQUEST_NEW_SIGNUP);
                break;
            case R.id.btnSignin:
                if (isValid()) {
                    try {
                        progressDialog = new ProgressDialog(this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage(getString(R.string.msg_authenticating));
                        progressDialog.show();
                        odoo = Odoo.createInstance(this, AppConfig.HOST_URL);
                        odoo.setOnConnect(new OdooConnectionListener() {
                            @Override
                            public void onConnect(Odoo odoo) {
                                odoo.authenticate(editEmail.getText().toString().trim(), editPassword.getText().toString().trim(),
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
        this.odoo = odoo;
        if (DeviceAccountUtils.get(this).createAccount(user)) {
            getUserData(user);
        }
    }

    private void getUserData(final OUser user) {
        progressDialog.setMessage(getString(R.string.msg_setting_your_account));
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                registerForFCM(user);

                ProjectTeams teams = new ProjectTeams(LoginActivity.this);
                SyncAdapter adapter = teams.getSyncAdapter();
                SyncResult result = adapter.syncModelData();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();
                startSplashScreen();
            }
        }.execute();
    }

    private void registerForFCM(OUser user) {
        if (user.getFCMId() != null && !user.getFCMId().equals("false")) {
            try {
                String token = FirebaseInstanceId.getInstance().getToken(user.getFCMId(),
                        FirebaseMessaging.INSTANCE_ID_SCOPE);
                OArguments args = new OArguments();
                args.add(token);
                args.add(Build.DEVICE + " " + Build.BRAND);
                args.add("fcm");
                OdooResult result = odoo.callMethod("mail_push.device", "add_device", args, new HashMap<String, Object>());
                Log.e(">>>", result + "<<<");
            } catch (IOException e) {
                e.printStackTrace();
            }
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
