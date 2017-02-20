package com.odoo.work;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.odoo.work.core.support.OdooActivity;
import com.odoo.work.orm.data.ListRow;
import com.odoo.work.orm.OListAdapter;
import com.odoo.work.addons.customer.model.ResPartner;

public class HomeActivity extends OdooActivity implements View.OnClickListener,
        OListAdapter.OnViewBindInflateListener, LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private OListAdapter listAdapter;
    private ResPartner resPartner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        resPartner = new ResPartner(this);

        ListView partner_listView = (ListView) findViewById(R.id.partner_list);
        listAdapter = new OListAdapter(this, null, R.layout.partner_list_item);
        listAdapter.setViewBindInflateListener(this);
        partner_listView.setOnItemClickListener(this);
        partner_listView.setAdapter(listAdapter);

        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onViewBind(View view, Cursor cursor, ListRow row) {

        TextView textPartnerName, textPartnerMail;

        textPartnerName = (TextView) view.findViewById(R.id.partner_name);
        textPartnerMail = (TextView) view.findViewById(R.id.partner_mail);

        textPartnerName.setText(row.getString("name"));
        textPartnerMail.setText(row.getString("email"));

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, resPartner.getUri(), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        listAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        listAdapter.changeCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
