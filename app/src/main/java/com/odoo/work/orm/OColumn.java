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
 * Created on 17/1/17 11:36 AM
 */
package com.odoo.work.orm;

public class OColumn {
    public String name, label, relModel;
    public boolean isPrimaryKey = false, isAutoIncrement = false, isLocal = false;
    public ColumnType columnType;
    public Object defValue;

    public OColumn(String label, ColumnType columnType) {
        this(label, columnType, null);
    }

    public OColumn(String label, ColumnType columnType, String relModel) {
        this.label = label;
        this.columnType = columnType;
        this.relModel = relModel;
    }

    public OColumn makePrimaryKey() {
        isPrimaryKey = true;
        return this;
    }

    public OColumn makeAutoIncrement() {
        isAutoIncrement = true;
        return this;
    }

    public OColumn makeLocal() {
        isLocal = true;
        return this;
    }

    public OColumn setDefaultValue(Object defValue) {
        this.defValue = defValue;
        return this;
    }
}
