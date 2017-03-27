package com.odoo.work;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.rpc.helper.ORecordValues;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.support.OUser;
import com.odoo.work.addons.project.models.ProjectProject;
import com.odoo.work.addons.teams.TeamDetailView;
import com.odoo.work.addons.teams.models.ProjectTeams;
import com.odoo.work.core.support.OdooActivity;
import com.odoo.work.orm.OListAdapter;
import com.odoo.work.orm.data.ListRow;
import com.odoo.work.orm.sync.OSyncUtils;
import com.odoo.work.utils.CBind;
import com.odoo.work.utils.OAppBarUtils;

import java.util.List;

public class HomeActivity extends OdooActivity implements OListAdapter.OnNewViewInflateListener, OListAdapter.OnViewBindInflateListener,
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private ProjectTeams projectTeams;
    private ProjectProject projectProject;
    private OListAdapter adapter;
    private Odoo odoo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        OAppBarUtils.setHasAppBar(this, false);
        setTitle(R.string.title_dashboard);
        try {
            odoo = Odoo.createWithUser(this, OUser.current(this));
        } catch (OdooVersionException e) {
            e.printStackTrace();
        }
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
        findViewById(R.id.addNewProject).setOnClickListener(this);

    }

    @Override
    public View onNewView(Context context, Cursor cursor, ViewGroup parent) {
        boolean is_team = cursor.getString(cursor.getColumnIndex("is_team")).equals("true");
        return LayoutInflater.from(context).inflate(
                is_team ? R.layout.dashboard_team_item_view : R.layout.dashboard_item_view,
                parent, false);
    }

    @Override
    public void onViewBind(View view, Cursor cursor, final ListRow row) {
        CBind.setText((TextView) view.findViewById(R.id.display_name), row.getString("display_name"));
        if (!row.getBoolean("is_team")) {
            view.findViewById(R.id.projectColor)
                    .setBackgroundColor(Color.parseColor(ProjectProject.COLORS[row.getInt("color")]));
        } else if (view.findViewById(R.id.teamDetailView) != null) {
            view.findViewById(R.id.teamDetailView).setVisibility((row.getInt("_id") == -99) ? View.GONE : View.VISIBLE);
            view.findViewById(R.id.teamDetailView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailView = new Intent(HomeActivity.this, TeamDetailView.class);
                    detailView.putExtra(TeamDetailView.KEY_TEAM_ID, row.getInt(BaseColumns._ID));
                    startActivity(detailView);
                }
            });
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
        OSyncUtils.get(this, new ProjectTeams(this)).sync(new Bundle());
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addNewProject:
                createNewProject();
                break;
        }

    }

    private void createNewProject() {
        final View promptsView = LayoutInflater.from(this).inflate(R.layout.create_project_view, null);
        final Spinner spinner = (Spinner) promptsView.findViewById(R.id.spinner);
        final EditText editText = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        final List<ListRow> teams = projectTeams.select();
        ListRow noTeam = new ListRow();
        noTeam.put("_id", -1);
        noTeam.put("name", getString(R.string.label_no_team));
        teams.add(0, noTeam);
        ArrayAdapter<ListRow> dataAdapter = new ArrayAdapter<ListRow>(this, android.R.layout.simple_list_item_1, teams) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ListRow item = getItem(position);
                convertView = super.getView(position, convertView, parent);
                CBind.setText((TextView) convertView, item.getString("name"));
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return getView(position, convertView, parent);
            }
        };
        spinner.setAdapter(dataAdapter);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.title_create_project);
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.label_create,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!editText.getText().toString().trim().isEmpty()) {

                            int team_id = spinner.getSelectedItemPosition() > 0 ?
                                    teams.get(spinner.getSelectedItemPosition()).getInt("id") : 0;

                            addProject(editText.getText().toString(), team_id);
                        } else {
                            Toast.makeText(HomeActivity.this, R.string.toast_project_name_required, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialog.setNegativeButton(android.R.string.cancel, null);
        alertDialog.show();
    }

    private void addProject(final String projectName, final int team_id) {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(HomeActivity.this);
                dialog.setCancelable(false);
                dialog.setMessage(getString(R.string.msg_creating_project));
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                ORecordValues values = new ORecordValues();
                values.put("name", projectName);
                values.put("team_id", team_id);
                OdooResult result = odoo.createRecord(projectProject.getModelName(), values);
                if (result.containsKey("result")) {
                    projectProject.syncData();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                dialog.dismiss();
                getSupportLoaderManager().restartLoader(0, null, HomeActivity.this);
            }
        }.execute();
    }
}
