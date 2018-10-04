package com.izettle.html2bitmap.content;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;

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
