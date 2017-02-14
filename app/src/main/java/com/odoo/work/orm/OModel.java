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
 * Created on 17/1/17 11:30 AM
 */
package com.odoo.work.orm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.odoo.work.R;
import com.odoo.work.orm.models.ModelRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OModel extends SQLiteOpenHelper implements BaseColumns {
    public static final String TAG = OModel.class.getSimpleName();
    public static final String DATABASE_NAME = "OdooWork";
    public static final int DATABASE_VERSION = 1;
    public String mModelName;
    private Context mContext;

    OColumn _id = new OColumn("Local id", ColumnType.INTEGER).makePrimaryKey()
            .makeAutoIncrement().makeLocal();
    OColumn id = new OColumn("Server Id", ColumnType.INTEGER);

    public OModel(Context context, String model) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mModelName = model;
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        HashMap<String, OModel> map = new ModelRegistry().models(mContext);
        for (OModel model : map.values()) {
            CreateQueryBuilder queryBuilder = new CreateQueryBuilder(model);
            String sql = queryBuilder.createQuery();
            if (sql != null) {
                db.execSQL(sql);
            }
        }
        Log.e(">>>>>>>>>", "Models are registered");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public String getTableName() {
        return mModelName.replace(".", "_");
    }

    public String getModelName() {
        return mModelName;
    }

    public List<OColumn> getAllColumns() {
        List<OColumn> columnList = new ArrayList<>();
        List<Field> fieldList = new ArrayList<>();

        fieldList.addAll(Arrays.asList(getClass().getSuperclass().getDeclaredFields()));
        fieldList.addAll(Arrays.asList(getClass().getDeclaredFields()));

        for (Field field : fieldList) {
            field.setAccessible(true);
            if (field.getType().isAssignableFrom(OColumn.class)) {
                try {
                    OColumn column = (OColumn) field.get(this);
                    column.name = field.getName();
                    columnList.add(column);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return columnList;
    }

    public int create(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(getTableName(), null, values);
        db.close();
        return (int) id;
    }

    public int update(ContentValues values, String where, String... args) {
        SQLiteDatabase db = getWritableDatabase();
        int id = db.update(getTableName(), values, where, args);
        db.close();
        return id;
    }

    public List<ListRow> select(String where, String... args) {

        List<ListRow> rows = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        args = args.length > 0 ? args : null;

        Cursor cursor = db.query(getTableName(), null, where, args, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                rows.add(new ListRow(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return rows;
    }

    public int count() {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT COUNT(*) AS TOTAL FROM " + getTableName(), null);

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    public String[] getServerColumns() {
        List<String> serverColumns = new ArrayList<>();
        for (OColumn column : getAllColumns()) {
            if (!column.isLocal) {
                serverColumns.add(column.name.toString());
            }
        }
        return serverColumns.toArray(new String[serverColumns.size()]);
    }

    public static OModel createInstance(String relModel, Context mContext) {

        HashMap<String, OModel> models = new ModelRegistry().models(mContext);
        for (String key : models.keySet()) {
            OModel model = models.get(key);
            if (model.getModelName().equals(relModel)) {
                return model;
            }
        }
        return null;
    }

    public int updateOrCreate(ContentValues values, String where, String... args) {
        List<ListRow> records = select(where, args);

        if (records.size() > 0) {
            ListRow row = records.get(0);
            update(values, where, args);
            return row.getInt(_ID);
        } else {
            create(values);
        }
        return 0;
    }

    public String getAuthority() {
        return mContext.getString(R.string.main_authority);
    }

    public Uri getUri() {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("content");
        uriBuilder.authority(getAuthority());
        uriBuilder.appendPath(getModelName());
        return uriBuilder.build();
    }
}
