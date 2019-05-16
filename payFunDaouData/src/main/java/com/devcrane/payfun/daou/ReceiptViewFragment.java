package com.devcrane.payfun.daou;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.ObServerHelper;
import com.devcrane.payfun.daou.utility.PaymentTask;
import com.devcrane.payfun.daou.utility.VanHelper;

public class ReceiptViewFragment extends ReceiptFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_receipt_view, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        onStartReceiptFragment();
    }

    @Override
    protected void doForwardReceipt() {
        showPopup();
    }

    @Override
    protected void doCanceldReceipt() {
        new PaymentTask(mActivity) {

            @Override
            public String run() {
                BHelper.db("mReEntity.getF_TypeSub():" + re.getF_TypeSub());
                //temporary dont use cancel feature if typesub is 현금매출
//				if((re.getF_TypeSub().equals("현금매출")||re.getF_TypeSub().equals("일반영수증")))
//					return VanHelper.cancel(re, true);
                return VanHelper.setVanCancel(re, re.getF_CardNo());
            }

            @Override
            public boolean res(String result) {
                if (result != null) {
                    StaticData.sResultPayment = result;
                    setRecipt();
                }
                ObServerHelper.processObserver(getActivity());
                return result != null;
            }
        };

    }

    @Override
    protected void setEnableButton() {
        // TODO Auto-generated method stub

    }

}
