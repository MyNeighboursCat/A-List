/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.myapp.alist.BasicApplication;
import com.myapp.alist.db.ListEntity;

import java.util.HashMap;
import java.util.List;

// WARNING: A ViewModel must never reference a view, Lifecycle, or any class that may hold a
// reference to the activity context.
/**
 * List view model to call the {@link com.myapp.alist.datarepository.DataRepository}.
 *
 * @author Colin Walters
 * @version 1.0, 17/04/2023
 * @since 1.0
 */
public final class ListViewModel extends AndroidViewModel {
    /**
     * MediatorLiveData can observe other LiveData objects and react on their emissions.
     */
    private final MediatorLiveData<List<ListEntity>> mObservableItems;

    /**
     * Set the <code>MediatorLiveData</code> observable items from the
     * {@link com.myapp.alist.datarepository.DataRepository}.
     *
     * @param application the {@link BasicApplication}
     */
    public ListViewModel(final Application application) {
        super(application);

        mObservableItems = new MediatorLiveData<>();
        // Set by default null, until we get data from the database.
        mObservableItems.setValue(null);

        final LiveData<List<ListEntity>> items =
                ((BasicApplication) application).getRepository().getItems();

        // Observe the changes of the items from the database and forward them.
        mObservableItems.addSource(items, mObservableItems::setValue);
    }

    /**
     * Expose the LiveData Products query so the UI can observe it.
     */
    public LiveData<List<ListEntity>> getItems() {
        return mObservableItems;
    }

    /**
     * Query to select the required rows from the database.  Updates the observable list in
     * {@link com.myapp.alist.datarepository.DataRepository} which in turn updates
     * {@link #mObservableItems}.
     *
     * @param query     the <code>SELECT</code> query with <code>WHERE</code> and
     *                  <code>ORDER BY</code>
     * @param whereArgs the arguments to insert into the <code>WHERE</code> clause in place of '?'
     */
    public void selectItems(final String query, final String[] whereArgs) {
        ((BasicApplication) this.getApplication()).getRepository().selectItems(query, whereArgs);
    }

    /**
     * Insert a row into the database.
     *
     * @param values    the <code>HashMap</code> containing the details of the row to insert
     * @return          the id of the inserted row
     */
    public long insertItem(final HashMap<String, String> values) {
        return ((BasicApplication) this.getApplication()).getRepository().insertItem(values);
    }

    /**
     * Update a row in the database.
     *
     * @param id        the id of the row to update
     * @param values    the new values of the row to update
     * @return          the number of items updated.  Should always be one.
     */
    public long updateItem(final long id, final HashMap<String, String> values) {
        return ((BasicApplication) this.getApplication()).getRepository().updateItem(id, values);
    }

    /**
     * Delete a row(s) from the database.
     *
     * @param listEntities  the <code>List<ListEntity></code> containing the rows to delete
     * @return              the number of row deleted
     */
    public long deleteItems(final List<ListEntity> listEntities) {
        return ((BasicApplication) this.getApplication()).getRepository().deleteItems(listEntities);
    }
}