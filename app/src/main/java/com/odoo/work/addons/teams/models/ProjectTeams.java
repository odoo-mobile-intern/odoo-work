package com.odoo.work.addons.teams.models;

import android.content.Context;

import com.odoo.work.orm.ColumnType;
import com.odoo.work.orm.OColumn;
import com.odoo.work.orm.OModel;

public class ProjectTeams extends OModel {
    public static final String TAG = ProjectTeams.class.getSimpleName();

    OColumn name = new OColumn("Team Name", ColumnType.VARCHAR);
    OColumn team_id = new OColumn("Team ID", ColumnType.MANY2ONE);
    OColumn team_member_ids = new OColumn("Team members", ColumnType.MANY2MANY, "res.partner")
            .setBaseColumn("team_id")
            .setRelColumn("partner_id")
            .setRelTableName("project_team_members");

    public ProjectTeams(Context context) {
        super(context, "project.teams");
    }
}
