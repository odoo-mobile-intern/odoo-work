package com.odoo.work.addons.project.models;

import android.content.Context;

import com.odoo.work.orm.ColumnType;
import com.odoo.work.orm.OColumn;
import com.odoo.work.orm.OModel;

public class ProjectTaskType extends OModel {

    OColumn name = new OColumn("Name", ColumnType.VARCHAR);
    OColumn project_ids = new OColumn("Projects", ColumnType.MANY2MANY, "project.project")
            .setRelTableName("project_task_type_rel")
            .setBaseColumn("type_id")
            .setRelColumn("project_id");
    OColumn sequence = new OColumn("Sequence", ColumnType.INTEGER).setDefaultValue(0);

    public ProjectTaskType(Context context) {
        super(context, "project.task.type");
    }

}
