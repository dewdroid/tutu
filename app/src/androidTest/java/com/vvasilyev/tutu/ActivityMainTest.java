package com.vvasilyev.tutu;

import android.app.Fragment;
import android.content.Context;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.DatePicker;

import com.vvasilyev.tutu.service.SearchService;
import com.vvasilyev.tutu.model.SimpleStation;
import com.vvasilyev.tutu.service.ServiceProvider;
import com.vvasilyev.tutu.ui.ToastManager;
import com.vvasilyev.tutu.ui.activity.FormFragment;
import com.vvasilyev.tutu.ui.activity.MainActivity;
import com.vvasilyev.tutu.model.StationType;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.assertion.ViewAssertions.selectedDescendantsMatch;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 *
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ActivityMainTest extends AcitivityTest{

    private ToastManager mockToastManager;
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            mockToastManager = Mockito.mock(ToastManager.class);

            ServiceProvider.instance().use(mockToastManager);
        }
    };

    private Fragment fragment() {
        return rule.getActivity().getFragmentManager().findFragmentByTag(FormFragment.TAG);
    }

    @Test
    public void testContainsFragment() {
        Fragment fragment = fragment();
        assertThat(fragment, is(notNullValue()));
        assertThat(fragment, is(instanceOf(FormFragment.class)));
    }

    @Test
    public void testDefaultDepartureDate() {
        // example 04 July, 1776
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM, EEE");

        Calendar calendar = Calendar.getInstance();
        String text = format.format(calendar.getTime());
        // check
        onView(withId(R.id.pick_date)).check(matches(withText(text)));
    }

    @Test
    public void testIfDepartureDateIsSelectable() {
        // example 04 July, 1776
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM, EEE");
        Calendar calendar = Calendar.getInstance();

        calendar.set(2016, 10, 10);
        onView(withId(R.id.pick_date)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2016, 11, 10));

        // click on OK
        onView(withId(android.R.id.button1)).perform(click());
        // check that selected date was set
        onView(withId(R.id.pick_date)).check(matches(withText(format.format(calendar.getTime()))));
    }

    private void testStationSelecton(int id, SimpleStation station) {
        // click on selector
        onView(withId(id)).check(matches(isClickable()))
                .perform(click());
        // click on available station
        onData(anything()).inAdapterView(withId(R.id.station_list)).atPosition(0).onChildView(withId(android.R.id.text1))
                .perform(click());
        // verify that selected station is displayed
        onView(withId(id)).check(selectedDescendantsMatch(withId(R.id.text2), withText(station.name)));
    }


    @Test
    public void testDepartingSelection() {
        // Mock data
        SimpleStation station = SimpleStation.builder().id(1)
                .name("Grand Central").city("NY").country("United States of America")
                .type(StationType.STATION_SINGLE)
                .build();

        SearchService mock = Mockito.mock(SearchService.class);
        mockSearchService(new SimpleStation[]{station}, mock.findDeparting(Mockito.anyString()), mock);

        testStationSelecton(R.id.departingSelector, station);
    }

    @Test
    public void testDestinationSelection() {
        // Mock data
        SimpleStation station = SimpleStation.builder().id(1)
                .name("Victoria Station").city("Toronto").country("Canada")
                .type(StationType.STATION_SINGLE)
                .build();

        SearchService mock = Mockito.mock(SearchService.class);
        mockSearchService(new SimpleStation[]{station}, mock.findDestination(Mockito.anyString()), mock);

        testStationSelecton(R.id.destinationSelector, station);
    }

    @Test
    public void testDisplayScheduleButton() throws Exception {
        onView(withId(R.id.display_schedule)).check(matches(isClickable())).perform(click());
        Mockito.verify(mockToastManager).makeSelectDepartingToast(Mockito.any(Context.class));
    }
}
