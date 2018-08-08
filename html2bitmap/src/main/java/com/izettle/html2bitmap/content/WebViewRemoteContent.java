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
    public WebResourceResponse loadResourceImpl(Context context, Uri uri) {
        if (uri.equals(Uri.parse(url.toString()))) {
            return null;
        }
        String protocol = uri.getScheme();
        if (protocol.equals("http") || protocol.equals("https")) {
            return getRemoteFile(uri);
        }
        return null;
    }
}
