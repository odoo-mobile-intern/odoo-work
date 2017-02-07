package com.odoo.work;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.support.OUser;

public class TeamName extends AppCompatActivity implements View.OnClickListener {

    private Odoo odoo;
    private OUser mUser;
    private EditText editTeamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_name);
        mUser = OUser.current(this);

        try {
            odoo = Odoo.createWithUser(this, mUser);
        } catch (OdooVersionException e) {
            e.printStackTrace();
        }

        editTeamName = (EditText) findViewById(R.id.team_name);
        findViewById(R.id.btn_team_create).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (!editTeamName.getText().toString().isEmpty()) {
            /*ORecordValues values = new ORecordValues();
            values.put("name", editTeamName.getText().toString());
            odoo.createRecord("project.teams", values);*/
            startActivity(new Intent(TeamName.this, TeamAddMembers.class));
            finish();
            //TODO : Team name add to local db
        }
    }
}