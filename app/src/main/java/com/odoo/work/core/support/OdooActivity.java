package com.odoo.work.core.support;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class OdooActivity extends AppCompatActivity {

    public View getContentView() {
        return findViewById(android.R.id.content);
    }

    public Bundle getArgs() {
        return getIntent().getExtras();
    }
}
