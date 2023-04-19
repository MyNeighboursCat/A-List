/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.myapp.alist.BasicApplication;
import com.myapp.alist.R;
import com.myapp.alist.databinding.ActivityMainBinding;
import com.myapp.alist.databinding.ContentMainBinding;
import com.myapp.alist.databinding.ListRowBinding;
import com.myapp.alist.db.ListEntity;
import com.myapp.alist.viewmodel.ListViewModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Displays a list of inputted items which have a name, description, category and status.
 * <p>
 * Uses a {@link ListViewModel} to query, insert, update and delete rows in a
 * {@link com.myapp.alist.db.AListDatabase}.
 *
 * @author Colin Walters
 * @version 1.0, 17/04/2023
 * @since 1.0
 */
public class MainActivity extends AppCompatActivity {
    /**
     * The key for the delete warning dialog.
     */
    private static final String DELETE_ALERT_DIALOG_TAG = "DELETE_ALERT_DIALOG_TAG";
    /**
     * The key for the bundle used to save the activity state.
     */
    private static final String MAIN_BUNDLE = "MAIN_BUNDLE_";
    /**
     * Contains the column names of the database required for display.
     */
    private static final String[] PROJECTION = new String[]{
            ListEntity._ID,
            ListEntity.NAME,
            ListEntity.DESCRIPTION,
            ListEntity.CATEGORY,
            ListEntity.STATUS
    };
    /**
     * Stores the classname to tag log errors.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * Determines if views are allowed to react to the users actions.
     */
    private boolean mAllowEvents;
    /**
     * The row ID from the database.  Initialise to invalid.
     */
    private volatile long mActivatedID = RecyclerView.NO_ID;
    /**
     * The current input for selecting the name column from the database.
     */
    private String mCurrentSelectInput1 = "";
    /**
     * The current input for selecting the description column from the database.
     */
    private String mCurrentSelectInput2 = "";
    /**
     * The currently selected category column from the database.
     */
    private String mCurrentSelectInput3 = "";
    /**
     * The currently selected status column from the database.
     */
    private String mCurrentSelectInput4 = "";
    /**
     * The currently selected order by column from the database.
     */
    private String mCurrentSelectInput5 = "";
    /**
     * The activity binding to get access to the UI components.
     */
    private ActivityMainBinding mActivityMainBinding = null;
    /**
     * The toolbar.
     */
    private Toolbar mToolbar;
    /**
     * Used to link the database column names to the views in the row layout in the recycler view.
     */
    private RecyclerViewAdapter mRecyclerViewAdapter;
    /**
     * Displays the currently select parameters text.
     */
    private TextView mCurrentSelectTextView;
    /**
     * Displays the message relating to the outcome of the last database call.
     */
    private TextView mMessageTextView;
    /**
     * The name data inputted.
     */
    private EditText mInput1EditText;
    /**
     * The description inputted.
     */
    private EditText mInput2EditText;
    /**
     * The category chosen.
     */
    private Spinner mSpinner1;
    /**
     * The status chosen.
     */
    private Spinner mSpinner2;
    /**
     * The order by chosen.
     */
    private Spinner mSpinner3;
    /**
     * The view model to use to update the database using the data repository.
     */
    private volatile ListViewModel mListViewModel;
    /**
     * The list of database items returned from a query.
     */
    private volatile List<ListEntity> mListEntities;
    /**
     * Displays the rows of the database.
     */
    private RecyclerView mRecyclerView1;
    /**
     * Contains the text to share with another app.
     */
    private Intent mSendIntent = null;
    /**
     * The text to share with another app.
     */
    private StringBuilder mSendText = null;

