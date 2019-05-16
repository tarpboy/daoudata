package com.devcrane.payfun.daou.utility;

import com.devcrane.payfun.daou.data.StaticData;

/**
 * Created by Administrator on 2/17/2017.
 */

public class KeypadHelper {
    IUpdateUI mCallback;
    Double lastAmount = 0.0;
    public KeypadHelper(IUpdateUI mCallback){
        this.mCallback =mCallback;
    }
    public void checkAmount(Double value){
        boolean result = false;
        boolean isShow = false;
        BHelper.db("last amount:"+ lastAmount);
        if(value> StaticData.SIGNATURE_AMOUNT_LIMIT && lastAmount<= StaticData.SIGNATURE_AMOUNT_LIMIT){
            isShow = true;
            result = true;
        }else if (value<=StaticData.SIGNATURE_AMOUNT_LIMIT && lastAmount> StaticData.SIGNATURE_AMOUNT_LIMIT){
            result =true;
            isShow = false;
        }

        lastAmount = value;
        if(result)
            mCallback.setSignatureLayout(isShow);
    }
}
