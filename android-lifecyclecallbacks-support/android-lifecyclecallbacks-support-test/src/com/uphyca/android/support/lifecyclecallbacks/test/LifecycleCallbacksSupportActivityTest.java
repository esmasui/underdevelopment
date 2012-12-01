/*
 * Copyright (C) 2012 uPhyca Inc. http://www.uphyca.com/
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
package com.uphyca.android.support.lifecyclecallbacks.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;

import com.uphyca.android.support.lifecyclecallbacks.LifecycleCallbacksSupportActivity;
import com.uphyca.android.support.lifecyclecallbacks.LifecycleCallbacksSupportApplication;
import com.uphyca.android.support.lifecyclecallbacks.LifecycleCallbacksSupportApplication.ActivityLifecycleCallbacksCompat;
import com.uphyca.android.support.lifecyclecallbacks.test.LifecycleCallbacksSupportActivityTest.LifecycleCallbacksSupportActivityTestActivity;

/**
 * {@link ActivityLifecycleCallbacks} をICS以前でも使えるようにbackportした {@link ActivityLifecycleCallbacks} がオリジナルと同様の動作をすることを確認する
 */

public class LifecycleCallbacksSupportActivityTest extends ActivityUnitTestCase<LifecycleCallbacksSupportActivityTestActivity> {

    public static final class LifecycleCallbacksSupportActivityTestActivity extends LifecycleCallbacksSupportActivity {
    }

    public static final class LifecycleCallbacksSupportActivityTestApplication extends LifecycleCallbacksSupportApplication {
    }

    private Intent mStartIntent;
    private ActivityLifecycleCallbacksCompat mCallbackSpy;
    private LifecycleCallbacksSupportApplication mApplication;

    public LifecycleCallbacksSupportActivityTest() {
        super(LifecycleCallbacksSupportActivityTestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mCallbackSpy = mock(ActivityLifecycleCallbacksCompat.class);
        mApplication = new LifecycleCallbacksSupportActivityTestApplication();
        setApplication(mApplication);
        mStartIntent = new Intent(Intent.ACTION_MAIN);
    }

    /**
     * Activity.onCreate()がコールバックされること　
     */
    public void testDispatchOnActivityCreated() {
        // Given
        Bundle savedInstanceState = new Bundle();
        Object lastNonConfigurationInstance = null;
        mApplication.registerSupportActivityLifecycleCallbacks(mCallbackSpy);
        // When
        startActivity(mStartIntent, savedInstanceState, lastNonConfigurationInstance);
        // Then
        verify(mCallbackSpy, times(1)).onActivityCreated(getActivity(), savedInstanceState);
    }

    /**
     * Activity.onStart()がコールバックされること　
     */

    public void testDispatchOnActivityStarted() {
        // Given
        Bundle savedInstanceState = new Bundle();
        Object lastNonConfigurationInstance = null;
        mApplication.registerSupportActivityLifecycleCallbacks(mCallbackSpy);
        // When
        startActivity(mStartIntent, savedInstanceState, lastNonConfigurationInstance);
        getInstrumentation().callActivityOnStart(getActivity());
        // Then
        verify(mCallbackSpy, times(1)).onActivityStarted(getActivity());
    }

    /**
     * Activity.onResume()がコールバックされること　
     */

    public void testDispatchOnActivityResumed() {
        // Given
        Bundle savedInstanceState = new Bundle();
        Object lastNonConfigurationInstance = null;
        mApplication.registerSupportActivityLifecycleCallbacks(mCallbackSpy);
        // When
        startActivity(mStartIntent, savedInstanceState, lastNonConfigurationInstance);
        getInstrumentation().callActivityOnResume(getActivity());
        // Then
        verify(mCallbackSpy, times(1)).onActivityResumed(getActivity());
    }

    /**
     * Activity.onPause()がコールバックされること　
     */

    public void testDispatchOnActivityPaused() {
        // Given
        Bundle savedInstanceState = new Bundle();
        Object lastNonConfigurationInstance = null;
        mApplication.registerSupportActivityLifecycleCallbacks(mCallbackSpy);
        // When
        startActivity(mStartIntent, savedInstanceState, lastNonConfigurationInstance);
        getInstrumentation().callActivityOnPause(getActivity());
        // Then
        verify(mCallbackSpy, times(1)).onActivityPaused(getActivity());
    }

    /**
     * Activity.onStop()がコールバックされること　
     */

    public void testDispatchOnActivityStopped() {
        // Given
        Bundle savedInstanceState = new Bundle();
        Object lastNonConfigurationInstance = null;
        mApplication.registerSupportActivityLifecycleCallbacks(mCallbackSpy);
        // When
        startActivity(mStartIntent, savedInstanceState, lastNonConfigurationInstance);
        getInstrumentation().callActivityOnStop(getActivity());
        // Then
        verify(mCallbackSpy, times(1)).onActivityStopped(getActivity());
    }

    /**
     * Activity.onSaveInstanceState()がコールバックされること　
     */

    public void testDispatchOnActivitySaveInstanceState() {
        // Given
        Bundle savedInstanceState = new Bundle();
        Object lastNonConfigurationInstance = null;
        mApplication.registerSupportActivityLifecycleCallbacks(mCallbackSpy);
        // When
        startActivity(mStartIntent, savedInstanceState, lastNonConfigurationInstance);
        getInstrumentation().callActivityOnSaveInstanceState(getActivity(), savedInstanceState);
        // Then
        verify(mCallbackSpy, times(1)).onActivitySaveInstanceState(getActivity(), savedInstanceState);
    }

    /**
     * Activity.onDestroy()がコールバックされること　
     */

    public void testDispatchOnActivityDestroyed() {
        // Given
        Bundle savedInstanceState = new Bundle();
        Object lastNonConfigurationInstance = null;
        mApplication.registerSupportActivityLifecycleCallbacks(mCallbackSpy);
        // When
        startActivity(mStartIntent, savedInstanceState, lastNonConfigurationInstance);
        getInstrumentation().callActivityOnDestroy(getActivity());
        // Then
        verify(mCallbackSpy, times(1)).onActivityDestroyed(getActivity());
    }
}
