
package com.uphyca.example.volley_config;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import javax.net.ssl.SSLSocketFactory;

import com.android.volley.toolbox.HurlStack;

public class MyHurlStack extends HurlStack {

    public MyHurlStack() {
    }

    public MyHurlStack(UrlRewriter urlRewriter) {
        super(urlRewriter);
    }

    public MyHurlStack(UrlRewriter urlRewriter, SSLSocketFactory sslSocketFactory) {
        super(urlRewriter, sslSocketFactory);
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {

        //
        Proxy proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved("109.174.157.97", 8080));
        HttpURLConnection returnThis = (HttpURLConnection) url.openConnection(proxy);

        //
        returnThis.setRequestProperty("User-Agent", "Mozilla/5.0");

        return returnThis;
    }
}
