/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p>
 * Created on 17/1/17 11:32 AM
 */
package com.odoo.work.orm.models;

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
