package com.izettle.html2bitmap.content;

import android.content.Context;
import android.net.Uri;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.net.URL;

class WebViewRemoteContent extends WebViewContent {
    private URL url;

    WebViewRemoteContent(URL url) {
        this.url = url;
    }

    @Override
    public void loadContent(WebView webview) {
        webview.loadUrl(url.toString());
    }

    @Override
    public WebResourceResponse loadResourceImpl(Context context, WebViewResource webViewResource) {
        if (webViewResource.getUri().equals(Uri.parse(url.toString()))) {
            webViewResource.setNativeLoad();
            resourceLoaded();
            return null;
        }

        return getRemoteFile(webViewResource);
    }
}
