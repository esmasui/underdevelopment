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
package com.uphyca.myfirst.di.test;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.uphyca.myfirst.di.MainActivity;
import com.uphyca.myfirst.di.R;
import com.uphyca.myfirst.di.inject.InjectionActivityLifecycleCallbacks;
import com.uphyca.myfirst.di.model.WeatherReport;
import com.uphyca.myfirst.di.model.WeatherReportSchedule;
import com.uphyca.testing.InjectionActivityUnitTestCase;

/** 自分でモックオブジェクトを作成する{@link MainActivity}のユニットテスト */
public class HandWritingMockMainActivityUnitTestCase extends InjectionActivityUnitTestCase<MainActivity> {

    private Context mContext;
    private Intent mStartIntent;

    public HandWritingMockMainActivityUnitTestCase() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        mContext = getInstrumentation().getTargetContext();
        mStartIntent = new Intent();
    }
    
    /** スケジュールに従った時間に対して、天気予報オブジェクトから取得した天気が画面に表示されていることを確認する */
    public void testShowWeatherOnActivityCreated() {

        //-- Given

        //次の予報対象時間
        final long reportTimeMillis = 1L;
        //川崎の住所
        final String addressKawasaki = mContext.getString(R.string.address_kawasaki);
        //横浜の住所
        final String addressYokohama = mContext.getString(R.string.address_yokohama);

        //次の対象予報時間を返すようにモックオブジェクトを設定する
        final WeatherReportSchedule mockWeatherReportSchedule = new WeatherReportSchedule() {
            @Override
            public long getNextReportTimeMillis() {
                return reportTimeMillis;
            }
        };
        //川崎の予報として晴れを返すようにモックオブジェクトを設定する
        //横浜の予報として曇を返すようにモックオブジェクトを設定する
        final WeatherReport mockWeatherReport = new WeatherReport() {
            @Override
            public String reportWeather(String address,
                                        long date) {
                return address.equals(addressKawasaki) ? "晴れ" : address.equals(addressYokohama) ? "曇" : "まだわかんないよ";
            }
        };

        //モックオブジェクトをInjectionするためのモジュール
        Module mockModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(WeatherReportSchedule.class).toInstance(mockWeatherReportSchedule);
                bind(WeatherReport.class).toInstance(mockWeatherReport);
            }
        };

        //Injectionを行うオブジェクト
        Injector injector = Guice.createInjector(mockModule);

        //モックオブジェクトがActivityにInjectionされるようにコールバックを登録する
        Application app = new Application();
        app.registerActivityLifecycleCallbacks(new InjectionActivityLifecycleCallbacks(injector));
        setApplication(app);
        
        //-- When

        //Activityを起動する
        startActivity(mStartIntent, null, null);

        //-- Then

        //川崎の天気の表示が晴れになっていること
        TextView weatherKawasaki = (TextView) getActivity().findViewById(R.id.weather_kawasaki);
        assertEquals("川崎の天気は晴れ", weatherKawasaki.getText(), "晴れ");

        //横浜の天気の表示が曇になっていること
        TextView weatherYokohama = (TextView) getActivity().findViewById(R.id.weather_yokohama);
        assertEquals("横浜の天気は曇", weatherYokohama.getText(), "曇");
    }
}
