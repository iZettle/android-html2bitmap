package com.izettle.html2bitmap;

import android.support.annotation.MainThread;
import android.webkit.WebView;

interface Html2BitmapConfigurationCallback {
    @MainThread
    void configureWebView(WebView webview);
}
