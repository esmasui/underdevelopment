package com.uphyca.mockitoonart;

import android.test.AndroidTestCase;

import java.io.File;

import static org.mockito.MockitoAnnotations.initMocks;


public class MockitoTestCase extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ensureDexCache();
        initMocks(this);
    }

    protected void ensureDexCache() throws Exception {
        File dexCacheDir = new File(getContext().getCacheDir(), "dexmaker");
        if (!dexCacheDir.exists() && !dexCacheDir.mkdirs()) {
            throw new IllegalStateException("Failed to create dexcache dir.");
        }
        System.setProperty("dexmaker.dexcache", dexCacheDir.getAbsolutePath());
    }
}
