package com.odoo.work.addons.project.utils;

import android.support.v7.widget.CardView;

/**
 * Created by Vedant on 27-03-2017.
 */

public interface CardAdapter {

    CardView getCardViewAt(int position);

    int getCount();
}