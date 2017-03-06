package com.odoo.work.addons.project.provider;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.odoo.work.addons.project.models.ProjectProject;
import com.odoo.work.addons.teams.models.ProjectTeams;
import com.odoo.work.orm.data.ListRow;
import com.odoo.work.orm.sync.provider.BaseContentProvider;

import java.util.List;

public class ProjectProvider extends BaseContentProvider {
    public static final int DASHBOARD = 145;
    public static final String TAG = ProjectProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {
        ProjectProject projectProject = new ProjectProject(getContext());
        matcher.addURI(projectProject.getAuthority(), projectProject.getModelName() + "/dashboard_uri", DASHBOARD);
        return super.onCreate();
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String order) {

        int match = matcher.match(uri);
        if (match != DASHBOARD)
            return super.query(uri, projection, selection, selectionArgs, order);

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"_id", "id", "display_name", "is_team", "color"});

        ProjectTeams teams = new ProjectTeams(getContext());
        ProjectProject projectProject = new ProjectProject(getContext());

        // loading personal projects
        List<ListRow> personalProjects = projectProject.select("team_id is NULL");
        if (!personalProjects.isEmpty()) {
            matrixCursor.addRow(new Object[]{"-1", "-1", "Personal", true, null});
            addProjects(personalProjects, matrixCursor);
        }

        for (ListRow row : teams.select()) {
            matrixCursor.addRow(new Object[]{row.getInt("_id"), row.getInt("id"), row.getString("name"), true, null});
            addProjects(projectProject.select("team_id = ?", row.getString("_id")), matrixCursor);
        }

        return new MergeCursor(new Cursor[]{matrixCursor});
    }

    private void addProjects(List<ListRow> projects, MatrixCursor cursor) {
        for (ListRow project : projects) {
            cursor.addRow(new Object[]{project.getInt("_id"), project.getInt("id"), project.getString("name"), false, project.getInt("color")});
        }
    }
}