    /**
     * Restores the state if necessary, sets up the display including initiating the
     * <code>ViewModel</code>.
     *
     * @param savedInstanceState the bundle to be restored.  May be <code>null</code>.
     * @see #onSaveInstanceState(Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        this.setContentView(this.mActivityMainBinding.getRoot());
        this.mToolbar = this.mActivityMainBinding.toolbar;
        this.setSupportActionBar(this.mToolbar);

        ContentMainBinding contentMainBinding = this.mActivityMainBinding.includeContentMain;
        this.mCurrentSelectTextView = contentMainBinding.currentSelectTextView;
        this.mMessageTextView = contentMainBinding.messageTextView;
        this.mInput1EditText = contentMainBinding.input1EditText;
        this.mInput2EditText = contentMainBinding.input2EditText;
        this.mSpinner1 = contentMainBinding.spinner1;
        this.mSpinner2 = contentMainBinding.spinner2;
        this.mSpinner3 = contentMainBinding.spinner3;

        // Make sure only the music stream volume is adjusted.
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // This is needed to allow the EditText's hyperlinks to be both amended and enabled.
        // The EditText autoLink property is set to "web" in the XML layout.
        this.mInput2EditText.setMovementMethod(
                HyperlinkMovementMethod.getHyperlinkMovementMethodInstance());

        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_list, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears.
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        this.mSpinner1.setAdapter(arrayAdapter);

        // Create an ArrayAdapter using the string array and a default spinner layout.
        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.status_list,
                android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears.
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        this.mSpinner2.setAdapter(arrayAdapter);

        // Create an ArrayAdapter using the string array and a default spinner layout.
        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.order_by_list,
                android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears.
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        this.mSpinner3.setAdapter(arrayAdapter);

        this.mRecyclerView1 = contentMainBinding.recyclerView1;
        // Use this setting to improve performance if you know that changes in content do not change
        // the layout size of the RecyclerView.
        this.mRecyclerView1.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.mRecyclerView1.setLayoutManager(linearLayoutManager);

        // Display dividers in between items.
        this.mRecyclerView1.addItemDecoration(new DividerItemDecoration(
                this.mRecyclerView1.getContext(), linearLayoutManager.getOrientation()));

        if (savedInstanceState != null) {
            // Don't use 'counter' as variable name because it increases with each line for some
            // reason!
            int cnt = 0;

            this.mCurrentSelectInput1 = savedInstanceState.getString(MainActivity.MAIN_BUNDLE +
                    cnt++);
            this.mCurrentSelectInput2 = savedInstanceState.getString(MainActivity.MAIN_BUNDLE +
                    cnt++);
            this.mCurrentSelectInput3 = savedInstanceState.getString(MainActivity.MAIN_BUNDLE +
                    cnt++);
            this.mCurrentSelectInput4 = savedInstanceState.getString(MainActivity.MAIN_BUNDLE +
                    cnt++);
            this.mCurrentSelectInput5 = savedInstanceState.getString(MainActivity.MAIN_BUNDLE +
                    cnt++);
            this.showMessage(0, savedInstanceState.getString(
                    MainActivity.MAIN_BUNDLE + cnt++));
            this.mActivatedID = savedInstanceState.getLong(MainActivity.MAIN_BUNDLE +
                    cnt);
            // There is no need to reactivate a position as this is done automatically by Android.
        } else {
            // Store the spinners' default values.
            this.mCurrentSelectInput3 = this.mSpinner1.getSelectedItem().toString();
            this.mCurrentSelectInput4 = this.mSpinner2.getSelectedItem().toString();
            this.mCurrentSelectInput5 = this.mSpinner3.getSelectedItem().toString();
        }
        this.mCurrentSelectTextView.setText(this.setCurrentSelectDetails());

        // StrictMode does not complain about using the the main thread to call ListViewModel and
        // therefore DataRepository constructor which then calls Room to select all rows from the
        // database.
        // DataRepository constructor sets a LiveData to the rows returned from SELECT all.  This
        // appears to be OK on the main thread because using LiveData (see notes in DataRepository
        // constructor).
        // Room throws an exception if database is accessed on the main thread.
        // Room generates all the necessary code to update the LiveData object when a database is
        // updated. The generated code runs the query asynchronously on a background thread when
        // needed.
        // Re-created activities receive the same ViewModel instance created by the first
        // activity so don't clear ViewModel variables in releaseResources.
        mListViewModel = new ViewModelProvider(this).get(ListViewModel.class);
        //noinspection Convert2Lambda
        mListViewModel.getItems().observe(this, new Observer<List<ListEntity>>() {
            @Override
            public void onChanged(@Nullable final List<ListEntity> listEntities) {
                if (listEntities != null) {
                    MainActivity.this.mListEntities = listEntities;

                    MainActivity.this.mRecyclerViewAdapter = new RecyclerViewAdapter(listEntities);
                    MainActivity.this.mRecyclerView1.setAdapter(mRecyclerViewAdapter);

                    final int count = listEntities.size();
                    String message = count + " ";
                    if (count == 1) {
                        message += MainActivity.this.getString(R.string.row_selected);
                    } else {
                        message += MainActivity.this.getString(R.string.rows_selected);
                    }
                    MainActivity.this.showMessage(0, message);

                    // Send text
                    MainActivity.this.mSendText = new StringBuilder();
                    if (count > 0) {
                        for(int i = 0; i < count; i++) {
                            ListEntity listEntity = listEntities.get(i);
                            MainActivity.this.mSendText
                                    .append(listEntity.getName())
                                    .append(", ")
                                    .append(listEntity.getDescription())
                                    .append(", ")
                                    .append(listEntity.getCategory())
                                    .append(", ")
                                    .append(listEntity.getStatus())
                                    .append("\n");
                        }
                    }
                    MainActivity.this.setSendText();

                    MainActivity.this.setViewsStatus(true);
                }
            }
        });

        // If name is changed, redo list.
        this.mInput1EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count,
                                          final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before,
                                      final int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (MainActivity.this.mActivatedID == RecyclerView.NO_ID) {
                    MainActivity.this.select();
                }
            }
        });

        // If description is changed, redo list.
        this.mInput2EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count,
                                          final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before,
                                      final int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (MainActivity.this.mActivatedID == RecyclerView.NO_ID) {
                    MainActivity.this.select();
                }
            }
        });

        // If category spinner item selected, redo list.
        this.mSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int
                    position, final long id) {
                if (MainActivity.this.mActivatedID == RecyclerView.NO_ID) {
                    MainActivity.this.select();
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

            }
        });

        // If status spinner item selected, redo list.
        this.mSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int
                    position, final long id) {
                if (MainActivity.this.mActivatedID == RecyclerView.NO_ID) {
                    MainActivity.this.select();
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

            }
        });

        // If order by spinner item selected, redo list.
        this.mSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int
                    position, final long id) {
                if (MainActivity.this.mActivatedID == RecyclerView.NO_ID) {
                    MainActivity.this.select();
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

            }
        });

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                // These intents are cleared after search has been selected.
                String sharedText = intent.getStringExtra(Intent.EXTRA_SUBJECT);
                if (sharedText != null) {
                    this.mInput1EditText.setText(sharedText);
                    intent.removeExtra(Intent.EXTRA_SUBJECT);
                }

                sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    this.mInput2EditText.setText(sharedText);
                    intent.removeExtra(Intent.EXTRA_TEXT);
                }
            }
        }

        this.setViewsStatus(false);
    }

    /**
     * Enables views' statuses.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Stop more than one action caused by rapid tapping on the same action.  This is needed
        // after returning from Rate, Help and About.
        this.setViewsStatus(true);
    }

    /**
     * Save the required values needed for restore.
     *
     * @param outState contains the values needed for restore
     * @see #onCreate(Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Don't use 'counter' as variable name as it increases with each line for some reason!
        int cnt = 0;

        outState.putString(MainActivity.MAIN_BUNDLE + cnt++,
                this.mCurrentSelectInput1);
        outState.putString(MainActivity.MAIN_BUNDLE + cnt++,
                this.mCurrentSelectInput2);
        outState.putString(MainActivity.MAIN_BUNDLE + cnt++,
                this.mCurrentSelectInput3);
        outState.putString(MainActivity.MAIN_BUNDLE + cnt++,
                this.mCurrentSelectInput4);
        outState.putString(MainActivity.MAIN_BUNDLE + cnt++,
                this.mCurrentSelectInput5);
        outState.putString(MainActivity.MAIN_BUNDLE + cnt++,
                this.mMessageTextView.getText().toString());
        outState.putLong(MainActivity.MAIN_BUNDLE + cnt,
                this.mActivatedID);

        super.onSaveInstanceState(outState);
    }

    /**
     * If finishing the app then release resources.
     *
     * @see #releaseResources()
     */
    @Override
    protected void onDestroy() {
        this.releaseResources();

        super.onDestroy();
    }

