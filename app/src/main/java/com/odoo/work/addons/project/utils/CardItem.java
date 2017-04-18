package com.odoo.work.addons.project.utils;

import com.odoo.work.orm.data.ListRow;

public class CardItem {
    public String title;
    public ListRow data;

    public CardItem(String title, ListRow data) {
        this.title = title;
        this.data = data;
    }
}
