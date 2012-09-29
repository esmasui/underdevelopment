/*
 * Copyright (C) 2012 uPhyca Inc. & @vvakame
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
package com.uphyca.testing.robolectric;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;

public abstract class RobolectricActivityUnitTestCase<T extends Activity> {

    private Instrumentation _instrumentation;
    private Class<T> _activityClass;
    private T _activity;

    protected void setUp() throws Exception {

    }

    protected void tearDown() throws Exception {

    }

    public RobolectricActivityUnitTestCase(Class<T> activityClass) {
        _activityClass = activityClass;
        injectInstrumentation(new Instrumentation() {
            @Override
            public void callActivityOnCreate(Activity activity,
                                             Bundle icicle) {
                invokeCallActivityOnCreate(activity, icicle);
            }
        });
    }

    protected T startActivity(Intent intent,
                              Bundle savedInstanceState,
                              Object lastNonConfigurationInstance) {
        try {
            _activity = _activityClass.newInstance();
            return _activity;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected T getActivity() {
        return _activity;
    }

    protected void invokeCallActivityOnCreate(Activity activity,
                                              Bundle icicle) {
        try {
            Method method = activity.getClass()
                                    .getMethod("onCreate", Bundle.class);
            method.setAccessible(true);
            method.invoke(activity, icicle);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Instrumentation getInstrumentation() {
        return _instrumentation;
    }

    public void injectInstrumentation(Instrumentation instrumentation) {
        _instrumentation = instrumentation;
    }
}
