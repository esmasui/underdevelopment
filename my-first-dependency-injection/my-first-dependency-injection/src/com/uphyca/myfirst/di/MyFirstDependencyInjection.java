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
package com.uphyca.myfirst.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.uphyca.myfirst.di.config.DevelopmentModule;
import com.uphyca.myfirst.di.config.ProductionModule;
import com.uphyca.myfirst.di.inject.InjectionApplication;

/**
 *  {@linkplain http://androidadvent.blogspot.jp/ Android Advent Calendar 2012} エントリ.
 *  {@linkplain http://d.hatena.ne.jp/esmasui/20121130/1354275028 はじめてのDependency Injection }
 *  
 */
public class MyFirstDependencyInjection extends InjectionApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        //Injectorを生成して設定する
        Injector injector = newInjector();
        setInjector(injector);
    }

    /**
     * Injectorを生成する
     */
    private Injector newInjector() {

        Module module;
        if (BuildConfig.DEBUG) {
            //デバッグ環境では開発用のモジュールを使う
            module = new DevelopmentModule();
        } else {
            //本番環境では本番用のモジュールを使う
            module = new ProductionModule();
        }

        //Injectorを生成して返す
        Injector injector = Guice.createInjector(module);
        return injector;
    }
}
