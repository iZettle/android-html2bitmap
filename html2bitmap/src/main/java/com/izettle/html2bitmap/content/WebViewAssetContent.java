package com.izettle.html2bitmap.content;

import android.content.Context;
import android.net.Uri;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class WebViewAssetContent extends WebViewContent {
    static final String HTML2BITMAP_PROTOCOL = "http";

    private String baseUrl = HTML2BITMAP_PROTOCOL + "://html2bitmap/";
    private String html;

    WebViewAssetContent(String html) {
        this.html = html;
    }

    @Override
    public void loadContent(WebView webview) {
        webview.loadDataWithBaseURL(baseUrl, html, "text/html", "utf-8", null);
    }

    @Override
    public WebResourceResponse loadResourceImpl(Context context, Uri uri) {
        String protocol = uri.getScheme();
        if (protocol.equals(HTML2BITMAP_PROTOCOL) && uri.getHost().equals("html2bitmap")) {
            return getAssetFile(context, uri);
        } else if (protocol.equals("http") || protocol.equals("https")) {
            return getRemoteFile(uri);
        }
        return null;
    }


    private WebResourceResponse getAssetFile(Context context, Uri uri) {
        if (uri.getScheme().equals(HTML2BITMAP_PROTOCOL)) {
            work.incrementAndGet();

            try {
                String mimeType = context.getContentResolver().getType(uri);

                InputStreamReader open = new InputStreamReader(context.getAssets().open(uri.getLastPathSegment()));
                String encoding = open.getEncoding();
                open.close();

                InputStream in = new InputStreamWrapper(new InputStreamWrapper.Callback() {
                    @Override
                    public void onClose() {
                        work.decrementAndGet();
                        progressChanged();
                    }
                }, context.getAssets().open(uri.getLastPathSegment()));
                return new WebResourceResponse(mimeType, encoding, in);
            } catch (IOException e) {
                e.printStackTrace();
                work.decrementAndGet();
                progressChanged();
            }
        }

        return null;
    }
}
