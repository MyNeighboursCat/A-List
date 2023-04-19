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

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ListDaoInsertTest {
    private static final String NAME_1 = "Name 1";
    private static final String NAME_2 = "Name 2";
    private static final String DESCRIPTION_1 = "Description 1";
    private static final String DESCRIPTION_2 = "Description 2";
    private static final String CATEGORY_1 = "To Do";
    private static final String CATEGORY_2 = "News";
    private static final String STATUS_1 = "Current";
    private static final String STATUS_2 = "In Progress";
    private AListDatabase mAListDatabase;
    private ListDao mListDao;

    // Both insert and update use verifyValues() - see the DataRepository class.  The tests that
    // fail in this method (insert() or update() are not called) will be in the
    // ListEntityVerifyValues class.
    @Before
    public void setUp() {
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        mAListDatabase = AListDatabase.switchToInMemory(
                ApplicationProvider.getApplicationContext());
        mListDao = mAListDatabase.list();
    }

    @After
    public void closeDatabase() {
        mAListDatabase.close();
    }

    @Test
    public void testInsert_callInsertAndGetExpectedId() {
        final HashMap<String, String> values = new HashMap<>();
        final ListEntity listEntity;

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, STATUS_1);

        listEntity = ListEntity.verifyValues(values);
        assertNotNull(listEntity);

        long id = mListDao.insert(listEntity);
        assertEquals(1, id);
    }

    @Test
    public void testInsert_callInsertWithDuplicateRowAndGetExpectedError() {
        testInsert_callInsertAndGetExpectedId();

        final HashMap<String, String> values = new HashMap<>();
        final ListEntity listEntity;

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, STATUS_1);

        listEntity = ListEntity.verifyValues(values);
        assertNotNull(listEntity);

        try {
            // Ignore returned id because exception should be thrown.
            mListDao.insert(listEntity);
            fail("Expected a SQLiteConstraintException to be thrown");
        } catch (SQLiteConstraintException e) {
            assertThat(e.getMessage(), is("UNIQUE constraint failed: list.name (code 2067 " +
                    "SQLITE_CONSTRAINT_UNIQUE[2067])"));
        } catch (Exception e) {
            fail("Expected a SQLiteConstraintException to be thrown");
        }
    }

    @Test
    public void testInsert_callInsertWithDuplicateNameOnlyAndGetExpectedError() {
        testInsert_callInsertAndGetExpectedId();

        final HashMap<String, String> values = new HashMap<>();
        final ListEntity listEntity;

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_2);
        values.put(ListEntity.CATEGORY, CATEGORY_2);
        values.put(ListEntity.STATUS, STATUS_2);

        listEntity = ListEntity.verifyValues(values);
        assertNotNull(listEntity);

        try {
            // Ignore returned id because exception should be thrown.
            mListDao.insert(listEntity);
            fail("Expected a SQLiteConstraintException to be thrown");
        } catch (SQLiteConstraintException e) {
            assertThat(e.getMessage(), is("UNIQUE constraint failed: list.name (code 2067 " +
                    "SQLITE_CONSTRAINT_UNIQUE[2067])"));
        } catch (Exception e) {
            fail("Expected a SQLiteConstraintException to be thrown");
        }
    }

    @Test
    public void testInsert_callInsertWithDuplicateDescriptionOnlyAndGetExpectedId() {
        testInsert_callInsertAndGetExpectedId();

        final HashMap<String, String> values = new HashMap<>();
        final ListEntity listEntity;

        values.put(ListEntity.NAME, NAME_2);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_2);
        values.put(ListEntity.STATUS, STATUS_2);

        listEntity = ListEntity.verifyValues(values);
        assertNotNull(listEntity);

        long id = mListDao.insert(listEntity);
        assertEquals(2, id);
    }

    @Test
    public void testInsert_callInsertWithEmptyDescriptionAndGetExpectedId() {
        final HashMap<String, String> values = new HashMap<>();
        final ListEntity listEntity;

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, "");
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, STATUS_1);

        listEntity = ListEntity.verifyValues(values);
        assertNotNull(listEntity);

        long id = mListDao.insert(listEntity);
        // Accept "" but not " ".
        assertEquals(1, id);
    }
}
