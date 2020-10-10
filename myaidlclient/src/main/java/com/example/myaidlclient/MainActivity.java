package com.example.myaidlclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.example.myaidlserver.IMyAidlCallBack;
import com.example.myaidlserver.IMyAidlInterface;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private IMyAidlInterface iMyAidlInterface;
    private IMyAidlCallBack iMyAidlCallBack;
    private ServiceConnection serviceConnection;

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

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.v("hello", "onServiceConnected -> ComponentName:" + name);
                iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
                try {
                    iMyAidlCallBack = new CallBackBinder();
                    iMyAidlInterface.registerListener(iMyAidlCallBack);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.v("hello", "onServiceDisconnected -> ComponentName:" + name);
                try {
                    iMyAidlInterface.unRegisterListener(iMyAidlCallBack);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };

        mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void btnClick2(View view) {
        mContext.unbindService(serviceConnection);
        try {
            iMyAidlInterface.unRegisterListener(iMyAidlCallBack);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void btnClick3(View view) {
        Resources resources = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(resources, R.drawable.timg);
//        Drawable drawable = getResources().getDrawable(R.drawable.test0);
//        BitmapDrawable bd = (BitmapDrawable) drawable;
//        Bitmap bm= bd.getBitmap();

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        //图片格式很重要，可能为jpeg等，不然出现异常
        byte [] bitmapByte =baos.toByteArray();

        Bundle bundle = new Bundle();
        bundle.putByteArray("bitmap", bitmapByte);
        try {
            iMyAidlInterface.sendBitmap(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class CallBackBinder extends IMyAidlCallBack.Stub {

        @Override
        public void callBack(int result) throws RemoteException {
            Log.v("hello", "IMyAidlCallBack -> callBack: " + result);
        }
    }

    public void btnClick1(View view) {
        try {
            Log.v("hello", "before remote call");
            iMyAidlInterface.test1();
            Log.v("hello", "test1");

            String s = iMyAidlInterface.test2(10);
            Log.v("hello", "test2: " + s);

            Log.v("hello", "after remote call");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
