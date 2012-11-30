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
package com.uphyca.myfirst.di.config;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.uphyca.myfirst.di.model.WeatherReport;
import com.uphyca.myfirst.di.model.WeatherReportSchedule;
import com.uphyca.myfirst.di.model.impl.FixedWeatherReportSchedule;
import com.uphyca.myfirst.di.model.impl.StubWeatherReport;

/**
 * 開発用のモジュール
 */
public class DevelopmentModule extends AbstractModule {

    @Override
    protected void configure() {

        //本番用の天気予報のスケジュール管理オブジェクトを使う
        bind(WeatherReportSchedule.class).to(FixedWeatherReportSchedule.class);
        
        //スタブの天気予報オブジェクトを使う
        bind(WeatherReport.class).to(StubWeatherReport.class).in(Scopes.SINGLETON);
    }
}
