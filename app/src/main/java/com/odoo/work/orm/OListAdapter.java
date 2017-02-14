/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p>
 * Created on 24/1/17 2:35 PM
 */
package com.odoo.work.orm;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public class OListAdapter extends CursorAdapter {
    public static final String TAG = OListAdapter.class.getSimpleName();
    private OnNewViewInflateListener newViewInflateListener;
    private OnViewBindInflateListener viewBindInflateListener;
    private int resId;
    private Context mContext;

    public OListAdapter(Context context, Cursor c, int layout) {
        super(context, null, layout);
        this.resId = layout;
        mContext = context;
    }

    public int getResource() {
        return resId;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        if (newViewInflateListener != null) {
            return newViewInflateListener.onNewView(context, cursor, viewGroup);
        }
        return LayoutInflater.from(mContext).inflate(getResource(), viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (viewBindInflateListener != null) {
            ListRow row = new ListRow(cursor);
            viewBindInflateListener.onViewBind(view, cursor, row);
        }

    }

    public void setNewViewInflateListener(OnNewViewInflateListener listener) {
        newViewInflateListener = listener;
    }

    public void setViewBindInflateListener(OnViewBindInflateListener bindListener) {
        viewBindInflateListener = bindListener;
    }

    public interface OnNewViewInflateListener {
        public View onNewView(Context context, Cursor cursor, ViewGroup parent);
    }

    public interface OnViewBindInflateListener {
        public void onViewBind(View view, Cursor cursor, ListRow row);
    }

}
