package com.izettle.html2bitmaptest;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.izettle.html2bitmap.Html2Bitmap;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TestBitmapSize {
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

        Bitmap bitmap = Html2Bitmap.getBitmap(appContext, "<html><body><h1>Hello world</h1><p>foo <strong>bar</strong></p></body</html>", 300);
        assertNotNull(bitmap);
        assertEquals(300, bitmap.getWidth());
        assertTrue(bitmap.getHeight() > 100);
    }

    @Test
    public void testWideBitmap() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        Bitmap bitmap = Html2Bitmap.getBitmap(appContext, "<html><body><h1>Hello world</h1><p>foo <strong>bar</strong></p></body</html>", 800);
        assertNotNull(bitmap);
        assertEquals(800, bitmap.getWidth());
        assertTrue(bitmap.getHeight() > 100);
    }

    @Test
    public void testLongBitmap() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            sb.append("<p>i</p>");
        }
        Bitmap bitmap = Html2Bitmap.getBitmap(appContext, "<html><body><h1>Hello world</h1>" + sb.toString() + "<p>foo <strong>bar</strong></p></body</html>", 100);
        assertNotNull(bitmap);
        assertEquals(100, bitmap.getWidth());
        assertTrue(bitmap.getHeight() > 800);
    }

    @Test
    public void testExtraLongBitmap() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5000; i++) {
            sb.append("<p>i</p>");
        }
        Bitmap bitmap = Html2Bitmap.getBitmap(appContext, "<html><body><h1>Hello world</h1>" + sb.toString() + "<p>foo <strong>bar</strong></p></body</html>", 100);
        assertNotNull(bitmap);
        assertEquals(100, bitmap.getWidth());
        assertTrue(bitmap.getHeight() > 170000);
    }
}
