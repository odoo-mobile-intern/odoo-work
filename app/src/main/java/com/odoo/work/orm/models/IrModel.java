package com.odoo.work.orm.models;

import android.content.Context;

import com.odoo.work.orm.ColumnType;
import com.odoo.work.orm.OColumn;
import com.odoo.work.orm.OModel;

public class IrModel extends OModel {

    OColumn model = new OColumn("Model", ColumnType.VARCHAR);
    OColumn last_sync_on = new OColumn("Last Sync Datetime", ColumnType.DATETIME);

    public IrModel(Context context) {
        super(context, "ir.model");
    }
}
