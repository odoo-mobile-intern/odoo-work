package com.odoo.work;

import android.content.Intent;
import android.os.Bundle;

import com.odoo.work.core.support.OdooActivity;

public class HomeActivity extends OdooActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
        startActivity(new Intent(this, TeamName.class));
    }
}
