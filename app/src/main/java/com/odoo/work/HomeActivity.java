package com.odoo.work;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.odoo.work.addons.project.models.ProjectProject;
import com.odoo.work.addons.project.models.ProjectTeams;
import com.odoo.work.core.support.OdooActivity;
import com.odoo.work.orm.OListAdapter;
import com.odoo.work.orm.data.ListRow;
import com.odoo.work.orm.sync.OSyncUtils;
import com.odoo.work.utils.CBind;
import com.odoo.work.utils.OAppBarUtils;

public class HomeActivity extends OdooActivity implements OListAdapter.OnNewViewInflateListener, OListAdapter.OnViewBindInflateListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private ProjectTeams projectTeams;
    private ProjectProject projectProject;
    private OListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        OAppBarUtils.setHasAppBar(this, false);
        setTitle(R.string.title_dashboard);
        init();
    }

    private void init() {
        projectTeams = new ProjectTeams(this);
        projectProject = new ProjectProject(this);
        ListView teamsWithProjects = (ListView) findViewById(R.id.teamsWithProjects);
        adapter = new OListAdapter(this, null, R.layout.dashboard_item_view);
        adapter.setNewViewInflateListener(this);
        adapter.setViewBindInflateListener(this);
        teamsWithProjects.setAdapter(adapter);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onNewView(Context context, Cursor cursor, ViewGroup parent) {
        boolean is_team = cursor.getString(cursor.getColumnIndex("is_team")).equals("true");
        return LayoutInflater.from(context).inflate(
                is_team ? R.layout.dashboard_team_item_view : R.layout.dashboard_item_view,
                parent, false);
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ListRow row) {
        CBind.setText((TextView) view.findViewById(R.id.display_name), row.getString("display_name"));
        if (!row.getBoolean("is_team")) {
            view.findViewById(R.id.projectColor)
                    .setBackgroundColor(Color.parseColor(ProjectProject.COLORS[row.getInt("color")]));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, projectProject.getDashboardUri(), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        OSyncUtils.get(this, projectProject).sync(new Bundle());
    }
}
