/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import android.database.sqlite.SQLiteConstraintException;

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
public class ListDaoUpdateTest {
    private static final int ID_INVALID = 0;
    private static final int ID_1 = 1;
    private static final int ID_2 = 2;
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
    private static final String[] UPDATED_WHERE_ARGS = {NAME_2, DESCRIPTION_2, CATEGORY_2,
            STATUS_2};
    private static final String QUERY = "SELECT * FROM " + ListEntity.TABLE_NAME +
            " WHERE " + WHERE;
    private AListDatabase mAListDatabase;
    private ListDao mListDao;

    // This is needed for LiveData testing combined with LiveDataTestUtil.
    // A JUnit Test Rule that swaps the background executor used by the Architecture Components with
    // a different one which executes each task synchronously.
    @Rule
    public InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    // Both insert and update use verifyValues() - see the DataRepository class.  The tests that
    // fail in this method (insert() or update() are not called) will be in the
    // ListEntityVerifyValues class.
    @Before
    public void setUp() {
        final HashMap<String, String> values = new HashMap<>();
        final ListEntity listEntity;

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

        long id = mListDao.insert(listEntity);
        assertEquals(ID_1, id);
    }

    @After
    public void closeDatabase() {
        mAListDatabase.close();
    }

    @Test
    public void testUpdate_callUpdateAndGetExpectedData() throws InterruptedException {
        final HashMap<String, String> values = new HashMap<>();
        ListEntity listEntity;
        List<ListEntity> listEntities;

        values.put(ListEntity.NAME, NAME_2);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_2);
        values.put(ListEntity.CATEGORY, CATEGORY_2);
        values.put(ListEntity.STATUS, STATUS_2);

        listEntity = ListEntity.verifyValues(values);
        assertNotNull(listEntity);

        listEntity.setId(ID_1);
        int updatedRows = mListDao.update(listEntity);
        assertEquals("Update number of rows is not one", 1, updatedRows);

        // Select original details - none found.
        // See @Rule InstantTaskExecutorRule
        listEntities = LiveDataTestUtil.getValue(mListDao.doRawQuery(
                new SimpleSQLiteQuery(QUERY, WHERE_ARGS)));
        assertNotNull("List<ListEntity> is null", listEntities);
        assertEquals("List<ListEntity> count is not zero", 0,
                listEntities.size());

