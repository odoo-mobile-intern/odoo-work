package com.odoo.work.orm.data;

import android.database.Cursor;

import com.odoo.work.orm.OColumn;
import com.odoo.work.orm.OModel;
import com.odoo.work.orm.models.M2MModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListRow extends HashMap<String, Object> {

    private OModel baseModel;

    public ListRow(Cursor cursor) {
        this(null, cursor);
    }

    public ListRow(OModel model, Cursor cursor) {
        this.baseModel = model;
        for (String column : cursor.getColumnNames()) {
            int index = cursor.getColumnIndex(column);
            switch (cursor.getType(index)) {
                case Cursor.FIELD_TYPE_INTEGER:
                    put(column, cursor.getInt(index));
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    put(column, cursor.getBlob(index));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    put(column, cursor.getString(index));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    put(column, cursor.getFloat(index));
                    break;
                case Cursor.FIELD_TYPE_NULL:
                    put(column, false);
                    break;
            }
        }
    }

    public int getInt(String key) {
        return containsKey(key) ? Integer.parseInt(get(key) + "") : null;
    }

    public String getString(String key) {
        return containsKey(key) ? get(key) + "" : "false";
    }

    public float getFloat(String key) {
        return containsKey(key) ? Float.parseFloat(get(key) + "") : null;
    }

    public boolean getBoolean(String key) {
        return getString(key).equals("true");
    }

    public List<ListRow> getM2M(String key) {
        List<ListRow> items = new ArrayList<>();
        if (baseModel != null) {
            OColumn column = baseModel.getColumn(key);
            if (column != null) {
                M2MModel m2MModel = new M2MModel(baseModel.getContext(), baseModel, column);
                items.addAll(m2MModel.browseRecords(getInt("_id")));
            }
        }
        return items;
    }

    public ListRow getM2O(String key) {
        if (baseModel != null) {
            OColumn column = baseModel.getColumn(key);
            if (column != null) {
                return baseModel.createModel(column.relModel).browse(getInt(key));
            }
        }
        return null;
    }
}
