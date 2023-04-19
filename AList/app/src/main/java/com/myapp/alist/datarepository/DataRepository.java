/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist.datarepository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.myapp.alist.db.AListDatabase;
import com.myapp.alist.db.ListEntity;

import java.util.HashMap;
import java.util.List;

/**
 * Data repository to interface with the database using the Data Access Object (DAO).
 *
 * @author Colin Walters
 * @version 1.0, 17/04/2023
 * @since 1.0
 */
public final class DataRepository {
    /**
     * Data repository singleton instance.
     */
    private static DataRepository sDataRepository;
    /**
     * The database containing the DAO.
     */
    private final AListDatabase mAListDatabase;
    /**
     * An observable list which can be used to merge different <code>LiveData</code> sources.
     */
    private final MediatorLiveData<List<ListEntity>> mObservableList = new MediatorLiveData<>();
    /**
     * The list of items to be used as <code>LiveData</code>.
     */
    private LiveData<List<ListEntity>> mListEntityLiveData;
    /**
     * Stores the classname to tag log errors.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * Create the data repository singleton instance.
     *
     * @param database the database containing the DAO
     */
    private DataRepository(final AListDatabase database) {
        mAListDatabase = database;

        // This appears to be OK to be called on the main thread (see notes in
        // MainActivity.onCreate() ViewModelProviders section) because using LiveData.
        // Room generates all the necessary code to update the LiveData object when a database is
        // updated. The generated code runs the query asynchronously on a background thread when
        // needed.
        mListEntityLiveData = mAListDatabase.list().selectAllQuery();
        addSourceToObservableList();
    }

    /**
     * Get the data repository singleton instance.
     *
     * @param database the database containing the DAO
     * @return the data repository singleton instance
     */
    public static DataRepository getInstance(final AListDatabase database) {
        synchronized (DataRepository.class) {
            if (sDataRepository == null) {
                sDataRepository = new DataRepository(database);
            }
        }

        return sDataRepository;
    }

    /**
     * Get the list of items from the database and get notified when the data changes.
     */
    public LiveData<List<ListEntity>> getItems() {
        return mObservableList;
    }

    /**
     * Get a list of items from the database matching the selected criteria.
     *
     * @param query     the <code>SELECT</code> query including <code>WHERE</code> and
     *                  <code>ORDER BY</code> details
     * @param whereArgs the <code>WHERE</code> arguments to insert into the <code>SELECT</code>
     *                  query in the place of '?' placeholders
     */
    public void selectItems(final String query, final String[] whereArgs) {
        // This appears to be OK to be called on the main thread (see notes in constructor).
        mObservableList.removeSource(mListEntityLiveData);
        mListEntityLiveData = mAListDatabase.list().doRawQuery(
                new SimpleSQLiteQuery(query, whereArgs));
        addSourceToObservableList();
    }

    /**
     * Insert an item into the database.
     *
     * @param values a <code>HashMap</code> containing the item details
     * @return the row ID of the inserted item
     */
    public long insertItem(final HashMap<String, String> values) {
        final ListEntity listEntity = ListEntity.verifyValues(values);
        if (listEntity == null) {
            Log.e(TAG, "insertItem error 1");
            return -1L;
        } else {
            try {
                // ListDao:
                // @Insert(onConflict = OnConflictStrategy.ABORT)
                // long insert(ListEntity item);
                // causes RuntimeException() to be thrown.
                return mAListDatabase.list().insert(listEntity);
            } catch (Exception e) {
                Log.e(TAG, "insertItem error 2");
                e.printStackTrace();
                return -1L;
            }
        }
    }

    /**
     * Update an item in the database.
     *
     * @param id     the id of the row to update
     * @param values the new values of the row to update
     * @return the number of rows updated.  Should always be one.
     */
    public long updateItem(final long id, final HashMap<String, String> values) {
        final ListEntity listEntity = ListEntity.verifyValues(values);
        if (listEntity == null) {
            Log.e(TAG, "updateItem error 1");
            return -1L;
        } else {
            try {
                // ListDao:
                // @Update(onConflict = OnConflictStrategy.ABORT)
                // int update(ListEntity item);
                // causes RuntimeException() to be thrown.
                listEntity.setId(id);
                return mAListDatabase.list().update(listEntity);
            } catch (Exception e) {
                Log.e(TAG, "updateItem error 2");
                e.printStackTrace();
                return -1L;
            }
        }
    }

    /**
     * Delete item(s) from the database.
     *
     * @param listEntities a list of {@link ListEntity} to delete from the
     *                     database
     * @return the number of items deleted from the database
     */
    public long deleteItems(final List<ListEntity> listEntities) {
        return mAListDatabase.list().delete(listEntities);
    }

    /**
     * Add the <code>LiveData</code> list of {@link ListEntity} to the
     * <code>MediatorLiveData</code> list and observe.
     */
    private void addSourceToObservableList() {
        //noinspection Convert2Lambda
        mObservableList.addSource(mListEntityLiveData,
                new Observer<List<ListEntity>>() {
                    @Override
                    public void onChanged(final List<ListEntity> listEntities) {
                        if (mAListDatabase.getDatabaseCreated().getValue() != null) {
                            // Could use setValue() as the code stands because called on the main
                            // thread.  Use postValue() like example app to future proof.
                            mObservableList.postValue(listEntities);
                        }
                    }
                });
    }
}
