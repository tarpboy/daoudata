package com.devcrane.payfun.daou;

import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import static com.devcrane.payfun.daou.MainActivity.isBTReaderConnected;

public class CancelSelectorFragment extends Fragment{
	private static Activity at;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_cancel_selector, container, false);
		return v;
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initComponent();
	}
	private void initComponent() {
		BHelper.setActivity(at = getActivity());
		// TODO Auto-generated method stub
		at.findViewById(R.id.btn_cancel_credit).setOnClickListener(l);
		at.findViewById(R.id.btn_cancel_cash).setOnClickListener(l);
	}
	OnClickListener l = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MainActivity.isResumeOnMain = false;
			switch (v.getId()) {
			case R.id.btn_cancel_credit:

				if (!isBTReaderConnected && !Helper.isHeadsetConnected(at))
				{
//					showRequestDevice();
//					return;
					BHelper.showToast("단말기 연결이 필요합니다.");
				}
				MainActivity.setFragment(new CancelPaymentFragment());
				break;
			case R.id.btn_cancel_cash:
				MainActivity.setFragment(new CancelCashFragment());
				break;
			default:
				break;
			}
		}
	};


	void showRequestDevice(){




		new AlertDialog.Builder(getContext())
				.setTitle("단말기 연결이 필요합니다.")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("예", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						RegistervanNewFragment fragment = new RegistervanNewFragment();
						MainActivity.setFragment(fragment);
					}
				})


//                .setNegativeButton("아니오(cancel)", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//              }
//           })
				.show();
	}
}
