package com.odoo.work.orm.models;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.odoo.work.orm.ColumnType;
import com.odoo.work.orm.OColumn;
import com.odoo.work.orm.OModel;
import com.odoo.work.orm.data.ListRow;

import java.util.ArrayList;
import java.util.List;

public class M2MModel extends OModel {

    private OModel baseModel;
    private OModel relModel;
    private String baseColumnName;
    private OColumn baseColumn = new OColumn("Base Column", ColumnType.INTEGER);
    private OColumn relColumn = new OColumn("Rel Column", ColumnType.INTEGER);

    public M2MModel(Context context, OModel baseModel, OColumn column) {
        super(context, column.getModelName(baseModel));
        this.baseModel = baseModel;
        this.relModel = baseModel.createModel(column.relModel);
        this.baseColumnName = column.name;
        baseColumn.name = column.base_column != null ? column.base_column :
                baseModel.getTableName() + "_id";
        relColumn.name = column.rel_column != null ? column.rel_column :
                column.relModel.replaceAll("\\.", "_") + "_id";
    }

    public String getBaseColumn() {
        return baseColumn.name;
    }

    public String getRelColumn() {
        return relColumn.name;
    }

    @Override
    public Uri getUri() {
        Uri.Builder builder = super.getUri().buildUpon();
        builder.appendQueryParameter("type", "many_to_many");
        builder.appendQueryParameter("base_model", baseModel.getModelName());
        builder.appendQueryParameter("base_column", baseColumnName);
        return builder.build();
    }

    public void insertIds(int base_id, List<Integer> relIds) {
        for (int id : relIds) {
            ContentValues values = new ContentValues();
            values.put(baseColumn.name, base_id);
            values.put(relColumn.name, id);
            create(values);
        }
    }

    public List<ListRow> browseRecords(int base_id) {
        List<Integer> ids = new ArrayList<>();
        ids.addAll(selectRowIds(getRelColumn(), getBaseColumn() + " = ? ", base_id + ""));
        return relModel.select("_id in (" + TextUtils.join(", ", ids) + ")");
    }
}
