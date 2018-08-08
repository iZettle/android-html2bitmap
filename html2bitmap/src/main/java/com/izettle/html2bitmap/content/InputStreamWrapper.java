package com.izettle.html2bitmap.content;

import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

class InputStreamWrapper extends BufferedInputStream {

    private final Callback callback;

    InputStreamWrapper(Callback callback, @NonNull InputStream in) {
        super(in);
        this.callback = callback;
    }

    @Override
    public void close() throws IOException {
        super.close();
        callback.onClose();
    }

    interface Callback {
        void onClose();
    }
}
