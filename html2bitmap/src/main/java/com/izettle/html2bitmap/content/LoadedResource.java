package com.izettle.html2bitmap.content;

import android.net.Uri;

public class LoadedResource {

    private final Uri uri;
    private boolean loaded = false;

    public LoadedResource(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    @Override
    public String toString() {
        return "LoadedResource{" +
                "uri=" + uri +
                ", loaded=" + loaded +
                '}';
    }
}
