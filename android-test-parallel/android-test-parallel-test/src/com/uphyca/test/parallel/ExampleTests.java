package com.uphyca.test.parallel;

import android.test.AndroidTestCase;

public class ExampleTests extends AndroidTestCase {

    private static final long WAIT_MILLIS = 3000;

    public void testPreconditions() {
        assertNotNull(getContext());
    }

    public void testExecution1() throws InterruptedException {
        Thread.sleep(WAIT_MILLIS);
    }

    public void testExecution2() throws InterruptedException {
        Thread.sleep(WAIT_MILLIS);
    }

    public void testExecution3() throws InterruptedException {
        Thread.sleep(WAIT_MILLIS);
    }

    public void testExecution4() throws InterruptedException {
        Thread.sleep(WAIT_MILLIS);
    }

    public void testExecution5() throws InterruptedException {
        Thread.sleep(WAIT_MILLIS);
    }

}
