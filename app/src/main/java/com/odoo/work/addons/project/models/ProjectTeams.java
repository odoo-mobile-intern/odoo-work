package com.odoo.work.addons.project.models;

import android.content.Context;

import com.odoo.work.orm.ColumnType;
import com.odoo.work.orm.OColumn;
import com.odoo.work.orm.OModel;

public class ProjectTeams extends OModel {
    public static final String TAG = ProjectTeams.class.getSimpleName();

    OColumn name = new OColumn("Team Name", ColumnType.VARCHAR);
    //OColumn team_member_ids = new OColumn("Team member Ids", ColumnType.MANY2MANY, "project.team.members");

    public ProjectTeams(Context context) {
        super(context, "project.teams");
    }
}
