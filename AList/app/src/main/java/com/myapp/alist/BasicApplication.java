/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist;

import android.app.Application;
import android.content.Context;

import com.myapp.alist.datarepository.DataRepository;
import com.myapp.alist.db.AListDatabase;

/**
 * Android Application class. Used for accessing singletons.
 *
 * @author Colin Walters
 * @version 1.0, 17/04/2023
 * @since 1.0
 */
public final class BasicApplication extends Application {
    /**
     * Global executor pools for the whole application.
     */
    public static final AppExecutors APP_EXECUTORS = new AppExecutors();
    /**
     * The application singleton.
     */
    private static BasicApplication sBasicApplication;

    /**
     * Get the application context.
     *
     * @return the application <code>Context</code>
     */
    public static Context getContext() {
        return sBasicApplication.getApplicationContext();
    }

    /**
     * Set the application singleton and global executor pools.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        sBasicApplication = this;
    }

    /**
     * Get the {@link DataRepository} database.
     *
     * @return {@link DataRepository} database
     */
    public DataRepository getRepository() {
        return DataRepository.getInstance(AListDatabase.getInstance(this, APP_EXECUTORS));
    }
}
