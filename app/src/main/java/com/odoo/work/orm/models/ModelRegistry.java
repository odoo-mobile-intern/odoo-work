package com.odoo.work.orm.models;

import android.content.Context;

import com.odoo.work.orm.OModel;
import com.odoo.work.addons.project.models.ProjectProject;
import com.odoo.work.addons.customer.model.ResPartner;

import java.util.HashMap;

public class ModelRegistry {

    public HashMap<String, OModel> models(Context context) {
        HashMap<String, OModel> model = new HashMap<>();
        model.put("ir.model", new IrModel(context));
        model.put("local.record.state", new LocalRecordState(context));
        model.put("res.partner", new ResPartner(context));
        model.put("res.country.state", new ResState(context));
        model.put("res.country", new ResCountry(context));
        model.put("project.project", new ProjectProject(context));
        model.put("res.users", new ResUsers(context));
        return model;
    }

    public static OModel getModel(Context context, String model) {
        return new ModelRegistry().models(context).get(model);
    }
}
