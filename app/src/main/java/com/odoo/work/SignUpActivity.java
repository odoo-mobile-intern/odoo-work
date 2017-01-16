package com.odoo.work;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;

import com.odoo.config.AppConfig;
import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.rpc.listeners.OdooError;
import com.odoo.core.rpc.listeners.OdooSignUpCallback;
import com.odoo.core.support.OUser;
import com.odoo.work.core.support.OdooActivity;
import com.odoo.work.core.support.account.DeviceAccountUtils;

public class SignUpActivity extends OdooActivity implements View.OnClickListener {

    private EditText editName, editEmail, editPassword;
    private Odoo odoo;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_signup_activity);
        setResult(RESULT_CANCELED);
        try {
            odoo = Odoo.createInstance(this, AppConfig.HOST_URL);
        } catch (OdooVersionException e) {
            e.printStackTrace();
        }

        editName = (EditText) findViewById(R.id.edit_name);
        editEmail = (EditText) findViewById(R.id.edit_email);
        editPassword = (EditText) findViewById(R.id.edit_password);

        // click listeners
        findViewById(R.id.btnSignup).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignup:
                if (isValid()) {
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage(getString(R.string.msg_signing_up));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    odoo.signUp(editName.getText().toString(), editEmail.getText().toString(),
                            editPassword.getText().toString(), new OdooSignUpCallback() {
                                @Override
                                public void onSignUpSuccess(OUser user) {
                                    progressDialog.dismiss();
                                    if (DeviceAccountUtils.get(SignUpActivity.this)
                                            .createAccount(user)) {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                }

                                @Override
                                public void onSignUpFail(OdooError error) {
                                    super.onSignUpFail(error);
                                    progressDialog.dismiss();
                                    int error_res = R.string.error_unable_to_signup;
                                    if (error.getMessage().contains("already exists")) {
                                        error_res = R.string.error_account_already_exists;
                                    }
                                    Snackbar.make(getContentView(),
                                            error_res, Snackbar.LENGTH_SHORT).show();
                                }
                            });
                }
                break;
        }
    }

    private boolean isValid() {
        editName.setError(null);
        if (editName.getText().toString().trim().isEmpty()) {
            editName.setError(getString(R.string.error_enter_name));
            editName.setFocusable(true);
            return false;
        }

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
}
