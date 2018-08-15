package com.izettle.html2bitmap.content;

import android.content.Context;
import android.net.Uri;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class WebViewAssetContent extends WebViewContent {
    static final String HTML2BITMAP_PROTOCOL = "html2bitmap";

    private String baseUrl = HTML2BITMAP_PROTOCOL + "://android_assets/";
    private String html;

    WebViewAssetContent(String html) {
        this.html = html;
    }

    @Override
    public void loadContent(WebView webview) {
        webview.loadDataWithBaseURL(baseUrl, html, "text/html", "utf-8", null);
    }

    @Override
    public WebResourceResponse loadResourceImpl(Context context, WebViewResource webViewResource) {
        String protocol = webViewResource.getUri().getScheme();
        if (protocol.equals(HTML2BITMAP_PROTOCOL)) {
            return getAssetFile(context, webViewResource);
        } else if (protocol.equals("http") || protocol.equals("https")) {
            return getRemoteFile(webViewResource);
        } else {
            webViewResource.setLoaded();
        }
        return null;
    }


    private WebResourceResponse getAssetFile(Context context, final WebViewResource webViewResource) {
        final Uri uri = webViewResource.getUri();
        if (uri.getScheme().equals(HTML2BITMAP_PROTOCOL)) {

            try {
                String mimeType = context.getContentResolver().getType(uri);

                InputStreamReader open = new InputStreamReader(context.getAssets().open(uri.getLastPathSegment()));
                String encoding = open.getEncoding();
                open.close();

                InputStream in = new InputStreamWrapper(new InputStreamWrapper.Callback() {
                    @Override
                    public void onClose() {
                        webViewResource.setLoaded();
                        resourceLoaded(webViewResource);
                    }
                }, context.getAssets().open(uri.getLastPathSegment()));
                return new WebResourceResponse(mimeType, encoding, in);
            } catch (IOException e) {
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
}
