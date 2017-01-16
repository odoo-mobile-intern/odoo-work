package com.odoo.work;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.rpc.listeners.OdooSignUpCallback;
import com.odoo.core.support.OUser;
import com.odoo.work.core.support.OdooActivity;

public class SignUpActivity extends OdooActivity implements View.OnClickListener {

    private EditText editName, editEmail, editPassword;
    private Odoo odoo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_signup_activity);

        try {
            odoo = Odoo.createInstance(this, "http://192.168.199.101:8069");
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
                    odoo.signUp(editName.getText().toString(), editEmail.getText().toString(),
                            editPassword.getText().toString(), new OdooSignUpCallback() {
                                @Override
                                public void onSignUpSuccess(OUser user) {
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
