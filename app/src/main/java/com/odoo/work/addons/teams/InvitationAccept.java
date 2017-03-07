package com.odoo.work.addons.teams;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.rpc.helper.OArguments;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.rpc.helper.ORecordValues;
import com.odoo.core.rpc.helper.ORelValues;
import com.odoo.core.rpc.helper.OdooFields;
import com.odoo.core.rpc.helper.utils.gson.OdooRecord;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.rpc.listeners.OdooError;
import com.odoo.core.rpc.listeners.OdooResponse;
import com.odoo.core.support.OUser;
import com.odoo.work.R;
import com.odoo.work.addons.teams.models.ProjectTeams;
import com.odoo.work.core.support.OdooActivity;
import com.odoo.work.utils.CBind;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.odoo.work.utils.DataUtils.getDoubletToInt;

public class InvitationAccept extends OdooActivity implements View.OnClickListener {

    private ProjectTeams teams;
    private Odoo odoo;
    private Bundle data;
    private OdooRecord teamData;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_accept);
        data = getIntent().getExtras();
        teams = new ProjectTeams(this);
        try {
            odoo = Odoo.createWithUser(this, OUser.current(this));
            ODomain domain = new ODomain();
            domain.add("id", "=", Integer.parseInt(data.getString("res_id")));
            odoo.searchRead(teams.getModelName(), new OdooFields(), domain, 0, 1, null, new OdooResponse() {
                @Override
                public void onResponse(OdooResult response) {
                    findViewById(R.id.progressBar2).setVisibility(View.GONE);
                    findViewById(R.id.team_invitation_container).setVisibility(View.VISIBLE);
                    bindView(response.getRecords().get(0));
                }
            });
        } catch (OdooVersionException e) {
            e.printStackTrace();
        }
    }

    private void bindView(OdooRecord result) {
        teamData = result;
        findViewById(R.id.btnAccept).setOnClickListener(this);
        findViewById(R.id.btnReject).setOnClickListener(this);

        CBind.setText((TextView) findViewById(R.id.teamName), result.getString("name"));
        CBind.setText((TextView) findViewById(R.id.invitationMessage),
                getString(R.string.msg_invitation, data.getString("author_name")));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAccept:
                acceptOrReject(true);
                break;
            case R.id.btnReject:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.title_confirmation);
                builder.setMessage(R.string.msg_are_you_sure_want_to_reject_invitation);
                builder.setPositiveButton(R.string.label_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        acceptOrReject(false);
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.show();
                break;
        }
    }

    private void acceptOrReject(final boolean accepted) {

        final int team_id = Integer.parseInt(data.getString("res_id"));
        final int partner_id = OUser.current(this).getPartnerId();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(team_id);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.label_working);

        List<Integer> invitation_ids = getDoubletToInt(teamData.getArray("invitation_member_ids"));
        List<Integer> team_member_ids = getDoubletToInt(teamData.getArray("team_member_ids"));
        ORecordValues values = new ORecordValues();
        if (accepted) {
            progressDialog.setMessage(getString(R.string.msg_joining_team));
            team_member_ids.add(partner_id);
            values.put("team_member_ids", new ORelValues().replace(team_member_ids));
        } else {
            progressDialog.setMessage(getString(R.string.msg_rejecting_invitation));
        }
        invitation_ids.remove(invitation_ids.indexOf(partner_id));
        values.put("invitation_member_ids", new ORelValues().replace(invitation_ids));
        progressDialog.setCancelable(false);
        progressDialog.show();

        odoo.updateRecord(teams.getModelName(), values, team_id, new OdooResponse() {
            @Override
            public void onResponse(OdooResult response) {
                if (!accepted) {
                    progressDialog.dismiss();
                } else {
                    subscribeToTeam(team_id);
                }
            }

            @Override
            public void onError(OdooError error) {
                super.onError(error);
                progressDialog.dismiss();
            }
        });
    }

    private void subscribeToTeam(int team_id) {
        try {
            OUser user = OUser.current(this);
            OArguments args = new OArguments();
            args.add(new JSONArray().put(team_id));
            args.add(new JSONArray().put(user.getUserId()));
            args.addNULL();
            args.add(new JSONObject().put("uid", user.getUserId()));
            odoo.callMethod(teams.getModelName(), "message_subscribe_users", args, null, new OdooResponse() {
                @Override
                public void onResponse(OdooResult response) {
                    progressDialog.dismiss();
                    finish();
                }

                @Override
                public void onError(OdooError error) {
                    super.onError(error);
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
