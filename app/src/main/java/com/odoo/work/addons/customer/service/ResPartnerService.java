package com.odoo.work.addons.customer.service;

import android.content.Context;

import com.odoo.work.addons.customer.model.ResPartner;
import com.odoo.work.orm.OModel;
import com.odoo.work.orm.sync.SyncService;

public class ResPartnerService extends SyncService {
    public static final String TAG = ResPartnerService.class.getSimpleName();

    @Override
    public OModel getModel(Context context) {
        return new ResPartner(context);
    }
}
