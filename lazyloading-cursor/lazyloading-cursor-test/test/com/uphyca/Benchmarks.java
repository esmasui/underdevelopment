
package com.uphyca;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class Benchmarks extends TestCase {

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(LazyLoadingProviderTest.class);
        suite.addTestSuite(BasicProviderTest.class);

        return suite;
    }
}
