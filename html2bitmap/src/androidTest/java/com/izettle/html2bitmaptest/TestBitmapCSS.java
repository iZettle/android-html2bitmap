package com.izettle.html2bitmaptest;

import android.content.Context;
import android.graphics.Bitmap;

import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.R;
import com.izettle.html2bitmap.content.WebViewContent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class TestBitmapCSS {
    private Context appContext;

    @Before
    public void setUp() {
        appContext = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testBitmap() throws Exception {
        InputStream inputStream = appContext.getResources().openRawResource(R.raw.csstest);

        String html = stringFromStream(inputStream);

        Html2Bitmap build = new Html2Bitmap.Builder(appContext, WebViewContent.html(html))
                .setBitmapWidth(300)
                .setTimeout(5)
                .setStrictMode(true)
                .build();

        Bitmap bitmap = build.getBitmap();

        assertNotNull(bitmap);
        assertEquals(300, bitmap.getWidth());
        assertThat(bitmap.getHeight(), allOf(greaterThan(1870), lessThan(1920)));
    }

    private String stringFromStream(InputStream inputStream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
}
