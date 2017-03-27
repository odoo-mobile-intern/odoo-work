package com.odoo.work.orm.models;

import android.content.Context;

import com.odoo.work.orm.ColumnType;
import com.odoo.work.orm.OColumn;
import com.odoo.work.orm.OModel;

public class MailMessage extends OModel {

    OColumn subject = new OColumn("Subject", ColumnType.VARCHAR);
    OColumn date = new OColumn("Date", ColumnType.DATETIME);
    OColumn author_id = new OColumn("Author", ColumnType.MANY2ONE, "res.partner");
    OColumn record_name = new OColumn("Record Name", ColumnType.VARCHAR);
    OColumn model = new OColumn("Model", ColumnType.VARCHAR);
    OColumn res_id = new OColumn("Res Id", ColumnType.INTEGER);
    OColumn message_type = new OColumn("Type", ColumnType.VARCHAR);
    OColumn body = new OColumn("Body", ColumnType.TEXT);

    public MailMessage(Context context) {
        super(context, "mail.message");
    }
}