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

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.Suppress;
import android.widget.TextView;

import com.uphyca.myfirst.di.MainActivity;
import com.uphyca.myfirst.di.MainActivityNonDependencyInjection;
import com.uphyca.myfirst.di.R;

/** {@link MainActivity}のユニットテスト */
public class MainActivityNonDependencyUnitTestCase extends ActivityUnitTestCase<MainActivityNonDependencyInjection> {

    private Intent mStartIntent;

    public MainActivityNonDependencyUnitTestCase() {
        super(MainActivityNonDependencyInjection.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mStartIntent = new Intent();
    }

    /** 事前条件を確認する */
    public void testPreconditions() {
        startActivity(mStartIntent, null, null);
        assertNotNull(getActivity());
    }

    /** スケジュールに従った時間に対して、天気予報オブジェクトから取得した天気が画面に表示されていることを確認する */
    public void testShowUnknownOnActivityCreated() {

        //-- Given
        // 設定すべき前提はない

        //-- When

        //Activityを起動する
        startActivity(mStartIntent, null, null);

        //-- Then

        //川崎の天気の表示がわかんないんだよになっていること
        TextView weatherKawasaki = (TextView) getActivity().findViewById(R.id.weather_kawasaki);
        assertEquals("川崎の天気はまだわかんないんだよ", weatherKawasaki.getText(), "まだわかんないんだよ");

        //横浜の天気の表示がわかんないんだよになっていること
        TextView weatherYokohama = (TextView) getActivity().findViewById(R.id.weather_yokohama);
        assertEquals("横浜の天気はまだわかんないんだよ", weatherYokohama.getText(), "まだわかんないんだよ");
    }

    /** スケジュールに従った時間に対して、天気予報オブジェクトから取得した天気が画面に表示されていることを確認する */
    @Suppress()
    public void testShowWeatherOnActivityCreated() {

        //-- Given
        // 設定すべき前提はない

        //-- When

        //Activityを起動する
        startActivity(mStartIntent, null, null);

        //-- Then

        //川崎の天気の表示が晴れになっていること
        TextView weatherKawasaki = (TextView) getActivity().findViewById(R.id.weather_kawasaki);
        assertEquals("川崎の天気は晴れ", weatherKawasaki.getText(), "晴れ");

        //横浜の天気の表示が曇になっていること
        TextView weatherYokohama = (TextView) getActivity().findViewById(R.id.weather_yokohama);
        assertEquals("横浜の天気は晴れ", weatherYokohama.getText(), "曇");
    }
}
