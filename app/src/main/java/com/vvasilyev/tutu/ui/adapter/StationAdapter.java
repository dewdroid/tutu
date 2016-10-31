package com.vvasilyev.tutu.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vvasilyev.tutu.R;
import com.vvasilyev.tutu.service.SearchService;
import com.vvasilyev.tutu.model.SimpleStation;
import com.vvasilyev.tutu.model.StationType;

/**
 *
 */
public class StationAdapter extends BaseAdapter {

    private SearchService.StationCursor cursor;

    private Context context;

    public StationAdapter(Context context, SearchService.StationCursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        cursor.moveToPosition(position);
        SimpleStation station = cursor.get();
        return station;
    }

    @Override
    public long getItemId(int position) {
        return getItemId(getStation(position));
    }

    private long getItemId(SimpleStation station) {
        return station.hashCode();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final View view;

        final SimpleStation object = getStation(position);

        int type = getItemViewType(position);
        if (type == StationType.CITY) {
            CityViewHolder holder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_city, parent, false);
                view.setTag(holder = new CityViewHolder(view));
            } else {
                view = convertView;
                holder = (CityViewHolder) convertView.getTag();
            }
            holder.bind(object);

        } else if (type == StationType.STATION_IN_CITY) {
            CityStationViewHolder holder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_station_in_city, parent, false);
                view.setTag(holder = new CityStationViewHolder(view));
            } else {
                view = convertView;
                holder = (CityStationViewHolder) convertView.getTag();
            }
            holder.bind(object);
        } else {
            SingleStationViewHolder holder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_station_single, parent, false);
                view.setTag(holder = new SingleStationViewHolder(view));
            } else {
                view = convertView;
                holder = (SingleStationViewHolder) convertView.getTag();
            }
            holder.bind(object);
        }


        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return ((SimpleStation) getItem(position)).getType();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    public Context getContext() {
        return context;
    }

    public void changeCursor(SearchService.StationCursor cursor) {
        if (this.cursor != null) {
            this.cursor.close();
        }
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    public SimpleStation getStation(int position) {
        return (SimpleStation) getItem(position);
    }

    /**
     * Represents city
     * <p>
     * Displays city name and country name
     * <p>
     * Moscow, Russia
     * All stops
     */
    class CityViewHolder {
        protected final TextView text1;

        protected final TextView text2;

        public CityViewHolder(View view) {
            text1 = (TextView) view.findViewById(android.R.id.text1);

            text2 = (TextView) view.findViewById(android.R.id.text2);
        }

        public void bind(SimpleStation city) {
            text1.setText(city.city + ", " + city.country);
        }
    }

    /**
     * Represents station within country if there are several of them
     * <p>
     * Displays only name of a station
     */
    class CityStationViewHolder {
        private final TextView text1;

        public CityStationViewHolder(View view) {
            text1 = (TextView) view.findViewById(android.R.id.text1);
        }

        public void bind(SimpleStation station) {
            text1.setText(station.name);
        }
    }

    /**
     * Represents station when station is only within a city
     * <p>
     *  London, UK
     *  Victoria Couch Station
     */
    class SingleStationViewHolder extends CityViewHolder {

        public SingleStationViewHolder(View view) {
            super(view);
        }

        public void bind(SimpleStation city) {
            text1.setText(city.name);
            text2.setText(city.city + ", " + city.country);
        }
    }

}
