package com.odoo.work;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public static final String KEY_MEMBER_NAME = "member_name";
    public static final String KEY_MEMBER_ID = "member_id";
    private Odoo odoo;
    private EditText editAddMembers;
    private ArrayAdapter<OdooRecord> arrayAdapter;
    private ArrayList<OdooRecord> memberList;
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
        OdooRecord record = arrayAdapter.getItem(position);
        assert record != null;
        Intent intent = new Intent();
        intent.putExtra(KEY_MEMBER_ID, record.getInt("id"));
        intent.putExtra(KEY_MEMBER_NAME, record.getString("name"));
        setResult(RESULT_OK, intent);
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
            domain.add("id", "!=", OUser.current(this).getPartnerId());
            domain.add("name", "ilike", editAddMembers.getText().toString());
            odoo.searchRead("res.partner", fields, domain, 0, 0, null, new OdooResponse() {
                @Override
                public void onResponse(OdooResult response) {
                    if (response.getSize() > 0) {
                        textNoRecord.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);

                        List<OdooRecord> records = response.getRecords();
                        memberList.clear();
                        for (OdooRecord record : records) {
                            if (record != null) {
                                memberList.add(record);
                                arrayAdapter = new ArrayAdapter<OdooRecord>(SelectMembers.this,
                                        android.R.layout.simple_list_item_1, android.R.id.text1, memberList) {
                                    @NonNull
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        if (convertView == null)
                                            convertView = LayoutInflater.from(SelectMembers.this).inflate(android.R.layout.simple_list_item_1, parent, false);
                                        TextView view = (TextView) convertView.findViewById(android.R.id.text1);
                                        view.setText(getItem(position).getString("name"));
                                        return convertView;
                                    }
                                };
                                listView.setAdapter(arrayAdapter);
                            }
                        }
                    } else {
                        textNoRecord.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
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
