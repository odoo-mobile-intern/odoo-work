package com.odoo.work.orm;

public class OColumn {
    public String name, label, relModel;
    public boolean isPrimaryKey = false, isAutoIncrement = false, isLocal = false;
    public ColumnType columnType;
    public Object defValue;

    public String base_column, rel_column, rel_table_name;

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

    public OColumn setBaseColumn(String column) {
        base_column = column;
        return this;
    }

    public OColumn setRelColumn(String column) {
        rel_column = column;
        return this;
    }

    public OColumn setRelTableName(String name) {
        rel_table_name = name;
        return this;
    }

    public String getModelName(OModel baseModel) {
        if (rel_table_name != null) {
            return rel_table_name;
        }
        return baseModel.getTableName() + "_" + relModel.replaceAll("\\.", "_") + "_rel";
    }

    @Override
    public String toString() {
        return "OColumn{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", columnType=" + columnType +
                '}';
    }
}
