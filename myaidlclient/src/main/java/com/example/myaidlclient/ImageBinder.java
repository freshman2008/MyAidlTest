package com.example.myaidlclient;

import android.graphics.Bitmap;
import android.os.Binder;

/**
 * @author: Li Xiuliang
 * @date: 2020/10/10 18:15
 */
public class ImageBinder extends Binder {
    private Bitmap bitmap;

    public ImageBinder(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    Bitmap getBitmap() {
        return bitmap;
    }
}