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
package com.example.my_app;

import android.app.Application;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class MyApplication extends Application {

    //Injector(guice-3.0-no_aop.jar)に依存
    private Injector injector;

    @Override
    public void onCreate() {
        super.onCreate();
        
        //Guice, Injector, AbstractModule(guice-3.0-no_aop.jar)に依存
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(String.class).toInstance("Hello, world.");
            }
        });
    }

    //Injector(guice-3.0-no_aop.jar)に依存
    Injector getInjector() {
        return injector;
    }
}
