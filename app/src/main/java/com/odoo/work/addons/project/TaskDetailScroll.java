package com.odoo.work.addons.project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.odoo.work.R;
import com.odoo.work.addons.project.models.ProjectProject;
import com.odoo.work.addons.project.models.ProjectTask;
import com.odoo.work.core.support.OdooActivity;
import com.odoo.work.orm.data.ListRow;
import com.odoo.work.utils.BitmapUtils;
import com.odoo.work.utils.CBind;

public class TaskDetailScroll extends OdooActivity implements View.OnClickListener {

    private static final java.lang.String KEY_TASK_ID = "id";
    private ProjectProject projectProject;
    private ProjectTask projectTask;
    private Bundle extra;
    private ListRow projectData;
    private EditText taskDesc;
    private ImageView taskUser;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail_scroll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        taskDesc = (EditText) findViewById(R.id.taskDesc);
        taskUser = (ImageView) findViewById(R.id.taskUser);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        extra = getIntent().getExtras();
        projectProject = new ProjectProject(this);
        projectTask = new ProjectTask(this);
        projectData = projectTask.browse(Integer.parseInt(extra.getString(KEY_TASK_ID)));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            taskDesc.setText(Html.fromHtml(projectData.getString("description"), Html.FROM_HTML_MODE_LEGACY));
        } else {
            taskDesc.setText(Html.fromHtml(projectData.getString("description")));
        }
        taskDesc.setFocusable(false);
        taskDesc.setOnClickListener(this);
        taskDesc.setCursorVisible(false);
        taskDesc.setBackgroundColor(Color.TRANSPARENT);
        CBind.setText((TextView) findViewById(R.id.taskUserName), projectData.getM2O("user_id").getString("name"));


        if(projectData.getM2O("user_id").getString("image_medium").equals("false"))
        {   Bitmap image = BitmapFactory.decodeResource(getResources(),
                R.drawable.user_profile);
            image = Bitmap.createScaledBitmap(image, 150, 150, true);
          taskUser.setImageBitmap(image);


        }
        else {
            CBind.setImage(taskUser, BitmapUtils.getBitmapImage(this, projectData.getM2O("user_id").getString("image_medium")));
        }
        CBind.setText((TextView) findViewById(R.id.taskProject),projectData.getM2O("project_id").getString("name"));
        CBind.setText((TextView) findViewById(R.id.taskStage),projectData.getM2O("stage_id").getString("name"));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        toolbarLayout.setTitle(projectData.getString("name"));
        if(Integer.parseInt(projectData.getString("priority"))==0)
        {
            fab.setImageResource(R.drawable.ic_star_border);
        }
        else
        {
            fab.setImageResource(R.drawable.ic_star_filled);
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
                taskDesc.setCursorVisible(true);
                taskDesc.setFocusableInTouchMode(true);
                break;
            }
        }
    }
}
