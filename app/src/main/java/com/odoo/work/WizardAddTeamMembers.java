package com.odoo.work;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class WizardAddTeamMembers extends AppCompatActivity implements View.OnClickListener {

    private EditText addMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_add_members);

        findViewById(R.id.editAddMember).setOnClickListener(this);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Vedant");
        arrayList.add("pratik");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        ListView l1 = (ListView) findViewById(R.id.lst_team_members);
        l1.setAdapter(arrayAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_skip:
                break;
            case R.id.editAddMember:
                startActivity(new Intent(this, SelectMembers.class));
                break;
        }
    }
}
