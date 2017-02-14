package com.odoo.work;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
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

public class SelectMembers extends OdooActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, TextWatcher {

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
        listView.setOnItemClickListener(this);
        memberList = new ArrayList<>();

        editAddMembers = (EditText) findViewById(R.id.editAddTeamMember);
        editAddMembers.addTextChangedListener(this);

        findViewById(R.id.image_search).setOnClickListener(this);

        try {
            odoo = Odoo.createWithUser(this, OUser.current(this));
        } catch (OdooVersionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        searchMembers();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        String memberName = arrayAdapter.getItem(position);
        Intent intent = new Intent();
        intent.putExtra("member_name", memberName);
        setResult(1, intent);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                searchMembers();
            }
        }, 3000);
    }

    private void searchMembers() {
        if (!editAddMembers.getText().toString().trim().isEmpty()) {
            OdooFields fields = new OdooFields("name");
            ODomain domain = new ODomain();
            domain.add("share", "=", false);
            domain.add("name", "like", editAddMembers.getText().toString());
            odoo.searchRead("res.users", fields, domain, 0, 0, null, new OdooResponse() {
                @Override
                public void onResponse(OdooResult response) {
                    if (response.getSize() > 0) {
                        textNoRecord.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);

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
                    } else {
                        textNoRecord.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                        //TODO::
                    }
                }
            });
        } else {
            memberList.clear();
            listView.setAdapter(arrayAdapter);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
