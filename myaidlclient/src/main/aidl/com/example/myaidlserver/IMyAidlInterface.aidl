// IMyAidlInterface.aidl
package com.example.myaidlserver;

import com.example.myaidlserver.IMyAidlCallBack;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    oneway void test1();
    String test2(int val);
    oneway void sendBitmap(in Bundle bundle);

    //----添加----//
    void registerListener(IMyAidlCallBack callBack);
    void unRegisterListener(IMyAidlCallBack callBack);
   //-----------//
}
