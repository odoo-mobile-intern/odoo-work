package com.odoo.work.orm.models;

import android.content.Context;

import com.odoo.work.addons.customer.model.ResPartner;
import com.odoo.work.addons.project.models.ProjectProject;
import com.odoo.work.addons.project.models.ProjectTask;
import com.odoo.work.addons.project.models.ProjectTaskType;
import com.odoo.work.addons.teams.models.ProjectTeams;
import com.odoo.work.orm.OModel;

import java.util.HashMap;

public class ModelRegistry {

    public HashMap<String, OModel> models(Context context) {
        HashMap<String, OModel> model = new HashMap<>();
        model.put("ir.model", new IrModel(context));
        model.put("local.record.state", new LocalRecordState(context));
        model.put("mail.message", new MailMessage(context));
        model.put("res.partner", new ResPartner(context));
        model.put("project.project", new ProjectProject(context));
        model.put("project.task.type", new ProjectTaskType(context));
        model.put("project.task", new ProjectTask(context));
        model.put("project.teams", new ProjectTeams(context));
        model.put("res.users", new ResUsers(context));
        return model;
    }

    public static OModel getModel(Context context, String model) {
        return new ModelRegistry().models(context).get(model);
    }
}
