

package com.vvasilyev.tutu.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.view.Menu;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.vvasilyev.tutu.service.ServiceProvider;
import com.vvasilyev.tutu.service.SearchService;
import com.vvasilyev.tutu.R;
import com.vvasilyev.tutu.model.Station;
import com.vvasilyev.tutu.model.StationType;
import com.vvasilyev.tutu.ui.adapter.StationAdapter;

/**
 *  Activity
 */
public class StationPickerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,StationListLoader.QueryProvider {

    public static final String STATION_EXTRA = "station";
    public static final String MODE_EXTRA = "mode";
    public static final String ID_EXTRA = "id";

    public static final int PICK_DEPARTING_CODE = 101;
    public static final int PICK_DESTINATION_CODE = 102;

    private static final int STATION_LIST_LOADER = 101;
    private static final int STATION_DATA_LOADER = 102;

    private SearchService db = ServiceProvider.instance().provideSearchService(this);

    private StationAdapter adapter;

    private ListView listView;

    private ContentLoadingProgressBar progress;

    private TextView notFound;

    private String query;

    private ModeFactory modeFactory = new ModeFactory(db);

    private Mode mode;

    public boolean ready;

    private LoaderManager.LoaderCallbacks<Station> callback = new LoaderManager.LoaderCallbacks<Station>() {
        @Override
        public Loader<Station> onCreateLoader(int id, Bundle args) {
            return new StationLoader(getApplicationContext(), mode, args.getLong(ID_EXTRA));
        }

        @Override
        public void onLoadFinished(Loader<Station> loader, Station station) {
            View dialogView = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.fragment_station_data, null);
            ((TextView) dialogView.findViewById(R.id.station_data_name)).setText(station.name);
            ((TextView) dialogView.findViewById(R.id.station_data_city)).setText(station.city);
            ((TextView) dialogView.findViewById(R.id.station_data_country)).setText(station.country);
            ((TextView) dialogView.findViewById(R.id.station_data_region)).setText(station.region);

            dialog.dismiss();
            new MaterialDialog.Builder(StationPickerActivity.this)
                    .title(R.string.station_data)
                    .customView(dialogView, true)
                    .positiveText(R.string.ok)
                    .show();
        }

        @Override
        public void onLoaderReset(Loader<Station> loader) {

        }
    };
    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickstation);

        mode = modeFactory.get(getIntent().getIntExtra(MODE_EXTRA, PICK_DEPARTING_CODE));

        notFound = (TextView) findViewById(R.id.not_found);
        notFound.setVisibility(View.INVISIBLE);
        progress = (ContentLoadingProgressBar) findViewById(R.id.data_looking_up);
        listView = (ListView) findViewById(R.id.station_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();

                intent.putExtra(STATION_EXTRA, adapter.getStation(position));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getStation(position).getType() == StationType.CITY) {
                    return true;
                }
                dialog = new MaterialDialog.Builder(StationPickerActivity.this)
                        .title(R.string.station_data)
                        .progress(true, 0)
                        .positiveText(R.string.ok).build();

                dialog.show();
                final long stationId = adapter.getStation(position).id;
                Bundle bundle = new Bundle();
                bundle.putLong(ID_EXTRA, stationId);
                getSupportLoaderManager().restartLoader(STATION_DATA_LOADER, bundle, callback).forceLoad();
                return true;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportLoaderManager().initLoader(STATION_LIST_LOADER, null, StationPickerActivity.this);
    }

    public static void start(Activity context, int request) {
        Intent intent = new Intent(context, StationPickerActivity.class);
        intent.putExtra(MODE_EXTRA, request);
        context.startActivityForResult(intent, request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                listView.setVisibility(View.INVISIBLE);
                notFound.setVisibility(View.INVISIBLE);
                progress.show();
                StationPickerActivity.this.query = query;
                getSupportLoaderManager().getLoader(STATION_LIST_LOADER).forceLoad();
                return false;
            }
        });
        searchView.setQueryHint(mode.getQueryHint());
        searchView.onActionViewExpanded();

        return true;
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new StationListLoader(StationPickerActivity.this, StationPickerActivity.this, mode);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {

        progress.hide();
        if (data.getCount() == 0) {
            notFound.setVisibility(View.VISIBLE);
            return;
        }

        if (adapter == null) {
            adapter = new StationAdapter(this, (SearchService.StationCursor) data);
            listView.setAdapter(adapter);
        } else {
            adapter.changeCursor((SearchService.StationCursor) data);
        }

        listView.setVisibility(View.VISIBLE);


        Handler handler = new Handler();
        handler.post(new Runnable() {

            @Override
            public void run() {
                listView.setSelectionAfterHeaderView();
            }
        });

        this.ready = true;
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public String query() {
        return query;
    }

    public interface Mode {

        SearchService.StationCursor find(String query);

        Station get(long id);

        String getQueryHint();
    }

    class ModeFactory {
        private SearchService db;

        ModeFactory(SearchService db) {
            this.db = db;
        }

        public Mode get(int code) {
            if (code == PICK_DEPARTING_CODE) {
                return new DepartingPickMode(db);
            } else if (code == PICK_DESTINATION_CODE) {
                return new DestinationPickMode(db);
            } else {
                throw new IllegalArgumentException("Unknown code: " + code);
            }
        }
    }

    class DepartingPickMode implements Mode {

        private SearchService db;

        DepartingPickMode(SearchService db) {
            this.db = db;
        }

        @Override
        public SearchService.StationCursor find(String query) {
            return db.findDeparting(query);
        }

        @Override
        public Station get(long id) {
            return db.getDepartingStation(id);
        }

        @Override
        public String getQueryHint() {
            return getResources().getString(R.string.departing);
        }
    }

    class DestinationPickMode implements Mode {

        private SearchService db;

        public DestinationPickMode(SearchService db) {
            this.db = db;
        }

        @Override
        public SearchService.StationCursor find(String query) {
            return db.findDestination(query);
        }

        @Override
        public Station get(long id) {
            return db.getDestinationStation(id);
        }

        @Override
        public String getQueryHint() {
            return getResources().getString(R.string.destination);
        }
    }
}
