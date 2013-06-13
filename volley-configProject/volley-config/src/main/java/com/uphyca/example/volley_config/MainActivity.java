
package com.uphyca.example.volley_config;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.view.Menu;
import butterknife.InjectView;
import butterknife.Views;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

public class MainActivity extends Activity {

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    @InjectView(R.id.imageView)
    NetworkImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Views.inject(this);
        ensureRequestQueue();
        fetchImage("http://developer.android.com/images/brand/Android_Robot_100.png");
    }

    private void ensureRequestQueue() {
        mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new MyHurlStack());
        mImageLoader = new ImageLoader(mRequestQueue, newImageCache(10));
    }

    private ImageLoader.ImageCache newImageCache(final int size) {
        return new ImageLoader.ImageCache() {
            private LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(size);

            @Override
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
        };
    }

    private void fetchImage(String url) {
        mImageView.setImageUrl(url, mImageLoader);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
