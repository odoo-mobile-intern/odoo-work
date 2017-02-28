package com.odoo.work.utils;

import android.widget.TextView;

public class CBind {

    public static void setText(TextView textView, String value) {
        if (textView != null) {
            textView.setText(value);
        }
    }
}
