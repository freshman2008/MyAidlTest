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
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.MemoryFile;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.example.myaidlserver.IMyAidlCallBack;
import com.example.myaidlserver.IMyAidlInterface;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private IMyAidlInterface iMyAidlInterface;
    private IMyAidlCallBack iMyAidlCallBack;
    private ServiceConnection serviceConnection;
    private ServiceConnection serviceConnection2;

    private Messenger mService;
    private Messenger mGetReplyMessenger = new Messenger(new MessengerHandler());

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


        /*----------------------------------------------------------------------------------------*/
        serviceConnection2 = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = new Messenger(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        Intent intent2 = new Intent();
        intent2.setComponent(new ComponentName("com.example.myaidlserver", //参数1：应用的包名
                "com.example.myaidlserver.service.MessengerService")); //参数2：Service的全路径名(包名+类型)
        mContext.bindService(intent2, serviceConnection2, Context.BIND_AUTO_CREATE);

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
        Bitmap bmp = BitmapFactory.decodeResource(resources, R.drawable.timg3);
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

    public void btnClick4(View view) {

        Resources resources = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(resources, R.drawable.timg3);
/*         Bundle bundle = new Bundle();
       bundle.putBinder("bitmap", new ImageBinder(bmp));
        try {
            iMyAidlInterface.sendBitmap(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/

        Intent intent = new Intent(this, TestActivity.class);
        Bundle bundle = new Bundle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bundle.putBinder("bitmap", new ImageBinder(bmp));
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void btnClick5(View view) throws IOException {
        try {
            //将本地图片转为bitmap
            Resources resources = getResources();
            Bitmap bitmap_animal = BitmapFactory.decodeResource(resources, R.drawable.timg3);
//将图片写入共享内存
            MemoryFile memoryFile = new MemoryFile("test", bitmap_animal.getByteCount() );
            memoryFile.getOutputStream().write(bitmap2Bytes(bitmap_animal));
//获取文件FD
            Method method = MemoryFile.class.getDeclaredMethod("getFileDescriptor");
            method.setAccessible(true);
            FileDescriptor fd = (FileDescriptor) method.invoke(memoryFile);
//保存FD到这个序列化对象
            ParcelFileDescriptor descriptor = ParcelFileDescriptor.dup(fd);
//创建Bundle，传递对象
            Bundle bundle = new Bundle();
            bundle.putParcelable("client", descriptor);
//调用service接口
//            memoryInterface.shareMemory(bundle);
            iMyAidlInterface.sendBitmap(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    byte[] bitmap2Bytes(Bitmap bmp) {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        //图片格式很重要，可能为jpeg等，不然出现异常
        byte [] bitmapByte =baos.toByteArray();
        return  bitmapByte;
    }

    public void btnClick6(View view) {
        Message msg = Message.obtain(null, 1234);
        Bundle data = new Bundle();
        data.putString("msg", "Hello,this is Client.");
        msg.setData(data);
        //TODO 后加入代码  将Messenger对象通过replyTo参数传递给服务端
        msg.replyTo = mGetReplyMessenger;
        try {
            mService.send(msg);
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

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.e("hello", "recevie msg from Service :" + msg.getData().getString("reply"));
                    break;
            }
        }
    }
}
