package com.example.myaidlserver.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ImageView;

import com.example.myaidlserver.IMyAidlCallBack;
import com.example.myaidlserver.IMyAidlInterface;
import com.example.myaidlserver.MainActivity;

import java.io.FileDescriptor;
import java.io.FileInputStream;

import androidx.annotation.Nullable;

/**
 * @author: Li Xiuliang
 * @date: 2020/10/10 14:21
 */
public class MyTestService extends Service {
    private final RemoteCallbackList<IMyAidlCallBack> mCallBacks = new RemoteCallbackList<>();
//    private Handler mHandler = new Handler(getMainLooper());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v("hello", "onUnbind");
        return super.onUnbind(intent);
    }

    private class MyBinder extends IMyAidlInterface.Stub {
        @Override
        public void test1() throws RemoteException {
            try {
                Thread.sleep(10000);

                int n = mCallBacks.beginBroadcast();
                for (int i = 0; i < n; i++) {
                    mCallBacks.getBroadcastItem(i).callBack(i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mCallBacks.finishBroadcast();
            }
        }

        @Override
        public String test2(int val) throws RemoteException {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello from server: " + val;
        }


        class ImageBinder extends Binder {
            private Bitmap bitmap;

            public ImageBinder(Bitmap bitmap) {
                this.bitmap = bitmap;
            }

            Bitmap getBitmap() {
                return bitmap;
            }
        }

        @Override
        public void sendBitmap(Bundle bundle) throws RemoteException {
//            byte[] bis = bundle.getByteArray("bitmap");
//            Bitmap bitmap= BitmapFactory.decodeByteArray(bis, 0, bis.length);

//            ImageBinder imageBinder = (ImageBinder) bundle.getBinder("bitmap");
//            Bitmap bitmap = imageBinder.getBitmap();
//            ImageView imageView = MainActivity.getImageView();
//            imageView.setImageBitmap(bitmap);


            //service接收
            ParcelFileDescriptor parcelable = bundle.getParcelable("client");
            //获取FD
            FileDescriptor fd = parcelable.getFileDescriptor();
            //通过FD获得输入流
            FileInputStream fileInputStream = new FileInputStream(fd);
            //转化Bitmap进行展示
            Bitmap bitmap = inputStream2Bitmap(fileInputStream);
            ImageView imageView = MainActivity.getImageView();
            imageView.setImageBitmap(bitmap);
        }

        Bitmap inputStream2Bitmap(FileInputStream fileInputStream) {
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
            return bitmap;
        }

        @Override
        public void registerListener(IMyAidlCallBack callBack) throws RemoteException {
            Log.i("hello", "registerListener -> callBack:" + callBack);
            Log.i("hello", "registerListener -> mRemoteCallbackList:" + mCallBacks);
            if (mCallBacks != null) {
                mCallBacks.register(callBack);
            }
        }

        @Override
        public void unRegisterListener(IMyAidlCallBack callBack) throws RemoteException {
            Log.i("hello", "unRegisterListener -> callBack:" + callBack);
            if (mCallBacks != null) {
                mCallBacks.unregister(callBack);
            }
        }
    }
}
