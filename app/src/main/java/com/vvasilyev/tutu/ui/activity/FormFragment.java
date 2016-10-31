package com.vvasilyev.tutu.ui.activity;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.vvasilyev.tutu.R;
import com.vvasilyev.tutu.base.ui.SwitchView;
import com.vvasilyev.tutu.model.SimpleStation;
import com.vvasilyev.tutu.model.StationType;
import com.vvasilyev.tutu.service.ServiceProvider;
import com.vvasilyev.tutu.ui.ToastManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.vvasilyev.tutu.ui.activity.StationPickerActivity.PICK_DEPARTING_CODE;
import static com.vvasilyev.tutu.ui.activity.StationPickerActivity.PICK_DESTINATION_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FormFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    public static final String TAG = "fragment_form";

    SimpleDateFormat FORMAT = new SimpleDateFormat("dd MMMM, EEE");

    SwitchView departingSelector;

    SwitchView destinationSelector;

    Button pickDayButton;

    Button displayScheduleButton;

    Model model = new Model();

    SwitchView.ViewBinder binder = new SwitchView.ViewBinder<SimpleStation>() {
        @Override
        public void bind(View view, SimpleStation value) {
            TextView text1 = ((TextView) view.findViewById(R.id.text1));
            TextView text2 = ((TextView) view.findViewById(R.id.text2));
            if (value.getType() == StationType.CITY) {
                text1.setText(value.city);
                text2.setText(R.string.all_stops);
            } else {
                text1.setText(value.city);
                text2.setText(value.name);
            }
        }
    };
    private ToastManager toastManager = ServiceProvider.instance().provideToastManager();

    public FormFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form, container, false);

        departingSelector = (SwitchView) view.findViewById(R.id.departingSelector);
        departingSelector.setViewBinder(binder);
        departingSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormFragment.this.onDepartingStationRequested();
            }
        });

        destinationSelector = (SwitchView) view.findViewById(R.id.destinationSelector);
        destinationSelector.setViewBinder(binder);
        destinationSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormFragment.this.onDestinationStationRequested();
            }
        });

        pickDayButton = (Button) view.findViewById(R.id.pick_date);
        pickDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getContext(), FormFragment.this,
                        today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH),
                        today.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(today.getTimeInMillis());
                dialog.show();
            }
        });

        Calendar calendar = Calendar.getInstance();
        pickDayButton.setText(FORMAT.format(calendar.getTime()));

        displayScheduleButton = (Button) view.findViewById(R.id.display_schedule);
        displayScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormFragment.this.onScheduleRequested();
            }
        });

        return view;
    }

    private void onScheduleRequested() {
        try {
            if (model.validate()) {
                displaySchedule();
            }
        } catch (Model.SameDepartingAndDestinationException e) {
            toastManager.makeSelectDifferentToast(getContext());
        } catch (Model.DepartingNotProvidedException e) {
            toastManager.makeSelectDepartingToast(getContext());
        } catch (Model.DestinationNotProvidedException e) {
            toastManager.makeSelectDestinationToast(getContext());
        } catch (Exception e) {
            toastManager.makeToast(e.getLocalizedMessage(), getContext());
        }
    }

    private void displaySchedule() throws Exception{
        throw new UnsupportedOperationException(getString(R.string.schedule_not_supported_yet));
    }

    private void onDestinationStationRequested() {
        StationPickerActivity.start(getActivity(), PICK_DESTINATION_CODE);
    }

    private void onDepartingStationRequested() {
        StationPickerActivity.start(getActivity(), PICK_DEPARTING_CODE);
    }


    public void selected(SimpleStation station, int code) {
        if (station == null) {
            throw null;
        }
        if (code == PICK_DESTINATION_CODE) {
            destinationSelector.set(station);
            model.destination = station;
        } else if (code == PICK_DEPARTING_CODE){
            departingSelector.set(station);
            model.departing = station;
        } else {
            throw new IllegalArgumentException("Unknown code: " +code);
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        pickDayButton.setText(FORMAT.format(calendar.getTime()));
        model.departingDay = calendar.getTime();
    }

    /**
     *  Model class to hold fragment state
     */
    static class Model {

        SimpleStation departing;

        SimpleStation destination;

        Date departingDay;

        /**
         *
         * @return
         * @throws SameDepartingAndDestinationException
         * @throws DepartingNotProvidedException
         * @throws DestinationNotProvidedException
         */
        boolean validate()
                throws SameDepartingAndDestinationException, DepartingNotProvidedException, DestinationNotProvidedException {
            if (departing == null) {
                throw new DepartingNotProvidedException();
            }
            if (destination == null) {
                throw new DestinationNotProvidedException();
            }
            if (departing.equals(destination)) {
                throw new SameDepartingAndDestinationException();
            }
            return true;
        }

        /**
         * Base class for exception
         */
        private class ValidationException extends Exception {
        }

        private class SameDepartingAndDestinationException extends ValidationException {
        }

        private class DepartingNotProvidedException extends ValidationException {
        }

        private class DestinationNotProvidedException extends ValidationException {
        }
    }
}
