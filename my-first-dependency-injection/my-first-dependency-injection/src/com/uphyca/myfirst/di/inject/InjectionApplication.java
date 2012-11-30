/*
 * Copyright (C) 2012 uPhyca Inc. http://www.uphyca.com/
 * 
 * Android Advent Calendar 2012 http://androidadvent.blogspot.jp/
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
package com.uphyca.myfirst.di.inject;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.logging.Level;

import android.app.Application;

import com.google.inject.Injector;

/** 生成されたActivityにDependency Injectionを行うApplication */
public class InjectionApplication extends Application {

    /** ActivityにDIするためのコールバックオブジェクト */
    private InjectionActivityLifecycleCallbacks mCallbacks;

    /** 現在設定されているInjectorオブジェクト */
    private Reference<Injector> mInjector;

    /** このアプリケーションで使う{@link Injector}を設定する */
    public Injector setInjector(Injector newInjector) {
        synchronized (this) {
            final Injector previousInjector = (mInjector != null) ? mInjector.get() : null;
            mInjector = new WeakReference<Injector>(newInjector);
            unregisterPreviousCallbacks();
            registerCallbacks(newInjector);
            return previousInjector;
        }
    }

    /**　ActivityにDIするためのコールバックオブジェクトを登録する */
    private void registerCallbacks(Injector injector) {
        ActivityLifecycleCallbacks container = new InjectionActivityLifecycleCallbacks(injector);
        registerActivityLifecycleCallbacks(container);
    }

    /** コールバックがすでに登録されている場合は除去する */
    private void unregisterPreviousCallbacks() {
        if (mCallbacks == null) {
            return;
        }
        unregisterActivityLifecycleCallbacks(mCallbacks);
    }

    static {
        //Android環境でGuiceが出力するWARNログを抑制する
        java.util.logging.Logger.getLogger(com.google.inject.internal.util.$FinalizableReferenceQueue.class.getName()).setLevel(Level.SEVERE);
    }
}
