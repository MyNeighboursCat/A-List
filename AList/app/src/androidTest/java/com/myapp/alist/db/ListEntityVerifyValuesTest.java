/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.myapp.alist.BasicApplication;
import com.myapp.alist.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ListEntityVerifyValuesTest {
    private static final String NAME_1 = "Name 1";
    private static final String DESCRIPTION_1 = "Description 1";
    private static final String CATEGORY_1 = "To Do";
    private static final String STATUS_1 = "Current";
    private BasicApplication mBasicApplication;

    @Before
    public void setUp() {
        mBasicApplication = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithNullValues() {
        try {
            // Ignore returned listEntity because exception should be thrown.
            ListEntity.verifyValues(null);
            fail("Expected a RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("verifyValues error 1"));
        } catch (Exception e) {
            fail("Expected a RuntimeException to be thrown");
        }
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithNonNullValuesAndMissingDetails() {
        final HashMap<String, String> values = new HashMap<>();

        try {
            // Ignore returned listEntity because exception should be thrown.
            ListEntity.verifyValues(values);
            fail("Expected a RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("verifyValues error 5"));
        } catch (Exception e) {
            fail("Expected a RuntimeException to be thrown");
        }
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithMissingName() {
        final HashMap<String, String> values = new HashMap<>();

        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, STATUS_1);

        try {
            // Ignore returned listEntity because exception should be thrown.
            ListEntity.verifyValues(values);
            fail("Expected a RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("verifyValues error 5"));
        } catch (Exception e) {
            fail("Expected a RuntimeException to be thrown");
        }
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithMissingDescription() {
        final HashMap<String, String> values = new HashMap<>();

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, STATUS_1);

        try {
            // Ignore returned listEntity because exception should be thrown.
            ListEntity.verifyValues(values);
            fail("Expected a RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("verifyValues error 7"));
        } catch (Exception e) {
            fail("Expected a RuntimeException to be thrown");
        }
    }


    @Test
    public void testVerifyValues_callVerifyValuesWithMissingCategory() {
        final HashMap<String, String> values = new HashMap<>();

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.STATUS, STATUS_1);

        try {
            // Ignore returned listEntity because exception should be thrown.
            ListEntity.verifyValues(values);
            fail("Expected a RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("verifyValues error 9"));
        } catch (Exception e) {
            fail("Expected a RuntimeException to be thrown");
        }
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithMissingStatus() {
        final HashMap<String, String> values = new HashMap<>();

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_1);

        try {
            // Ignore returned listEntity because exception should be thrown.
            ListEntity.verifyValues(values);
            fail("Expected a RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("verifyValues error 11"));
        } catch (Exception e) {
            fail("Expected a RuntimeException to be thrown");
        }
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithNullName() {
        final HashMap<String, String> values = new HashMap<>();

        values.put(ListEntity.NAME, null);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, STATUS_1);

        try {
            // Ignore returned listEntity because exception should be thrown.
            ListEntity.verifyValues(values);
            fail("Expected a RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("verifyValues error 4"));
        } catch (Exception e) {
            fail("Expected a RuntimeException to be thrown");
        }
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithNullDescription() {
        final HashMap<String, String> values = new HashMap<>();

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, null);
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, STATUS_1);

        try {
            // Ignore returned listEntity because exception should be thrown.
            ListEntity.verifyValues(values);
            fail("Expected a RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("verifyValues error 6"));
        } catch (Exception e) {
            fail("Expected a RuntimeException to be thrown");
        }
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithNullCategory() {
        final HashMap<String, String> values = new HashMap<>();

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, null);
        values.put(ListEntity.STATUS, STATUS_1);

        try {
            // Ignore returned listEntity because exception should be thrown.
            ListEntity.verifyValues(values);
            fail("Expected a RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("verifyValues error 8"));
        } catch (Exception e) {
            fail("Expected a RuntimeException to be thrown");
        }
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithNullStatus() {
        final HashMap<String, String> values = new HashMap<>();

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, null);

        try {
            // Ignore returned listEntity because exception should be thrown.
            ListEntity.verifyValues(values);
            fail("Expected a RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("verifyValues error 10"));
        } catch (Exception e) {
            fail("Expected a RuntimeException to be thrown");
        }
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithEmptyName() {
        final HashMap<String, String> values = new HashMap<>();
        final ListEntity listEntity;

        values.put(ListEntity.NAME, "");
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, STATUS_1);

        listEntity = ListEntity.verifyValues(values);
        assertNull(listEntity);
    }

    // Empty description is allowed so the tests are in the insert and update test classes.

    @Test
    public void testVerifyValues_callVerifyValuesWithEmptyCategory() {
        final HashMap<String, String> values = new HashMap<>();
        final ListEntity listEntity;

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, "");
        values.put(ListEntity.STATUS, STATUS_1);

        listEntity = ListEntity.verifyValues(values);
        assertNull(listEntity);
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithEmptyStatus() {
        final HashMap<String, String> values = new HashMap<>();
        final ListEntity listEntity;

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, "");

        listEntity = ListEntity.verifyValues(values);
        assertNull(listEntity);
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithSpaceName() {
        final HashMap<String, String> values = new HashMap<>();
        final ListEntity listEntity;

        values.put(ListEntity.NAME, " ");
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, STATUS_1);

        listEntity = ListEntity.verifyValues(values);
        assertNull(listEntity);
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithSpaceDescription() {
        final HashMap<String, String> values = new HashMap<>();
        final ListEntity listEntity;

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, " ");
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, STATUS_1);

        listEntity = ListEntity.verifyValues(values);
        assertNull(listEntity);
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithSpaceCategory() {
        final HashMap<String, String> values = new HashMap<>();
        final ListEntity listEntity;

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, " ");
        values.put(ListEntity.STATUS, STATUS_1);

        listEntity = ListEntity.verifyValues(values);
        assertNull(listEntity);
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithSpaceStatus() {
        final HashMap<String, String> values = new HashMap<>();
        final ListEntity listEntity;

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, " ");

        listEntity = ListEntity.verifyValues(values);
        assertNull(listEntity);
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithAllCategory() {
        final HashMap<String, String> values = new HashMap<>();
        final ListEntity listEntity;

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, mBasicApplication.getResources()
                .getStringArray(R.array.category_list)[0]);
        values.put(ListEntity.STATUS, STATUS_1);

        listEntity = ListEntity.verifyValues(values);
        assertNull(listEntity);
    }

    @Test
    public void testVerifyValues_callVerifyValuesWithAllStatus() {
        final HashMap<String, String> values = new HashMap<>();
        final ListEntity listEntity;

        values.put(ListEntity.NAME, NAME_1);
        values.put(ListEntity.DESCRIPTION, DESCRIPTION_1);
        values.put(ListEntity.CATEGORY, CATEGORY_1);
        values.put(ListEntity.STATUS, mBasicApplication.getResources()
                .getStringArray(R.array.status_list)[0]);

        listEntity = ListEntity.verifyValues(values);
        assertNull(listEntity);
    }
}
