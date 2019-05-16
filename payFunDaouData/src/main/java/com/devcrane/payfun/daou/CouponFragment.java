package com.devcrane.payfun.daou;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.CouponEntity;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.manager.CouponManager;
import com.devcrane.payfun.daou.ui.CouponAdapter;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.MyTask;

public class CouponFragment extends Fragment {
	Button btnCounpon;
	Activity at;
	ListView listCoupon;
	List<CouponEntity> list;
	CouponAdapter adapter;
	public static CouponEntity mCouponEntity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_coupon, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		BHelper.setActivity(getActivity());
		BHelper.setTypeface(getView());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		at = getActivity();
		listCoupon = (ListView) at.findViewById(R.id.listCoupon);
		btnCounpon = (Button) at.findViewById(R.id.btnCoupon);
		btnCounpon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCouponEntity = null;
//				AppDataStatic.GETCOUPON = true;
				CompanyEntity comEntity = CompanyManger.getCompanyByID(AppHelper.getCurrentVanID());
				if (comEntity == null) {
					Toast.makeText(at, R.string.please_select_company_first, Toast.LENGTH_SHORT).show();
				} else
					MainActivity.setFragment(new CouponDetailFragment());
			}
		});
		listCoupon.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				adapter.setPosition(position);
				CouponEntity cou = (CouponEntity) listCoupon.getAdapter().getItem(position);
				mCouponEntity = cou;
				MainActivity.setFragment(new CouponDetailFragment());
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		if (!AppHelper.getIsLogin() && !StaticData.KeyCodeBack) {
			MainActivity.setFragment(new HomeFragment());
		}
		new MyTask(at) {

			@Override
			public boolean run() {
				list = CouponManager.getCouponByUser(AppHelper.getCurrentUserID());
				return list != null;
			}

			@Override
			public boolean res(boolean result) {
				if (list != null) {
					adapter = new CouponAdapter(at, list);
					listCoupon.setAdapter(adapter);
				}
				return false;
			}
		};
	}
}