package com.vvasilyev.tutu.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.vvasilyev.tutu.model.SimpleStation;
import com.vvasilyev.tutu.model.Station;
import com.vvasilyev.tutu.model.StationType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 *  Service providing search functionality
 */
public class SearchService {

    private static final String DB_NAME = "STATION_DICTIONARY";
    private static final int DB_VERSION = 14;
    private static final String STATION_TO_VIRTUAL_TABLE = "STATION_TO";
    private static final String STATION_FROM_VIRTUAL_TABLE = "STATION_FROM";

    private final DatabaseOpenHelper helper;

    public SearchService(Context context) {
        helper = new DatabaseOpenHelper(context);
    }

    public StationCursor getWordMatches(String query, String table) {
        String selection = Column.CITY + " LIKE ?";
        String[] selectionArgs = new String[] {"%" + query + "%"};

        return query(selection, selectionArgs, Column.onlyRequiredForList(), table);
    }

    public StationCursor findDeparting(String query) {
        return getWordMatches(query, STATION_FROM_VIRTUAL_TABLE);
    }

    public StationCursor findDestination(String query) {
        return getWordMatches(query, STATION_TO_VIRTUAL_TABLE);
    }

    /**
     * Search for station with id
     *
     * @param id
     * @return null if not found
     */
    public Station get(long id, String table) {

        StationCursor cursor = query(Column.STATION_ID + " = ?", new String[]{Long.toString(id)}, null, table);
        if (cursor.moveToFirst()) {
            return  Station.stationBuilder()
                    .id(cursor.id())
                    .name(cursor.name())
                    .city(cursor.city())
                    .country(cursor.country())
                    .region(cursor.region())
                    .build();
        } else {
            return null;
        }
    }

    public Station getDepartingStation(long id) {
        return get(id, STATION_FROM_VIRTUAL_TABLE);
    }

    public Station getDestinationStation(long id) {
        return get(id, STATION_TO_VIRTUAL_TABLE);
    }

    private StationCursor query(String selection, String[] selectionArgs, String[] columns, String table) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(table);

        Cursor cursor = builder.query(helper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null,
                Column.CITY + ", " + Column.CITY_ID + ", " + Column.STATION_NAME);

        return new StationCursor(cursor);
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {

        private static final java.lang.String FTS_TABLE_CREATE =
                "CREATE VIRTUAL TABLE %s USING fts3 (" + Column.stringValue() + ")";
        private final Context context;

        private SQLiteDatabase db;

        public DatabaseOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            this.db = db;
            db.execSQL(String.format(FTS_TABLE_CREATE, STATION_FROM_VIRTUAL_TABLE));
            db.execSQL(String.format(FTS_TABLE_CREATE, STATION_TO_VIRTUAL_TABLE));
            loadDictionary();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + STATION_FROM_VIRTUAL_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + STATION_TO_VIRTUAL_TABLE);
            onCreate(db);
        }

        public String loadJSONFromAsset() {
            String json = null;
            try {
                InputStream is = context.getAssets().open("allStations.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            return json;
        }

        private void processCities(JSONArray cityArray, String table) throws JSONException {
            for (int i=0; i < cityArray.length(); i++) {
                JSONObject cityObject = cityArray.getJSONObject(i);
                String city = cityObject.getString("cityTitle");
                String country = cityObject.getString("countryTitle");
                String region = cityObject.getString("regionTitle");
                int cityId = cityObject.getInt("cityId");

                JSONArray cityStations = cityObject.getJSONArray("stations");
                // if there are > 1 station add "All stops" station
                int type = StationType.STATION_SINGLE;
                if (cityStations.length() > 1) {
                    insert(-1, "", city, cityId, country, region, StationType.CITY, table);
                    type = StationType.STATION_IN_CITY;
                }
                for (int j=0; j < cityStations.length(); j++) {
                    JSONObject stationObject = cityStations.getJSONObject(j);
                    String station = stationObject.getString("stationTitle");
                    long id = stationObject.getLong("stationId");
                    insert(id, station, city, cityId, country, region, type, table);
                }

            }
        }

        private void loadDictionary() {
            try {
                JSONObject json = new JSONObject(loadJSONFromAsset());
                processCities(json.getJSONArray("citiesFrom"), STATION_FROM_VIRTUAL_TABLE);
                processCities(json.getJSONArray("citiesTo"), STATION_TO_VIRTUAL_TABLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void insert(long id, String station, String city, int cityId, String country, String region, int type, String table) {
            ContentValues value = new ContentValues();
            value.put(Column.STATION_ID, Long.toString(id));
            value.put(Column.STATION_NAME, station);
            value.put(Column.CITY, city);
            value.put(Column.CITY_ID, cityId);
            value.put(Column.COUNTRY, country);
            value.put(Column.TYPE, type);
            value.put(Column.REGION, region);

            db.insert(table, null, value);
        }
    }

    public static class Column {
        static final String CITY_ID = "CITY_ID";
        static final String TYPE = "TYPE";
        static final String STATION_ID = "STATION_ID";
        static final String STATION_NAME = "STATION_NAME";
        static final String CITY = "CITY";
        static final String COUNTRY = "COUNTRY";
        static final String REGION = "REGION";

        public static String[] all() {
            return new String[] {STATION_ID, STATION_NAME, CITY, CITY_ID, COUNTRY, REGION, TYPE};
        }

        public static String[] onlyRequiredForList() {
            return new String[] {STATION_ID, STATION_NAME, CITY, COUNTRY, TYPE};
        }

        static String stringValue() {
            return Arrays.toString(all()).replace("[", "").replace("]", "");
        }
    }

    public static class StationCursor extends CursorWrapper {

        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public StationCursor(Cursor cursor) {
            super(cursor);
        }

        public SimpleStation get() {
            SimpleStation station = SimpleStation.builder()
                    .id(id())
                    .name(name())
                    .city(city())
                    .country(country())
                    .type(type())
                    .build();

            return station;
        }

        public long id() {
            return getLong(getColumnIndex(Column.STATION_ID));
        }

        private String name() {
            return getString(getColumnIndex(Column.STATION_NAME));
        }

        public String city() {
            return getString(getColumnIndex(Column.CITY));
        }

        private String country() {
            return getString(getColumnIndex(Column.COUNTRY));
        }

        private int type() {
            return getInt(getColumnIndex(Column.TYPE));
        }

        public String region() {
            return getString(getColumnIndex(Column.REGION));
        }
    }

    public static class StationMatrixCursor extends MatrixCursor {

        public StationMatrixCursor() {
            super(Column.onlyRequiredForList());
        }

        public StationMatrixCursor add(SimpleStation station) {
            addRow(new Object[]{station.id, station.name, station.city, station.country, station.getType()});
            return this;
        }

    }
}
