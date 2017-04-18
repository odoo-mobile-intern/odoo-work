package com.odoo.work.addons.project;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.rpc.helper.ORecordValues;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.rpc.listeners.OdooResponse;
import com.odoo.core.support.OUser;
import com.odoo.work.R;
import com.odoo.work.addons.project.models.ProjectTask;
import com.odoo.work.addons.project.models.ProjectTaskType;
import com.odoo.work.core.support.OdooActivity;
import com.odoo.work.orm.data.ListRow;
import com.odoo.work.utils.BitmapUtils;
import com.odoo.work.utils.CBind;

import java.util.ArrayList;
import java.util.List;

public class TaskDetailScroll extends OdooActivity implements View.OnClickListener {

    private static final java.lang.String KEY_TASK_ID = "id";
    private ProjectTask projectTask;
    private ProjectTaskType stageType;
    private ListRow projectData;
    private EditText taskDesc, taskTitleName;
    private Menu menu;
    private Bundle extra;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail_scroll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        taskDesc = (EditText) findViewById(R.id.taskDesc);
        taskTitleName = (EditText) findViewById(R.id.taskTitleName);
        ImageView taskUser = (ImageView) findViewById(R.id.taskUser);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        extra = getIntent().getExtras();

        projectTask = new ProjectTask(this);
        stageType = new ProjectTaskType(this);
        projectData = projectTask.browse(Integer.parseInt(extra.getString(KEY_TASK_ID)));
        CBind.setText((TextView) findViewById(R.id.taskTitleName), projectData.getString("name"));
        if (!projectData.getString("description").equals("false")) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                CBind.setText(taskDesc, Html.fromHtml(projectData.getString("description"), Html.FROM_HTML_MODE_LEGACY).toString());
            } else {
                CBind.setText(taskDesc, Html.fromHtml(projectData.getString("description")).toString());
            }
        } else taskDesc.setHint(R.string.hint_task_description);

        disableEditText(taskDesc);
        disableEditText(taskTitleName);
        CBind.setText((TextView) findViewById(R.id.taskUserName), projectData.getM2O("user_id").getString("name"));

        if (projectData.getM2O("user_id").getString("image_medium").equals("false")) {
            Bitmap image = BitmapFactory.decodeResource(getResources(),
                    R.drawable.user_profile);
            image = Bitmap.createScaledBitmap(image, 150, 150, true);
            taskUser.setImageBitmap(image);
        } else
            CBind.setImage(taskUser, BitmapUtils.getBitmapImage(this, projectData.getM2O("user_id").getString("image_medium")));

        CBind.setText((TextView) findViewById(R.id.taskProject), projectData.getM2O("project_id").getString("name"));
        CBind.setText((TextView) findViewById(R.id.taskStage), projectData.getM2O("stage_id").getString("name"));
        findViewById(R.id.projectTaskLayout).setOnClickListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (Integer.parseInt(projectData.getString("priority")) == 0) {
            fab.setImageResource(R.drawable.ic_star_border);
        } else fab.setImageResource(R.drawable.ic_star_filled);

        switch (projectData.getString("kanban_state")) {
            case "normal": {
                findViewById(R.id.taskKanban).setBackgroundColor(Color.parseColor("#AEAEAE"));
                CBind.setText((TextView) findViewById(R.id.taskKanban), "In Progress");
                break;
            }
            case "done": {
                findViewById(R.id.taskKanban).setBackgroundColor(Color.parseColor("#5CB85C"));
                CBind.setText((TextView) findViewById(R.id.taskKanban), "Ready to process");
                break;
            }
            case "blocked": {
                findViewById(R.id.taskKanban).setBackgroundColor(Color.parseColor("#D9534F"));
                CBind.setText((TextView) findViewById(R.id.taskKanban), "Blocked");
                break;
            }
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.taskDesc: {
                enableEditText(taskDesc);
                break;
            }
            case R.id.taskTitleName: {
                enableEditText(taskTitleName);
                break;
            }
            case R.id.projectTaskLayout: {
                alertBoxEnable();
            }
        }
    }

    private void alertBoxEnable() {
        final View promptsView = LayoutInflater.from(this).inflate(R.layout.move_project_task, null);
        final Spinner taskSpinner = (Spinner) promptsView.findViewById(R.id.project_stage);
        final TextView textStageName = (TextView) promptsView.findViewById(R.id.textStageName);
        final ArrayList<String> stages = new ArrayList<>();
        final List<ListRow> stageName = projectTask.select("project_id = ? ", new String[]{projectData.getM2O("project_id").getString("_id")});

        for (ListRow row : stageName) {
            if (row != null && row.getM2O("stage_id") != null) {
                if (!stages.contains(row.getM2O("stage_id").getString("name")))
                    stages.add(row.getM2O("stage_id").getString("name"));
            }
        }
        final ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stages) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = super.getView(position, convertView, parent);
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return getView(position, convertView, parent);
            }
        };
        taskSpinner.setAdapter(dataAdapter1);
        taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String str = dataAdapter1.getItem(position);
                CBind.setText(textStageName, str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.title_move_task);
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.label_move, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                moveTask(textStageName.getText().toString().trim());
            }
        });
        alertDialog.setNegativeButton(android.R.string.cancel, null);
        alertDialog.show();
    }

    private void moveTask(final String stageName) {
        try {
            int task_id = projectTask.getServerId(Integer.parseInt(extra.getString(KEY_TASK_ID)));
            int stage_id = stageType.getServerId(stageName);

            Odoo odoo = Odoo.createWithUser(this, OUser.current(this));
            ORecordValues values = new ORecordValues();
            values.put("stage_id", stage_id);

            odoo.updateRecord("project.task", values, task_id, new OdooResponse() {
                @Override
                public void onResponse(OdooResult response) {
                    CBind.setText((TextView) findViewById(R.id.taskStage), stageName);
                    Toast.makeText(TaskDetailScroll.this, "Task moved to " + stageName,
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (OdooVersionException e) {
            e.printStackTrace();
        }
    }

    public void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setOnClickListener(this);
        editText.setCursorVisible(false);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    public void enableEditText(EditText editText) {
        editText.setCursorVisible(true);
        editText.setFocusableInTouchMode(true);
        menu.findItem(R.id.action_save).setVisible(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_task_save, menu);
        menu.findItem(R.id.action_save).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                if (!taskTitleName.getText().toString().trim().isEmpty()) {
                    ContentValues values = new ContentValues();
                    values.put("description", taskDesc.getText().toString().trim());
                    values.put("name", taskTitleName.getText().toString().trim());
                    projectTask.update(values, "_id = ?", extra.getString(KEY_TASK_ID));
                    finish();
                } else
                    Toast.makeText(this, R.string.toast_task_title_required, Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
