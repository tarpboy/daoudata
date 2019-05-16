package com.devcrane.payfun.daou.utility;

/**
 * Created by Administrator on 1/13/2017.
 */

public class DaouSignHelper {
    static {
        System.loadLibrary("daou-sign-jni");
    }
    public native byte[] convertSignature(byte[] bmp);
//    public native byte[] convertSignature2(byte[] bmp);

}
