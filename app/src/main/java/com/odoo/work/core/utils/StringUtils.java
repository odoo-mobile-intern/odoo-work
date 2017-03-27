package com.odoo.work.core.utils;

import android.text.Html;

public class StringUtils {
    public static String htmlToString(String html) {

        return Html.fromHtml(
                html.replaceAll("\\<.*?\\>", "").replaceAll("\n", "")
                        .replaceAll("\t", " ")).toString();
    }
}