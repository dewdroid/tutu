package com.vvasilyev.tutu.ui.activity;


import android.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvasilyev.tutu.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StationDetailsFragment extends DialogFragment {


    public StationDetailsFragment() {
        // Required empty public constructor
    }

    static StationDetailsFragment newInstance() {
        StationDetailsFragment f = new StationDetailsFragment();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        getDialog().setTitle("Информация о станции");
        return inflater.inflate(R.layout.fragment_station_data, container, false);
    }

}
