package com.example.myaidlclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.example.myaidlserver.IMyAidlInterface;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private IMyAidlInterface iMyAidlInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        init();
    }

    void init() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.example.myaidlserver", //参数1：应用的包名
                "com.example.myaidlserver.service.MyTestService")); //参数2：Service的全路径名(包名+类型)

        mContext.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.v("hello", "onServiceConnected -> ComponentName:" + name);
                iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.v("hello", "onServiceDisconnected -> ComponentName:" + name);
            }
        }, Context.BIND_AUTO_CREATE);
    }

    public void btnClick1(View view) {
        try {
            Log.v("hello", "before remote call");
            int i = iMyAidlInterface.test1();
            Log.v("hello", "test1: " + i);

            String s = iMyAidlInterface.test2(10);
            Log.v("hello", "test2: " + s);

            Log.v("hello", "after remote call");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
