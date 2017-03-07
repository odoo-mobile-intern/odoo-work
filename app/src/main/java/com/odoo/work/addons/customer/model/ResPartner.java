package com.odoo.work.addons.customer.model;

import android.content.Context;

import com.odoo.work.R;
import com.odoo.work.orm.ColumnType;
import com.odoo.work.orm.OColumn;
import com.odoo.work.orm.OModel;

public class ResPartner extends OModel {
    public static final String TAG = ResPartner.class.getSimpleName();
    private Context mContext;

    OColumn name = new OColumn("Name", ColumnType.VARCHAR);
    OColumn email = new OColumn("Email Address", ColumnType.VARCHAR);
    OColumn image_medium = new OColumn("Contact Image", ColumnType.BLOB);

    public ResPartner(Context context) {
        super(context, "res.partner");
        mContext = context;
    }

    @Override
    public String getAuthority() {
        return mContext.getString(R.string.partner_authority);
    }
}
