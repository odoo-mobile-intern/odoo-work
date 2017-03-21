package com.odoo.work.addons.teams;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.helper.OArguments;
import com.odoo.core.support.OUser;
import com.odoo.work.R;
import com.odoo.work.WizardAddTeamMembers;
import com.odoo.work.addons.teams.models.ProjectTeams;
import com.odoo.work.core.support.OdooActivity;
import com.odoo.work.orm.data.ListRow;
import com.odoo.work.utils.BitmapUtils;
import com.odoo.work.utils.CBind;
import com.odoo.work.utils.OAppBarUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamDetailView extends OdooActivity implements View.OnClickListener {

    public static final String KEY_TEAM_ID = "team_id";
    public static final int REQUEST_ADD_MEMBER = 1;
    private ProjectTeams teams;
    private ListRow teamData;
    private ArrayAdapter<ListRow> adapter;
    private List<ListRow> members = new ArrayList<>();
    private boolean isOwner = false;
    private OUser mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);
        OAppBarUtils.setHasAppBar(this, true);
        teams = new ProjectTeams(this);
        mUser = teams.getUser();
        teamData = teams.browse(getIntent().getExtras().getInt(KEY_TEAM_ID));
        setTitle(teamData.getString("name"));
        findViewById(R.id.addMember).setOnClickListener(this);
        ListRow userData = teamData.getM2O("create_uid");
        if (userData != null) {
            if (userData.getInt("id") == mUser.getUserId()) {
                isOwner = true;
            }
        }
        if (!isOwner) {
            findViewById(R.id.addMember).setVisibility(View.GONE);
        }
        bindMembers();
    }

    private void bindMembers() {
        adapter = new ArrayAdapter<ListRow>(this, R.layout.team_member_item_view, members) {
            @NonNull
            @Override
            public View getView(final int position, @Nullable View view, @NonNull ViewGroup parent) {
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
                view.findViewById(R.id.removeMember).setVisibility(isOwner ? View.VISIBLE : View.GONE);
                view.findViewById(R.id.removeMember).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeMember(getItem(position));
                    }
                });
                return view;
            }
        };

        ListView memberList = (ListView) findViewById(R.id.memberList);
        memberList.setAdapter(adapter);
        showMembers();
    }

    private void removeMember(final ListRow member) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_confirmation);
        builder.setMessage(getString(R.string.remove_member_from_team));
        builder.setPositiveButton(R.string.label_remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new LeaveTeamTask(member.getInt("id"), false).execute();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    private void showMembers() {
        findViewById(R.id.leaveTeam).setVisibility(View.GONE);
        findViewById(R.id.leaveTeam).setOnClickListener(this);
        members.clear();
        members.addAll(teamData.getM2M("team_member_ids"));
        if (!isOwner) {
            for (ListRow member : members) {
                if (member.getInt("id") == mUser.getPartnerId()) {
                    findViewById(R.id.leaveTeam).setVisibility(View.VISIBLE);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_team_detail, menu);
        if (!isOwner) {
            menu.findItem(R.id.menu_delete_team).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_delete_team:
                deleteTeam();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteTeam() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_confirmation);
        builder.setMessage(getString(R.string.remove_team));
        builder.setPositiveButton(R.string.label_remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new AsyncTask<Void, Void, Void>() {
                    private ProgressDialog progressDialog;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog = new ProgressDialog(TeamDetailView.this);
                        progressDialog.setMessage(getString(R.string.title_removing_team));
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            Odoo odoo = Odoo.createWithUser(TeamDetailView.this, mUser);
                            odoo.unlinkRecord(teams.getModelName(), teamData.getInt("id"));
                            teams.delete(teamData.getInt("_id"), true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        progressDialog.dismiss();
                        finish();
                        Toast.makeText(TeamDetailView.this, R.string.toast_team_removed, Toast.LENGTH_SHORT).show();
                    }
                }.execute();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
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
            case R.id.leaveTeam:
                leaveTeam();
                break;
        }
    }

    private void leaveTeam() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_confirmation);
        builder.setMessage(getString(R.string.leave_team_confirm_msg));
        builder.setPositiveButton(R.string.label_leave, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new LeaveTeamTask(mUser.getPartnerId(), true).execute();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
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

    private class LeaveTeamTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;
        private int partnerId;
        private boolean autoCloseActivity;

        public LeaveTeamTask(int partner_id, boolean autoCloseActivity) {
            partnerId = partner_id;
            this.autoCloseActivity = autoCloseActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(TeamDetailView.this);
            progressDialog.setMessage(autoCloseActivity ? getString(R.string.title_leaving_team)
                    : getString(R.string.title_removing_member));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Odoo odoo = Odoo.createWithUser(TeamDetailView.this, mUser);
                OArguments args = new OArguments();
                args.add(new JSONArray());
                args.add(teamData.getInt("id"));
                args.add(partnerId);
                odoo.callMethod(teams.getModelName(), "leave_team", args, new HashMap<String, Object>());
                if (autoCloseActivity)
                    teams.delete(teamData.getInt("_id"), true);
                teams.syncData();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (autoCloseActivity) {
                Toast.makeText(TeamDetailView.this, R.string.toast_you_left_team, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                showMembers();
                Toast.makeText(TeamDetailView.this, R.string.msg_member_removed, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
