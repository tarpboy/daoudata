package com.devcrane.payfun.daou.van;


import com.devcrane.payfun.daou.entity.EncPayInfo;
import com.devcrane.payfun.daou.entity.ReceiptEntity;

/**
 * Created by Administrator on 8/30/2016.
 * 10,76,77,17
 */

public interface IPayment{
    String pay(ReceiptEntity entity, EncPayInfo encPayInfo);
    String cancel(ReceiptEntity entity, EncPayInfo encPayInfo);
    byte[] makeDataPayment(ReceiptEntity entity);
}
