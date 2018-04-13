package com.example.html2bitmapdemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements BitmapGeneratingAsyncTask.Callback {

    private EditText htmlEditText;
    private ImageView imageView;
    private EditText widthEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        htmlEditText = findViewById(R.id.html_edit_text);
        widthEditText = findViewById(R.id.width_edit_text);

        updateBitmap();
        findViewById(R.id.generate_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBitmap();
            }
        });
    }

    private void updateBitmap() {
        getEnteredWidthOrDefault();
        new BitmapGeneratingAsyncTask(this, htmlEditText.getText().toString(), getEnteredWidthOrDefault(), this).execute();
    }

    @Override
    public void done(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    public int getEnteredWidthOrDefault() {
        String enteredValue = widthEditText.getText().toString();
        if (!TextUtils.isEmpty(enteredValue)) {
            return Integer.parseInt(enteredValue);
        } else {
            return 150;
        }
    }



}
