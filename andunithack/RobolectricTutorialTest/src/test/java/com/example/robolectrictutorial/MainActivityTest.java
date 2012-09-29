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
package com.example.robolectrictutorial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;
import android.widget.TextView;

import com.example.robolectrictutorial.MainActivity;
import com.example.robolectrictutorial.R;
import com.uphyca.testing.robolectric.RobolectricActivityUnitTestCase;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest extends RobolectricActivityUnitTestCase<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    private Intent _startIntent;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        _startIntent = new Intent(Intent.ACTION_MAIN);
        startActivity(_startIntent, null, null);
    }

    @Test
    public void assertPreconditions() {

        assertNotNull(getActivity());
    }

    @Test
    public void shouldHaveTheMessage() {

        getInstrumentation().callActivityOnCreate(getActivity(), null);

        TextView text = (TextView) getActivity().findViewById(R.id.hello);
        assertEquals("Hello world!", text.getText()
                                         .toString());
    }
}
