package com.odoo.work.addons.project.models;

import android.content.Context;

import com.odoo.work.orm.ColumnType;
import com.odoo.work.orm.OColumn;
import com.odoo.work.orm.OModel;

public class ProjectTeamMember extends OModel {
    public static final String TAG = ProjectTeamMember.class.getSimpleName();

    OColumn name = new OColumn("Name", ColumnType.VARCHAR);
    OColumn user_id = new OColumn("User Id", ColumnType.MANY2ONE, "res.users");
    OColumn state = new OColumn("State", ColumnType.VARCHAR);  //new - accepted - rejected
    OColumn team_id = new OColumn("Team Id", ColumnType.MANY2ONE, "project.teams");

    public ProjectTeamMember(Context context) {
        super(context, "project.team.members");
    }
}
