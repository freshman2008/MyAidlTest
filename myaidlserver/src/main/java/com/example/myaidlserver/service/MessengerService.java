package com.example.myaidlserver.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

/**
 * @author: Li Xiuliang
 * @date: 2020/10/12 15:57
 */
public class MessengerService extends Service {
    private static final String TAG = "MessengerService";


    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1234:
                    Log.e(TAG, "receive msg form Client:" + msg.getData().getString("msg"));
                    break;
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    public MessengerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, mMessenger.getBinder() + "");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, mMessenger.getBinder() + "");
        return mMessenger.getBinder();
    }
}
