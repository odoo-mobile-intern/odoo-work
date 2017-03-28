package com.odoo.widget.list;

import java.util.List;

public interface ExpandableListOperationListener<Type> {
    void onAdapterDataChange(List<Type> items);
}