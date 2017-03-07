package com.odoo.work.addons.teams;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.odoo.work.R;
import com.odoo.work.WizardAddTeamMembers;
import com.odoo.work.addons.teams.models.ProjectTeams;
import com.odoo.work.core.support.OdooActivity;
import com.odoo.work.orm.data.ListRow;
import com.odoo.work.utils.BitmapUtils;
import com.odoo.work.utils.CBind;
import com.odoo.work.utils.OAppBarUtils;

import java.util.ArrayList;
import java.util.List;

public class TeamDetailView extends OdooActivity implements View.OnClickListener {

    public static final String KEY_TEAM_ID = "team_id";
    public static final int REQUEST_ADD_MEMBER = 1;
    private ProjectTeams teams;
    private ListRow teamData;
    private ArrayAdapter<ListRow> adapter;
    private List<ListRow> members = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);
        OAppBarUtils.setHasAppBar(this, true);
        teams = new ProjectTeams(this);
        teamData = teams.browse(getIntent().getExtras().getInt(KEY_TEAM_ID));
        setTitle(teamData.getString("name"));
        bindMembers();
        findViewById(R.id.addMember).setOnClickListener(this);
    }

    private void bindMembers() {
        adapter = new ArrayAdapter<ListRow>(this, R.layout.team_member_item_view, members) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
                if (view == null) {
                    view = LayoutInflater.from(TeamDetailView.this).inflate(R.layout.team_member_item_view, parent, false);
                }
                ListRow row = getItem(position);
                if (!row.getString("image_medium").equals("false")) {
                    CBind.setImage(view.findViewById(R.id.userAvatar), BitmapUtils.getBitmapImage(view.getContext(),
                            row.getString("image_medium")));
                } else {
                    CBind.setImage(view.findViewById(R.id.userAvatar), R.drawable.user_profile);
                }
                CBind.setText((TextView) view.findViewById(R.id.partnerName), row.getString("name"));
                CBind.setText((TextView) view.findViewById(R.id.partnerEmail), row.getString("email"));
                return view;
            }
        };

        ListView memberList = (ListView) findViewById(R.id.memberList);
        memberList.setAdapter(adapter);
        showMembers();
    }

    private void showMembers() {
        members.clear();
        members.addAll(teamData.getM2M("team_member_ids"));
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_team_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addMember:
                Intent addMember = new Intent(this, WizardAddTeamMembers.class);
                addMember.putExtra(WizardAddTeamMembers.TEAM_ID, teamData.getInt("id"));
                addMember.putExtra(WizardAddTeamMembers.KEY_SET_RESULT, true);
                startActivityForResult(addMember, REQUEST_ADD_MEMBER);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADD_MEMBER:
                    showMembers();
                    break;
            }
        }
    }
}
