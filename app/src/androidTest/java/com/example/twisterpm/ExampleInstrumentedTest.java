package com.example.twisterpm;

import android.content.Context;

import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.List;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule<>(AllMessagesActivity.class);
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.twisterpm", appContext.getPackageName());
    }

    @Test
    public void addMessageButtonTest() {

        onView(withId(R.id.action_add)).perform(click());
        onView(withId(R.id.allMessagesAddLayout)).check(matches(isDisplayed()));
    }
    @Test
    public void profilButtonTest(){
        onView(withId(R.id.action_profile)).perform(click());
        onView(withId(R.id.ProfileText)).check(matches(isDisplayed()));
    }


    @Test
     public void backBtnTest(){
         onView(withId(R.id.fab)).perform(click());
         onView(withId(R.id.mainSignInbtn)).check(matches(isDisplayed()));
         // below I am checking the press back of the phone not the method
         pressBack();
     }

     @Test
     // test if recyclerview comes into view when the activity is launched
    public void ListOfMessages_Visible_onLaunch(){
        onView(withId(R.id.messageRecyclerView)).check(matches(isDisplayed()));
    }
}