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
package com.uphyca.testing;

import java.lang.reflect.Field;

import org.mockito.Mock;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;

/** テストケースのフィールドにモックオブジェクトを設定し、同様のオブジェクトを登録するモジュール */
public class MockModule extends AbstractModule {

    /** テストケース */
    private final Object mTestCase;

    public MockModule(Object testCase) {
        mTestCase = testCase;
    }

    @Override
    protected void configure() {

        //テストケースに定義されたフィールドをイテレートする
        for (final Field each : mTestCase.getClass().getDeclaredFields()) {

            //フィールドに@Dependencyが注釈されていなければ無視する
            if (!each.isAnnotationPresent(Dependency.class)) {
                continue;
            }
            processField(each.getType());
        }
    }

    /** {@link Mock}が注釈されたフィールド型をモジュールに登録する */
    private void processField(Class<?> type) {

        //指定した型のモックオブジェクトを作成する
        Object mock = Mockito.mock(type);

        //モックオブジェクトをテスト対象のActivityにDIするために登録する
        bindMock(type, type.cast(mock));
    }

    /** 指定された型のオブジェクトをモジュールに登録する */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private final void bindMock(Class type,
                                final Object value) {

        //テスト対象オブジェクトにInjectionするための設定
        bind(type).toInstance(value);

        //テストクラスにInjectionするための設定
        bind(type).annotatedWith(Dependency.class).toInstance(value);
    }
}
