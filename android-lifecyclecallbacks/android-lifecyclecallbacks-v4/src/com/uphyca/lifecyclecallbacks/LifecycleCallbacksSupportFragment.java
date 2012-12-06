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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LifecycleCallbacksSupportFragment extends Fragment {

    private LifecycleCallbacksSupportApplication getApplicationCompat() {
        return LifecycleCallbacksSupportApplication.applicationOf(getActivity());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dispatchAttach(getApplicationCompat(), this, activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dispatchDetach(getApplicationCompat(), this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatchCreate(getApplicationCompat(), this, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        dispatchStart(getApplicationCompat(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        dispatchResume(getApplicationCompat(), this);
    }

    @Override
    public void onPause() {
        super.onPause();
        dispatchPause(getApplicationCompat(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        dispatchStop(getApplicationCompat(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dispatchDestroy(getApplicationCompat(), this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        dispatchSavedInstanceState(getApplicationCompat(), this, outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dispatchActivityCreate(getApplicationCompat(), this, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        dispatchCreateView(getApplicationCompat(), this, inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dispatchViewCreated(getApplicationCompat(), this, view, savedInstanceState);
    }

    private static void dispatchAttach(LifecycleCallbacksSupportApplication application,
                                       Fragment fragment,
                                       Activity activity) {
        if (application == null) {
            return;
        }
        application.dispatchFragmentAttachCompat(fragment, activity);
    }

    private static void dispatchDetach(LifecycleCallbacksSupportApplication application,
                                       Fragment fragment) {
        if (application == null) {
            return;
        }
        application.dispatchFragmentDetachCompat(fragment);
    }

    private static void dispatchCreate(LifecycleCallbacksSupportApplication application,
                                       Fragment fragment,
                                       Bundle savedInstanceState) {
        if (application == null) {
            return;
        }
        application.dispatchFragmentCreatedCompat(fragment, savedInstanceState);
    }

    private static void dispatchStart(LifecycleCallbacksSupportApplication application,
                                      Fragment fragment) {
        if (application == null) {
            return;
        }
        application.dispatchFragmentStartedCompat(fragment);
    }

    private static void dispatchResume(LifecycleCallbacksSupportApplication application,
                                       Fragment fragment) {
        if (application == null) {
            return;
        }
        application.dispatchFragmentResumedCompat(fragment);
    }

    private static void dispatchPause(LifecycleCallbacksSupportApplication application,
                                      Fragment fragment) {
        if (application == null) {
            return;
        }
        application.dispatchFragmentPausedCompat(fragment);
    }

    private static void dispatchStop(LifecycleCallbacksSupportApplication application,
                                     Fragment fragment) {
        if (application == null) {
            return;
        }
        application.dispatchFragmentStoppedCompat(fragment);
    }

    private static void dispatchDestroy(LifecycleCallbacksSupportApplication application,
                                        Fragment fragment) {
        if (application == null) {
            return;
        }
        application.dispatchFragmentDestroyedCompat(fragment);
    }

    private static void dispatchSavedInstanceState(LifecycleCallbacksSupportApplication application,
                                                   Fragment fragment,
                                                   Bundle outState) {
        if (application == null) {
            return;
        }
        application.dispatchFragmentSaveInstanceStateCompat(fragment, outState);
    }

    private static void dispatchActivityCreate(LifecycleCallbacksSupportApplication application,
                                               Fragment fragment,
                                               Bundle savedInstanceState) {
        if (application == null) {
            return;
        }
        application.dispatchFragmentActivityCreatedCompat(fragment, savedInstanceState);
    }

    private static void dispatchCreateView(LifecycleCallbacksSupportApplication application,
                                           Fragment fragment,
                                           LayoutInflater inflater,
                                           ViewGroup container,
                                           Bundle savedInstanceState) {
        if (application == null) {
            return;
        }
        application.dispatchFragmentCreateView(fragment, inflater, container, savedInstanceState);
    }

    private static void dispatchViewCreated(LifecycleCallbacksSupportApplication application,
                                            Fragment fragment,
                                            View view,
                                            Bundle savedInstanceState) {
        if (application == null) {
            return;
        }
        application.dispatchFragmentViewCreated(fragment, view, savedInstanceState);
    }

}
