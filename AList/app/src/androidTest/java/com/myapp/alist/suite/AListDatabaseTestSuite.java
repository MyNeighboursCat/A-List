/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist.suite;

import com.myapp.alist.db.ListDaoDeleteTest;
import com.myapp.alist.db.ListDaoInsertTest;
import com.myapp.alist.db.ListDaoQueryTest;
import com.myapp.alist.db.ListDaoUpdateTest;
import com.myapp.alist.db.ListEntityVerifyValuesTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ListDaoQueryTest.class,
        ListEntityVerifyValuesTest.class,
        ListDaoInsertTest.class,
        ListDaoUpdateTest.class,
        ListDaoDeleteTest.class,
})

public class AListDatabaseTestSuite {
}
