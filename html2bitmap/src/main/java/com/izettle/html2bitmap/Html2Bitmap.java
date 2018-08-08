package com.izettle.html2bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.izettle.html2bitmap.content.WebViewContent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class Html2Bitmap {

    private static final String TAG = "Html2Bitmap";
    private final Context context;
    private final WebViewContent content;
    private final int bitmapWidth;
    private final int measureDelay;
    private final int screenshotDelay;
    private boolean strictMode;
    private long timeout;

    private Html2Bitmap(Context context, WebViewContent content, int bitmapWidth, int measureDelay, int screenshotDelay, boolean strictMode, long timeout) {
        this.context = context;
        this.content = content;
        this.bitmapWidth = bitmapWidth;
        this.measureDelay = measureDelay;
        this.screenshotDelay = screenshotDelay;
        this.strictMode = strictMode;
        this.timeout = timeout;
    }

    @Nullable
    private static Bitmap getBitmap(final Html2Bitmap html2Bitmap) {
        final BitmapCallable bitmapCallable = new BitmapCallable();
        FutureTask<Bitmap> bitmapFutureTask = new FutureTask<>(bitmapCallable);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(bitmapFutureTask);

        Handler mainHandler = new Handler(html2Bitmap.context.getMainLooper());

        final Html2BitmapWebView html2BitmapWebView = new Html2BitmapWebView(html2Bitmap.context, html2Bitmap.content, html2Bitmap.bitmapWidth, html2Bitmap.measureDelay, html2Bitmap.screenshotDelay, html2Bitmap.strictMode);
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                html2BitmapWebView.load(bitmapCallable);
            }
        });

        try {
            return bitmapFutureTask.get(html2Bitmap.timeout, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            Log.e(TAG, "", e);
        } finally {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    html2BitmapWebView.cleanup();
                }
            });
        }
        return null;
    }

    public Bitmap getBitmap() {
        return getBitmap(this);
    }

    public static class Builder {
        private Context context;
        private int bitmapWidth = 480;
        private int measureDelay = 300;
        private int screenshotDelay = 300;
        private boolean strictMode = false;
        private long timeout = 15;
        private WebViewContent content;

        public Builder() {

        }

        public Builder(@NonNull Context context, @NonNull WebViewContent content) {
            setContext(context);
            setContent(content);
        }

        public Builder setContext(@NonNull Context context) {
            this.context = context;
            return this;
        }

        public Builder setContent(@NonNull WebViewContent content) {
            this.content = content;
            return this;
        }

        public Builder setBitmapWidth(int bitmapWidth) {
            this.bitmapWidth = bitmapWidth;
            return this;
        }

        public Builder setMeasureDelay(int measureDelay) {
            this.measureDelay = measureDelay;
            return this;
        }

        public Builder setScreenshotDelay(int screenshotDelay) {
            this.screenshotDelay = screenshotDelay;
            return this;
        }

        public Builder setStrictMode(boolean strictMode) {
            this.strictMode = strictMode;
            return this;
        }

        public Builder setTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Html2Bitmap build() {
            if (context == null) {
                throw new NullPointerException();
            }
            if (content == null) {
                throw new NullPointerException();
            }
            return new Html2Bitmap(context, content, bitmapWidth, measureDelay, screenshotDelay, strictMode, timeout);
        }
    }
}
