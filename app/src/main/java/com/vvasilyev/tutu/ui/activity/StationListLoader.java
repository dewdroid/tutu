package com.vvasilyev.tutu.ui.activity;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

/**
 *
 */
class StationListLoader extends AsyncTaskLoader<Cursor> {

    private QueryProvider queryProvider;

    private StationPickerActivity.Mode db;

    public StationListLoader(Context context, QueryProvider queryProvider, StationPickerActivity.Mode db) {
        super(context);
        this.queryProvider = queryProvider;
        this.db = db;
    }

    @Override
    public Cursor loadInBackground() {
        return db.find(queryProvider.query());
    }

    interface QueryProvider {
        String query();
    }
}
