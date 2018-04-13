package com.example.html2bitmapdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.izettle.html2bitmap.Html2Bitmap;

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

        return Html2Bitmap.getBitmap(context, html, width, 15);
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
