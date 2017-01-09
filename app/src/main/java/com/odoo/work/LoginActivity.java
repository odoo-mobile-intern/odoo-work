package com.odoo.work;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.odoo.work.core.support.OdooActivity;

public class LoginActivity extends OdooActivity implements View.OnClickListener {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_login_activity);

        // click listeners
        findViewById(R.id.btnSignup).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignup:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }
}
