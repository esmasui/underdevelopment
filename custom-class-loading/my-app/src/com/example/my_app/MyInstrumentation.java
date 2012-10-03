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

import java.io.File;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import dalvik.system.PathClassLoader;

public class MyInstrumentation extends Instrumentation {

    /** my-app と my-library を対象にしたクラスローダー. */
    private ClassLoader myClassLoader;

    @Override
    public void onCreate(Bundle arguments) {

        super.onCreate(arguments);

        getCustomLoader(getTargetContext());
    }

    private ClassLoader getCustomLoader(Context context) {
        if (myClassLoader != null)
            return myClassLoader;
        myClassLoader = buildCustomClassLoader(context);
        return myClassLoader;
    }

    /**
     * パッケージの.apkファイルのパスを返す
     */
    private String getLibraryCodePath(Context context,
                                      String packageName) throws NameNotFoundException {
        int flag = Context.CONTEXT_RESTRICTED | Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY;
        Context libraryContext = context.createPackageContext(packageName, flag);
        String packageCodePath = libraryContext.getPackageCodePath();
        return packageCodePath;
    }

    private PathClassLoader buildCustomClassLoader(Context context) {
        String libraryCodePath = "";
        try {
            //my_libraryの.apkのパスを得る
            libraryCodePath = getLibraryCodePath(context, "com.example.my_library");
        } catch (NameNotFoundException ignore) {
        }

        //my_appのクラスローダーの親クラスローダーを得る
        //        final ClassLoader parent = context.getClassLoader()
        //                                          .getParent();
        final ClassLoader parent = ClassLoader.getSystemClassLoader();
        if (parent == null)
            throw new IllegalStateException();
        //my_app, my_libraryをセパレータで区切ってdexPathを構成する
        String dexPath = libraryCodePath + File.pathSeparator + context.getPackageCodePath();
        //dexPathを指定してPathClassLoaderを構成する
        PathClassLoader customLoader = new PathClassLoader(dexPath,
                                                           parent);
        return customLoader;
    }

    /**
     * カスタムしたアプリケーションでActivityをロードする.
     */
    @Override
    public Activity newActivity(ClassLoader cl,
                                String className,
                                Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newActivity(getCustomLoader(getTargetContext()), className, intent);
    }

    /**
     * カスタムしたクラスローダーでApplicationをロードする.
     */
    @Override
    public Application newApplication(ClassLoader cl,
                                      String className,
                                      Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(getCustomLoader(context), className, context);
    }
}
