package com.izettle.html2bitmap;

import android.graphics.Bitmap;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

class BitmapCallable implements Callable<Bitmap>, BitmapCallback {

    private CountDownLatch latch = new CountDownLatch(1);
    private Bitmap bitmap;

    BitmapCallable() {
    }

    @Override
    public Bitmap call() throws Exception {
        latch.await();
        return bitmap;
    }

    @Override
    public void finished(Bitmap bitmap) {
        this.bitmap = bitmap;
        latch.countDown();
    }

    @Override
    public void error(Throwable error) {
        latch.countDown();
    }
}
