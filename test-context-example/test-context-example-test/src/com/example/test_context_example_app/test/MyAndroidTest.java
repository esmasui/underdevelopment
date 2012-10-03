package com.example.test_context_example_app.test;

import android.content.Context;
import android.test.AndroidTestCase;

public class MyAndroidTest extends AndroidTestCase {

    private Context mTestContext;

    public void setTestContext(Context testContext) {
        mTestContext = testContext;
    }

    public void testThatTestContextInjected() {

        //getContext()は、//instrumentaiton/targetPackageのコンテキストを返す
        assertEquals("com.example.test_context_example_app", getContext().getPackageName());
        
        //mTestContextには、//instrumentaiton/targetPackageのコンテキストが設定される
        assertEquals("com.example.test_context_example_app.test", mTestContext.getPackageName());
    }
}
