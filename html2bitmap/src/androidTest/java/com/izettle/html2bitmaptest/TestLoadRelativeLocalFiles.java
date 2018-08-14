package com.izettle.html2bitmaptest;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.R;
import com.izettle.html2bitmap.content.LoadedResource;
import com.izettle.html2bitmap.content.WebViewContent;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class TestLoadRelativeLocalFiles {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.izettle.html2bitmap.test", appContext.getPackageName());
    }

    @Test
    public void testLoadRelativeImage() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        Html2Bitmap html2Bitmap = new Html2Bitmap.Builder()
                .setContext(appContext)
                .setContent(WebViewContent.html("<html><body><h1>Hello world</h1><img src='faces_200_400.png'></body</html>"))
                .setTimeout(15).build();

        Bitmap bitmap = html2Bitmap.getBitmap();
        assertNotNull(bitmap);

        List<LoadedResource> loadedResources = html2Bitmap.getWebViewContent().getRemoteResources();

        assertEquals(2, loadedResources.size());
        assertEquals(Uri.parse("http://html2bitmap/faces_200_400.png"), loadedResources.get(1).getUri());
    }

    @Test
    public void testLoadRemoteFontFilesFromGoogle() throws IOException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        InputStream inputStream = InstrumentationRegistry.getContext().getResources().openRawResource(R.raw.remotefonttest);

        String html = stringFromStream(inputStream);

        Html2Bitmap html2Bitmap = new Html2Bitmap.Builder()
                .setContext(appContext)
                .setContent(WebViewContent.html(html))
                .setTimeout(15).build();

        Bitmap bitmap = html2Bitmap.getBitmap();
        assertNotNull(bitmap);

        List<LoadedResource> loadedResources = html2Bitmap.getWebViewContent().getRemoteResources();

        assertEquals(3, loadedResources.size());
        assertEquals(Uri.parse("https://fonts.googleapis.com/css?family=Hanalei+Fill"), loadedResources.get(1).getUri());
        assertEquals(Uri.parse("https://fonts.gstatic.com/s/hanaleifill/v6/fC1mPYtObGbfyQznIaQzPQi8UAjFhFqtag.ttf"), loadedResources.get(2).getUri());
    }

    @Test
    public void testLoadLocalFontFiles() throws IOException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        InputStream inputStream = InstrumentationRegistry.getContext().getResources().openRawResource(R.raw.localfonttest);

        String html = stringFromStream(inputStream);

        Html2Bitmap html2Bitmap = new Html2Bitmap.Builder()
                .setContext(appContext)
                .setContent(WebViewContent.html(html))
                .setTimeout(15).build();

        Bitmap bitmap = html2Bitmap.getBitmap();
        assertNotNull(bitmap);

        List<LoadedResource> loadedResources = html2Bitmap.getWebViewContent().getRemoteResources();

        assertEquals(2, loadedResources.size());
        assertEquals(Uri.parse("http://html2bitmap/haneli.woff2"), loadedResources.get(1).getUri());
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
