package com.devcrane.payfun.daou;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.manager.ReceiptManager;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.ObServerHelper;
import com.devcrane.payfun.daou.utility.PaymentTask;
import com.devcrane.payfun.daou.utility.VanHelper;

public class ReceiptCancelFragment extends ReceiptFragment {
	ReceiptEntity mReEntity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_receipt_cancel, container, false);
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
	protected void setEnableButton() {
//		if (checkEnableButton()) {
//			btnCancel.setEnabled(true);
//		} else{
//			btnCancel.setEnabled(false);
//			btnCancel.setSelected(false);
//		}

	}

	private boolean checkEnableButton() {
		if (re == null)
			return false;
		mReEntity = ReceiptManager.getReceiptByID(re.getF_ID());
		if (mReEntity == null)
			return false;
		if (mReEntity.getF_revStatus().equals("1"))
			return true;
		return false;
	}

	@Override
	protected void doCanceldReceipt() {
		new PaymentTask(mActivity) {

			@Override
			public String run() {
				BHelper.db("mReEntity.getF_TypeSub():"+mReEntity.getF_TypeSub());
				//temporary dont use cancel feature if typesub is 현금매출
//				if((mReEntity.getF_TypeSub().equals("현금매출")||mReEntity.getF_TypeSub().equals("일반영수증")))
//					return VanHelper.cancel(mReEntity, true);
				return VanHelper.setVanCancel(mReEntity, mReEntity.getF_CardNo());
			}

			@Override
			public boolean res(String result) {
				BHelper.db("cancel result in receipt :"+result);
				if (result != null&& !result.equals("")) {
					
					StaticData.sResultPayment = result;
					MainActivity.setFragment(new ReceiptViewFragment());
				}
				ObServerHelper.processObserver(getActivity());
				return result != null;
			}
		};
	}

}
