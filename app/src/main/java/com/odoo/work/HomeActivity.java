package com.odoo.work;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.rpc.helper.ORecordValues;
import com.odoo.core.rpc.helper.OdooFields;
import com.odoo.core.rpc.helper.utils.gson.OdooRecord;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.rpc.listeners.OdooError;
import com.odoo.core.rpc.listeners.OdooResponse;
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

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends OdooActivity implements OListAdapter.OnNewViewInflateListener, OListAdapter.OnViewBindInflateListener,
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private ProjectTeams projectTeams;
    private ProjectProject projectProject;
    private OListAdapter adapter;
    private EditText editText;
    Odoo odoo;
    Spinner spinner;

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
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                alertDialog.setTitle(R.string.title_create_project);
                LayoutInflater li = LayoutInflater.from(this);
                final View promptsView = li.inflate(R.layout.create_project_view, null);
                spinner = (Spinner) promptsView.findViewById(R.id.spinner);
                editText = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                List<String> teams = new ArrayList<>();
                teams.add("No team");
                List<ListRow> rows = projectTeams.select();
                if (rows != null) {
                    for (ListRow row : rows) {
                        teams.add(row.getString("name"));
                    }
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, teams) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView lbl = (TextView) super.getView(position, convertView, parent);
                        lbl.setText(getItem(position));
                        return lbl;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        TextView lbl = (TextView) super.getView(position, convertView, parent);
                        lbl.setText(getItem(position));
                        return lbl;
                    }
                };
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);
                spinner.setOnItemSelectedListener(this);
                alertDialog.setView(promptsView);
                alertDialog.setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        if (isValid()) {

                                            Log.e(">>>", spinner.getSelectedItem().toString());
                                            addproject(editText.getText().toString(),spinner.getSelectedItem().toString());
                                        }else
                                        {
                                            Toast.makeText(HomeActivity.this, "Project Name is Empty", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                alertDialog.show();
                break;
        }

    }

    private void addproject(String projectname, String teamname) {
        final Double[] temp_team_id = new Double[1];
        OdooFields fields = new OdooFields("id");
        ODomain domain = new ODomain();
        domain.add("name", "=",teamname);
        odoo.searchRead("project.teams", fields, domain, 0, 0, null, new OdooResponse() {

            @Override
            public void onResponse(OdooResult response) {
                Log.e(">>>res ", response+"");
                List<OdooRecord> records = response.getRecords();
                for (OdooRecord record : records) {
                temp_team_id[0] = record.getDouble("id");
                }


            }
        });

        ORecordValues values = new ORecordValues();
        values.put("name", projectname);
        values.put("team_id", temp_team_id[0]);
        odoo.createRecord("project.project", values, new OdooResponse() {

            @Override
            public void onResponse(OdooResult response) {
                Log.e(">>>>Sucess",response+" ");
            }

            @Override
            public void onError(OdooError error) {
                super.onError(error);
                Log.e("error ", error+"");
            }
        });
        
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String teamName = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(), "Selected: " + teamName, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private boolean isValid() {

        if (editText.getText().toString().trim().isEmpty()) {
            return false;
        }
        return true;
    }
}
