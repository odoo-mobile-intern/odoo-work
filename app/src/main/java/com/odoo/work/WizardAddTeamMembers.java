package com.odoo.work;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.rpc.helper.ORecordValues;
import com.odoo.core.rpc.helper.ORelValues;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.rpc.listeners.OdooResponse;
import com.odoo.core.support.OUser;

import java.util.ArrayList;

public class WizardAddTeamMembers extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    public static final String TEAM_ID = "team_id";
    public static final String KEY_SET_RESULT = "key_set_result";
    private ArrayList<Bundle> arrayList = new ArrayList<>();
    private ArrayAdapter<Bundle> arrayAdapter;
    private ListView memberListView;
    private ArrayList<Integer> memberIds;
    private Odoo odoo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_add_members);
        setResult(RESULT_CANCELED);
        try {
            odoo = Odoo.createWithUser(this, OUser.current(this));
        } catch (OdooVersionException e) {
            e.printStackTrace();
        }


        findViewById(R.id.editAddMember).setOnClickListener(this);
        findViewById(R.id.btn_continue).setOnClickListener(this);
        findViewById(R.id.btn_skip).setOnClickListener(this);

        memberIds = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<Bundle>(this, R.layout.member_list_item,
                R.id.textMemberName, arrayList) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(WizardAddTeamMembers.this).inflate(R.layout.member_list_item, parent, false);
                }
                TextView view = (TextView) convertView.findViewById(R.id.textMemberName);
                view.setText(getItem(position).getString(SelectMembers.KEY_MEMBER_NAME, ""));
                return convertView;
            }
        };
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
        if (resultCode == RESULT_OK && data != null) {
            int partner_id = data.getIntExtra(SelectMembers.KEY_MEMBER_ID, -1);
            if (memberIds.indexOf(partner_id) == -1) {
                arrayList.add(data.getExtras());
                memberListView.setAdapter(arrayAdapter);
                memberIds.add(partner_id);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        memberIds.remove(memberIds.indexOf(arrayList.get(position).getInt(SelectMembers.KEY_MEMBER_ID)));
        arrayList.remove(arrayAdapter.getItem(position));
        memberListView.setAdapter(arrayAdapter);
    }

    private void addMemberIds() {
        int teamId = getIntent().getIntExtra(TEAM_ID, -1);
        if (teamId != -1) {
            ORecordValues values = new ORecordValues();
            values.put("team_id", teamId);
            values.put("member_ids", new ORelValues().replace(memberIds));
            odoo.createRecord("project.team.invitation", values, new OdooResponse() {
                @Override
                public void onResponse(OdooResult response) {
                    if (getIntent().getExtras().containsKey(KEY_SET_RESULT)) {
                        setResult(RESULT_OK);
                    } else {
                        startActivity(new Intent(WizardAddTeamMembers.this, HomeActivity.class));
                    }
                    finish();
                }
            });
        }
    }
}
