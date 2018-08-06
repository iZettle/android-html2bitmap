package com.izettle.html2bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class Html2Bitmap {

    private static final String TAG = "Html2Bitmap";
    private static final int MSG_MEASURE = 2;
    private static final int MSG_SCREENSHOT = 5;
    private final HandlerThread handlerThread;
    private final Handler backgroundHandler;
    private final Handler mainHandler;

    private final int delayMeasure = 30;
    private final int delayScreenShot = 30;
    private final String html;
    private final int paperWidth;
    private final Context context;
    int resourcesLoaded = 0;
    private int resourcesLoading = 0;
    private WebView webView;

    @AnyThread
    private Html2Bitmap(@NonNull final Context context, @NonNull String html, final int paperWidth, @NonNull final BitmapCallback callback) {
        this.context = context;
        this.html = html;
        this.paperWidth = paperWidth;

        mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (resourcesLoading > resourcesLoaded) {
                    Log.d(TAG, "waiting for resources...");
                    return;
                }

                if (webView.getContentHeight() == 0) {
                    pageFinished(delayMeasure);
                    return;
                }

                // set the correct height of the webview and do measure and layout using it before taking the screenshot
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(paperWidth, View.MeasureSpec.EXACTLY);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(webView.getContentHeight(), View.MeasureSpec.EXACTLY);
                webView.measure(widthMeasureSpec, heightMeasureSpec);
                webView.layout(0, 0, webView.getMeasuredWidth(), webView.getMeasuredHeight());

                backgroundHandler.removeMessages(MSG_SCREENSHOT);
                backgroundHandler.sendEmptyMessageDelayed(MSG_SCREENSHOT, delayScreenShot);
            }
        };

        handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();

        backgroundHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (resourcesLoading > resourcesLoaded) {
                    return;
                }

                if (webView.getMeasuredHeight() == 0) {
                    pageFinished(delayMeasure);
                    return;
                }
                try {
                    Bitmap screenshot = screenshot(webView);

                    callback.finished(screenshot);
                } catch (Throwable t) {

                    callback.error(t);
                }
                handlerThread.interrupt();
            }
        };
    }

    @Nullable
    public static Bitmap getBitmap(@NonNull Context context, @NonNull String html, int width) {
        return getBitmap(context, html, width, 15);
    }

    @Nullable
    public static Bitmap getBitmap(@NonNull Context context, @NonNull String html, int width, int timeout) {
        BitmapCallable bitmapCallable = new BitmapCallable();
        FutureTask<Bitmap> bitmapFutureTask = new FutureTask<>(bitmapCallable);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(bitmapFutureTask);

        final Html2Bitmap html2Bitmap = new Html2Bitmap(context, html, width, bitmapCallable);

        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                html2Bitmap.load();
            }
        });

        try {
            return bitmapFutureTask.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            Log.e(TAG, "", e);
        } finally {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    html2Bitmap.cleanup();
                }
            });
        }
        return null;
    }

    @MainThread
    private void load() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }

        webView = new WebView(context);

        webView.setInitialScale(100);

        webView.setVerticalScrollBarEnabled(false);

        final WebSettings settings = webView.getSettings();
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100 && resourcesLoading == resourcesLoaded) {
                    pageFinished(delayMeasure);
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                try {
                    URL resourceUrl = new URL(url);
                    return getResponse(resourceUrl);
                } catch (MalformedURLException ignored) {
                }

                return super.shouldInterceptRequest(view, url);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                try {
                    URL resourceUrl = new URL(request.getUrl().toString());
                    return getResponse(resourceUrl);
                } catch (MalformedURLException ignored) {
                }

                return super.shouldInterceptRequest(view, request);
            }

            WebResourceResponse getResponse(URL url) {

                String protocol = url.getProtocol();
                if (protocol.equals("http") || protocol.equals("https")) {

                    resourcesLoading++;
                    try {
                        URLConnection urlConnection = url.openConnection();
                        InputStream in = new InputStreamWrapper(new InputStreamWrapper.Callback() {
                            @Override
                            public void onClose() {
                                resourcesLoaded++;
                            }
                        }, urlConnection.getInputStream());
                        return new WebResourceResponse(urlConnection.getContentType(), urlConnection.getContentEncoding(), in);
                    } catch (Exception e) {
                        e.printStackTrace();
                        resourcesLoaded++;
                    }

                }

                return null;
            }
        });

        // set the correct height of the webview and do measure and layout using it before taking the screenshot
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(paperWidth, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(10, View.MeasureSpec.EXACTLY);
        webView.measure(widthMeasureSpec, heightMeasureSpec);
        webView.layout(0, 0, webView.getMeasuredWidth(), webView.getMeasuredHeight());

        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
    }

    @MainThread
    private void cleanup() {
        webView.stopLoading();

        mainHandler.removeCallbacksAndMessages(null);
        backgroundHandler.removeCallbacksAndMessages(null);
        handlerThread.quit();
    }

    private void pageFinished(int delay) {
        backgroundHandler.removeMessages(MSG_SCREENSHOT);
        mainHandler.removeMessages(MSG_MEASURE);
        mainHandler.sendEmptyMessageDelayed(MSG_MEASURE, delay);
    }

    private Bitmap screenshot(WebView webView) {

        Bitmap bitmap = Bitmap.createBitmap(webView.getMeasuredWidth(), webView.getMeasuredHeight(), Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG, 0));

        webView.draw(canvas);
        return bitmap;
    }
}
