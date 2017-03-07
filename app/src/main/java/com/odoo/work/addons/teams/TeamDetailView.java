package com.odoo.work.addons.teams;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.odoo.work.R;
import com.odoo.work.addons.teams.models.ProjectTeams;
import com.odoo.work.core.support.OdooActivity;
import com.odoo.work.orm.data.ListRow;
import com.odoo.work.utils.OAppBarUtils;

public class TeamDetailView extends OdooActivity {

    public static final String KEY_TEAM_ID = "team_id";
    private ProjectTeams teams;
    private ListRow teamData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);
        OAppBarUtils.setHasAppBar(this, true);
        teams = new ProjectTeams(this);
        teamData = teams.browse(getIntent().getExtras().getInt(KEY_TEAM_ID));
        setTitle(teamData.getString("name"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_team_detail, menu);
        return super.onCreateOptionsMenu(menu);
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
}
