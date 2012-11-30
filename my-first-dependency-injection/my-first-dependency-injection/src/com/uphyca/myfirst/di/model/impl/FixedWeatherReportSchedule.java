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

import java.util.Calendar;

import com.uphyca.myfirst.di.model.WeatherReportSchedule;

/** 翌日午前9時を対象の時刻とする {@link WeatherReportSchedule}オブジェクト */
public class FixedWeatherReportSchedule implements WeatherReportSchedule {

    @Override
    public long getNextReportTimeMillis() {
        return getTomorrow9OclockMillis(System.currentTimeMillis());
    }

    /** 翌日の午前9時の日時を返す */
    private long getTomorrow9OclockMillis(long currentTimeMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTimeMillis);

        //一日進める
        cal.add(Calendar.DAY_OF_MONTH, 1);

        //午前9に設定する
        cal.set(Calendar.HOUR_OF_DAY, 9);

        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
