package com.odoo.work;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class TeamAddMembers extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_add_members);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        findViewById(R.id.btn_add_member).setOnClickListener(this);
        setSupportActionBar(toolbar);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Vedant");
        arrayList.add("pratik");
        ArrayAdapter<String>arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        ListView l1 = (ListView) findViewById(R.id.lst_team_members);
        l1.setAdapter(arrayAdapter);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_member:
                startActivity(new Intent(TeamAddMembers.this, Add_by_email.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.actionmenu,menu);
        menu.findItem(R.id.action_save).setVisible(false);
        return true;
    }
}
