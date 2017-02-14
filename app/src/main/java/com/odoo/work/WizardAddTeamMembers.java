package com.odoo.work;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.rpc.helper.OdooFields;
import com.odoo.core.rpc.helper.utils.gson.OdooRecord;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.rpc.listeners.OdooResponse;
import com.odoo.core.support.OUser;

import java.util.ArrayList;

public class WizardAddTeamMembers extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;
    private ListView memberListView;
    private ArrayList<Integer> memberIds;
    private Odoo odoo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_add_members);

        try {
            odoo = Odoo.createWithUser(this, OUser.current(this));
        } catch (OdooVersionException e) {
            e.printStackTrace();
        }


        findViewById(R.id.editAddMember).setOnClickListener(this);
        findViewById(R.id.btn_continue).setOnClickListener(this);
        findViewById(R.id.btn_skip).setOnClickListener(this);

        arrayList = new ArrayList<>();
        memberIds = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.member_list_item,
                R.id.textMemberName, arrayList);
        memberListView = (ListView) findViewById(R.id.lst_team_members);
        memberListView.setOnItemClickListener(this);

        memberListView.setAdapter(arrayAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_skip:
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
            case R.id.editAddMember:
                Intent intent = new Intent(this, SelectMembers.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_continue:
                addMemberIds();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1 && data != null && !arrayList.contains(data.getStringExtra("member_name"))) {
            arrayList.add(data.getStringExtra("member_name"));
            memberListView.setAdapter(arrayAdapter);
            updateMemberIds(data.getStringExtra("member_name"), true);
        }
    }

    private void updateMemberIds(String name, final boolean toAdd) {
        OdooFields fields = new OdooFields("id");
        ODomain domain = new ODomain();
        domain.add("name", "like", name);

        odoo.searchRead("res.users", fields, domain, 0, 0, null, new OdooResponse() {
            @Override
            public void onResponse(OdooResult response) {
                for (OdooRecord record : response.getRecords()) {
                    if (toAdd)
                        memberIds.add(record.getInt("id"));
                    else
                        memberIds.remove(record.getInt("id"));
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        updateMemberIds(arrayAdapter.getItem(position), false);
        arrayList.remove(arrayAdapter.getItem(position));
        memberListView.setAdapter(arrayAdapter);
    }

    private void addMemberIds() {
        // TODO :

       /* Log.e(">>>>..", memberIds + "");
        ORecordValues values = new ORecordValues();
        values.put("team_member_ids", memberIds);
        odoo.updateRecord("project.teams", values, getIntent().getIntExtra("prj_id", 0), new OdooResponse() {
            @Override
            public void onResponse(OdooResult response) {
                Log.e(">>>>>>>>", "success");
            }
        });*/
        finish();

    }
}
