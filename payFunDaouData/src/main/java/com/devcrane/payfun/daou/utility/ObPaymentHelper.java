package com.devcrane.payfun.daou.utility;

/**
 * Created by Administrator on 2/21/2017.
 */

public class ObPaymentHelper {
    public static final int PAYMENT_STEP_PREPARE =-1;
    public static final int PAYMENT_STEP_WAITING_CARD =1;
    public static final int PAYMENT_STEP_START_EMV = 2;
    public static final int PAYMENT_STEP_SEND_VAN_REQUEST = 3;
    public static final int PAYMENT_STEP_RECEIVED_DATA_VAN = 4;

    public static int ObStatus = PAYMENT_STEP_PREPARE;

//    public static final int PAYMENT_STEP_
}
