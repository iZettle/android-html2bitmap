package com.izettle.html2bitmaptest;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.izettle.html2bitmap.Html2Bitmap;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        assertThat(bitmap.getHeight(), allOf(greaterThan(100), lessThan(110)));
    }

    @Test
    public void testWideBitmap() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        Bitmap bitmap = Html2Bitmap.getBitmap(appContext, "<html><body><h1>Hello world</h1><p>foo <strong>bar</strong></p></body</html>", 800);
        assertNotNull(bitmap);
        assertEquals(800, bitmap.getWidth());
        assertThat(bitmap.getHeight(), allOf(greaterThan(100), lessThan(110)));
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
        assertThat(bitmap.getHeight(), allOf(greaterThan(830), lessThan(890)));
    }

    @Test
    public void testExtraLongBitmap() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            sb.append("<p>i</p>");
        }
        Bitmap bitmap = Html2Bitmap.getBitmap(appContext, "<html><body><h1>Hello world</h1>" + sb.toString() + "<p>foo <strong>bar</strong></p></body</html>", 100);
        assertNotNull(bitmap);
        assertEquals(100, bitmap.getWidth());

        assertThat(bitmap.getHeight(), allOf(greaterThan(17600), lessThan(18200)));
    }
}
