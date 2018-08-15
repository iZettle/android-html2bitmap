package com.izettle.html2bitmap.content;

import android.net.Uri;

public class WebViewResource {

    private final Uri uri;
    private boolean loaded = false;
    private boolean nativeLoad = false;
    private Exception exception;

    public WebViewResource(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded() {
        this.loaded = true;
    }

    @Override
    public String toString() {
        return "WebViewResource{" +
                "uri=" + uri +
                ", loaded=" + loaded +
                ", nativeLoad=" + nativeLoad +
                ", exception=" + exception +
                '}';
    }

    public void setNativeLoad() {
        setLoaded();
        this.nativeLoad = true;
    }

    public boolean isNativeLoad() {
        return nativeLoad;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        setLoaded();
        this.exception = exception;
    }
}
