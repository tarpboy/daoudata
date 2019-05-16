package com.devcrane.payfun.daou.van;


import com.devcrane.payfun.daou.entity.EncPayInfo;
import com.devcrane.payfun.daou.entity.ReceiptEntity;

/**
 * Created by Administrator on 8/30/2016.
 */
public class PaymentCashIC extends PaymentBase implements IPayment{


    @Override
    public String pay(ReceiptEntity entity, EncPayInfo encPayInfo) {
        return null;
    }

    @Override
    public String cancel(ReceiptEntity entity, EncPayInfo encPayInfo) {
        return null;
    }

    @Override
    public byte[] makeDataPayment(ReceiptEntity entity) {
        return new byte[0];
    }
}
