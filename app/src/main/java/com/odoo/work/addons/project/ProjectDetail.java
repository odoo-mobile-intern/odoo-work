package com.odoo.work.addons.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.rpc.helper.ORecordValues;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.rpc.listeners.OdooResponse;
import com.odoo.core.support.OUser;
import com.odoo.work.R;
import com.odoo.work.addons.project.models.ProjectProject;
import com.odoo.work.addons.project.models.ProjectTask;
import com.odoo.work.addons.project.utils.CardFragmentPagerAdapter;
import com.odoo.work.addons.project.utils.CardItem;
import com.odoo.work.addons.project.utils.CardPagerAdapter;
import com.odoo.work.addons.project.utils.ShadowTransformer;
import com.odoo.work.core.support.OdooActivity;
import com.odoo.work.orm.OListAdapter;
import com.odoo.work.orm.data.ListRow;
import com.odoo.work.utils.BitmapUtils;
import com.odoo.work.utils.CBind;
import com.odoo.work.utils.OAppBarUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ProjectDetail extends OdooActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_PROJECT_ID = "project_id";
    private ViewPager viewPager;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;

    private Bundle extra;
    private ProjectProject projectProject;
    private ProjectTask projectTask;
    private ListRow projectData;

    private HashMap<String, ListRow> stages = new HashMap<>();
    private HashMap<String, OListAdapter> adapters = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_detail);
        OAppBarUtils.setHasAppBar(this, true);
        extra = getIntent().getExtras();
        projectProject = new ProjectProject(this);
        projectTask = new ProjectTask(this);

        projectData = projectProject.browse(extra.getInt(KEY_PROJECT_ID));
        setTitle(projectData.getString("name"));

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        mCardAdapter = new CardPagerAdapter() {

            @Override
            public View getView(ViewGroup container, int position, CardItem item) {
                if (item.data != null)
                    return super.getView(container, position, item);
                return LayoutInflater.from(ProjectDetail.this).inflate(R.layout.kanban_card_new_stage, container, false);
            }

            @Override
            public void bind(final CardItem item, View view) {
                CBind.setText((TextView) view.findViewById(R.id.stageName), item.title);
                if (item.data != null) {
                    OListAdapter adapter = getAdapter(view, item.data);
                    adapters.put("adapter_" + item.data.getInt("_id"), adapter);
                    getSupportLoaderManager().initLoader(item.data.getInt("_id"), null, ProjectDetail.this);
                    view.findViewById(R.id.addNewTask).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(ProjectDetail.this, "Add new task", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    final View newState = (View) view.findViewById(R.id.stageName).getParent();
                    newState.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            final EditText addStage = new EditText(ProjectDetail.this);
                            addStage.setHint("New stage name");
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            addStage.setLayoutParams(lp);
                            AlertDialog.Builder addStageBuilder = new AlertDialog.Builder(ProjectDetail.this);
                            addStageBuilder.setView(addStage, 100, 30, 100, 30);
                            addStageBuilder.setPositiveButton(R.string.label_add, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    createNewStage(addStage.getText().toString().trim());
                                }
                            });
                            addStageBuilder.setNegativeButton(R.string.label_cancel, null);
                            addStageBuilder.show();
                        }
                    });
                }
            }
        };
        bindStages();
        mCardAdapter.addCardItem(new CardItem(getString(R.string.label_add_stage), null));
        mFragmentCardAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager());

        mCardShadowTransformer = new ShadowTransformer(viewPager, mCardAdapter);
        mFragmentCardShadowTransformer = new ShadowTransformer(viewPager, mFragmentCardAdapter);

        viewPager.setAdapter(mCardAdapter);
        viewPager.setPageTransformer(false, mCardShadowTransformer);
        viewPager.setOffscreenPageLimit(3);
    }

    private void bindStages() {
        for (ListRow stage : projectData.getM2M("type_ids")) {
            mCardAdapter.addCardItem(new CardItem(stage.getString("name"), stage));
            stages.put("stage_" + stage.getInt("_id"), stage);
        }
    }

    private void createNewStage(final String stageName) {
        if (!stageName.equals("")) {
            OUser user = OUser.current(this);
            try {
                Odoo odoo = Odoo.createWithUser(this, user);
                ORecordValues values = new ORecordValues();
                values.put("name", stageName);
                values.put("project_ids", Arrays.asList(Arrays.asList(6, false, Arrays.asList(extra.getInt(KEY_PROJECT_ID)))));
                odoo.createRecord("project.task.type", values, new OdooResponse() {
                    @Override
                    public void onResponse(OdooResult response) {
                        Toast.makeText(ProjectDetail.this, "new stage added", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (OdooVersionException e) {
                e.printStackTrace();
            }
        }
    }

    private OListAdapter getAdapter(View view, final ListRow stage) {
        GridView gridView = (GridView) view.findViewById(R.id.pagerGridView);
        final OListAdapter adapter = new OListAdapter(ProjectDetail.this, null, R.layout.project_task_card_view) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(ProjectDetail.this).inflate(R.layout.project_task_card_view, parent, false);
                }
                final ListRow row = new ListRow(projectProject, (Cursor) getItem(position));
                CBind.setText((TextView) convertView.findViewById(R.id.taskTitle), row.getString("name"));
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ProjectDetail.this, TaskDetailScroll.class);
                        intent.putExtra("prj_id",extra.getInt(KEY_PROJECT_ID));
                        intent.putExtra("id", row.getString("_id"));
                        startActivity(intent);
                    }
                });
                switch (row.getString("kanban_state")) {
                    case "normal":
                        convertView.findViewById(R.id.kanban_state).setBackgroundColor(Color.parseColor("#AEAEAE"));
                        break;
                    case "done":
                        convertView.findViewById(R.id.kanban_state).setBackgroundColor(Color.parseColor("#5CB85C"));
                        break;
                    case "blocked":
                        convertView.findViewById(R.id.kanban_state).setBackgroundColor(Color.parseColor("#D9534F"));
                        break;
                }
                ListRow user = row.getM2O("user_id");
                String image = "false";
                if (user != null) {
                    image = row.getM2O("user_id").getString("image_medium");
                }
                if (image.equals("false")) {
                    CBind.setImage(convertView.findViewById(R.id.assigneeAvatar), R.drawable.user_profile);
                } else {
                    CBind.setImage(convertView.findViewById(R.id.assigneeAvatar), BitmapUtils.getBitmapImage(ProjectDetail.this, image));
                }

                ImageView toggle = (ImageView) convertView.findViewById(R.id.priorityToggle);
                if (row.getString("priority").equals("0")) {
                    toggle.setImageResource(R.drawable.ic_star_border);
                    toggle.setColorFilter(null);
                } else {
                    toggle.setImageResource(R.drawable.ic_star_filled);
                    toggle.setColorFilter(Color.parseColor("#FFD700"));
                }
                return convertView;
            }
        };

        gridView.setAdapter(adapter);
        return adapter;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String where = "stage_id = ?";
        String[] selectionArgs = {id + ""};
        return new CursorLoader(this, projectTask.getUri(), null, where, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapters.get("adapter_" + loader.getId()).changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapters.get("adapter_" + loader.getId()).changeCursor(null);
    }
}