        // Select updated details.
        // See @Rule InstantTaskExecutorRule
        listEntities = LiveDataTestUtil.getValue(mListDao.doRawQuery(
                new SimpleSQLiteQuery(QUERY, UPDATED_WHERE_ARGS)));
        assertNotNull("List<ListEntity> is null", listEntities);
        assertEquals("List<ListEntity> count is not one", 1,
                listEntities.size());
        listEntity = listEntities.get(0);
        assertEquals("ListEntity _id column value is not equal to " + ID_1,
                ID_1, listEntity.getId());
        assertEquals("ListEntity name column value is not equal to " + NAME_2,
                NAME_2, listEntity.getName());
        assertEquals("ListEntity description column value is not equal to " +
                DESCRIPTION_2, DESCRIPTION_2, listEntity.getDescription());
        assertEquals("ListEntity category column value is not equal to " + CATEGORY_2,
                CATEGORY_2, listEntity.getCategory());
        assertEquals("ListEntity status column value is not equal to " + STATUS_2,
                STATUS_2, listEntity.getStatus());
    }

    @Test
    public void testUpdate_callUpdateWithDuplicateRowAndGetExpectedData()
            throws InterruptedException {
        final HashMap<String, String> values = new HashMap<>();
        ListEntity listEntity;
        List<ListEntity> listEntities;

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, STATUS_1);

        listEntity = ListEntity.verifyValues(values);
        assertNotNull(listEntity);

        listEntity.setId(ID_1);
        int updatedRows = mListDao.update(listEntity);
        assertEquals("Update number of rows is not one", 1, updatedRows);

        // Select updated details.
        // See @Rule InstantTaskExecutorRule
        listEntities = LiveDataTestUtil.getValue(mListDao.doRawQuery(
                new SimpleSQLiteQuery(QUERY, WHERE_ARGS)));
        assertNotNull("List<ListEntity> is null", listEntities);
        assertEquals("List<ListEntity> count is not one", 1,
                listEntities.size());
        listEntity = listEntities.get(0);
        assertEquals("ListEntity _id column value is not equal to " + ID_1,
                ID_1, listEntity.getId());
        assertEquals("ListEntity name column value is not equal to " + NAME_1,
                NAME_1, listEntity.getName());
        assertEquals("ListEntity description column value is not equal to " +
                DESCRIPTION_1, DESCRIPTION_1, listEntity.getDescription());
        assertEquals("ListEntity category column value is not equal to " + CATEGORY_1,
                CATEGORY_1, listEntity.getCategory());
        assertEquals("ListEntity status column value is not equal to " + STATUS_1,
                STATUS_1, listEntity.getStatus());
    }

    @Test
    public void testUpdate_callUpdateWithEmptyDescriptionAndGetUpdate() {
        final HashMap<String, String> values = new HashMap<>();
        ListEntity listEntity;

        values.put(ListEntity.NAME, NAME_2);
        values.put(ListEntity.DESCRIPTION, "");
        values.put(ListEntity.CATEGORY, CATEGORY_2);
        values.put(ListEntity.STATUS, STATUS_2);

        listEntity = ListEntity.verifyValues(values);
        assertNotNull(listEntity);

        listEntity.setId(ID_1);

        int updatedRows = mListDao.update(listEntity);
        assertEquals("Updated number of rows is not one", 1, updatedRows);
    }

    @Test
    public void testUpdate_callUpdateWithInvalidDetailsAndGetNoUpdate() {
        final HashMap<String, String> values = new HashMap<>();
        ListEntity listEntity;

        values.put(ListEntity.NAME, NAME_2);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_2);
        values.put(ListEntity.CATEGORY, CATEGORY_2);
        values.put(ListEntity.STATUS, STATUS_2);

        listEntity = ListEntity.verifyValues(values);
        assertNotNull(listEntity);

        listEntity.setId(ID_2);
        int updatedRows = mListDao.update(listEntity);
        assertEquals("Update number of rows is not zero", 0, updatedRows);
    }

    @Test
    public void testUpdate_callUpdateWithInvalidRowIDAndGetNoUpdate() {
        final HashMap<String, String> values = new HashMap<>();
        ListEntity listEntity;

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, STATUS_1);

        listEntity = ListEntity.verifyValues(values);
        assertNotNull(listEntity);

        listEntity.setId(ID_INVALID);

        int updatedRows = mListDao.update(listEntity);
        assertEquals("Updated number of rows is not zero", 0, updatedRows);
    }

    @Test
    public void testUpdate_callUpdateWithDuplicateNameAndGetExpectedError() {
        final HashMap<String, String> values = new HashMap<>();
        ListEntity listEntity;

        insertSecondRow();

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, STATUS_1);

        listEntity = ListEntity.verifyValues(values);
        assertNotNull(listEntity);

        listEntity.setId(ID_2);

        try {
            // Ignore returned updated row number because exception should be thrown.
            mListDao.update(listEntity);
            fail("Expected a SQLiteConstraintException to be thrown");
        } catch (SQLiteConstraintException e) {
            assertThat(e.getMessage(), is("UNIQUE constraint failed: list.name (code 2067 " +
                    "SQLITE_CONSTRAINT_UNIQUE[2067])"));
        } catch (Exception e) {
            fail("Expected a SQLiteConstraintException to be thrown");
        }
    }

    @Test
    public void testUpdate_callUpdateWithDuplicateDescriptionAndGetUpdate() {
        final HashMap<String, String> values = new HashMap<>();
        ListEntity listEntity;

        insertSecondRow();

        values.put(ListEntity.NAME, NAME_2);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_2);
        values.put(ListEntity.STATUS, STATUS_2);

        listEntity = ListEntity.verifyValues(values);
        assertNotNull(listEntity);

        listEntity.setId(ID_2);

        int updatedRows = mListDao.update(listEntity);
        assertEquals("Updated number of rows is not one", 1, updatedRows);
    }

    private void insertSecondRow() {
        final HashMap<String, String> values = new HashMap<>();
        final ListEntity listEntity;

        values.put(ListEntity.NAME, NAME_2);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_2);
        values.put(ListEntity.CATEGORY, CATEGORY_2);
        values.put(ListEntity.STATUS, STATUS_2);

        listEntity = ListEntity.verifyValues(values);
        assertNotNull(listEntity);

        long id = mListDao.insert(listEntity);
        assertEquals(ID_2, id);
    }
}
