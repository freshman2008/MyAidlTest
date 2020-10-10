// IMyAidlCallBack.aidl
package com.example.myaidlserver;

// Declare any non-default types here with import statements

interface IMyAidlCallBack {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
    void callBack(int result);
}
