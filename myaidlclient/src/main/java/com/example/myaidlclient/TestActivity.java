package com.example.myaidlclient;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author: Li Xiuliang
 * @date: 2020/10/10 18:02
 */
public class TestActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView mTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Log.v("hello", "sss0");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Log.v("hello", "sss1");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                ImageBinder imageBinder = (ImageBinder) bundle.getBinder("bitmap");
                Bitmap bitmap = imageBinder.getBitmap();
                imageView = findViewById(R.id.imageView);
                mTv = findViewById(R.id.textView);
                mTv.setText(String.format(("bitmap大小为%dkB"), bitmap.getByteCount() / 1024));
                imageView.setImageBitmap(bitmap);
            }

        } else {
            Log.v("hello", "sss2");
        }


    }
}
