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
package com.uphyca.lifecyclecallbacks;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

public class LifecycleCallbacksSupportActivity extends Activity {

    private LifecycleCallbacksSupportApplication getApplicationCompat() {
        return LifecycleCallbacksSupportApplication.applicationOf(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        dispatchAttachBaseContext(getApplicationCompat(), this, newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatchCreate(getApplicationCompat(), this, savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        dispatchStart(getApplicationCompat(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dispatchResume(getApplicationCompat(), this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dispatchPause(getApplicationCompat(), this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dispatchStop(getApplicationCompat(), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dispatchDestroy(getApplicationCompat(), this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        dispatchSavedInstanceState(getApplicationCompat(), this, outState);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        dispatchAttachFragment(getApplicationCompat(), this, fragment);
    }

    private static void dispatchAttachBaseContext(LifecycleCallbacksSupportApplication application,
                                                  Activity activity,
                                                  Context newBase) {
        if (application == null) {
            return;
        }
        application.dispatchActivityAttachBaseContextCompat(activity, newBase);
    }

    private static void dispatchCreate(LifecycleCallbacksSupportApplication application,
                                       Activity activity,
                                       Bundle savedInstanceState) {
        if (application == null) {
            return;
        }
        application.dispatchActivityCreatedCompat(activity, savedInstanceState);
    }

    private static void dispatchStart(LifecycleCallbacksSupportApplication application,
                                      Activity activity) {
        if (application == null) {
            return;
        }
        application.dispatchActivityStartedCompat(activity);
    }

    private static void dispatchResume(LifecycleCallbacksSupportApplication application,
                                       Activity activity) {
        if (application == null) {
            return;
        }
        application.dispatchActivityResumedCompat(activity);
    }

    private static void dispatchPause(LifecycleCallbacksSupportApplication application,
                                      Activity activity) {
        if (application == null) {
            return;
        }
        application.dispatchActivityPausedCompat(activity);
    }

    private static void dispatchStop(LifecycleCallbacksSupportApplication application,
                                     Activity activity) {
        if (application == null) {
            return;
        }
        application.dispatchActivityStoppedCompat(activity);
    }

    private static void dispatchDestroy(LifecycleCallbacksSupportApplication application,
                                        Activity activity) {
        if (application == null) {
            return;
        }
        application.dispatchActivityDestroyedCompat(activity);
    }

    private static void dispatchSavedInstanceState(LifecycleCallbacksSupportApplication application,
                                                   Activity activity,
                                                   Bundle outState) {
        if (application == null) {
            return;
        }
        application.dispatchActivitySaveInstanceStateCompat(activity, outState);
    }

    private static void dispatchAttachFragment(LifecycleCallbacksSupportApplication application,
                                               Activity activity,
                                               Fragment fragment) {
        if (application == null) {
            return;
        }
        application.dispatchActivityAttachFragmentCompat(activity, fragment);
    }
}
