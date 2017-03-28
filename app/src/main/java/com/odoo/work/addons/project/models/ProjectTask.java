package com.odoo.work.addons.project.models;

import android.content.Context;

import com.odoo.work.orm.ColumnType;
import com.odoo.work.orm.OColumn;
import com.odoo.work.orm.OModel;

public class ProjectTask extends OModel {

    OColumn name = new OColumn("Name", ColumnType.VARCHAR);
    OColumn project_id = new OColumn("Project", ColumnType.MANY2ONE, "project.project");
    OColumn user_id = new OColumn("Assign To", ColumnType.MANY2ONE, "res.users");
    OColumn deadline_date = new OColumn("Deadline", ColumnType.DATE);
    OColumn description = new OColumn("Description", ColumnType.TEXT);
    OColumn kanban_state = new OColumn("Kanban State", ColumnType.VARCHAR);
    OColumn stage_id = new OColumn("Task Stage", ColumnType.MANY2ONE, "project.task.type");
    OColumn priority = new OColumn("Priority", ColumnType.VARCHAR).setDefaultValue(0);


    public ProjectTask(Context context) {
        super(context, "project.task");
    }
}
