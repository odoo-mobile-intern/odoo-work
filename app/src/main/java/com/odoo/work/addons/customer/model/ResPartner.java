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
    OColumn street = new OColumn("Street", ColumnType.VARCHAR);
    OColumn street2 = new OColumn("Street2", ColumnType.VARCHAR);
    OColumn city = new OColumn("City", ColumnType.VARCHAR);
    OColumn state_id = new OColumn("State id", ColumnType.MANY2ONE, "res.country.state");
    OColumn zip = new OColumn("Pincode", ColumnType.VARCHAR);
    OColumn country_id = new OColumn("Country id", ColumnType.MANY2ONE, "res.country");
    OColumn website = new OColumn("Website", ColumnType.VARCHAR);
    OColumn function = new OColumn("Job Position", ColumnType.VARCHAR);
    OColumn phone = new OColumn("Phone Number", ColumnType.VARCHAR);
    OColumn mobile = new OColumn("Mobile Number", ColumnType.VARCHAR);
    OColumn fax = new OColumn("Fax Number", ColumnType.VARCHAR);
    OColumn email = new OColumn("Email Address", ColumnType.VARCHAR);
    OColumn image_medium = new OColumn("Contact Image", ColumnType.BLOB);
    OColumn company_type = new OColumn("Company_type", ColumnType.VARCHAR); // company or person

    public ResPartner(Context context) {
        super(context, "res.partner");
        mContext = context;
    }

    @Override
    public String getAuthority() {
        return mContext.getString(R.string.partner_authority);
    }
}
