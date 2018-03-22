package com.izettle.html2bitmaptest;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TestBitmapCSS {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.izettle.html2bitmap.test", appContext.getPackageName());
    }

    @Test
    public void testBitmap() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        InputStream inputStream = InstrumentationRegistry.getContext().getResources().openRawResource(R.raw.csstest);

        String html = stringFromStream(inputStream);

        Bitmap bitmap = Html2Bitmap.getBitmap(appContext, html, 300);
        assertNotNull(bitmap);
        assertEquals(300, bitmap.getWidth());
        assertTrue("Height was " + bitmap.getHeight(), bitmap.getHeight() > 1850);
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
