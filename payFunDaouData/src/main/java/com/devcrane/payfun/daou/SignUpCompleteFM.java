package com.devcrane.payfun.daou;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.UserEntity;
import com.devcrane.payfun.daou.manager.UserManager;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.MyTask;

public class SignUpCompleteFM extends Fragment {
	UserEntity key = new UserEntity(0);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_signup_complete, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		BHelper.setActivity(getActivity());
		BHelper.setTypeface(getView());
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().findViewById(R.id.gotoRegisterVan).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.setFragment(new RegistervanNewFragment());
			}
		});
//		doCheckLogin();
	}

	private void doCheckLogin() {
		final String f_Passwd = AppHelper.prefGet(key.getF_Password(), "");
		if (!f_Passwd.equals("")) {
			final String f_Email = AppHelper.prefGet(key.getF_Email(), "");
			new MyTask(getActivity()) {
				@Override
				public boolean run() {
					String userID = UserManager.checkLogin(f_Email, f_Passwd);
					AppHelper.setCurrentUserID(userID);
					return userID != null;
				}

				@Override
				public boolean res(boolean result) {
					if (result) {
						doLoginSuccess();
					}
					return false;
				}
			};
		}
	}

	private void doLoginSuccess() {
		AppHelper.setIsLogin(true);

	}
}