    /**
     * If the hardware back key has been pressed and events are allowed then execute the activity
     * <code>onBackPressed()</code>.
     *
     * @see #mAllowEvents
     */
    @Override
    public void onBackPressed() {
        if (this.mAllowEvents) {
            super.onBackPressed();
        }
    }

    /**
     * Sets up the Action Bar options.
     *
     * @param menu the menu create by Android
     * @return the boolean of the activity <code>onCreateOptionsMenu()</code>
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Nested submenus are not allowed i.e. a submenu within a submenu.
        // Inflate the menu items for use in the action bar.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        if (this.mSendIntent == null) {
            this.mSendIntent = new Intent(Intent.ACTION_SEND);
            this.mSendIntent.setType("text/plain");
            this.setSendText();
        }

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handles the action bar options.  Calls the database actions (Insert, Select, Update and
     * Delete), help and about activities or Google Play to rate when these options are selected.
     *
     * @param item the menu item selected
     * @return true if handled or the boolean returned by the activity
     * <code>onOptionsItemSelected()</code>
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (!this.mAllowEvents) {
            return true;
        }

        boolean handled = false;
        // NOTE: No binding for menus at the present time so leave using R.id.etc.
        final int itemSelectedID = item.getItemId();
        Intent intent;

        // This is need if the user selects Share then presses back without selecting an app.
        if (itemSelectedID != R.id.action_share) {
            // Stop more than one action caused by rapid tapping on the same action.
            this.setViewsStatus(false);
        }

        // Handle presses on the action bar items.
        if (itemSelectedID == R.id.action_insert) {
            handled = true;
            this.insert();
        } else if (itemSelectedID == R.id.action_update) {
            handled = true;
            this.update();
        } else if (itemSelectedID == R.id.action_delete) {
            handled = true;
            this.delete();
        } else if (itemSelectedID == R.id.action_share) {
            Intent shareIntent = Intent.createChooser(this.mSendIntent, null);
            startActivity(shareIntent);
        } else if (itemSelectedID == R.id.action_help) {
            handled = true;
            intent = new Intent(this, HelpActivity.class);
            this.startActivity(intent);
        } else if (itemSelectedID == R.id.action_about) {
            handled = true;
            intent = new Intent(this, AboutActivity.class);
            this.startActivity(intent);
        }

        return handled || super.onOptionsItemSelected(item);
    }

    /**
     * Enables or disables the views.
     *
     * @param enableViews <code>true</code> if views are to be enabled
     *                    <code>false</code> if views are to be disabled
     */
    private void setViewsStatus(final boolean enableViews) {
        if (!enableViews) {
            this.mAllowEvents = false;

            // Don't do this because it alters the screen layout causing a jerking movement.
            // This isn't needed anyway because when an action bar item is selected,
            // onOptionsItemSelected() is called which returns straight away if this.mAllowEvents is
            // false.
            /*ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }

            Window window = this.getWindow();
            if (window != null) {
	            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }*/
        }

        /* Can't do this because some JUnit tests fail because of disabling.
        if (this.mInput1EditText != null) {
            this.mInput1EditText.setEnabled(enableViews);
        }

        if (this.mInput2EditText != null) {
            this.mInput2EditText.setEnabled(enableViews);
        }
        */

        if (this.mSpinner1 != null) {
            this.mSpinner1.setEnabled(enableViews);
        }

        if (this.mSpinner2 != null) {
            this.mSpinner2.setEnabled(enableViews);
        }

        if (this.mSpinner3 != null) {
            this.mSpinner3.setEnabled(enableViews);
        }

        if (this.mRecyclerView1 != null) {
            this.mRecyclerView1.setEnabled(enableViews);
        }

        if (enableViews) {
            // Don't do this because it alters the screen layout causing a jerking movement.
            // This isn't needed anyway because when an action bar item is selected,
            // onOptionsItemSelected() is called which returns straight away if this.mAllowEvents is
            // false.
            /*if (actionBar != null) {
                actionBar.show();
            }

            Window window = this.getWindow();
            if (window != null) {
	            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }*/

            this.mAllowEvents = true;
        }
    }

