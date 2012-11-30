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
package com.uphyca.myfirst.di.model.impl;

import java.util.Random;

import com.uphyca.myfirst.di.model.WeatherReport;

/**
 * 天気予報オブジェクトのスタブ
 */
public class StubWeatherReport implements WeatherReport {

    /**
     * ランダムに返す天気予報のリスト 
     */
    private static final String[] mWeathers = { "晴れ", "曇", "雨" };

    private Random mRandom = new Random();

    /** ランダムな天気予報を返す */
    @Override
    public String reportWeather(String address,
                                long date) {
        return mWeathers[mRandom.nextInt(mWeathers.length)];
    }
}
