package com.odoo.work.utils;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.odoo.work.R;
import com.odoo.work.core.support.OdooActivity;

public class OAppBarUtils {


    public static void setHasAppBar(OdooActivity activity, boolean setHasHomeUp) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            activity.setSupportActionBar(toolbar);
            if (setHasHomeUp) {
                ActionBar actionBar = activity.getSupportActionBar();
                assert actionBar != null;
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }
}
