/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MainActivityEspressoTestSuite.class,
        AListDatabaseTestSuite.class
})

public class AllTestSuite {
}