    /**
     * Releases resources when leaving this app.
     */
    private void releaseResources() {
        // Do not set strings to "" but just clear reference setting to "" may effect save bundles
        // because it has a reference to the same string.
        this.mCurrentSelectInput1 = null;
        this.mCurrentSelectInput2 = null;
        this.mCurrentSelectInput3 = null;
        this.mCurrentSelectInput4 = null;
        this.mCurrentSelectInput5 = null;
        this.mActivityMainBinding = null;
        this.mToolbar = null;
        this.mRecyclerViewAdapter = null;
        this.mCurrentSelectTextView = null;
        this.mMessageTextView = null;
        this.mInput1EditText = null;

        if (this.mInput2EditText != null) {
            HyperlinkMovementMethod hyperlinkMovementMethod =
                    (HyperlinkMovementMethod) this.mInput2EditText.getMovementMethod();
            if (hyperlinkMovementMethod != null) {
                hyperlinkMovementMethod.releaseResources();
            }
        }
        this.mInput2EditText = null;

        this.mSpinner1 = null;
        this.mSpinner2 = null;
        this.mSpinner3 = null;
        // Re-created activities receive the same ViewModel instance created by the first
        // activity.
        // The ViewModel exists from when you first request a ViewModel until the activity is
        // finished and destroyed.  Rotating the screen does not call the activity's finish() so the
        // same ViewModel still exists.
        // When the owner activity is finished, the framework calls the ViewModel object's
        // onCleared() method so that it can clean up resources.
        this.mListViewModel = null;
        // Warning: do not do this because this is a pointer to the List<ListEntity> returned by the
        // ViewModel.  Clearing this would clear the observed list.
        //this.mListEntities.clear();
        this.mListEntities = null;

        this.mRecyclerView1 = null;
        this.mSendIntent = null;
        this.mSendText = null;
    }

    /**
     * Displays the message relating to the outcome of the last database call.
     *
     * @param messageType type of message: 0 = TextView, 1 = Snack Bar
     * @param message     result text to display
     */
    private void showMessage(final int messageType, final String message) {
        switch (messageType) {
            case 0:
                this.mMessageTextView.setText(message);
                break;
            case 1:
                Snackbar.make(this.mToolbar, message, Snackbar.LENGTH_LONG).show();
                break;
            default:
                Log.e(this.TAG, "showMessage error 1");
                throw new RuntimeException();
        }
    }

    /**
     * Builds the text to display in the current select text view.
     *
     * @return the current select text
     * @see #mCurrentSelectTextView
     */
    String setCurrentSelectDetails() {
        String currentSelectText = this.getString(R.string.select_mode) + ": ";

        if (this.mActivatedID != RecyclerView.NO_ID) {
            currentSelectText += this.getString(R.string.row);
        } else {
            if (TextUtils.isEmpty(this.mCurrentSelectInput1) &&
                    TextUtils.isEmpty(this.mCurrentSelectInput2) &&
                    this.mCurrentSelectInput3.compareToIgnoreCase(this.getResources()
                            .getStringArray(R.array.category_list)[0]) == 0 &&
                    this.mCurrentSelectInput4.compareToIgnoreCase(this.getResources()
                            .getStringArray(R.array.status_list)[0]) == 0) {
                currentSelectText += this.getString(R.string.select_all);
            } else {
                currentSelectText += this.getString(R.string.containing);
            }
        }

        return currentSelectText;
    }

