/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.myapp.alist.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ListDaoDeleteTest {
    private static final String NAME_1 = "Name 1";
    private static final String NAME_2 = "Name 2";
    private static final String DESCRIPTION_1 = "Description 1";
    private static final String DESCRIPTION_2 = "Description 2";
    private static final String CATEGORY_1 = "To Do";
    private static final String CATEGORY_2 = "News";
    private static final String STATUS_1 = "Current";
    private static final String STATUS_2 = "In Progress";
    private static final String WHERE = ListEntity.NAME + " LIKE ? AND " +
            ListEntity.DESCRIPTION + " LIKE ? AND " +
            ListEntity.CATEGORY + " LIKE ? AND " +
            ListEntity.STATUS + " LIKE ?";
    private static final String[] WHERE_ARGS = {NAME_1, DESCRIPTION_1, CATEGORY_1, STATUS_1};
    private static final String INVALID = "Invalid";
    private static final String[] INVALID_WHERE_ARGS = {INVALID, INVALID, INVALID, INVALID};
    private static final String QUERY = "SELECT * FROM " + ListEntity.TABLE_NAME +
            " WHERE " + WHERE;
    private AListDatabase mAListDatabase;
    private ListDao mListDao;

    // This is needed for LiveData testing combined with LiveDataTestUtil.
    // A JUnit Test Rule that swaps the background executor used by the Architecture Components with
    // a different one which executes each task synchronously.
    @Rule
    public InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        long id;
        final HashMap<String, String> values = new HashMap<>();
        ListEntity listEntity;

        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        mAListDatabase = AListDatabase.switchToInMemory(
                ApplicationProvider.getApplicationContext());
        mListDao = mAListDatabase.list();

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, STATUS_1);
        listEntity = ListEntity.verifyValues(values);
        assertNotNull(listEntity);
        id = mListDao.insert(listEntity);
        assertEquals(1, id);

        values.clear();
        values.put(ListEntity.NAME, NAME_2);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_2);
        values.put(ListEntity.CATEGORY, CATEGORY_2);
        values.put(ListEntity.STATUS, STATUS_2);
        listEntity = ListEntity.verifyValues(values);
        assertNotNull(listEntity);
        id = mListDao.insert(listEntity);
        assertEquals(2, id);
    }

    @After
    public void closeDatabase() {
        mAListDatabase.close();
    }

    @Test
    public void testDelete_callDeleteRowAndGetExpectedDeletedRows() throws InterruptedException {
        // Select first row details.
        // See @Rule InstantTaskExecutorRule
        List<ListEntity> listEntities = LiveDataTestUtil.getValue(mListDao.doRawQuery(
                new SimpleSQLiteQuery(QUERY, WHERE_ARGS)));
        assertNotNull("List<ListEntity> is null", listEntities);
        assertEquals("List<ListEntity> count is not one", 1, listEntities.size());

        long deletedRows = mListDao.delete(listEntities);
        assertEquals("Delete number of rows is not one", 1, deletedRows);

        listEntities = LiveDataTestUtil.getValue(mListDao.doRawQuery(
                new SimpleSQLiteQuery(QUERY, WHERE_ARGS)));
        assertEquals("List<ListEntity> count is not zero", 0,
                listEntities.size());
    }

    @Test
    public void testDelete_callDeleteAllSelectedAndGetExpectedDeletedRows()
            throws InterruptedException {
        // Select all.
        // See @Rule InstantTaskExecutorRule
        List<ListEntity> listEntities = LiveDataTestUtil.getValue(mListDao.selectAllQuery());
        assertNotNull("List<ListEntity> is null", listEntities);
        assertEquals("List<ListEntity> count is not two", 2, listEntities.size());

        long deletedRows = mListDao.delete(listEntities);
        assertEquals("Delete number of rows is not two", 2, deletedRows);

        listEntities = LiveDataTestUtil.getValue(mListDao.doRawQuery(
                new SimpleSQLiteQuery(QUERY, WHERE_ARGS)));
        assertEquals("List<ListEntity> count is not zero", 0,
                listEntities.size());
    }

    @Test
    public void testDelete_callDeleteInvalidRowAndGetExpectedNoDeletedRows()
            throws InterruptedException {
        // Select invalid row details.
        // See @Rule InstantTaskExecutorRule
        List<ListEntity> listEntities = LiveDataTestUtil.getValue(mListDao.doRawQuery(
                new SimpleSQLiteQuery(QUERY, INVALID_WHERE_ARGS)));
        assertNotNull("List<ListEntity> is null", listEntities);
        assertEquals("List<ListEntity> count is not zero", 0,
                listEntities.size());

        long deletedRows = mListDao.delete(listEntities);
        assertEquals("Delete number of rows is not zero", 0, deletedRows);

        listEntities = LiveDataTestUtil.getValue(mListDao.doRawQuery(
                new SimpleSQLiteQuery(QUERY, WHERE_ARGS)));
        assertEquals("List<ListEntity> count is not one", 1,
                listEntities.size());
    }
}
