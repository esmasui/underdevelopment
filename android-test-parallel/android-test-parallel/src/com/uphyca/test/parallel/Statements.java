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

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import junit.framework.TestResult;

class Statements implements TestListener {

    private final List<Statement> mStatements = new ArrayList<Statement>();

    private static interface Statement {
        void evaluate(TestResult result);
    }

    @Override
    public void addError(final Test test,
                         final Throwable t) {
        mStatements.add(new Statement() {
            @Override
            public void evaluate(TestResult result) {
                result.addError(test, t);
            }
        });
    }

    @Override
    public void addFailure(final Test test,
                           final AssertionFailedError t) {
        mStatements.add(new Statement() {
            @Override
            public void evaluate(TestResult result) {
                result.addFailure(test, t);
            }
        });
    }

    @Override
    public void endTest(final Test test) {
        mStatements.add(new Statement() {
            @Override
            public void evaluate(TestResult result) {
                result.endTest(test);
            }
        });
    }

    @Override
    public void startTest(final Test test) {
        mStatements.add(new Statement() {
            @Override
            public void evaluate(TestResult result) {
                result.startTest(test);
            }
        });
    }

    public void evaluate(TestResult result) {
        for (Statement each : mStatements)
            each.evaluate(result);
    }
}
