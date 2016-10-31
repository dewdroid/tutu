package com.vvasilyev.tutu.ui.activity;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.vvasilyev.tutu.model.Station;

/**
 *
 */
public class StationLoader extends AsyncTaskLoader<Station>{

    private StationPickerActivity.Mode db;
    private long id;

    public StationLoader(Context context,  StationPickerActivity.Mode db, long id) {
        super(context);
        this.db = db;
        this.id = id;
    }

    @Override
    public Station loadInBackground() {
        return db.get(id);
    }
}
