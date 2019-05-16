package com.devcrane.payfun.daou;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devcrane.payfun.cardreader.ReaderFragment;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.utility.BHelper;

public class CardReadingFragment extends ReaderFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_card_reading, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		checkEmvCard();
		
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		StaticData.KeyCodeBack  = true;
		BHelper.setTypeface(getView());
		initCard();
	}

	@Override
	protected void doConfirmPayment() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doCard() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doReset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean validateCredit() {
		// TODO Auto-generated method stub
		return true;
	}

}
