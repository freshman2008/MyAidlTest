package com.example.myaidlserver.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ImageView;

import com.example.myaidlserver.IMyAidlCallBack;
import com.example.myaidlserver.IMyAidlInterface;
import com.example.myaidlserver.MainActivity;

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

        @Override
        public void sendBitmap(Bundle bundle) throws RemoteException {
            byte[] bis = bundle.getByteArray("bitmap");
            Bitmap bitmap= BitmapFactory.decodeByteArray(bis, 0, bis.length);
            ImageView imageView = MainActivity.getImageView();
            imageView.setImageBitmap(bitmap);

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
