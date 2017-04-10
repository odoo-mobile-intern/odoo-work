package com.odoo.work.orm;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;
import com.odoo.core.utils.ODateUtils;
import com.odoo.work.R;
import com.odoo.work.orm.data.ListRow;
import com.odoo.work.orm.models.IrModel;
import com.odoo.work.orm.models.LocalRecordState;
import com.odoo.work.orm.models.ModelRegistry;
import com.odoo.work.orm.sync.SyncAdapter;
import com.odoo.work.utils.OStorageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OModel extends SQLiteOpenHelper implements BaseColumns {
    public static final String TAG = OModel.class.getSimpleName();
    private static final String DB_NAME = "OdooWork";
    private static final int DB_VERSION = 1;
    public static final int INVALID_ROW_ID = -1;

    OColumn _id = new OColumn("Local Id", ColumnType.INTEGER).makePrimaryKey()
            .makeAutoIncrement().makeLocal();
    OColumn id = new OColumn("Server Id", ColumnType.INTEGER);
    OColumn write_date = new OColumn("Write date", ColumnType.DATETIME).makeLocal().setDefaultValue("false");
    OColumn create_uid = new OColumn("Owner", ColumnType.MANY2ONE, "res.users");
    OColumn is_dirty = new OColumn("Is dirty", ColumnType.BOOLEAN).makeLocal().setDefaultValue("false");
    private String mModelName;
    private Context mContext;

    public OModel(Context context, String model) {
        super(context, DB_NAME, null, DB_VERSION);
        mModelName = model;
        mContext = context;
    }

    public static OModel createInstance(String modelName, Context mContext) {

        HashMap<String, OModel> models = new ModelRegistry().models(mContext);
        for (String key : models.keySet()) {
            OModel model = models.get(key);
            if (model.getModelName().equals(modelName)) {
                return model;
            }
        }
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        HashMap<String, OModel> map = new ModelRegistry().models(mContext);
        for (OModel model : map.values()) {
            CreateQueryBuilder statementBuilder = new CreateQueryBuilder(model);
            String sql = statementBuilder.createQuery();
            if (sql != null) {
                db.execSQL(sql);
                Log.d(TAG, "Table created: " + model.getTableName());
            }
            for (String table : statementBuilder.relTableQueries().keySet()) {
                db.execSQL(statementBuilder.relTableQueries().get(table));
                Log.d(TAG, "Table created: " + table);
            }
        }

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

    public String getAuthority() {
        return mContext.getString(R.string.main_authority);
    }

    public String authority() {
        return getAuthority() + "_" + getModelName();
    }

    public Uri getUri() {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(getAuthority());
        uriBuilder.appendPath(getModelName());
        uriBuilder.scheme("content");
        uriBuilder.appendQueryParameter("model", getModelName());
        return uriBuilder.build();
    }

    public OColumn getColumn(String column) {
        Field field = null;
        try {
            field = getClass().getDeclaredField(column);
        } catch (NoSuchFieldException e) {
            try {
                field = getClass().getSuperclass().getDeclaredField(column);
            } catch (NoSuchFieldException e1) {
            }
        }
        if (field != null) {
            field.setAccessible(true);
            if (field.getType().isAssignableFrom(OColumn.class)) {
                try {
                    OColumn columnObj = (OColumn) field.get(this);
                    if (columnObj.name == null)
                        columnObj.name = field.getName();
                    return columnObj;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public List<OColumn> getColumns() {
        List<OColumn> columnList = new ArrayList<>();
        List<Field> fieldList = new ArrayList<>();

        fieldList.addAll(Arrays.asList(getClass().getSuperclass().getDeclaredFields()));
        fieldList.addAll(Arrays.asList(getClass().getDeclaredFields()));

        for (Field field : fieldList) {
            field.setAccessible(true);
            if (field.getType().isAssignableFrom(OColumn.class)) {
                try {
                    OColumn column = (OColumn) field.get(this);
                    if (column.name == null)
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
        Long id = null;
        if (getUri() != null) {
            Uri uri = mContext.getContentResolver().insert(getUri(), values);
            return Integer.parseInt(uri.getLastPathSegment());
        } else {
            SQLiteDatabase database = getWritableDatabase();
            id = database.insert(getTableName(), null, values);
            database.close();
            return id.intValue();
        }
    }

    public int update(ContentValues values, String where, String... args) {
        SQLiteDatabase database = getReadableDatabase();
        int id = database.update(getTableName(), values, where, args);
        database.close();
        return id;
    }

    public void deleteAll() {
        delete(null);
    }

    public int delete(String where, String... args) {
        LocalRecordState recordState = new LocalRecordState(mContext);
        List<Integer> serverIds = selectServerIds(where, args);
        recordState.addDeleted(getModelName(), serverIds);

        SQLiteDatabase database = getWritableDatabase();
        int id = database.delete(getTableName(), where, args);
        database.close();
        return id;
    }

    public int deleteAll(List<Integer> serverIds) {
        SQLiteDatabase database = getWritableDatabase();
        int id = database.delete(getTableName(), "id in (" + TextUtils.join(",", serverIds) + ")", null);
        database.close();
        return id;
    }

    public int delete(int row_id, boolean permenent) {
        SQLiteDatabase database = getWritableDatabase();
        int id = database.delete(getTableName(), "_id = ?", new String[]{row_id + ""});
        database.close();
        return id;
    }

    public int delete(int row_id) {
        return delete("_id = ?", row_id + "");
    }

    public int count() {
        int count = 0;
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT COUNT(*) AS TOTAL FROM " + getTableName(), null);

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        database.close();
        return count;
    }

    public ListRow browse(int row_id) {
        List<ListRow> rows = select("_id = ?", new String[]{row_id + ""});
        return rows.isEmpty() ? null : rows.get(0);
    }

    public List<ListRow> select() {
        return select(null, null);
    }

    public List<Integer> selectRowIds(List<Integer> serverIds) {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(getTableName(), new String[]{"_id"}, "id in ( " + TextUtils.join(",", serverIds) + ")",
                null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ids.add(cursor.getInt(0));
            } while (cursor.moveToNext());
            cursor.close();
        }
        database.close();
        return ids;
    }

    public int selectRowId(int server_id) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(getTableName(), new String[]{"_id"}, "id = ? ",
                new String[]{server_id + ""}, null, null, null);
        int row_id = INVALID_ROW_ID;
        if (cursor.moveToFirst()) {
            row_id = cursor.getInt(0);
        }
        database.close();
        cursor.close();
        return row_id;
    }

    public List<Integer> selectRowIds(String relColumn, String where, String... args) {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(getTableName(), new String[]{relColumn}, where, args,
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ids.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        database.close();
        cursor.close();
        return ids;
    }

    public List<Integer> selectServerIds(String where, String... args) {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(getTableName(), new String[]{"id"}, where, args,
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ids.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        database.close();
        cursor.close();
        return ids;
    }

    public List<ListRow> select(String where, String[] args) {
        return select(null, null, where, where != null && args == null ? new String[]{} : args);
    }

    public List<ListRow> select(String[] projection, String orderBy, String where, String... args) {
        List<ListRow> rows = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        args = args != null && args.length > 0 ? args : null;
        orderBy = orderBy == null ? "id " : orderBy;
        Cursor cursor = database.query(getTableName(), projection, where, args, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                rows.add(new ListRow(this, cursor));
            } while (cursor.moveToNext());
        }

        database.close();
        cursor.close();
        return rows;
    }

    public String[] getServerColumns() {
        List<String> serverColumns = new ArrayList<>();
        for (OColumn column : getColumns()) {
            if (!column.isLocal) {
                serverColumns.add(column.name);
            }
        }
        serverColumns.add("write_date");
        return serverColumns.toArray(new String[serverColumns.size()]);
    }

    public int updateOrCreate(ContentValues values, String where, String... args) {
        List<ListRow> records = select(where, args);
        if (records.size() > 0) {
            ListRow row = records.get(0);
            update(values, where, args);
            return row.getInt(_ID);
        } else {
            return create(values);
        }
    }

    public ContentProviderResult[] batchInsert(List<ContentValues> values) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        for (ContentValues value : values) {
            operations.add(ContentProviderOperation.newInsert(getUri())
                    .withValues(value).withYieldAllowed(true).build());
        }
        try {
            return mContext.getContentResolver().applyBatch(getAuthority(), operations);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void batchUpdate(List<ContentValues> values) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        for (ContentValues value : values) {
            operations.add(ContentProviderOperation
                    .newUpdate(Uri.withAppendedPath(getUri(), value.get("_id") + ""))
                    .withValues(value)
                    .withYieldAllowed(true)
                    .build());
        }
        try {
            mContext.getContentResolver().applyBatch(getAuthority(), operations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OModel createModel(String modelName) {
        return ModelRegistry.getModel(mContext, modelName);
    }

    /**
     * Sets last sync date to current date time
     */
    public void updateLastSyncDate() {
        IrModel model = new IrModel(mContext);
        ContentValues values = new ContentValues();
        values.put("model", getModelName());
        values.put("last_sync_on", ODateUtils.getUTCDateTime());
        model.updateOrCreate(values, "model = ?", getModelName());
    }

    public String getLastSyncDate() {
        IrModel model = new IrModel(mContext);
        List<ListRow> items = model.select("model = ?", new String[]{getModelName()});
        if (!items.isEmpty()) {
            return items.get(0).getString("last_sync_on");
        }
        return null;
    }

    public List<Integer> getServerIds() {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(getTableName(), new String[]{"id"}, "id != ?", new String[]{"0"}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ids.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        database.close();
        cursor.close();
        return ids;
    }

    public ODomain syncDomain() {
        return new ODomain();
    }

    public boolean isEmpty() {
        return count() <= 0;
    }

    public SyncAdapter getSyncAdapter() {
        return new SyncAdapter(mContext, true, this);
    }

    public Context getContext() {
        return mContext;
    }

//    public String databaseLocalPath() {
//        Application app = (Application) mContext.getApplicationContext();
//        return app.getDatabasePath(getDatabaseName());
//        return Environment.getDataDirectory().getPath() +
//                "/data/" + app.getPackageName() + "/databases/" + getDatabaseName();
//    }

    public void exportDB() {
        FileChannel source;
        FileChannel destination;
//        String currentDBPath = databaseLocalPath();
        String backupDBPath = OStorageUtils.getDirectoryPath("file")
                + "/" + getDatabaseName();
        File currentDB = getContext().getDatabasePath(getDatabaseName());
        File backupDB = new File(backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            String subject = "Database Export: " + getDatabaseName();
            Uri uri = Uri.fromFile(backupDB);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.setType("message/rfc822");
            mContext.startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName(int row_id) {
        ListRow row = browse(row_id);
        return row != null ? row.getString("name") : "false";
    }

    public OUser getUser() {
        return OUser.current(mContext);
    }

    public void syncData() {
        SyncAdapter adapter = new SyncAdapter(mContext, true, this);
        adapter.onPerformSync(getUser().getAccount(), new Bundle(), getAuthority(), null, new SyncResult());
    }

}
