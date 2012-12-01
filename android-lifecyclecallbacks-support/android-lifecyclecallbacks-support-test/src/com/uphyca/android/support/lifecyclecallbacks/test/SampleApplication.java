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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.uphyca.android.support.lifecyclecallbacks.LifecycleCallbacksSupportApplication;

public class SampleApplication extends LifecycleCallbacksSupportApplication {

    static final String TAG = "SampleApplication";

    private static final class LoggableActivityLifecycleCallbacks implements ActivityLifecycleCallbacksCompat {

        @Override
        public void onActivityCreated(Activity activity,
                                      Bundle savedInstanceState) {
            Log.d(TAG, "onActivityCreated");

        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.d(TAG, "onActivitySatrted");
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.d(TAG, "onActivityResumed");
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.d(TAG, "onActivityPaused");
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.d(TAG, "onActivityStopped");
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity,
                                                Bundle outState) {
            Log.d(TAG, "onActivitySaveInstanceState");
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.d(TAG, "onActivityDestroyed");
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerSupportActivityLifecycleCallbacks(new LoggableActivityLifecycleCallbacks());
    }
}
