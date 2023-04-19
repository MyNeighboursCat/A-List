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

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.myapp.alist.AppExecutors;
import com.myapp.alist.R;
import com.myapp.alist.db.AListDatabase;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityClickInsertEspressoInstrumentationTest {
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

    // Delete the existing database.
    public MainActivityClickInsertEspressoInstrumentationTest() {
        CONTEXT_1.deleteDatabase(AListDatabase.DATABASE_NAME);
    }

    @Before
    public void waitForDatabaseCreation() throws InterruptedException {
        final AppExecutors appExecutors = new AppExecutors();
        final CountDownLatch latch = new CountDownLatch(1);
        final LiveData<Boolean> databaseCreated = AListDatabase.getInstance(CONTEXT_1, appExecutors)
                .getDatabaseCreated();
        // Run on the main thread to make sure the database is created before continuing.
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                databaseCreated.observeForever(new Observer<Boolean>() {

                    @Override
                    public void onChanged(@Nullable Boolean aBoolean) {
                        if (Boolean.TRUE.equals(aBoolean)) {
                            databaseCreated.removeObserver(this);
                            latch.countDown();
                        }
                    }
                });
            }
        });
        MatcherAssert.assertThat("Database was not initialised",
                latch.await(1, TimeUnit.MINUTES), is(true));
    }

    @Test
    public void testInsertButton_clickInsertButtonAndGetExpectedData() {
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

        onView(withId(R.id.action_insert)).perform(click());

        // Did originally have a Thread.sleep() here but works without.  If have problems in the
        // future, use a CountingTaskExecutorRule.drainTasks() call here and after update of the UI
        // data like the basic app example.  Espresso is supposed to handle these lags automatically
        // anyway?

        // Can't use onData with RecyclerView...
        // See RecyclerViewActions in Google online reference:
        // ViewActions to interact RecyclerView. RecyclerView works differently than AdapterView.
        // In fact, RecyclerView is not an AdapterView anymore, hence it can't be used in
        // combination with onData(Matcher).
        // To use ViewActions in this class use onView(Matcher) with a Matcher that matches your
        // RecyclerView, then perform a ViewAction from this class.
        /*onData(allOf(is(withRowString(ListEntity.NAME, NAME_1)),
                withRowString(ListEntity.DESCRIPTION, DESCRIPTION_1)))
                .check(matches(isDisplayed()));*/

        onView(withId(R.id.currentSelectTextView))
                .check(matches(withText(expectedCurrentSelectText)));

        // WARNING: insert message is now immediately changed to select message because of automatic
        // search when details are changed.
        onView(withId(R.id.messageTextView)).check(matches(withText("1 " +
                CONTEXT_1.getString(R.string.row_selected))));
    }
}
