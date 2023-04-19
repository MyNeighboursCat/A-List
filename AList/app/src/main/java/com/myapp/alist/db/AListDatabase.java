/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.myapp.alist.AppExecutors;
import com.myapp.alist.BasicApplication;

/**
 * The Room database.
 *
 * @author Colin Walters
 * @version 1.0, 17/04/2023
 * @since 1.0
 */
@Database(entities = {ListEntity.class}, version = 1, exportSchema = false)
public abstract class AListDatabase extends RoomDatabase {
    // WARNING: the previous version of this app used a content provider which has been removed.
    // Need to use the same name otherwise existing data will not be accessible.
    /**
     * Specifies the database name.
     */
    public static final String DATABASE_NAME = "a_list_content_provider.db";

    /**
     * Get the DAO to access the data.
     *
     * @return the DAO for the {@link ListEntity} table
     */
    public abstract ListDao list();

    /**
     * The database singleton instance.
     */
    private static AListDatabase sAListDatabase;

    // This can be set by background thread in buildDatabase().
    // LiveData is abstract; cannot be instantiated so use MutableLiveData.
    /**
     * States if the database has been created.
     */
    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    /**
     * Gets the database singleton instance.
     *
     * @param context the conetext
     * @return the database singleton instance
     */
    public static AListDatabase getInstance(final Context context, final AppExecutors executors) {
        synchronized (AListDatabase.class) {
            if (sAListDatabase == null) {
                sAListDatabase = buildDatabase(context.getApplicationContext(), executors);
                sAListDatabase.updateDatabaseCreated(context.getApplicationContext());
            }
        }

        return sAListDatabase;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     *
     * @param appContext the application context
     * @param executors the application executors
     * @return the {@link AListDatabase}
     */
    private static AListDatabase buildDatabase(final Context appContext,
                                               final AppExecutors executors) {
        return Room.databaseBuilder(appContext, AListDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        //noinspection Convert2Lambda
                        executors.getBackgroundThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                // sAListDatabase is now != null in getInstance() so database is
                                // just
                                // returned.
                                AListDatabase aListDatabase =
                                        AListDatabase.getInstance(appContext, executors);
                                // Inserting of initial data into database would go here.
                                // Starting with blank database in this app with only user generated
                                // rows.

                                // Notify that the database was created and it's ready to be used.
                                aListDatabase.setDatabaseCreated();
                            }
                        });
                    }
                }).build();
    }

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     *
     * @param context the context
     */
    private void updateDatabaseCreated(final Context context) {
        // Use background thread because getDatabasePath() reads from disk.
        //noinspection Convert2Lambda
        BasicApplication.APP_EXECUTORS.getBackgroundThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if (context.getDatabasePath(DATABASE_NAME).exists()) {
                    setDatabaseCreated();
                }
            }
        });
    }

    /**
     * Sets the boolean to state that the database has been created.
     */
    private void setDatabaseCreated(){
        // Use postValue() to the main thread not setValue because this method can be called by a
        // background thread.
        // postValue() can be called on the main thread as well.
        mIsDatabaseCreated.postValue(true);
    }

    // mIsDatabaseCreated is MutableLiveData but pass back LiveData which is abstract.
    /**
     * Determines whether the database has been created.
     *
     * @return the boolean to indicate whether the database has been created.
     */
    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }

    /**
     * Switches the internal implementation with an empty in-memory database.
     *
     * @param context the context
     * @return the {@link AListDatabase}
     */
    @VisibleForTesting
    static AListDatabase switchToInMemory(final Context context) {
        return sAListDatabase = Room.inMemoryDatabaseBuilder(
                context.getApplicationContext(), AListDatabase.class)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build();
    }
}
