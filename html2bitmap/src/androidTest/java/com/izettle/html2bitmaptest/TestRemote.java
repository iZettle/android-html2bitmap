package com.izettle.html2bitmaptest;

import android.content.Context;
import android.graphics.Bitmap;

import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.content.WebViewContent;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class TestRemote {

    @Test
    public void restRemote() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        String url = "https://raw.githubusercontent.com/iZettle/android-html2bitmap/develop/html2bitmap/src/debug/res/raw/simple.html";
        Html2Bitmap html2Bitmap = new Html2Bitmap.Builder(appContext, WebViewContent.url(new URL(url)))
                .setTimeout(5)
                .build();

        Bitmap bitmap = html2Bitmap.getBitmap();
        assertNotNull(bitmap);
    }

}
