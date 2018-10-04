package com.izettle.html2bitmaptest;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.content.WebViewContent;

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
    public void testBitmap() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        Bitmap bitmap = new Html2Bitmap.Builder()
                .setContext(appContext)
                .setStrictMode(true)
                .setContent(WebViewContent.html("<html><body><h1>Hello world</h1><p>foo <strong>bar</strong></p></body</html>"))
                .setBitmapWidth(300)
                .setTimeout(5)
                .build().getBitmap();

        assertNotNull(bitmap);
        assertEquals(300, bitmap.getWidth());
        assertThat(bitmap.getHeight(), allOf(greaterThan(100), lessThan(110)));
    }

    @Test
    public void testWideBitmap() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        Bitmap bitmap = new Html2Bitmap.Builder()
                .setContext(appContext)
                .setContent(WebViewContent.html("<html><body><h1>Hello world</h1><p>foo <strong>bar</strong></p></body</html>"))
                .setBitmapWidth(800)
                .setTimeout(5)
                .build().getBitmap();
        assertNotNull(bitmap);
        assertEquals(800, bitmap.getWidth());
        assertThat(bitmap.getHeight(), allOf(greaterThan(100), lessThan(110)));
    }

    @Test
    public void testLongBitmap() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            sb.append("<p>i</p>");
        }
        Bitmap bitmap = new Html2Bitmap.Builder()
                .setContext(appContext)
                .setContent(WebViewContent.html("<html><body><h1>Hello world</h1>" + sb.toString() + "<p>foo <strong>bar</strong></p></body</html>"))
                .setBitmapWidth(100)
                .setTimeout(5)
                .build().getBitmap();

        assertNotNull(bitmap);
        assertEquals(100, bitmap.getWidth());
        assertThat(bitmap.getHeight(), allOf(greaterThan(830), lessThan(890)));
    }

    @Test
    public void testExtraLongBitmap() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            sb.append("<p>i</p>");
        }

        Bitmap bitmap = new Html2Bitmap.Builder()
                .setContext(appContext)
                .setContent(WebViewContent.html("<html><body><h1>Hello world</h1>" + sb.toString() + "<p>foo <strong>bar</strong></p></body</html>"))
                .setBitmapWidth(100)
                .setTimeout(300)
                .build().getBitmap();

        assertNotNull(bitmap);
        assertEquals(100, bitmap.getWidth());

        assertThat(bitmap.getHeight(), allOf(greaterThan(17600), lessThan(18200)));
    }
}
