package com.izettle.html2bitmap.content;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RestrictTo;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class WebViewContent {


    private List<WebViewResource> webViewResources = new ArrayList<>();
    private WeakReference<ProgressChangedListener> doneListenerWeakReference;

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
        return getLoadingResources().size() == 0;
    }

    abstract WebResourceResponse loadResourceImpl(Context context, WebViewResource webViewResource);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public final WebResourceResponse loadResource(Context context, Uri uri) {

        WebViewResource webViewResource = new WebViewResource(uri);
        webViewResources.add(webViewResource);

        return loadResourceImpl(context, webViewResource);
    }

    protected WebResourceResponse getRemoteFile(final WebViewResource webViewResource) {
        Uri uri = webViewResource.getUri();
        String protocol = uri.getScheme();
        if (protocol.equals("http") || protocol.equals("https")) {

            try {
                URL url = new URL(uri.toString());
                URLConnection urlConnection = url.openConnection();
                InputStream in = new InputStreamWrapper(new InputStreamWrapper.Callback() {
                    @Override
                    public void onClose() {
                        webViewResource.setLoaded();
                        resourceLoaded(webViewResource);
                    }
                }, urlConnection.getInputStream());

                WebResourceResponse webResourceResponse = new WebResourceResponse(urlConnection.getContentType(), urlConnection.getContentEncoding(), in);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
                    Map<String, String> responseHeaders = new HashMap<>();

                    for (String key : headerFields.keySet()) {
                        responseHeaders.put(key, headerFields.get(key).get(0));
                    }
                    webResourceResponse.setResponseHeaders(responseHeaders);

                }
                return webResourceResponse;
            } catch (Exception e) {
                e.printStackTrace();
                webViewResource.setException(e);
                resourceLoaded(webViewResource);
            }
        } else {
            webViewResource.setNativeLoad();
            resourceLoaded(webViewResource);
        }
        return null;
    }

    public List<WebViewResource> getRemoteResources() {
        return webViewResources;
    }

    public List<WebViewResource> getWebViewResources() {
        List<WebViewResource> loaded = new ArrayList<>();
        for (WebViewResource webViewResource : webViewResources) {
            if (webViewResource.isLoaded()) {
                loaded.add(webViewResource);
            }
        }

        return loaded;
    }

    public List<WebViewResource> getLoadingResources() {
        List<WebViewResource> loading = new ArrayList<>();
        for (WebViewResource webViewResource : webViewResources) {
            if (!webViewResource.isLoaded()) {
                loading.add(webViewResource);
            }
        }

        return loading;
    }

    void resourceLoaded(WebViewResource webViewResource) {
        ProgressChangedListener progressChangedListener = this.doneListenerWeakReference.get();
        if (done() && this.doneListenerWeakReference != null) {
            progressChangedListener.progressChanged();
        }
    }

    public void setDoneListener(ProgressChangedListener progressChangedListener) {
        this.doneListenerWeakReference = new WeakReference<>(progressChangedListener);
    }
}
