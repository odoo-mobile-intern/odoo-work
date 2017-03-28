package com.odoo.work.utils;

import android.graphics.Bitmap;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
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

    public static void setSpannableText(View view, Spanned value) {
        if (view instanceof TextView) {
            ((TextView) view).setText(value);
        }
        if (view instanceof EditText) {
            ((EditText) view).setText(value);
        }
    }
}
