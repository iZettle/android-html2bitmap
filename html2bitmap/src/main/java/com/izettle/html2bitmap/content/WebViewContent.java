package com.izettle.html2bitmap.content;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.RestrictTo;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class WebViewContent {

    protected AtomicInteger work = new AtomicInteger(0);

    /***
     * Supports loading local files from /assets
     *
     * @param html An html formatted string e.g. "<html><body><p>Hello world</p></body></html>
     */
    public static WebViewContent html(String html) {
        return new WebViewAssetContent(html);
    }

    /***
     * @param url a remote url to load e.g. "https://www.example.com" and take a screenshot of
     */
    public static WebViewContent url(URL url) {
        return new WebViewRemoteContent(url);
    }

    public abstract void loadContent(WebView webview);

    public boolean done() {
        return work.get() == 0;
    }

    abstract WebResourceResponse loadResourceImpl(Context context, Uri uri);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public final WebResourceResponse loadResource(Context context, Uri uri) {
        work.incrementAndGet();
        try {
            return loadResourceImpl(context, uri);
        } finally {
            work.decrementAndGet();
        }
    }

    protected WebResourceResponse getRemoteFile(Uri uri) {
        String protocol = uri.getScheme();
        if (protocol.equals("http") || protocol.equals("https")) {

            work.incrementAndGet();
            try {
                URL url = new URL(uri.toString());
                URLConnection urlConnection = url.openConnection();
                InputStream in = new InputStreamWrapper(new InputStreamWrapper.Callback() {
                    @Override
                    public void onClose() {
                        work.decrementAndGet();
                    }
                }, urlConnection.getInputStream());
                return new WebResourceResponse(urlConnection.getContentType(), urlConnection.getContentEncoding(), in);
            } catch (Exception e) {
                e.printStackTrace();
                work.decrementAndGet();
            }

        }
        return null;
    }
}
