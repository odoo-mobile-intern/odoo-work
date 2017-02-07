package com.odoo.work;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class TeamAddMembers extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_add_members);
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
        }
    }
}
