package com.izettle.html2bitmap;

import android.graphics.Bitmap;

public interface BitmapCallback {
    void finished(Bitmap bitmap);

    void error(Throwable error);
}
