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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.inject.Inject;

//FragmentActivity(android-support-v4.jar)に依存
public class MainActivity extends FragmentActivity {

    //@Inject(guice-3.0-no_aop.jar)に依存
    @Inject
    private String _message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);

        tv.setText(_message);

        setContentView(tv);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        MyApplication app = MyApplication.class.cast(newBase.getApplicationContext());

        //Injector(guice-3.0-no_aop.jar)に依存
        app.getInjector()
           .injectMembers(this);
    }
}
