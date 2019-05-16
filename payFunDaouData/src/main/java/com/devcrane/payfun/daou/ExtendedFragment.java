package com.devcrane.payfun.daou;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devcrane.payfun.daou.utility.BHelper;

public class ExtendedFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_extended, container, false);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		BHelper.setActivity(getActivity());
		BHelper.setTypeface(getView());
	}

}