package com.izettle.html2bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.izettle.html2bitmap.content.WebViewContent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class Html2Bitmap {

    private static final String TAG = "Html2Bitmap";
    private final Context context;
    private final WebViewContent content;
    private final int bitmapWidth;
    private final int measureDelay;
    private final int screenshotDelay;
    private boolean strictMode;
    private long timeout;
    @Nullable
    private Integer textZoom;
    @Nullable
    private Html2BitmapConfigurator html2BitmapConfigurator;

    private Html2Bitmap(@NonNull Context context, @NonNull WebViewContent content, int bitmapWidth, int measureDelay, int screenshotDelay, boolean strictMode, long timeout, @Nullable Integer textZoom, @Nullable Html2BitmapConfigurator html2BitmapConfigurator) {
        this.context = context;
        this.content = content;
        this.bitmapWidth = bitmapWidth;
        this.measureDelay = measureDelay;
        this.screenshotDelay = screenshotDelay;
        this.strictMode = strictMode;
        this.timeout = timeout;
        this.textZoom = textZoom;
        this.html2BitmapConfigurator = html2BitmapConfigurator;
    }

    @Nullable
    private static Bitmap getBitmap(final Html2Bitmap html2Bitmap) {
        final BitmapCallable bitmapCallable = new BitmapCallable();
        FutureTask<Bitmap> bitmapFutureTask = new FutureTask<>(bitmapCallable);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(bitmapFutureTask);

        Handler mainHandler = new Handler(html2Bitmap.context.getMainLooper());

        final Html2BitmapWebView html2BitmapWebView = new Html2BitmapWebView(html2Bitmap.context, html2Bitmap.content, html2Bitmap.bitmapWidth, html2Bitmap.measureDelay, html2Bitmap.screenshotDelay, html2Bitmap.strictMode, html2Bitmap.textZoom, html2Bitmap.html2BitmapConfigurator);
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                html2BitmapWebView.load(bitmapCallable);
            }
        });

        try {
            return bitmapFutureTask.get(html2Bitmap.timeout, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {

            Log.e(TAG, html2Bitmap.content.getRemoteResources().toString(), e);
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

    /**
     * Will generate a bitmap from the provided {@link WebViewContent}. The process will be performed
     * mainly in the background (a {@link android.webkit.WebView} requires some calls to be made from
     * the main thread). It will return the bitmap once done or wait for {@link Builder#setTimeout(long)}
     * milliseconds if the process fails to terminate in a timely fashion.
     *
     * @return The generated bitmap or null if the process failed.
     */
    @Nullable
    public Bitmap getBitmap() {
        return getBitmap(this);
    }

    public WebViewContent getWebViewContent() {
        return content;
    }

    public static class Builder {
        private Context context;
        private int bitmapWidth = 480;
        private int measureDelay = 300;
        private int screenshotDelay = 300;
        private boolean strictMode = false;
        private long timeout = 15;
        private WebViewContent content;
        @Nullable
        private Integer textZoom = null;
        @Nullable
        private Html2BitmapConfigurator html2BitmapConfigurator;

        public Builder() {
        }

        public Builder(@NonNull Context context, @NonNull WebViewContent content) {
            setContext(context);
            setContent(content);
        }

        /**
         * The context to use
         */
        public Builder setContext(@NonNull Context context) {
            this.context = context;
            return this;
        }

        /**
         * The content to generate a bitmap from
         */
        public Builder setContent(@NonNull WebViewContent content) {
            this.content = content;
            return this;
        }

        /**
         * The width of the resulting bitmap
         */
        public Builder setBitmapWidth(int bitmapWidth) {
            this.bitmapWidth = bitmapWidth;
            return this;
        }

        /**
         * After indications that the webview is done we wait this delay before requesting a measure
         * from the webview as sometimes the webview reports being done several times so we can't be quite sure.
         */
        public Builder setMeasureDelay(int measureDelay) {
            this.measureDelay = measureDelay;
            return this;
        }

        /**
         * The delay to wait from the last request for a measure before taking the screenshot
         */
        public Builder setScreenshotDelay(int screenshotDelay) {
            this.screenshotDelay = screenshotDelay;
            return this;
        }

        /**
         * Verify that no handler calls are made to the main handler
         */
        public Builder setStrictMode(boolean strictMode) {
            this.strictMode = strictMode;
            return this;
        }

        /**
         * Set the timeout for the entire process of generating a bitmap from html
         */
        public Builder setTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * If provided sent into {@link android.webkit.WebSettings#setTextZoom(int)}
         */
        public Builder setTextZoom(Integer textZoom) {
            this.textZoom = textZoom;
            return this;
        }

        /**
         * A chance to do any configurations on the webview that might be lacking in this builder.
         */
        public Builder setConfigurator(@Nullable Html2BitmapConfigurator html2BitmapConfigurator) {
            this.html2BitmapConfigurator = html2BitmapConfigurator;

            return this;
        }

        public Html2Bitmap build() {
            if (context == null) {
                throw new NullPointerException();
            }
            if (content == null) {
                throw new NullPointerException();
            }
            return new Html2Bitmap(context, content, bitmapWidth, measureDelay, screenshotDelay, strictMode, timeout, textZoom, html2BitmapConfigurator);
        }
    }
}
