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
public class ListDaoQueryTest {
    private static final String NAME_1 = "Name 1";
    private static final String NAME_2 = "Name 2";
    private static final String DESCRIPTION_1 = "Description 1";
    private static final String DESCRIPTION_2 = "Description 2";
    private static final String CATEGORY_1 = "To Do";
    private static final String CATEGORY_2 = "News";
    private static final String STATUS_1 = "Current";
    private static final String STATUS_2 = "In Progress";
    private static final String QUERY_SELECT_ALL = "SELECT * FROM " + ListEntity.TABLE_NAME;
    private static final String QUERY = QUERY_SELECT_ALL + " WHERE " +
            ListEntity.NAME + " LIKE ? AND " + ListEntity.DESCRIPTION + " LIKE ? AND " +
            ListEntity.CATEGORY + " LIKE ? AND " + ListEntity.STATUS + " LIKE ?";
    private static final String[] WHERE_ARGS = {NAME_1, DESCRIPTION_1, CATEGORY_1, STATUS_1};
    private static final String[] INVALID_WHERE_ARGS = {"Invalid Name", "Invalid Description",
            "Invalid Category", "Invalid Status"};
    private static final String ORDER_BY = " ORDER BY ";
    private static final String DESC = " DESC";
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
    public void testQuery_callQueryAndGetExpectedData() throws InterruptedException {
        // Can't do this because getValue() returns null.
        /*LiveData<List<ListEntity>> listEntityLiveData =
                mListDao.doRawQuery(new SimpleSQLiteQuery(QUERY, WHERE_ARGS));
        List<ListEntity> listEntities = listEntityLiveData.getValue();*/

        // See @Rule InstantTaskExecutorRule
        List<ListEntity> listEntities = LiveDataTestUtil.getValue(mListDao.doRawQuery(
                new SimpleSQLiteQuery(QUERY, WHERE_ARGS)));
        assertNotNull("List<ListEntity> is null", listEntities);
        assertEquals("List<ListEntity> count is not one", 1, listEntities.size());

        checkFirstRow(0, listEntities);
    }

    @Test
    public void testQuery_callQueryWithInvalidWhereArguments() throws InterruptedException {
        List<ListEntity> listEntities = LiveDataTestUtil.getValue(mListDao.doRawQuery(
                new SimpleSQLiteQuery(QUERY, INVALID_WHERE_ARGS)));
        assertNotNull("List<ListEntity> is null", listEntities);
        assertEquals("List<ListEntity> count is not zero", 0,
                listEntities.size());
    }

    @Test
    public void testQuery_callQueryWithAscendingNameSortOrder() throws InterruptedException {
        int index = 0;
        List<ListEntity> listEntities = queryWithSortOrder(ListEntity.NAME);
        checkFirstRow(index++, listEntities);
        checkSecondRow(index, listEntities);
    }

    @Test
    public void testQuery_callQueryWithDescendingNameSortOrder() throws InterruptedException {
        int index = 0;
        List<ListEntity> listEntities = queryWithSortOrder(ListEntity.NAME + DESC);
        checkSecondRow(index++, listEntities);
        checkFirstRow(index, listEntities);
    }

    @Test
    public void testQuery_callQueryWithAscendingDescriptionSortOrder() throws InterruptedException {
        int index = 0;
        List<ListEntity> listEntities = queryWithSortOrder(ListEntity.DESCRIPTION);
        checkFirstRow(index++, listEntities);
        checkSecondRow(index, listEntities);
    }

    @Test
    public void testQuery_callQueryWithDescendingDescriptionSortOrder()
            throws InterruptedException {
        int index = 0;
        List<ListEntity> listEntities = queryWithSortOrder(ListEntity.DESCRIPTION + DESC);
        checkSecondRow(index++, listEntities);
        checkFirstRow(index, listEntities);
    }

    @Test
    public void testQuery_callQueryWithAscendingNameAndAscendingDescriptionSortOrder()
            throws InterruptedException {
        int index = 0;
        List<ListEntity> listEntities = queryWithSortOrder(ListEntity.NAME + ", " +
                ListEntity.DESCRIPTION);
        checkFirstRow(index++, listEntities);
        checkSecondRow(index, listEntities);
    }

    @Test
    public void testQuery_callQueryWithAscendingNameAndDescendingDescriptionSortOrder()
            throws InterruptedException {
        int index = 0;
        List<ListEntity> listEntities = queryWithSortOrder(ListEntity.NAME + ", " +
                ListEntity.DESCRIPTION + DESC);
        checkFirstRow(index++, listEntities);
        checkSecondRow(index, listEntities);
    }

