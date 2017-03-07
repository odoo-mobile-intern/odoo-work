package com.odoo.work;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.odoo.work.addons.teams.models.ProjectTeams;
import com.odoo.work.core.support.OdooActivity;
import com.odoo.work.core.support.account.DeviceAccountUtils;

public class SplashScreen extends OdooActivity {

    private ProjectTeams teams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_splash_screen);
        teams = new ProjectTeams(this);
        if (DeviceAccountUtils.get(this).hasAnyAccount()) {
            if (teams.count() == 0)
                checkForTeam();
            else {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();
                }
            }, 700);
        }
    }

    private void checkForTeam() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                teams.getSyncAdapter().syncModelData();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (teams.count() <= 0) {
                    startActivity(new Intent(SplashScreen.this, WizardNewTeam.class));
                } else {
                    startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                }
            }
        }.execute();
    }
}
