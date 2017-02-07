package com.odoo.work;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.rpc.helper.OdooFields;
import com.odoo.core.rpc.helper.utils.gson.OdooRecord;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.rpc.listeners.OdooResponse;
import com.odoo.core.support.OUser;
import com.odoo.work.core.support.OdooActivity;

import java.util.ArrayList;
import java.util.List;

public class SelectMembers extends OdooActivity implements View.OnClickListener {

    private Odoo odoo;
    private EditText editAddMembers;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> memberList;
    private ListView listView;
    private TextView textNoRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_members);

        textNoRecord = (TextView) findViewById(R.id.textEmpty);
        listView = (ListView) findViewById(R.id.list_team_members);
        memberList = new ArrayList<>();

        editAddMembers = (EditText) findViewById(R.id.editAddTeamMember);
        findViewById(R.id.image_search).setOnClickListener(this);

        try {
            odoo = Odoo.createWithUser(this, OUser.current(this));
        } catch (OdooVersionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        OdooFields fields = new OdooFields("name");
        ODomain domain = new ODomain();
        domain.add("share", "=", false);
        domain.add("name", "like", editAddMembers.getText().toString());
        odoo.searchRead("res.users", fields, domain, 0, 0, null, new OdooResponse() {
            @Override
            public void onResponse(OdooResult response) {
                List<OdooRecord> records = response.getRecords();
                memberList.clear();
                for (OdooRecord record : records) {
                    if (record != null) {
                        memberList.add(record.getString("name"));
                        arrayAdapter = new ArrayAdapter<>(SelectMembers.this,
                                android.R.layout.simple_list_item_1, android.R.id.text1, memberList);
                        listView.setAdapter(arrayAdapter);
                    }
                }
            }
        });
    }
}
