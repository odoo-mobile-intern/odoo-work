package com.odoo.work.orm.models;

import android.content.Context;

import com.odoo.work.orm.ColumnType;
import com.odoo.work.orm.OColumn;
import com.odoo.work.orm.OModel;

public class ResCountry extends OModel {
    public static final String TAG = ResCountry.class.getSimpleName();

    OColumn name = new OColumn("Name", ColumnType.VARCHAR);
    OColumn code = new OColumn("Code", ColumnType.VARCHAR);

    public ResCountry(Context context) {
        super(context, "res.country");
    }
}