    @Test
    public void testQuery_callQueryWithDescendingNameAndAscendingDescriptionSortOrder()
            throws InterruptedException {
        int index = 0;
        List<ListEntity> listEntities = queryWithSortOrder(ListEntity.NAME + DESC + ", " +
                ListEntity.DESCRIPTION);
        checkSecondRow(index++, listEntities);
        checkFirstRow(index, listEntities);
    }

    @Test
    public void testQuery_callQueryWithDescendingNameAndDescendingDescriptionSortOrder()
            throws InterruptedException {
        int index = 0;
        List<ListEntity> listEntities = queryWithSortOrder(ListEntity.NAME + DESC + ", " +
                ListEntity.DESCRIPTION + DESC);
        checkSecondRow(index++, listEntities);
        checkFirstRow(index, listEntities);
    }

    @Test
    public void testQuery_callQueryWithAscendingDescriptionAndAscendingNameSortOrder()
            throws InterruptedException {
        int index = 0;
        List<ListEntity> listEntities = queryWithSortOrder(ListEntity.DESCRIPTION + ", " +
                ListEntity.NAME);
        checkFirstRow(index++, listEntities);
        checkSecondRow(index, listEntities);
    }

    @Test
    public void testQuery_callQueryWithAscendingDescriptionAndDescendingNameSortOrder()
            throws InterruptedException {
        int index = 0;
        List<ListEntity> listEntities = queryWithSortOrder(ListEntity.DESCRIPTION + ", " +
                ListEntity.NAME + DESC);
        checkFirstRow(index++, listEntities);
        checkSecondRow(index, listEntities);
    }

    @Test
    public void testQuery_callQueryWithDescendingDescriptionAndAscendingNameSortOrder()
            throws InterruptedException {
        int index = 0;
        List<ListEntity> listEntities = queryWithSortOrder(ListEntity.DESCRIPTION + DESC + ", " +
                ListEntity.NAME);
        checkSecondRow(index++, listEntities);
        checkFirstRow(index, listEntities);
    }

    @Test
    public void testQuery_callQueryWithDescendingDescriptionAndDescendingNameSortOrder()
            throws InterruptedException {
        int index = 0;
        List<ListEntity> listEntities = queryWithSortOrder(ListEntity.DESCRIPTION + DESC + ", " +
                ListEntity.NAME + DESC);
        checkSecondRow(index++, listEntities);
        checkFirstRow(index, listEntities);
    }

    private List<ListEntity> queryWithSortOrder(String sortOrder) throws InterruptedException {
        List<ListEntity> listEntities = LiveDataTestUtil.getValue(mListDao.doRawQuery(
                new SimpleSQLiteQuery(QUERY_SELECT_ALL + ORDER_BY + sortOrder)));
        assertNotNull("List<ListEntity> is null", listEntities);
        assertEquals("List<ListEntity> count is not two", 2, listEntities.size());

        return listEntities;
    }

    private void checkFirstRow(int index, List<ListEntity> listEntities) {
        ListEntity listEntity = listEntities.get(index);
        assertEquals("ListEntity _id column value is not equal to one", 1,
                listEntity.getId());
        assertEquals("ListEntity name column value is not equal to " + NAME_1,
                NAME_1, listEntity.getName());
        assertEquals("ListEntity description column value is not equal to " + DESCRIPTION_1,
                DESCRIPTION_1, listEntity.getDescription());
        assertEquals("ListEntity category column value is not equal to " + CATEGORY_1,
                CATEGORY_1, listEntity.getCategory());
        assertEquals("ListEntity status column value is not equal to " + STATUS_1,
                STATUS_1, listEntity.getStatus());
    }

    private void checkSecondRow(int index, List<ListEntity> listEntities) {
        ListEntity listEntity = listEntities.get(index);
        assertEquals("ListEntity _id column value is not equal to one", 2,
                listEntity.getId());
        assertEquals("ListEntity name column value is not equal to " + NAME_2,
                NAME_2, listEntity.getName());
        assertEquals("ListEntity description column value is not equal to " + DESCRIPTION_2,
                DESCRIPTION_2, listEntity.getDescription());
        assertEquals("ListEntity category column value is not equal to " + CATEGORY_2,
                CATEGORY_2, listEntity.getCategory());
        assertEquals("ListEntity status column value is not equal to " + STATUS_2,
                STATUS_2, listEntity.getStatus());
    }
}
