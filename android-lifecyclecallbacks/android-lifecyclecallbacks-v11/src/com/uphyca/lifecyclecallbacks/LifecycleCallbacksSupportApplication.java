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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LifecycleCallbacksSupportApplication extends Application {

    public static class SimpleActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity,
                                      Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity,
                                                Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }

        @Override
        public void onActivityAttachBaseContext(Activity activity,
                                                Context newBase) {
        }

        @Override
        public void onActivityAttachFragment(Activity activity,
                                             Fragment fragment) {
        }
    }

    public static class SimpleFragmentLifecycleCallbacks implements FragmentLifecycleCallbacks {

        @Override
        public void onFragmentCreated(Fragment fragment,
                                      Bundle savedInstanceState) {
        }

        @Override
        public void onFragmentStarted(Fragment fragment) {
        }

        @Override
        public void onFragmentResumed(Fragment fragment) {
        }

        @Override
        public void onFragmentPaused(Fragment fragment) {
        }

        @Override
        public void onFragmentStopped(Fragment fragment) {
        }

        @Override
        public void onFragmentSaveInstanceState(Fragment fragment,
                                                Bundle outState) {
        }

        @Override
        public void onFragmentDestroyed(Fragment fragment) {
        }

        @Override
        public void onFragmentAttach(Fragment fragment,
                                     Activity activity) {
        }

        @Override
        public void onFragmentDetach(Fragment fragment) {
        }

        @Override
        public void onFragmentActivityCreated(Fragment fragment,
                                              Bundle savedInstanceState) {
        }

        @Override
        public void onFragmentCreateView(Fragment fragment,
                                         LayoutInflater inflater,
                                         ViewGroup container,
                                         Bundle savedInstanceState) {
        }

        @Override
        public void onFragmentViewCreated(Fragment fragment,
                                          View view,
                                          Bundle savedInstanceState) {
        }
    }

    public static LifecycleCallbacksSupportApplication applicationOf(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            Application app = activity.getApplication();
            if (app instanceof LifecycleCallbacksSupportApplication) {
                return (LifecycleCallbacksSupportApplication) app;
            }
        }

        if (context instanceof Service) {
            Service service = (Service) context;
            Application app = service.getApplication();
            if (app instanceof LifecycleCallbacksSupportApplication) {
                return (LifecycleCallbacksSupportApplication) app;
            }
        }

        Context appContext = context.getApplicationContext();
        if (appContext instanceof LifecycleCallbacksSupportApplication) {
            return (LifecycleCallbacksSupportApplication) appContext;
        }

        if (context instanceof ContextWrapper) {
            Context base = ((ContextWrapper) context).getBaseContext();

            if (base != null) {
                if (base instanceof LifecycleCallbacksSupportApplication) {
                    return (LifecycleCallbacksSupportApplication) base;
                }

                Context baseAppContext = base.getApplicationContext();
                if (appContext instanceof LifecycleCallbacksSupportApplication) {
                    return (LifecycleCallbacksSupportApplication) baseAppContext;
                }
            }
        }

        return null;
    }

    public static interface ActivityLifecycleCallbacks {

        void onActivityCreated(Activity activity,
                               Bundle savedInstanceState);

        void onActivityStarted(Activity activity);

        void onActivityResumed(Activity activity);

        void onActivityPaused(Activity activity);

        void onActivityStopped(Activity activity);

        void onActivitySaveInstanceState(Activity activity,
                                         Bundle outState);

        void onActivityDestroyed(Activity activity);

        void onActivityAttachBaseContext(Activity activity,
                                         Context newBase);

        void onActivityAttachFragment(Activity activity,
                                      Fragment fragment);
    }

    static interface ActivityLifecycleCallbacksRegistry {

        void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback);

        void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback);

        void dispatchActivityAttachBaseContext(Activity activity,
                                               Context newBase);

        void dispatchActivityCreated(Activity activity,
                                     Bundle savedInstanceState);

        void dispatchActivityStarted(Activity activity);

        void dispatchActivityResumed(Activity activity);

        void dispatchActivityPaused(Activity activity);

        void dispatchActivityStopped(Activity activity);

        void dispatchActivitySaveInstanceState(Activity activity,
                                               Bundle outState);

        void dispatchActivityDestroyed(Activity activity);

        void dispatchActivityAttachFragment(Activity activity,
                                            Fragment fragment);
    }

    private static final class LegacyActivityLifecycleCallbacksRegistry implements ActivityLifecycleCallbacksRegistry {

        private WeakReference<Application> mApplication;
        private List<ActivityLifecycleCallbacks> mActivityLifecycleCallbacks;

        public LegacyActivityLifecycleCallbacksRegistry(Application application) {
            mApplication = new WeakReference<Application>(application);
            mActivityLifecycleCallbacks = new ArrayList<LifecycleCallbacksSupportApplication.ActivityLifecycleCallbacks>();
        }

        @Override
        public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
            final Application application = mApplication.get();
            if (application == null) {
                return;
            }
            synchronized (mActivityLifecycleCallbacks) {
                mActivityLifecycleCallbacks.add(callback);
            }
        }

        @Override
        public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
            final Application application = mApplication.get();
            if (application == null) {
                return;
            }
            synchronized (mActivityLifecycleCallbacks) {
                mActivityLifecycleCallbacks.remove(callback);
            }
        }

        @Override
        public void dispatchActivityAttachBaseContext(Activity activity,
                                                      Context newBase) {
            Object[] callbacks = collectActivityLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((ActivityLifecycleCallbacks) callbacks[i]).onActivityAttachBaseContext(activity, newBase);
                }
            }
        }

        @Override
        public void dispatchActivityCreated(Activity activity,
                                            Bundle savedInstanceState) {
            Object[] callbacks = collectActivityLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((ActivityLifecycleCallbacks) callbacks[i]).onActivityCreated(activity, savedInstanceState);
                }
            }
        }

        @Override
        public void dispatchActivityStarted(Activity activity) {
            Object[] callbacks = collectActivityLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((ActivityLifecycleCallbacks) callbacks[i]).onActivityStarted(activity);
                }
            }
        }

        @Override
        public void dispatchActivityResumed(Activity activity) {
            Object[] callbacks = collectActivityLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((ActivityLifecycleCallbacks) callbacks[i]).onActivityResumed(activity);
                }
            }
        }

        @Override
        public void dispatchActivityPaused(Activity activity) {
            Object[] callbacks = collectActivityLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((ActivityLifecycleCallbacks) callbacks[i]).onActivityPaused(activity);
                }
            }
        }

        @Override
        public void dispatchActivityStopped(Activity activity) {
            Object[] callbacks = collectActivityLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((ActivityLifecycleCallbacks) callbacks[i]).onActivityStopped(activity);
                }
            }
        }

        @Override
        public void dispatchActivitySaveInstanceState(Activity activity,
                                                      Bundle outState) {
            Object[] callbacks = collectActivityLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((ActivityLifecycleCallbacks) callbacks[i]).onActivitySaveInstanceState(activity, outState);
                }
            }
        }

        @Override
        public void dispatchActivityDestroyed(Activity activity) {
            Object[] callbacks = collectActivityLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((ActivityLifecycleCallbacks) callbacks[i]).onActivityDestroyed(activity);
                }
            }
        }

        @Override
        public void dispatchActivityAttachFragment(Activity activity,
                                                   Fragment fragment) {
            Object[] callbacks = collectActivityLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((ActivityLifecycleCallbacks) callbacks[i]).onActivityAttachFragment(activity, fragment);
                }
            }
        }

        private Object[] collectActivityLifecycleCallbacks() {
            Object[] callbacks = null;
            synchronized (mActivityLifecycleCallbacks) {
                if (mActivityLifecycleCallbacks.size() > 0) {
                    callbacks = mActivityLifecycleCallbacks.toArray();
                }
            }
            return callbacks;
        }
    }

    /**
     * {@link ActivityLifecycleCallbacksRegistry}のICS以降の実装
     */
    @TargetApi(14)
    private static final class IceCreamSandwichActivityLifecycleCallbacksRegistry implements ActivityLifecycleCallbacksRegistry {

        private static final class DelegatingActivityLifecycleCallbacks implements android.app.Application.ActivityLifecycleCallbacks {

            private ActivityLifecycleCallbacks mCompat;

            public DelegatingActivityLifecycleCallbacks(ActivityLifecycleCallbacks compat) {
                mCompat = compat;
            }

            @Override
            public void onActivityCreated(Activity activity,
                                          Bundle savedInstanceState) {
                mCompat.onActivityCreated(activity, savedInstanceState);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                mCompat.onActivityStarted(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                mCompat.onActivityResumed(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                mCompat.onActivityPaused(activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                mCompat.onActivityStopped(activity);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity,
                                                    Bundle outState) {
                mCompat.onActivitySaveInstanceState(activity, outState);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                mCompat.onActivityDestroyed(activity);
            }
        }

        private WeakReference<Application> mApplication;
        private Map<ActivityLifecycleCallbacks, android.app.Application.ActivityLifecycleCallbacks> mDelegates;

        public IceCreamSandwichActivityLifecycleCallbacksRegistry(Application application) {
            mApplication = new WeakReference<Application>(application);
            mDelegates = new HashMap<LifecycleCallbacksSupportApplication.ActivityLifecycleCallbacks, Application.ActivityLifecycleCallbacks>();
        }

        @Override
        public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
            final Application application = mApplication.get();
            if (application == null) {
                return;
            }
            final android.app.Application.ActivityLifecycleCallbacks delegate = delegateOf(callback);
            application.registerActivityLifecycleCallbacks(delegate);
        }

        @Override
        public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
            final Application application = mApplication.get();
            if (application == null) {
                return;
            }
            final android.app.Application.ActivityLifecycleCallbacks delegate = delegateOf(callback);
            application.unregisterActivityLifecycleCallbacks(delegate);
        }

        private android.app.Application.ActivityLifecycleCallbacks delegateOf(ActivityLifecycleCallbacks compat) {
            synchronized (mDelegates) {
                if (mDelegates.containsKey(compat)) {
                    return mDelegates.get(compat);
                }

                android.app.Application.ActivityLifecycleCallbacks delegate = new DelegatingActivityLifecycleCallbacks(compat);
                mDelegates.put(compat, delegate);
                return delegate;
            }
        }

        @Override
        public void dispatchActivityAttachBaseContext(Activity activity,
                                                      Context newBase) {
            Object[] callbacks = mDelegates.keySet().toArray();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((ActivityLifecycleCallbacks) callbacks[i]).onActivityAttachBaseContext(activity, newBase);
                }
            }
        }

        @Override
        public void dispatchActivityAttachFragment(Activity activity,
                                                   Fragment fragment) {
            Object[] callbacks = mDelegates.keySet().toArray();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((ActivityLifecycleCallbacks) callbacks[i]).onActivityAttachFragment(activity, fragment);
                }
            }
        }

        @Override
        public void dispatchActivityCreated(Activity activity,
                                            Bundle savedInstanceState) {
            // ICS以降のApplicationのオリジナルのメソッドが実行されるので何もしない
        }

        @Override
        public void dispatchActivityStarted(Activity activity) {
            // ICS以降のApplicationのオリジナルのメソッドが実行されるので何もしない
        }

        @Override
        public void dispatchActivityResumed(Activity activity) {
            // ICS以降のApplicationのオリジナルのメソッドが実行されるので何もしない
        }

        @Override
        public void dispatchActivityPaused(Activity activity) {
            // ICS以降のApplicationのオリジナルのメソッドが実行されるので何もしない
        }

        @Override
        public void dispatchActivityStopped(Activity activity) {
            // ICS以降のApplicationのオリジナルのメソッドが実行されるので何もしない
        }

        @Override
        public void dispatchActivitySaveInstanceState(Activity activity,
                                                      Bundle outState) {
            // ICS以降のApplicationのオリジナルのメソッドが実行されるので何もしない
        }

        @Override
        public void dispatchActivityDestroyed(Activity activity) {
            // ICS以降のApplicationのオリジナルのメソッドが実行されるので何もしない
        }
    }

    public static interface FragmentLifecycleCallbacks {
        void onFragmentCreated(Fragment fragment,
                               Bundle savedInstanceState);

        void onFragmentStarted(Fragment fragment);

        void onFragmentResumed(Fragment fragment);

        void onFragmentPaused(Fragment fragment);

        void onFragmentStopped(Fragment fragment);

        void onFragmentSaveInstanceState(Fragment fragment,
                                         Bundle outState);

        void onFragmentDestroyed(Fragment fragment);

        void onFragmentAttach(Fragment fragment,
                              Activity activity);

        void onFragmentDetach(Fragment fragment);

        void onFragmentActivityCreated(Fragment fragment,
                                       Bundle savedInstanceState);

        void onFragmentCreateView(Fragment fragment,
                                  LayoutInflater inflater,
                                  ViewGroup container,
                                  Bundle savedInstanceState);

        void onFragmentViewCreated(Fragment fragment,
                                   View view,
                                   Bundle savedInstanceState);
    }

    static interface FragmentLifecycleCallbacksRegistry {

        void registerFragmentLifecycleCallbacks(FragmentLifecycleCallbacks callback);

        void unregisterFragmentLifecycleCallbacks(FragmentLifecycleCallbacks callback);

        void dispatchFragmentCreated(Fragment fragment,
                                     Bundle savedInstanceState);

        void dispatchFragmentStarted(Fragment fragment);

        void dispatchFragmentResumed(Fragment fragment);

        void dispatchFragmentPaused(Fragment fragment);

        void dispatchFragmentStopped(Fragment fragment);

        void dispatchFragmentSaveInstanceState(Fragment fragment,
                                               Bundle outState);

        void dispatchFragmentDestroyed(Fragment fragment);

        void dispatchFragmentActivityCreated(Fragment fragment,
                                             Bundle savedInstanceState);

        void dispatchFragmentAttach(Fragment fragment,
                                    Activity activity);

        void dispatchFragmentDetach(Fragment fragment);

        void dispatchFragmentCreateView(Fragment fragment,
                                        LayoutInflater inflater,
                                        ViewGroup container,
                                        Bundle savedInstanceState);

        void dispatchFragmentViewCreated(Fragment fragment,
                                         View view,
                                         Bundle savedInstanceState);
    }

    private static final class LegacyFragmentLifecycleCallbacksRegistry implements FragmentLifecycleCallbacksRegistry {

        private WeakReference<Application> mApplication;
        private List<FragmentLifecycleCallbacks> mFragmentLifecycleCallbacks;

        public LegacyFragmentLifecycleCallbacksRegistry(Application application) {
            mApplication = new WeakReference<Application>(application);
            mFragmentLifecycleCallbacks = new ArrayList<LifecycleCallbacksSupportApplication.FragmentLifecycleCallbacks>();
        }

        @Override
        public void registerFragmentLifecycleCallbacks(FragmentLifecycleCallbacks callback) {
            final Application application = mApplication.get();
            if (application == null) {
                return;
            }
            synchronized (mFragmentLifecycleCallbacks) {
                mFragmentLifecycleCallbacks.add(callback);
            }
        }

        @Override
        public void unregisterFragmentLifecycleCallbacks(FragmentLifecycleCallbacks callback) {
            final Application application = mApplication.get();
            if (application == null) {
                return;
            }
            synchronized (mFragmentLifecycleCallbacks) {
                mFragmentLifecycleCallbacks.remove(callback);
            }
        }

        @Override
        public void dispatchFragmentActivityCreated(Fragment fragment,
                                                    Bundle savedInstanceState) {
            Object[] callbacks = collectFragmentLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((FragmentLifecycleCallbacks) callbacks[i]).onFragmentActivityCreated(fragment, savedInstanceState);
                }
            }
        }

        @Override
        public void dispatchFragmentAttach(Fragment fragment,
                                           Activity activity) {
            Object[] callbacks = collectFragmentLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((FragmentLifecycleCallbacks) callbacks[i]).onFragmentAttach(fragment, activity);
                }
            }
        }

        @Override
        public void dispatchFragmentDetach(Fragment fragment) {
            Object[] callbacks = collectFragmentLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((FragmentLifecycleCallbacks) callbacks[i]).onFragmentDetach(fragment);
                }
            }
        }

        @Override
        public void dispatchFragmentCreated(Fragment fragment,
                                            Bundle savedInstanceState) {
            Object[] callbacks = collectFragmentLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((FragmentLifecycleCallbacks) callbacks[i]).onFragmentCreated(fragment, savedInstanceState);
                }
            }
        }

        @Override
        public void dispatchFragmentStarted(Fragment fragment) {
            Object[] callbacks = collectFragmentLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((FragmentLifecycleCallbacks) callbacks[i]).onFragmentStarted(fragment);
                }
            }
        }

        @Override
        public void dispatchFragmentResumed(Fragment fragment) {
            Object[] callbacks = collectFragmentLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((FragmentLifecycleCallbacks) callbacks[i]).onFragmentResumed(fragment);
                }
            }
        }

        @Override
        public void dispatchFragmentPaused(Fragment fragment) {
            Object[] callbacks = collectFragmentLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((FragmentLifecycleCallbacks) callbacks[i]).onFragmentPaused(fragment);
                }
            }
        }

        @Override
        public void dispatchFragmentStopped(Fragment fragment) {
            Object[] callbacks = collectFragmentLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((FragmentLifecycleCallbacks) callbacks[i]).onFragmentStopped(fragment);
                }
            }
        }

        @Override
        public void dispatchFragmentSaveInstanceState(Fragment fragment,
                                                      Bundle outState) {
            Object[] callbacks = collectFragmentLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((FragmentLifecycleCallbacks) callbacks[i]).onFragmentSaveInstanceState(fragment, outState);
                }
            }
        }

        @Override
        public void dispatchFragmentDestroyed(Fragment fragment) {
            Object[] callbacks = collectFragmentLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((FragmentLifecycleCallbacks) callbacks[i]).onFragmentDestroyed(fragment);
                }
            }
        }

        @Override
        public void dispatchFragmentCreateView(Fragment fragment,
                                               LayoutInflater inflater,
                                               ViewGroup container,
                                               Bundle savedInstanceState) {
            Object[] callbacks = collectFragmentLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((FragmentLifecycleCallbacks) callbacks[i]).onFragmentCreateView(fragment, inflater, container, savedInstanceState);
                }
            }
        }

        @Override
        public void dispatchFragmentViewCreated(Fragment fragment,
                                                View view,
                                                Bundle savedInstanceState) {
            Object[] callbacks = collectFragmentLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((FragmentLifecycleCallbacks) callbacks[i]).onFragmentViewCreated(fragment, view, savedInstanceState);
                }
            }
        }

        private Object[] collectFragmentLifecycleCallbacks() {
            Object[] callbacks = null;
            synchronized (mFragmentLifecycleCallbacks) {
                if (mFragmentLifecycleCallbacks.size() > 0) {
                    callbacks = mFragmentLifecycleCallbacks.toArray();
                }
            }
            return callbacks;
        }
    }

    public static interface ServiceLifecycleCallbacks {

        void onServiceAttachBaseContext(Service service,
                                        Context newBase);

        void onServiceCreated(Service service);
    }

    public static class SimpleServiceLifecycleCallbacks implements ServiceLifecycleCallbacks {

        @Override
        public void onServiceAttachBaseContext(Service service,
                                               Context newBase) {
        }

        @Override
        public void onServiceCreated(Service service) {
        }
    }

    static interface ServiceLifecycleCallbacksRegistry {

        void registerServiceLifecycleCallbacks(ServiceLifecycleCallbacks callback);

        void unregisterServiceLifecycleCallbacks(ServiceLifecycleCallbacks callback);

        void dispatchServiceCreated(Service service);

        void dispatchServiceAttachBaseContext(Service service,
                                              Context newBase);
    }

    private static final class LegacyServiceLifecycleCallbacksRegistry implements ServiceLifecycleCallbacksRegistry {

        private WeakReference<Application> mApplication;
        private List<ServiceLifecycleCallbacks> mServiceLifecycleCallbacks;

        public LegacyServiceLifecycleCallbacksRegistry(Application application) {
            mApplication = new WeakReference<Application>(application);
            mServiceLifecycleCallbacks = new ArrayList<LifecycleCallbacksSupportApplication.ServiceLifecycleCallbacks>();
        }

        @Override
        public void registerServiceLifecycleCallbacks(ServiceLifecycleCallbacks callback) {
            final Application application = mApplication.get();
            if (application == null) {
                return;
            }
            synchronized (mServiceLifecycleCallbacks) {
                mServiceLifecycleCallbacks.add(callback);
            }
        }

        @Override
        public void unregisterServiceLifecycleCallbacks(ServiceLifecycleCallbacks callback) {
            final Application application = mApplication.get();
            if (application == null) {
                return;
            }
            synchronized (mServiceLifecycleCallbacks) {
                mServiceLifecycleCallbacks.remove(callback);
            }
        }

        @Override
        public void dispatchServiceAttachBaseContext(Service service,
                                                     Context newBase) {
            Object[] callbacks = collectServiceLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((ServiceLifecycleCallbacks) callbacks[i]).onServiceAttachBaseContext(service, newBase);
                }
            }
        }

        @Override
        public void dispatchServiceCreated(Service service) {
            Object[] callbacks = collectServiceLifecycleCallbacks();
            if (callbacks != null) {
                for (int i = 0; i < callbacks.length; i++) {
                    ((ServiceLifecycleCallbacks) callbacks[i]).onServiceCreated(service);
                }
            }
        }

        private Object[] collectServiceLifecycleCallbacks() {
            Object[] callbacks = null;
            synchronized (mServiceLifecycleCallbacks) {
                if (mServiceLifecycleCallbacks.size() > 0) {
                    callbacks = mServiceLifecycleCallbacks.toArray();
                }
            }
            return callbacks;
        }
    }

    private ActivityLifecycleCallbacksRegistry mActivityLifecycleCallbacksRegistry;
    private FragmentLifecycleCallbacksRegistry mFragmentLifecycleCallbacksRegistry;
    private ServiceLifecycleCallbacksRegistry mServiceLifecycleCallbacksRegistry;

    public LifecycleCallbacksSupportApplication() {
        setupActivityLifecycleCallbacksRegistry();
        setupFragmentLifecycleCallbacksRegistry();
        setupServiceLifecycleCallbacksRegistry();
    }

    private void setupActivityLifecycleCallbacksRegistry() {
        if (introducedActivityLifecycleCallbacks()) {
            setupDelegatingActivityLifecycleCallbacks();
        } else {
            setupCompatActivityLifecycleCallbacks();
        }
    }

    private void setupFragmentLifecycleCallbacksRegistry() {
        setupCompatFragmentLifecycleCallbacks();
    }

    private void setupServiceLifecycleCallbacksRegistry() {
        setupCompatServiceLifecycleCallbacks();
    }

    private boolean introducedActivityLifecycleCallbacks() {
        try {
            Class.forName("android.app.Application$ActivityLifecycleCallbacks");
            return true;
        } catch (ClassNotFoundException e) {
        }
        return false;
    }

    private void setupCompatActivityLifecycleCallbacks() {
        mActivityLifecycleCallbacksRegistry = new LegacyActivityLifecycleCallbacksRegistry(this);
    }

    private void setupDelegatingActivityLifecycleCallbacks() {
        mActivityLifecycleCallbacksRegistry = new IceCreamSandwichActivityLifecycleCallbacksRegistry(this);
    }

    private void setupCompatFragmentLifecycleCallbacks() {
        mFragmentLifecycleCallbacksRegistry = new LegacyFragmentLifecycleCallbacksRegistry(this);
    }

    private void setupCompatServiceLifecycleCallbacks() {
        mServiceLifecycleCallbacksRegistry = new LegacyServiceLifecycleCallbacksRegistry(this);
    }

    public void registerSupportActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        mActivityLifecycleCallbacksRegistry.registerActivityLifecycleCallbacks(callback);
    }

    public void unregisterSupportActivityLifecycleCallbacksCompat(ActivityLifecycleCallbacks callback) {
        mActivityLifecycleCallbacksRegistry.unregisterActivityLifecycleCallbacks(callback);
    }

    void dispatchActivityAttachBaseContextCompat(Activity activity,
                                                 Context newBase) {
        mActivityLifecycleCallbacksRegistry.dispatchActivityAttachBaseContext(activity, newBase);
    }

    void dispatchActivityCreatedCompat(Activity activity,
                                       Bundle savedInstanceState) {
        mActivityLifecycleCallbacksRegistry.dispatchActivityCreated(activity, savedInstanceState);
    }

    void dispatchActivityStartedCompat(Activity activity) {
        mActivityLifecycleCallbacksRegistry.dispatchActivityStarted(activity);
    }

    void dispatchActivityResumedCompat(Activity activity) {
        mActivityLifecycleCallbacksRegistry.dispatchActivityResumed(activity);
    }

    void dispatchActivityPausedCompat(Activity activity) {
        mActivityLifecycleCallbacksRegistry.dispatchActivityPaused(activity);
    }

    void dispatchActivityStoppedCompat(Activity activity) {
        mActivityLifecycleCallbacksRegistry.dispatchActivityStopped(activity);
    }

    void dispatchActivitySaveInstanceStateCompat(Activity activity,
                                                 Bundle outState) {
        mActivityLifecycleCallbacksRegistry.dispatchActivitySaveInstanceState(activity, outState);
    }

    void dispatchActivityDestroyedCompat(Activity activity) {
        mActivityLifecycleCallbacksRegistry.dispatchActivityDestroyed(activity);
    }

    void dispatchActivityAttachFragmentCompat(Activity activity,
                                              Fragment fragment) {
        mActivityLifecycleCallbacksRegistry.dispatchActivityAttachFragment(activity, fragment);
    }

    public void registerSupportFragmentLifecycleCallbacks(FragmentLifecycleCallbacks callback) {
        mFragmentLifecycleCallbacksRegistry.registerFragmentLifecycleCallbacks(callback);
    }

    public void unregisterSupportFragmentLifecycleCallbacksCompat(FragmentLifecycleCallbacks callback) {
        mFragmentLifecycleCallbacksRegistry.unregisterFragmentLifecycleCallbacks(callback);
    }

    void dispatchFragmentActivityCreatedCompat(Fragment fragment,
                                               Bundle savedInstanceState) {
        mFragmentLifecycleCallbacksRegistry.dispatchFragmentActivityCreated(fragment, savedInstanceState);
    }

    void dispatchFragmentAttachCompat(Fragment fragment,
                                      Activity activity) {
        mFragmentLifecycleCallbacksRegistry.dispatchFragmentAttach(fragment, activity);
    }

    void dispatchFragmentDetachCompat(Fragment fragment) {
        mFragmentLifecycleCallbacksRegistry.dispatchFragmentDetach(fragment);
    }

    void dispatchFragmentCreatedCompat(Fragment fragment,
                                       Bundle savedInstanceState) {
        mFragmentLifecycleCallbacksRegistry.dispatchFragmentCreated(fragment, savedInstanceState);
    }

    void dispatchFragmentStartedCompat(Fragment fragment) {
        mFragmentLifecycleCallbacksRegistry.dispatchFragmentStarted(fragment);
    }

    void dispatchFragmentResumedCompat(Fragment fragment) {
        mFragmentLifecycleCallbacksRegistry.dispatchFragmentResumed(fragment);
    }

    void dispatchFragmentPausedCompat(Fragment fragment) {
        mFragmentLifecycleCallbacksRegistry.dispatchFragmentPaused(fragment);
    }

    void dispatchFragmentStoppedCompat(Fragment fragment) {
        mFragmentLifecycleCallbacksRegistry.dispatchFragmentStopped(fragment);
    }

    void dispatchFragmentSaveInstanceStateCompat(Fragment fragment,
                                                 Bundle outState) {
        mFragmentLifecycleCallbacksRegistry.dispatchFragmentSaveInstanceState(fragment, outState);
    }

    void dispatchFragmentDestroyedCompat(Fragment fragment) {
        mFragmentLifecycleCallbacksRegistry.dispatchFragmentDestroyed(fragment);
    }

    void dispatchFragmentCreateView(Fragment fragment,
                                    LayoutInflater inflater,
                                    ViewGroup container,
                                    Bundle savedInstanceState) {
        mFragmentLifecycleCallbacksRegistry.dispatchFragmentCreateView(fragment, inflater, container, savedInstanceState);
    }

    void dispatchFragmentViewCreated(Fragment fragment,
                                     View view,
                                     Bundle savedInstanceState) {
        mFragmentLifecycleCallbacksRegistry.dispatchFragmentViewCreated(fragment, view, savedInstanceState);
    }

    public void registerSupportServiceLifecycleCallbacks(ServiceLifecycleCallbacks callback) {
        mServiceLifecycleCallbacksRegistry.registerServiceLifecycleCallbacks(callback);
    }

    public void unregisterSupportServiceLifecycleCallbacksCompat(ServiceLifecycleCallbacks callback) {
        mServiceLifecycleCallbacksRegistry.unregisterServiceLifecycleCallbacks(callback);
    }

    void dispatchServiceAttachBaseContextCompat(Service service,
                                                Context newBase) {
        mServiceLifecycleCallbacksRegistry.dispatchServiceAttachBaseContext(service, newBase);
    }

    void dispatchServiceCreatedCompat(Service service) {
        mServiceLifecycleCallbacksRegistry.dispatchServiceCreated(service);
    }
}
