package com.example.myaidlserver.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.myaidlserver.IMyAidlInterface;

import androidx.annotation.Nullable;

/**
 * @author: Li Xiuliang
 * @date: 2020/10/10 14:21
 */
public class MyTestService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    private class MyBinder extends IMyAidlInterface.Stub {
        @Override
        public void test1() throws RemoteException {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
    }
}
