package com.odoo.widget.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class ExpandableListControl extends LinearLayout
        implements ExpandableListOperationListener {
    public static final String TAG = ExpandableListControl.class.getSimpleName();
    private ExpandableListAdapter mAdapter;
    private Context context;

    public ExpandableListControl(Context context) {
        super(context);
        this.context = context;
    }

    public ExpandableListControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public ExpandableListControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void onAdapterDataChange(List items) {
        removeAllViews();
        for (int i = 0; i < items.size(); i++) {
            View view = mAdapter.getView(i, null, this);
            addView(view);
        }
    }

    public <T> ExpandableListAdapter getAdapter(int resource, List<T> objects,
                                                final ExpandableListAdapterGetViewListener listener) {
        mAdapter = new ExpandableListAdapter<T>(context, resource, objects) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(getResource(), parent, false);
                }
                if (listener != null) {
                    return listener.getView(position, convertView, parent, this);
                }
                return convertView;
            }
        };
        mAdapter.setOperationListener(this);
        return mAdapter;
    }


    public abstract static class ExpandableListAdapter<Type> {
        private List<Type> objects = new ArrayList<>();
        private Context context;
        private int resource = android.R.layout.simple_list_item_1;
        private ExpandableListOperationListener<Type> listener;

        public ExpandableListAdapter(Context context, int resource, List<Type> objects) {
            this.context = context;
            this.objects = objects;
            this.resource = resource;
        }

        public abstract View getView(int position, View convertView, ViewGroup parent);

        public void notifyDataSetChanged(List items) {
            objects = items;
            listener.onAdapterDataChange(items);
        }

        public Type getItem(int position) {
            return objects.get(position);
        }

        public void setOperationListener(ExpandableListOperationListener listener) {
            this.listener = listener;
        }

        public int getResource() {
            return resource;
        }

    }

    public interface ExpandableListAdapterGetViewListener {
        View getView(int position, View view, ViewGroup parent, ExpandableListAdapter adapter);
    }


}