    /**
     * Clears the name and description edit text views and sets the focus to the name edit text.
     */
    private void clearDetails() {
        this.mInput1EditText.setText("");
        this.mInput2EditText.setText("");
        // Don't clear spinners.
        this.mInput1EditText.requestFocus();

        this.select();
    }

    /**
     * Creates the order by text.
     *
     * @return the order by text
     */
    private String getOrderBy() {
        String orderBy;

        switch (this.mCurrentSelectInput5.toLowerCase()) {
            case ListEntity.NAME:
                orderBy = ListEntity.NAME + ", " +
                        ListEntity.CATEGORY + ", " +
                        ListEntity.STATUS + ", " +
                        ListEntity.DESCRIPTION;
                break;
            case ListEntity.DESCRIPTION:
                orderBy = ListEntity.DESCRIPTION + ", " +
                        ListEntity.CATEGORY + ", " +
                        ListEntity.STATUS + ", " +
                        ListEntity.NAME;
                break;
            case ListEntity.CATEGORY:
                orderBy = ListEntity.CATEGORY + ", " +
                        ListEntity.STATUS + ", " +
                        ListEntity.NAME + ", " +
                        ListEntity.DESCRIPTION;
                break;
            case ListEntity.STATUS:
                orderBy = ListEntity.STATUS + ", " +
                        ListEntity.CATEGORY + ", " +
                        ListEntity.NAME + ", " +
                        ListEntity.DESCRIPTION;
                break;
            default:
                Log.e(this.TAG, "getOrderBy error 1");
                throw new RuntimeException();
        }

        return orderBy;
    }

