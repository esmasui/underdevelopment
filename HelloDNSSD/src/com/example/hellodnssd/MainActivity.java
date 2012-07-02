/*
 *    Copyright 2012 esmasui@gmail.com, masui@uphyca.com
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.example.hellodnssd;

import static java.lang.String.format;
import android.app.Activity;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdManager.RegistrationListener;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

/**
 * Activity that demonstrates publishing service.
 */
public class MainActivity extends Activity {

    /** The tag for logging */
    static final String TAG = "dnssd";

    /** The port of this service */
    static final int SERVICE_PORT = 8888;
    /** The type of this service */
    static final String SERVICE_TYPE = "_ipp._tcp.";
    /** The name of this service */
    static final String SERVICE_NAME = Build.MODEL + ' ' + "the pseudo printer";

    /**
     * Allocate ServiceInfo object for register service.
     * 
     * @return
     */
    static NsdServiceInfo allocateServiceInfo() {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setPort(SERVICE_PORT);
        serviceInfo.setServiceName(SERVICE_NAME);
        serviceInfo.setServiceType(SERVICE_TYPE);
        return serviceInfo;
    }

    /** The instance of NsdManager */
    NsdManager nsdManager;

    /**
     * Ensure SystemService objects.
     */
    void ensureSystemServices() {
        nsdManager = (NsdManager) getSystemService(NSD_SERVICE);
        if (nsdManager == null) {
            finish();
        }
    }

    /** The flag which the RegstrationListener was registered or not */
    boolean regstrationListenerRegistered;

    /** The RegstrationListener */
    RegistrationListener registrationListener = new NsdManager.RegistrationListener() {
        @Override
        public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.w(TAG, format("UnregstrationFailed serviceInfo=%s, errorCode=%d", serviceInfo, errorCode));
        }

        @Override
        public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
            Log.i(TAG, format("ServiceUnregisterd serviceInfo=%s", serviceInfo));
            regstrationListenerRegistered = false;
        }

        @Override
        public void onServiceRegistered(NsdServiceInfo serviceInfo) {
            Log.i(TAG, format("ServiceRegisterd serviceInfo=%s", serviceInfo));
            regstrationListenerRegistered = true;
        }

        @Override
        public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.w(TAG, format("RegstrationFailed serviceInfo=%s, errorCode=%d", serviceInfo, errorCode));
        }
    };

    /**
     * Register service to mDNS/DNS-SD.
     */
    void registerService() {
        NsdServiceInfo serviceInfo = allocateServiceInfo();
        int protocolType = NsdManager.PROTOCOL_DNS_SD;
        nsdManager.registerService(serviceInfo, protocolType, registrationListener);
    }

    /**
     * Unregister service from mDNS/DNS-SD.
     */
    void unregisterService() {
        if (regstrationListenerRegistered)
            nsdManager.unregisterService(registrationListener);
    }

    // Activity's lifecycle methods.

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ensureSystemServices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerService();
    }

    @Override
    protected void onPause() {
        unregisterService();
        super.onPause();
    }
}
