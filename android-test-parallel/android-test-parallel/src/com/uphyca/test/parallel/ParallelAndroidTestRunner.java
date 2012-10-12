/*
 * Copyright (C) 2012 uPhyca Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uphyca.test.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import android.app.Instrumentation;
import android.content.Context;
import android.test.AndroidTestCase;
import android.test.AndroidTestRunner;
import android.test.InstrumentationTestCase;

/**
 * The test runner that runs tests in parallel.
 */
public class ParallelAndroidTestRunner extends AndroidTestRunner {

    private TestResult mTestResult;
    private List<TestListener> mTestListeners = new ArrayList<TestListener>();
    private Instrumentation mInstrumentation;
    private Context mContext;

    @Override
    public void clearTestListeners() {
        mTestListeners.clear();
    }

    @Override
    public void addTestListener(TestListener testListener) {
        if (testListener != null) {
            mTestListeners.add(testListener);
        }
    }

    @Override
    @Deprecated
    public void setInstrumentaiton(Instrumentation instrumentation) {
        super.setInstrumentaiton(instrumentation);
        mInstrumentation = instrumentation;
    }

    @Override
    public void setInstrumentation(Instrumentation instrumentation) {
        super.setInstrumentation(instrumentation);
        mInstrumentation = instrumentation;
    }

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        mContext = context;
    }

    @Override
    public void runTest(TestResult testResult) {

        mTestResult = testResult;

        for (TestListener testListener : mTestListeners) {
            mTestResult.addListener(testListener);
        }

        final List<TestCase> testCases = getTestCases();
        final List<Future<Statements>> results = new ArrayList<Future<Statements>>(testCases.size());
        final ExecutorService service = Executors.newCachedThreadPool();

        final Context testContext = mInstrumentation == null ? mContext : mInstrumentation.getContext();

        for (final TestCase testCase : testCases) {

            setContextIfAndroidTestCase(testCase, mContext, testContext);
            setInstrumentationIfInstrumentationTestCase(testCase, mInstrumentation);
            // TODO setPerformanceWriterIfPerformanceCollectorTestCase(testCase, mPerfWriter);

            final Callable<Statements> task = new Callable<Statements>() {
                @Override
                public Statements call() throws Exception {

                    Statements statements = new Statements();
                    TestResult result = new TestResult();
                    result.addListener(statements);

                    testCase.run(result);

                    return statements;
                }
            };

            results.add(service.submit(task));
        }

        waitForFinished(results);
    }

    private void waitForFinished(List<Future<Statements>> results) {
        for (Future<Statements> each : results) {
            try {
                Statements result = each.get();
                result.evaluate(mTestResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setContextIfAndroidTestCase(Test test,
                                             Context context,
                                             Context testContext) {
        if (AndroidTestCase.class.isAssignableFrom(test.getClass())) {
            ((AndroidTestCase) test).setContext(context);
            // TODO ((AndroidTestCase) test).setTestContext(testContext);
        }
    }

    private void setInstrumentationIfInstrumentationTestCase(Test test,
                                                             Instrumentation instrumentation) {
        if (InstrumentationTestCase.class.isAssignableFrom(test.getClass())) {
            ((InstrumentationTestCase) test).injectInstrumentation(instrumentation);
        }
    }
}
