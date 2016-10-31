package com.vvasilyev.tutu;

import android.support.test.espresso.DataInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.vvasilyev.tutu.service.SearchService;
import com.vvasilyev.tutu.model.SimpleStation;
import com.vvasilyev.tutu.model.Station;
import com.vvasilyev.tutu.ui.activity.StationPickerActivity;
import com.vvasilyev.tutu.model.StationType;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class StationPickerTest extends AcitivityTest{

    @Rule
    public ActivityTestRule<StationPickerActivity> rule = new ActivityTestRule<StationPickerActivity>(StationPickerActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            SearchService mockService = Mockito.mock(SearchService.class);
            mockSearchService(new SimpleStation[]{

                    SimpleStation.builder().id(1)
                            .name("Grand Central").city("NY").country("United States of America")
                            .type(StationType.STATION_SINGLE)
                            .build(),

                    SimpleStation.builder().id(-1)
                            .name("").city("Toronto").country("Canada")
                            .type(StationType.CITY)
                            .build(),

                    SimpleStation.builder().id(2)
                            .name("Coach Terminal").city("Toronto").country("Canada")
                            .type(StationType.STATION_IN_CITY)
                            .build(),

                    SimpleStation.builder().id(3)
                            .name("Victoria Station").city("Toronto").country("Canada")
                            .type(StationType.STATION_IN_CITY)
                            .build(),
            }, mockService.findDeparting(Mockito.anyString()), mockService);
            Mockito.when(mockService.getDepartingStation(1))
                    .thenReturn(Station.stationBuilder().id(1)
                            .name("Grand Central").city("NY").country("United States of America")
                            .region("New York")
                            .build());
        }
    };

    DataInteraction list = onData(anything()).inAdapterView(withId(R.id.station_list));

    @Test
    public void testListPresented() throws Exception {
        onView(withId(R.id.station_list)).check(matches(isDisplayed()));
    }

    @Test
    public void testSingleStationDisplayed() {
        // check that data is displayed
        DataInteraction e;

        e = list.atPosition(0);
        // check if single station is displayed
        e.onChildView(withText("Grand Central")).check(matches(isDisplayed()));
        e.onChildView(withText("NY, United States of America")).check(matches(isDisplayed()));

    }

    @Test
    public void testCityStationDisplayed() {
        DataInteraction e;
        e = list.atPosition(1);

        e.onChildView(withText("Toronto, Canada")).check(matches(isDisplayed()));
        e.onChildView(withText(R.string.all_stops)).check(matches(isDisplayed()));

        e = list.atPosition(2);

        e.onChildView(withText("Coach Terminal")).check(matches(isDisplayed()));
        e.onChildView(withText("Toronto, Canada")).check(doesNotExist());

        e = list.atPosition(3);

        e.onChildView(withText("Victoria Station")).check(matches(isDisplayed()));
        e.onChildView(withText("Toronto, Canada")).check(doesNotExist());
    }

    @Test
    public void testStationData() {
        list.atPosition(0).perform(longClick());
        onView(withId(R.id.station_data_city)).check(matches(withText("NY")));
        onView(withId(R.id.station_data_country)).check(matches(withText("United States of America")));
        onView(withId(R.id.station_data_region)).check(matches(withText("New York")));
        onView(withId(R.id.station_data_name)).check(matches(withText("Grand Central")));
    }

    @Test
    public void testLongClickOnNotStation() {
        list.atPosition(1).perform(longClick());

        onView(withId(R.id.station_data_city)).check(doesNotExist());
        onView(withId(R.id.station_data_country)).check(doesNotExist());
        onView(withId(R.id.station_data_region)).check(doesNotExist());
        onView(withId(R.id.station_data_name)).check(doesNotExist());
    }
}
