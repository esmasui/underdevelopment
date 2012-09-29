/*
 * Copyright (C) 2012 uPhyca Inc. & @vvakame
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
package com.example.robolectric.test;

import com.example.robolectrictutorial.MainActivityTest;
import com.uphyca.testing.AndroidJUnit4TestAdapter;

public class MainActivityAndroidTest {

    public static junit.framework.Test suite() {
        return new AndroidJUnit4TestAdapter(MainActivityTest.class);
    }
}
