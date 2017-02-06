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
 * Created on 6/2/17 11:58 AM
 */
package com.odoo.work.orm.models;

import android.content.Context;

import com.odoo.work.orm.ColumnType;
import com.odoo.work.orm.OColumn;
import com.odoo.work.orm.OModel;

public class ProjectTeamMember extends OModel {
    public static final String TAG = ProjectTeamMember.class.getSimpleName();

    OColumn name = new OColumn("Name", ColumnType.VARCHAR);
    OColumn user_id = new OColumn("User Id", ColumnType.MANY2ONE, "res.users");
    OColumn state = new OColumn("State", ColumnType.VARCHAR);  //new - accepted - rejected
    OColumn team_id = new OColumn("Team Id", ColumnType.MANY2ONE, "project.teams");

    public ProjectTeamMember(Context context) {
        super(context, "project.team.members");
    }
}
