package com.odoo.work.addons.project.models;

import android.content.Context;

import com.odoo.work.R;
import com.odoo.work.orm.ColumnType;
import com.odoo.work.orm.OColumn;
import com.odoo.work.orm.OModel;

public class ProjectProject extends OModel {
    public static final String TAG = ProjectProject.class.getSimpleName();
    private Context mContext;

    OColumn name = new OColumn("Project Title", ColumnType.VARCHAR);
    OColumn use_tasks = new OColumn("is Task", ColumnType.VARCHAR);
    OColumn label_tasks = new OColumn("Task Label", ColumnType.VARCHAR);
    OColumn user_id = new OColumn("User Id", ColumnType.MANY2ONE, "res.users");
    OColumn partner_id = new OColumn("Partner Id", ColumnType.MANY2ONE, "res.partner");
//    OColumn team_ids = new OColumn("Team id", ColumnType.MANY2MANY, "project.teams");

    public ProjectProject(Context context) {
        super(context, "project.project");
        mContext = context;
    }

    @Override
    public String getAuthority() {
        return mContext.getString(R.string.project_authority);
    }
}
