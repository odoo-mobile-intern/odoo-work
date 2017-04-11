package com.odoo.work.addons.project;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.odoo.work.R;
import com.odoo.work.addons.project.models.ProjectProject;
import com.odoo.work.addons.project.models.ProjectTask;
import com.odoo.work.core.support.OdooActivity;
import com.odoo.work.orm.data.ListRow;
import com.odoo.work.utils.OAppBarUtils;

public class TaskDetail extends OdooActivity {
    private static final java.lang.String KEY_TASK_ID = "id";
    private ProjectProject projectProject;
    private ProjectTask projectTask;
    private Bundle extra;
    private ListRow projectData;
    private TextView user, stage, priority, project, kanban;
    private EditText Name, desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_detail);
        OAppBarUtils.setHasAppBar(this, true);
        extra = getIntent().getExtras();



    }
}

