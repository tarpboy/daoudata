package com.devcrane.payfun.daou.van;

import com.devcrane.payfun.daou.entity.EncPayInfo;
import com.devcrane.payfun.daou.entity.ReceiptEntity;

/**
 * Created by Administrator on 8/31/2016.
 */
public interface IPaymentEmv {
    String payEmv(ReceiptEntity entity, EncPayInfo encPayInfo);
    String cancelEmv(ReceiptEntity entity, EncPayInfo encPayInfo);
}
