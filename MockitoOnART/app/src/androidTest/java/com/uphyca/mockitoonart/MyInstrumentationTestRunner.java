package com.uphyca.mockitoonart;

import android.os.Bundle;
import android.test.InstrumentationTestRunner;

public class MyInstrumentationTestRunner extends InstrumentationTestRunner{

    private static final String ARGUMENT_TEST_PACKAGE = "package";

    @Override
    public void onCreate(Bundle arguments) {
        arguments.putString(ARGUMENT_TEST_PACKAGE, "com.uphyca.mockitoonart");
        super.onCreate(arguments);
    }
}
