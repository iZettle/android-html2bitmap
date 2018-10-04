package com.izettle.html2bitmap;

import android.webkit.WebView;

import androidx.annotation.MainThread;

interface Html2BitmapConfigurationCallback {
    @MainThread
    void configureWebView(WebView webview);
}
