package com.devcrane.payfun.daou;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;

public class ProfileFragment extends Fragment {
	private static Activity at;
	private TextView tvDigitalClock;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_profile, container, false);
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!AppHelper.getIsLogin())
			MainActivity.setFragment(new LoginFragment());
		else
		initComponent();
		TextView tvName = (TextView) getActivity().findViewById(R.id.tvUserNameProfile);
		tvName.setText(AppHelper.getCurrentUserName());
	}
	
	private void initComponent() {
		BHelper.setActivity(at = getActivity());
		BHelper.setTypeface(getView());
		
//		tvDigitalClock.setText(getDigitalClock(at));		
		final Map<Integer, Fragment> map = new HashMap<Integer, Fragment>();
		map.put(R.id.btnUserModify, new UserFragment());
		map.put(R.id.btnPaymentPeriod, new PaymentPeriodFM());
		map.put(R.id.btnConfig, new ConfigFragment());
		map.put(R.id.btnPrintConfig, new ConfigPrintFM());
		map.put(R.id.btnListCompany,new CompanyListFragment());
		map.put(R.id.btnRegistedVanCode, new RegistervanNewFragment());
		
		for (final Integer id : map.keySet()) {
			at.findViewById(id).setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View view) {
					if(view.getId() == R.id.btnPaymentPeriod){
						String url = "http://payfun.kr";
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(url));
						getActivity().startActivity(i);
					}else{
						if (view.getId() == R.id.btnUserModify)
							UserFragment.typeRegister = view.getId();
						MainActivity.setFragment(map.get(id));
					}
				}
			});
		}
	}

	public static String getDigitalClock(Activity at) {
		String str = at.getString(R.string.format_digital_clock);
		Date date = Calendar.getInstance().getTime();
		return new SimpleDateFormat(str).format(date);
	}

}