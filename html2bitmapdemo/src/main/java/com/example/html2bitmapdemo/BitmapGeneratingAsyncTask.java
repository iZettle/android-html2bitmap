package com.example.html2bitmapdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.webkit.WebView;

import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.Html2BitmapConfigurator;
import com.izettle.html2bitmap.content.WebViewContent;

import java.lang.ref.WeakReference;

class BitmapGeneratingAsyncTask extends AsyncTask<Void, Void, Bitmap> {

    private final WeakReference<Context> context;
    private final String html;
    private final int width;
    private final WeakReference<Callback> callback;

    BitmapGeneratingAsyncTask(Context context, String html, int width, Callback callback) {
        this.context = new WeakReference<>(context.getApplicationContext());
        this.html = html;
        this.width = width;
        this.callback = new WeakReference<>(callback);
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Context context = this.context.get();

        Html2BitmapConfigurator html2BitmapConfigurator = new Html2BitmapConfigurator() {
            @Override
            public void configureWebView(WebView webview) {
                webview.setBackgroundColor(Color.MAGENTA);
                webview.getSettings().setTextZoom(150);
            }
        };

        Html2Bitmap build = new Html2Bitmap.Builder()
                .setContext(context)
                .setContent(WebViewContent.html(html))
                .setBitmapWidth(width)
                .setMeasureDelay(10)
                .setScreenshotDelay(10)
                .setStrictMode(true)
                .setTimeout(5)
                .setTextZoom(150)
                .setConfigurator(html2BitmapConfigurator)
                .build();

        return build.getBitmap();

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        Callback bitmapCallback = this.callback.get();
        if (bitmapCallback != null) {
            bitmapCallback.done(bitmap);
        }
    }

    interface Callback {
        void done(Bitmap bitmap);
    }
}
