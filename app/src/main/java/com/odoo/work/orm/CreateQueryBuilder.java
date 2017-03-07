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
 * Created on 17/1/17 11:47 AM
 */
package com.odoo.work.orm;

import com.odoo.work.orm.models.M2MModel;

import java.util.HashMap;

public class CreateQueryBuilder {
    public static final String TAG = CreateQueryBuilder.class.getSimpleName();

    private OModel model;
    private HashMap<String, String> relTableQueries = new HashMap<>();

    public CreateQueryBuilder(OModel model) {
        this.model = model;
    }

    public String createQuery() {

        StringBuffer sql = new StringBuffer()
                .append("CREATE TABLE IF NOT EXISTS ")
                .append(model.getTableName())
                .append(" ( ");

        StringBuffer stringBuffer = new StringBuffer();
        for (OColumn column : model.getColumns()) {

            switch (column.columnType) {
                case MANY2MANY:
                    createM2M(column);
                    break;
                default:
                    stringBuffer.append(column.name)
                            .append(" ")
                            .append(column.columnType.toString());

                    if (column.isPrimaryKey) {
                        stringBuffer.append(" PRIMARY KEY ");
                    }
                    if (column.isAutoIncrement) {
                        stringBuffer.append(" AUTOINCREMENT ");
                    }
                    if (column.defValue != null) {
                        stringBuffer.append(" DEFAULT '").append(column.defValue.toString()).append("'");
                    }
                    stringBuffer.append(" , ");
                    break;
            }
        }

        String string = stringBuffer.toString();
        sql.append(string.substring(0, stringBuffer.length() - 2)).append(" )");
        return sql.toString();
    }

    private void createM2M(OColumn column) {

        M2MModel m2MModel = new M2MModel(model.getContext(), model, column);
        relTableQueries.put(m2MModel.getTableName(), new CreateQueryBuilder(m2MModel).createQuery());

//        String relTableName = column.relModel.replaceAll("\\.", "_");
//        String baseColumn = column.base_column != null ? column.base_column : model.getTableName() + "_id";
//        String relColumn = column.rel_column != null ? column.rel_column : relTableName + "_id";
//        String table_name = column.rel_table_name != null ? column.rel_table_name :
//                model.getTableName() + "_" + relTableName + "_rel";
//
//        relTableQueries.put(table_name, "CREATE TABLE IF NOT EXISTS " + table_name + " (" +
//                baseColumn + " INTEGER, " +
//                relColumn + " INTEGER )");
    }

    public HashMap<String, String> relTableQueries() {
        return relTableQueries;
    }
}
