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
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

/**
 * Activity that demonstrates publishing service.
 */
public class ResolveServiceActivity extends Activity {

    /** The tag for logging */
    static final String TAG = "dnssd";

    /** The type of service for searching */
    static final String SERVICE_TYPE = "_raop._tcp.";

    /**
     * Start discovery
     */
    private void startDiscovery() {
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    /**
     * Stop discovery
     */
    private void stopDiscovery() {
        if(discoveryStarted)
            nsdManager.stopServiceDiscovery(discoveryListener);
    }

    /** The instance of NsdManager */
    NsdManager nsdManager;

    boolean discoveryStarted;
    
    /**
     * Ensure SystemService objects.
     */
    void ensureSystemServices() {
        nsdManager = (NsdManager) getSystemService(NSD_SERVICE);
        if (nsdManager == null) {
            finish();
        }
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
        startDiscovery();
    }

    @Override
    protected void onPause() {
        stopDiscovery();
        super.onPause();
    }

    /**
     * The DiscoveryListener
     */
    NsdManager.DiscoveryListener discoveryListener = new NsdManager.DiscoveryListener() {
        
        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            Log.w(TAG, format("Failed to stop discovery serviceType=%s, errorCode=%d", serviceType, errorCode));
        }
        
        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            Log.w(TAG, format("Failed to start discovery serviceType=%s, errorCode=%d", serviceType, errorCode));
        }
        
        @Override
        public void onServiceLost(NsdServiceInfo serviceInfo) {
            Log.i(TAG, format("Service lost serviceInfo=%s", serviceInfo));
        }
        
        @Override
        public void onServiceFound(NsdServiceInfo serviceInfo) {
            Log.i(TAG, format("Service found serviceInfo=%s", serviceInfo));
            nsdManager.resolveService(serviceInfo, resolveListener);
        }
        
        @Override
        public void onDiscoveryStopped(String serviceType) {
            discoveryStarted = false;
            Log.i(TAG, format("Discovery stopped serviceType=%s", serviceType));
        }
        
        @Override
        public void onDiscoveryStarted(String serviceType) {
            discoveryStarted = true;
            Log.i(TAG, format("Discovery started serviceType=%s", serviceType));
        }
    };
    
    /**
     * The ResolverListener
     */    
    NsdManager.ResolveListener resolveListener = new NsdManager.ResolveListener() {
        
        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.i(TAG, format("Service resolved serviceInfo=%s", serviceInfo));
        }
        
        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.w(TAG, format("Failed to resolve serviceInfo=%s, errorCode=%d", serviceInfo, errorCode));
        }
    };
}
