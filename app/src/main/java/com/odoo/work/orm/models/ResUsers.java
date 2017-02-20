package com.odoo.work.orm.models;

import android.content.Context;

import com.odoo.work.orm.ColumnType;
import com.odoo.work.orm.OColumn;
import com.odoo.work.orm.OModel;

public class ResUsers extends OModel {
    public static final String TAG = ResUsers.class.getSimpleName();

    OColumn name = new OColumn("Name", ColumnType.VARCHAR);

    public ResUsers(Context context) {
        super(context, "res.users");
    }
}
