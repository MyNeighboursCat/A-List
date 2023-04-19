/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist.suite;

import com.myapp.alist.ui.MainActivityClickInsertEspressoInstrumentationTest;
import com.myapp.alist.ui.MainActivityClickSelectEspressoInstrumentationTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MainActivityClickInsertEspressoInstrumentationTest.class,
        MainActivityClickSelectEspressoInstrumentationTest.class
})

public class MainActivityEspressoTestSuite {
}
