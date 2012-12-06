/*
 * Copyright (C) 2012 uPhyca Inc. http://www.uphyca.com/
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
package com.uphyca.lifecyclecallbacks;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;

public abstract class LifecycleCallbacksSupportIntentService extends IntentService {

    public LifecycleCallbacksSupportIntentService(String name) {
        super(name);
    }

    private LifecycleCallbacksSupportApplication getApplicationCompat() {
        return LifecycleCallbacksSupportApplication.applicationOf(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        dispatchAttachBaseContext(getApplicationCompat(), this, base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dispatchCreate(getApplicationCompat(), this);
    }

    private static void dispatchAttachBaseContext(LifecycleCallbacksSupportApplication application,
                                                  Service service,
                                                  Context newBase) {
        if (application == null) {
            return;
        }
        application.dispatchServiceAttachBaseContextCompat(service, newBase);
    }

    private static void dispatchCreate(LifecycleCallbacksSupportApplication application,
                                       Service service) {
        if (application == null) {
            return;
        }
        application.dispatchServiceCreatedCompat(service);
    }
}
