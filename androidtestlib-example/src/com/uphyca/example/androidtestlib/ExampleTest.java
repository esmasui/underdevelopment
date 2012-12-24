package com.uphyca.example.androidtestlib;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import android.app.Instrumentation;
import android.content.Context;

import com.android.test.InjectContext;
import com.android.test.InjectInstrumentation;

public class ExampleTest {

    @InjectContext
    public Context mContext;

    @InjectInstrumentation
    public Instrumentation mInstrumentation;

    @Test
    public void assertPreconditions() {
        assertThat(mContext, not(nullValue()));
        assertThat(mInstrumentation, not(nullValue()));
    }
}
