package com.grofers.skthinks.my2048;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.grofers.skthinks.my2048.View.SplasherActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    SplasherActivity splasherActivity;

    @Rule
    public ActivityTestRule<SplasherActivity> activityTestRule = new ActivityTestRule<>(SplasherActivity.class);

    @Test
    public void useAppContext() throws Exception {

        splasherActivity = activityTestRule.getActivity();
        // Context of the app under test.
        onView(withId(R.id.btn_animation_screen)).perform(click());

    }
}
