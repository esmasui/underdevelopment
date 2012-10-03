package com.example.test_context_example_app.test;

import android.content.Context;
import android.test.AndroidTestCase;

public class MyInstrumentationTestSuiteAndroidTest extends AndroidTestCase {

    private Context mTestContext;


    public void testThatTestContextInjected() {

        assertEquals("com.example.test_context_example_app", getContext().getPackageName());
        assertEquals("com.example.test_context_example_app.test", mTestContext.getPackageName());
    }
}
