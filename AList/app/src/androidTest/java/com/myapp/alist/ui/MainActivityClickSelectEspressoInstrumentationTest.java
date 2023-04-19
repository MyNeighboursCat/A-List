/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.myapp.alist.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// WARNING: this test class should not be run by itself.
// MainActivityClickInsertEspressoInstrumentationTest always needs to be ran first.
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityClickSelectEspressoInstrumentationTest {
    private static final String NAME_1 = "Name 1";
    private static final String DESCRIPTION_1 = "Description 1";
    private static final String CATEGORY_1 = "To Do";
    private static final String STATUS_1 = "Current";
    private static final Context CONTEXT_1 =
            InstrumentationRegistry.getInstrumentation().getTargetContext();

    // This is still needed to start the activity.
    @Rule
    public ActivityScenarioRule<MainActivity> mMainActivityActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testSelectButton_clickSelectButtonAndGetExpectedText() {
        String expectedCurrentSelectText = CONTEXT_1.getString(R.string.select_mode) + ": " +
                CONTEXT_1.getString(R.string.containing);

        //If the ViewAssertion (.check()) fails, Espresso throws an AssertionFailedError.
        onView(withId(R.id.input1EditText))
                .perform(typeText(NAME_1), closeSoftKeyboard())
                .check(matches(withText(NAME_1)));

        onView(withId(R.id.input2EditText))
                .perform(typeText(DESCRIPTION_1), closeSoftKeyboard())
                .check(matches(withText(DESCRIPTION_1)));

        onView(withId(R.id.spinner1)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(CATEGORY_1))).perform(click());
        onView(withId(R.id.spinner1)).check(matches(withSpinnerText(containsString(CATEGORY_1))));

        onView(withId(R.id.spinner2)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(STATUS_1))).perform(click());
        onView(withId(R.id.spinner2)).check(matches(withSpinnerText(containsString(STATUS_1))));

        // No need to do this because automatic search is done when changing details.
        //onView(withId(R.id.action_select)).perform(click());

        onView(withId(R.id.currentSelectTextView))
                .check(matches(withText(expectedCurrentSelectText)));

        onView(withId(R.id.messageTextView)).check(matches(withText("1 " +
                CONTEXT_1.getString(R.string.row_selected))));
    }
}