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
 * Created on 24/1/17 11:53 AM
 */
package com.odoo.work.orm.models;

import android.content.Context;

import com.odoo.work.R;
import com.odoo.work.orm.ColumnType;
import com.odoo.work.orm.OColumn;
import com.odoo.work.orm.OModel;

public class ProjectProject extends OModel {
    public static final String TAG = ProjectProject.class.getSimpleName();
    private Context mContext;

    OColumn name = new OColumn("Project Title", ColumnType.VARCHAR);
    OColumn use_tasks = new OColumn("is Task", ColumnType.BOOLEAN);
    OColumn label_tasks = new OColumn("Task Label", ColumnType.VARCHAR);
    OColumn user_id = new OColumn("User Id", ColumnType.MANY2ONE, "res.users");
    OColumn partner_id = new OColumn("Partner Id", ColumnType.MANY2ONE, "res.partner");
    //OColumn team_ids = new OColumn("Team id", ColumnType.MANY2MANY, "project.teams");

    public ProjectProject(Context context) {
        super(context, "project.project");
        mContext = context;
    }

    @Override
    public String getAuthority() {
        return mContext.getString(R.string.project_authority);
    }
}
