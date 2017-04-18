package com.odoo.work.addons.project.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.work.R;
import com.odoo.work.orm.ColumnType;
import com.odoo.work.orm.OColumn;
import com.odoo.work.orm.OModel;

public class ProjectProject extends OModel {
    public static final String TAG = ProjectProject.class.getSimpleName();
    private Context mContext;

    public static String[] COLORS = {
            "#9D9D9D",
            "#00BBD3",
            "#4BAE4F",
            "#9B26AF",
            "#785447",
            "#FE5621",
            "#363F45",
            "#3F51B5",
            "#E91E63",
            "#374046"
    };

    OColumn name = new OColumn("Project Title", ColumnType.VARCHAR);
    OColumn use_tasks = new OColumn("is Task", ColumnType.VARCHAR);
    OColumn label_tasks = new OColumn("Task Label", ColumnType.VARCHAR);
    OColumn user_id = new OColumn("User Id", ColumnType.MANY2ONE, "res.users");
    OColumn partner_id = new OColumn("Partner Id", ColumnType.MANY2ONE, "res.partner");
    OColumn team_id = new OColumn("Team id", ColumnType.MANY2ONE, "project.teams");
    OColumn color = new OColumn("Color", ColumnType.INTEGER).setDefaultValue(0);

    OColumn type_ids = new OColumn("Task Stages", ColumnType.MANY2MANY, "project.task.type")
            .setRelTableName("project_task_type_rel")
            .setBaseColumn("project_id")
            .setRelColumn("type_id");

    public ProjectProject(Context context) {
        super(context, "project.project");
        mContext = context;
    }

    public Uri getDashboardUri() {
        return Uri.withAppendedPath(getUri(), "dashboard_uri");
    }

    @Override
    public String getAuthority() {
        return mContext.getString(R.string.project_authority);
    }
}
