package com.odoo.work.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CBind {

    public static void setText(TextView textView, String value) {
        if (textView != null) {
            textView.setText(value);
        }
    }

    public static void setImage(View view, Bitmap bitmap) {
        if (view instanceof ImageView && bitmap != null) {
            ((ImageView) view).setImageBitmap(bitmap);
        }
    }

    public static void setImage(View view, int res_id) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(res_id);
        }
    }
}