    // To avoid a memory leak, use a WeakReference to allow garbage collection.
    /**
     * Insert a row into the database.
     */
    private void insert() {
        // Background thread.
        //noinspection Convert2Lambda
        BasicApplication.APP_EXECUTORS.getBackgroundThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                long id = -1L;
                final WeakReference<MainActivity> mainActivityWeakReference =
                        new WeakReference<>(MainActivity.this);
                try {
                    // Get a reference to the activity if it is still there.
                    final MainActivity mainActivity = mainActivityWeakReference.get();
                    if (mainActivity == null || mainActivity.isFinishing()) {
                        return;
                    }

                    final ListViewModel listViewModel = mainActivity.mListViewModel;
                    if(listViewModel != null) {
                        id = listViewModel.insertItem(mainActivity.getValues());
                    }
                } finally {
                    // Must be final to access on main thread.
                    final long finalId = id;

                    // Main thread.
                    //noinspection Convert2Lambda
                    BasicApplication.APP_EXECUTORS.getMainThreadExecutor().execute(
                            new Runnable() {
                                @Override
                                public void run() {
                                    // Get a reference to the activity if it is still there.
                                    final MainActivity mainActivity =
                                            mainActivityWeakReference.get();
                                    if (mainActivity == null || mainActivity.isFinishing()) {
                                        return;
                                    }

                                    final String message;
                                    if (finalId > 0) {
                                        message = mainActivity.getString(R.string.row_inserted);
                                        mainActivity.clearDetails();
                                    } else {
                                        message = mainActivity.getString(R.string.insert_error);
                                        mainActivity.setViewsStatus(true);
                                    }
                                    mainActivity.showMessage(1, message);
                                }
                            });
                }
            }
        });
    }

    /**
     * Get the search details inputted by the user.  This is called when the selection criteria
     * changes apart from when a row is selected.  See the {@link RecyclerViewAdapter} for the
     * code which is called when the user selects a row.
     */
    private void select() {
        this.mActivatedID = RecyclerView.NO_ID;
        this.mCurrentSelectInput1 = this.mInput1EditText.getText().toString();
        this.mCurrentSelectInput2 = this.mInput2EditText.getText().toString();
        this.mCurrentSelectInput3 = this.mSpinner1.getSelectedItem().toString();
        this.mCurrentSelectInput4 = this.mSpinner2.getSelectedItem().toString();
        this.mCurrentSelectInput5 = this.mSpinner3.getSelectedItem().toString();
        this.doSelect();
    }

    /**
     * Select the row(s) from the database matching the inputted criteria or the row selected by the
     * user.
     */
    private void doSelect() {
        String where = "";
        ArrayList<String> whereArgs = new ArrayList<>();
        String[] whereArgsStringArray = null;

        this.mCurrentSelectTextView.setText(this.setCurrentSelectDetails());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ")
                .append(TextUtils.join(", ", MainActivity.PROJECTION))
                .append(" FROM ")
                .append(ListEntity.TABLE_NAME);

        // No input (for EditTexts) means select all.
        // A space means select rows with a blank column i.e. "".
        // trim() removes spaces and other characters.
        // trim() does not affect the values of the class member variables; to do so the code would
        // need to be: mCurrentSelectInput1 = mCurrentSelectInput1.trim().
        if (!TextUtils.isEmpty(this.mCurrentSelectInput1)) {
            where += ListEntity.NAME + " LIKE ?";

            // Make sure that if spaces only inputted, don't select all.
            if (this.mCurrentSelectInput1.trim().length() == 0) {
                whereArgs.add(this.mCurrentSelectInput1.trim());
            } else {
                whereArgs.add("%" + this.mCurrentSelectInput1.trim() + "%");
            }
        }

        if (!TextUtils.isEmpty(this.mCurrentSelectInput2)) {
            if (where.length() != 0) {
                where += " AND ";
            }
            where += ListEntity.DESCRIPTION + " LIKE ?";

            // Make sure that if spaces only inputted, don't select all.
            if (this.mCurrentSelectInput2.trim().length() == 0) {
                whereArgs.add(this.mCurrentSelectInput2.trim());
            } else {
                whereArgs.add("%" + this.mCurrentSelectInput2.trim() + "%");
            }
        }

        if (this.mCurrentSelectInput3.compareToIgnoreCase(
                this.getResources().getStringArray(R.array.category_list)[0]) != 0) {
            if (where.length() != 0) {
                where += " AND ";
            }
            where += ListEntity.CATEGORY + " LIKE ?";

            // Make sure that if spaces only inputted, don't select all.
            if (this.mCurrentSelectInput3.trim().length() == 0) {
                whereArgs.add(this.mCurrentSelectInput3.trim());
            } else {
                whereArgs.add("%" + this.mCurrentSelectInput3.trim() + "%");
            }
        }

        if (this.mCurrentSelectInput4.compareToIgnoreCase(
                this.getResources().getStringArray(R.array.status_list)[0]) != 0) {
            if (where.length() != 0) {
                where += " AND ";
            }
            where += ListEntity.STATUS + " LIKE ?";

            // Make sure that if spaces only inputted, don't select all.
            if (this.mCurrentSelectInput4.trim().length() == 0) {
                whereArgs.add(this.mCurrentSelectInput4.trim());
            } else {
                whereArgs.add("%" + this.mCurrentSelectInput4.trim() + "%");
            }
        }

        if (where.length() == 0) {
            where = null;
            // whereArgsStringArray is initialised to null.
        } else {
            whereArgsStringArray = whereArgs.toArray(new String[0]);
        }

        boolean addID = this.mActivatedID != RecyclerView.NO_ID;

        if (where == null) {
            if (addID) {
                stringBuilder.append(" WHERE ").append(ListEntity._ID).append(" = ")
                        .append(this.mActivatedID);
            }
        } else {
            stringBuilder.append(" WHERE ").append(where);
            if (addID) {
                stringBuilder.append(" AND ").append(ListEntity._ID).append(" = ")
                        .append(this.mActivatedID);
            }
        }

        stringBuilder.append(" ORDER BY ").append(this.getOrderBy());

        this.setViewsStatus(false);

        // This appears to be OK to be called on the main thread (see notes in onCreate()
        // ViewModelProviders section).
        this.mListViewModel.selectItems(stringBuilder.toString(), whereArgsStringArray);
    }

    // To avoid a memory leak, use a WeakReference to allow garbage collection.
    /**
     * Updates rows in the database.
     */
    private void update() {
        // Background thread.
        //noinspection Convert2Lambda
        BasicApplication.APP_EXECUTORS.getBackgroundThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                long updatedRecordsCount = -1L;
                final WeakReference<MainActivity> mainActivityWeakReference =
                        new WeakReference<>(MainActivity.this);
                try {
                    // Get a reference to the activity if it is still there.
                    final MainActivity mainActivity = mainActivityWeakReference.get();
                    if (mainActivity == null || mainActivity.isFinishing()) {
                        return;
                    }

                    final ListViewModel listViewModel = mainActivity.mListViewModel;
                    if(listViewModel != null) {
                        updatedRecordsCount = listViewModel.updateItem(mainActivity.mActivatedID,
                                mainActivity.getValues());
                    }
                } finally {
                    // Must be final to access on main thread.
                    final long finalUpdatedRecordsCount = updatedRecordsCount;

                    // Main thread.
                    //noinspection Convert2Lambda
                    BasicApplication.APP_EXECUTORS.getMainThreadExecutor().execute(
                            new Runnable() {
                                @Override
                                public void run() {
                                    // Get a reference to the activity if it is still there.
                                    final MainActivity mainActivity =
                                            mainActivityWeakReference.get();
                                    if (mainActivity == null || mainActivity.isFinishing()) {
                                        return;
                                    }

                                    // Note: If no item is selected, id passed in is
                                    // RecyclerView.NO_ID (-1) and zero is returned.
                                    final String message;
                                    if (finalUpdatedRecordsCount > 0) {
                                        // Only one row can be updated at a time because the name is
                                        // unique.
                                        message = mainActivity.getString(R.string.row_updated);
                                        mainActivity.clearDetails();
                                    } else {
                                        message = mainActivity.getString(R.string.update_error);
                                        mainActivity.setViewsStatus(true);
                                    }
                                    mainActivity.showMessage(1, message);                                }
                            });
                }
            }
        });
    }

    /**
     * Starts the Delete action.
     * <p>
     * Creates and displays a "Delete Warning" {@link AlertDialogFragment}.
     */
    private void delete() {
        // Reset any input to current select details.
        this.mInput1EditText.setText(this.mCurrentSelectInput1);
        this.mInput2EditText.setText(this.mCurrentSelectInput2);
        this.setSpinnerValue(this.mCurrentSelectInput3, this.mSpinner1);
        this.setSpinnerValue(this.mCurrentSelectInput4, this.mSpinner2);

        this.mCurrentSelectTextView.setText(this.setCurrentSelectDetails());

        // Only allow one dialog to be displayed.
        if (this.getSupportFragmentManager().findFragmentByTag(
                MainActivity.DELETE_ALERT_DIALOG_TAG) == null) {
            // An application is not allowed to commit fragment transactions while in
            // onLoaderFinished, since it can happen after an activity's state is saved.
            // Can't therefore put the alert dialog call in onLoadFinished().
            AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(
            );
            alertDialogFragment.show(this.getSupportFragmentManager(),
                    MainActivity.DELETE_ALERT_DIALOG_TAG);
        }
    }

    // To avoid a memory leak, use a WeakReference to allow garbage collection.
    /**
     * Deletes row(s) from the database.
     */
    void doDeleteOKPositiveClick() {
        // Background thread.
        //noinspection Convert2Lambda
        BasicApplication.APP_EXECUTORS.getBackgroundThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                long deletedRecordsCount = -1L;
                final WeakReference<MainActivity> mainActivityWeakReference =
                        new WeakReference<>(MainActivity.this);
                try {
                    // Get a reference to the activity if it is still there.
                    final MainActivity mainActivity = mainActivityWeakReference.get();
                    if (mainActivity == null || mainActivity.isFinishing()) {
                        return;
                    }

                    final ListViewModel listViewModel = mainActivity.mListViewModel;
                    if(listViewModel != null) {
                        deletedRecordsCount = listViewModel.deleteItems(mainActivity.mListEntities);
                    }
                } finally {
                    // Must be final to access on main thread.
                    final long finalDeletedRecordsCount = deletedRecordsCount;

                    // Main thread.
                    //noinspection Convert2Lambda
                    BasicApplication.APP_EXECUTORS.getMainThreadExecutor().execute(
                            new Runnable() {
                                @Override
                                public void run() {
                                    // Get a reference to the activity if it is still there.
                                    final MainActivity mainActivity =
                                            mainActivityWeakReference.get();
                                    if (mainActivity == null || mainActivity.isFinishing()) {
                                        return;
                                    }

                                    String message;
                                    if (finalDeletedRecordsCount > 0) {
                                        message = finalDeletedRecordsCount + " ";
                                        if (finalDeletedRecordsCount == 1) {
                                            message += mainActivity.getString(R.string.row_deleted);
                                        } else {
                                            message += mainActivity.getString(
                                                    R.string.rows_deleted);
                                        }
                                        mainActivity.clearDetails();
                                    } else {
                                        message = mainActivity.getString(R.string.delete_error);
                                        mainActivity.setViewsStatus(true);
                                    }
                                    mainActivity.showMessage(1, message);
                                }
                            });
                }
            }
        });
    }

    /**
     * Sets the view statuses to true.
     */
    void doDeleteCancelNegativeClick() {
        this.setViewsStatus(true);
    }

    /**
     * Creates and populates <code>HashMap</code> to insert or update into the database.
     *
     * @return the values to insert or update
     */
    private HashMap<String, String> getValues() {
        HashMap<String, String> values = new HashMap<>();
        values.put(ListEntity.NAME, this.mInput1EditText.getText().toString());
        values.put(ListEntity.DESCRIPTION, this.mInput2EditText.getText().toString());
        values.put(ListEntity.CATEGORY, this.mSpinner1.getSelectedItem().toString());
        values.put(ListEntity.STATUS, this.mSpinner2.getSelectedItem().toString());

        return values;
    }

    /**
     * Set the value of a spinner.
     *
     * @param status  the text which matches a choice in the spinner
     * @param spinner the spinner whose value is to be set
     */
    private void setSpinnerValue(final String status, final Spinner spinner) {
        final int spinnerAdapterCount = spinner.getAdapter().getCount();
        final SpinnerAdapter spinnerAdapter = spinner.getAdapter();

        for (int i = 0; i < spinnerAdapterCount; i++) {
            if (status.compareToIgnoreCase((String) spinnerAdapter.getItem(i)) == 0) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    /**
     * Sets the text to send to another app.
     */
    private void setSendText() {
        // Send intent could be null if not item type - no Share option available.
        if (this.mSendIntent != null && this.mSendText != null) {
            this.mSendIntent.putExtra(Intent.EXTRA_TEXT, this.mSendText.toString());
        }
    }

    /**
     * Displays data from the database.
     */
    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        // Warning: don't call this mListEntities because it clashes with MainActivity member.
        /**
         * Holds the data from the database.
         */
        private final List<ListEntity> mRecyclerListEntities;

        /**
         * Holds the pointers to views in the row and handles user clicks on a row.
         */
        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            // NOTE: This must be in the ViewHolder and not the RecyclerViewAdapter so each row is
            // set up.
            /**
             * Holds the list row binding.
             */
            final ListRowBinding mListRowBinding;
            /**
             * Constructor setting the pointers to the views in the row and sets the click listener.
             *
             * @param listRowBinding the list row binding
             */
            ViewHolder(ListRowBinding listRowBinding) {
                super(listRowBinding.getRoot());

                this.mListRowBinding = listRowBinding;
                listRowBinding.getRoot().setOnClickListener(this);
            }

            /**
             * Handles the user selection of a row item.
             *
             * @param view the row item selected by the user
             */
            @Override
            public void onClick(final View view) {
                long id = Long.parseLong(this.mListRowBinding.idTextView.getText().toString());

                // Selecting means positioning the focus whereas activating means showing a list
                // item in a highlighted state.
                if (MainActivity.this.mActivatedID == id) {
                    // Deselect item.
                    MainActivity.this.clearDetails();
                } else {
                    MainActivity.this.mActivatedID = id;

                    String name = this.mListRowBinding.data1TextView.getText().toString();
                    String description = this.mListRowBinding.data2TextView.getText().toString();
                    String category = this.mListRowBinding.data3TextView.getText().toString();
                    String status = this.mListRowBinding.data4TextView.getText().toString();

                    // The name is not allowed to be blank.
                    MainActivity.this.mInput1EditText.setText(name);
                    MainActivity.this.mCurrentSelectInput1 = name;
                    if (description.trim().length() == 0) {
                        MainActivity.this.mInput2EditText.setText("");
                        // If the description is blank then add a space to ensure only items with a
                        // blank description are selected.
                        MainActivity.this.mCurrentSelectInput2 = " ";
                    } else {
                        MainActivity.this.mInput2EditText.setText(description);
                        MainActivity.this.mCurrentSelectInput2 = description;
                    }

                    MainActivity.this.setSpinnerValue(category, MainActivity.this.mSpinner1);
                    MainActivity.this.mCurrentSelectInput3 = category;

                    MainActivity.this.setSpinnerValue(status, MainActivity.this.mSpinner2);
                    MainActivity.this.mCurrentSelectInput4 = status;

                    MainActivity.this.doSelect();
                }
            }
        }

        /**
         * Constructor which sets the listEntities from the database.
         *
         * @param listEntities holds the data from the database
         */
        RecyclerViewAdapter(final List<ListEntity> listEntities) {
            this.mRecyclerListEntities = listEntities;
        }

        /**
         * Sets up the row layout and creates the view holder to hold the pointers to the views in
         * the row layout.
         *
         * @param parent   container of the row item
         * @param viewType type of view
         * @return holds the pointers to the views in the row layout
         */
        @NonNull
        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                 int viewType) {
            ListRowBinding listRowBinding = ListRowBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(listRowBinding);
        }

        /**
         * Sets the row item data.
         *
         * @param viewHolder contains the links to the views in the row
         * @param position   the location of the current row
         */
        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            if (this.mRecyclerListEntities != null) {
                viewHolder.itemView.setActivated(MainActivity.this.mActivatedID !=
                        RecyclerView.NO_ID);

                ListEntity listEntity = this.mRecyclerListEntities.get(position);
                viewHolder.mListRowBinding.idTextView.setText(Long.toString(listEntity.getId()));
                viewHolder.mListRowBinding.data1TextView.setText(listEntity.getName());
                viewHolder.mListRowBinding.data2TextView.setText(listEntity.getDescription());
                viewHolder.mListRowBinding.data3TextView.setText(listEntity.getCategory());
                viewHolder.mListRowBinding.data4TextView.setText(listEntity.getStatus());

                // If the category is not All then hide the row category.
                if (MainActivity.this.mSpinner1.getSelectedItemPosition() == 0) {
                    viewHolder.mListRowBinding.data3TextView.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mListRowBinding.data3TextView.setVisibility(View.GONE);
                }

                // If the status is not All then hide the row status.
                if (MainActivity.this.mSpinner2.getSelectedItemPosition() == 0) {
                    viewHolder.mListRowBinding.data4TextView.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mListRowBinding.data4TextView.setVisibility(View.GONE);
                }
            }
        }

        /**
         * The number of items in the list.
         *
         * @return the number of items in the list
         */
        @Override
        public int getItemCount() {
            if (this.mRecyclerListEntities == null) {
                return 0;
            } else {
                return this.mRecyclerListEntities.size();
            }
        }
    }
}
