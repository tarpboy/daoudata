package com.devcrane.payfun.daou.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.entity.CouponEntity;
import com.devcrane.payfun.daou.utility.Helper;

public class CouponAdapter extends BaseAdapter {

	private List<CouponEntity> list;
	private int pos = -1;
	private Activity at;
	TextView txtCompanyNo, txtMachineCode, txtVanName, txtPercent, txtCompanyOwnerName;

	public CouponAdapter(Activity at, List<CouponEntity> list) {
		super();
		this.list = list;
		this.at = at;
	}

	public void setPosition(int position) {
		this.pos = position;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public CouponEntity getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	private void setTexColor(String color) {
		txtCompanyNo.setTextColor(Color.parseColor(color));
		txtMachineCode.setTextColor(Color.parseColor(color));
		txtVanName.setTextColor(Color.parseColor(color));
		txtPercent.setTextColor(Color.parseColor(color));
		txtCompanyOwnerName.setTextColor(Color.parseColor(color));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		LayoutInflater vi = (LayoutInflater) at.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = vi.inflate(R.layout.item_coupon, null);
		txtCompanyNo = (TextView) v.findViewById(R.id.tvCompanyNo);
		txtMachineCode = (TextView) v.findViewById(R.id.tvMachineCode);
		txtVanName = (TextView) v.findViewById(R.id.tvVanname);
		txtPercent = (TextView) v.findViewById(R.id.tvPercent);
		txtCompanyOwnerName = (TextView) v.findViewById(R.id.tvCouponName);
		CouponEntity comE = getItem(position);
		txtCompanyNo.setText(Helper.formatCompanyNo(comE.getF_CompanyNo()));
		txtMachineCode.setText(comE.getF_MachineCode());
		txtVanName.setText(comE.getF_VanName());
		txtPercent.setText(comE.getF_DiscountRate() + "%");
		txtCompanyOwnerName.setText(comE.getF_CouponName());
		if (position == pos) {
			setTexColor("#ffffff");
			v.setBackgroundColor(Color.parseColor("#32b1d1"));
		}
		return v;
	}

}
