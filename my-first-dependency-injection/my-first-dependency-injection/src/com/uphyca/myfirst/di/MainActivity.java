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

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.inject.Inject;
import com.uphyca.myfirst.di.model.WeatherReport;
import com.uphyca.myfirst.di.model.WeatherReportSchedule;

/** 川崎と横浜の天気予報を表示する画面 */
public class MainActivity extends Activity {

    /** 天気予報のスケジュールを管理するオブジェクト */
    @Inject
    private WeatherReportSchedule mWeatherReportSchedule;

    /** 天気予報オブジェクト */
    @Inject
    private WeatherReport mWeatherReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewCreated();
    }

    /** ビューが作成された後の処理を行う */
    private void viewCreated() {

        //予報の対象時間
        long reportTimeMillis = mWeatherReportSchedule.getNextReportTimeMillis();

        //川崎の天気を設定する
        TextView fieldKawasaki = viewById(R.id.weather_kawasaki);
        String addressKawasaki = getString(R.string.address_kawasaki);
        String weatherKawasaki = mWeatherReport.reportWeather(addressKawasaki, reportTimeMillis);
        fieldKawasaki.setText(weatherKawasaki);

        //横浜の天気を設定する
        TextView fieldYokohama = viewById(R.id.weather_yokohama);
        String addressYokohama = getString(R.string.address_yokohama);
        String weatherYokohama = mWeatherReport.reportWeather(addressYokohama, reportTimeMillis);
        fieldYokohama.setText(weatherYokohama);
    }

    /** 指定のIDを持つUIコンポーネントを返す */
    @SuppressWarnings("unchecked")
    private <T> T viewById(int resId) {
        return (T) findViewById(resId);
    }
}
