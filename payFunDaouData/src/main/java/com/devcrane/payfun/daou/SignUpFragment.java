package com.devcrane.payfun.daou;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.utility.BHelper;

public class SignUpFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_signup, container, false);
		int[] ids = { R.id.btnSignup1, R.id.btnSignup2, R.id.btnSignup3 };
		for (final int id : ids) {
			v.findViewById(id).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					UserFragment.typeRegister = id;
					startActivityForResult(new Intent(getActivity(), ConfirmActivity.class), StaticData.REQUEST_CONFIRM);
//					MainActivity.setFragment(new UserFragment());
				}
			});
		}
		return v;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		BHelper.db("RequestCode:"+requestCode+ "resultCode:"+resultCode);
		if(resultCode == Activity.RESULT_OK && requestCode == StaticData.REQUEST_CONFIRM){
			BHelper.db("open UserFragment");
			MainActivity.setFragment(new UserFragment());
		}
	}
	@Override
	public void onStart() {
		super.onStart();
		BHelper.setActivity(getActivity());
		BHelper.setTypeface(getView());
//		MainActivity.lockMenu(true);
	}
}
