package com.odoo.work;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.rpc.helper.ORecordValues;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.rpc.listeners.OdooResponse;
import com.odoo.core.support.OUser;
import com.odoo.work.core.support.OdooActivity;

public class WizardNewTeam extends OdooActivity implements View.OnClickListener {

    private Odoo odoo;
    private EditText editTeamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_name);

        try {
            odoo = Odoo.createWithUser(this, OUser.current(this));
        } catch (OdooVersionException e) {
            e.printStackTrace();
        }

        editTeamName = (EditText) findViewById(R.id.team_name);
        findViewById(R.id.btn_team_create).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (!editTeamName.getText().toString().isEmpty()) {
            ORecordValues values = new ORecordValues();
            values.put("name", editTeamName.getText().toString());
            odoo.createRecord("project.teams", values, new OdooResponse() {
                @Override
                public void onResponse(OdooResult response) {
                    Double dbl = Double.valueOf(response.getString("result"));
                    startActivity(new Intent(WizardNewTeam.this, WizardAddTeamMembers.class).
                            putExtra("prj_id", dbl.intValue()));
                    finish();
                }
            });
        }
    }
}