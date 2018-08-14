package com.izettle.html2bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.net.Uri;
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

import com.izettle.html2bitmap.content.DoneListener;
import com.izettle.html2bitmap.content.WebViewContent;

import java.util.Map;

class Html2BitmapWebView implements DoneListener {
    private static final String TAG = "Html2Bitmap";
    private static final int MSG_MEASURE = 2;
    private static final int MSG_SCREENSHOT = 5;
    private final HandlerThread handlerThread;
    private final Handler backgroundHandler;
    private final Handler mainHandler;

    private final int measureDelay;
    private final WebViewContent content;
    private final int bitmapWidth;
    private final Context context;
    private BitmapCallback callback;
    private WebView webView;


    @AnyThread
    Html2BitmapWebView(@NonNull final Context context, @NonNull final WebViewContent content, final int bitmapWidth, final int measureDelay, final int screenshotDelay, final boolean strictMode) {
        this.context = context;
        this.content = content;
        this.bitmapWidth = bitmapWidth;
        this.measureDelay = measureDelay;

        mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (handlerThread.isInterrupted()) {
                    if (strictMode) {
                        throw new IllegalStateException();
                    }
                    Log.d(TAG, "stopped but received call on mainthread...");

                    return;
                }

                if (!content.done()) {
                    return;
                }

                if (webView.getContentHeight() == 0) {
                    pageFinished(measureDelay);
                    return;
                }

                // set the correct height of the webview and do measure and layout using it before taking the screenshot
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(bitmapWidth, View.MeasureSpec.EXACTLY);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(webView.getContentHeight(), View.MeasureSpec.EXACTLY);
                webView.measure(widthMeasureSpec, heightMeasureSpec);
                webView.layout(0, 0, webView.getMeasuredWidth(), webView.getMeasuredHeight());

                backgroundHandler.removeMessages(MSG_SCREENSHOT);
                backgroundHandler.sendEmptyMessageDelayed(MSG_SCREENSHOT, screenshotDelay);
            }
        };

        handlerThread = new HandlerThread("Html2BitmapHandlerThread");
        handlerThread.start();

        backgroundHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (!content.done()) {
                    return;
                }

                if (webView.getMeasuredHeight() == 0) {
                    pageFinished(measureDelay);
                    return;
                }
                try {
                    Bitmap screenshot = screenshot(webView);
                    callback.finished(screenshot);
                } catch (Throwable t) {

                    callback.error(t);
                }
            }
        };
    }


    @MainThread
    void load(@NonNull final BitmapCallback callback) {
        this.callback = callback;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }

        webView = new WebView(context);

        webView.setInitialScale(100);

        webView.setVerticalScrollBarEnabled(false);

        final WebSettings settings = webView.getSettings();
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);
        settings.setAllowUniversalAccessFromFileURLs(true);

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.i(TAG, "newProgress = " + newProgress + ", " + " done = " + content.done());
                content.setProgress(newProgress);
            }
        });

        content.setDoneListener(this);

        webView.setWebViewClient(new WebViewClient() {

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    WebResourceResponse webResourceResponse = content.loadResource(view.getContext(), Uri.parse(url));

                    return webResourceResponse != null ? webResourceResponse : super.shouldInterceptRequest(view, url);
                }

                return super.shouldInterceptRequest(view, url);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                Map<String, String> headers = request.getRequestHeaders();
                if (headers != null) {
                    headers.put("Origin", "http://html2bitmap/");
                }

                WebResourceResponse webResourceResponse = content.loadResource(view.getContext(), request.getUrl());
                WebResourceResponse response = webResourceResponse != null ? webResourceResponse : super.shouldInterceptRequest(view, request);

                return response;
            }
        });

        // set the correct height of the webview and do measure and layout using it before taking the screenshot
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(bitmapWidth, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(10, View.MeasureSpec.EXACTLY);
        webView.measure(widthMeasureSpec, heightMeasureSpec);
        webView.layout(0, 0, webView.getMeasuredWidth(), webView.getMeasuredHeight());
        content.loadContent(webView);
    }

    @MainThread
    void cleanup() {
        webView.stopLoading();
        mainHandler.removeCallbacksAndMessages(null);
        backgroundHandler.removeCallbacksAndMessages(null);
        handlerThread.interrupt();
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

    @Override
    public void imDone() {
        pageFinished(measureDelay);
    }
}
