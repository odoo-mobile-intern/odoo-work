package com.odoo.work.addons.project.service;

import android.content.Context;

import com.odoo.work.orm.OModel;
import com.odoo.work.addons.project.models.ProjectProject;
import com.odoo.work.orm.sync.SyncService;

public class ProjectService extends SyncService {
    public static final String TAG = ProjectService.class.getSimpleName();

    @Override
    public OModel getModel(Context context) {
        return new ProjectProject(context);
    }
}
