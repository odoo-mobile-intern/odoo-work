package com.odoo.work;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.odoo.work.core.support.OdooActivity;
import com.odoo.work.orm.data.ListRow;
import com.odoo.work.orm.OListAdapter;
import com.odoo.work.orm.models.ProjectProject;

public class HomeActivity extends OdooActivity implements View.OnClickListener,
        OListAdapter.OnViewBindInflateListener, LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private OListAdapter listAdapter;
    private ProjectProject projectProject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        projectProject = new ProjectProject(this);

        ListView project_listView = (ListView) findViewById(R.id.project_list);
        listAdapter = new OListAdapter(this, null, R.layout.project_list_item);
        listAdapter.setViewBindInflateListener(this);
        project_listView.setOnItemClickListener(this);
        project_listView.setAdapter(listAdapter);

        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onViewBind(View view, Cursor cursor, ListRow row) {

        TextView textPartnerName, textPartnerMail;

        textPartnerName = (TextView) view.findViewById(R.id.project_name);
        textPartnerMail = (TextView) view.findViewById(R.id.project_task);

        textPartnerName.setText(row.getString("name"));
        Log.e(">>>>", row.getString("use_tasks"));

        if (!row.getString("use_tasks").equals("false")) {
            textPartnerMail.setText(row.getString("label_tasks"));
        } else {
            textPartnerMail.setVisibility(View.INVISIBLE);

        }
//

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, projectProject.getUri(), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        listAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        listAdapter.changeCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
