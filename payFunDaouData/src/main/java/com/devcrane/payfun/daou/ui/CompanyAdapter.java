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
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.Helper;

public class CompanyAdapter extends BaseAdapter {

	private List<CompanyEntity> list;
	private int pos = -1;
	private Activity at;
	private boolean isShowList = false;
	TextView txtCompanyNo, txtMachineCode, txtVanName, txtVanPhone, txtCompanyOwnerName;

	public CompanyAdapter(Activity at, List<CompanyEntity> list,boolean isShowList) {
		super();
		this.isShowList = isShowList;
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
	public CompanyEntity getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	private void setTexColor(String color){
		txtCompanyNo.setTextColor(Color.parseColor(color));
		txtMachineCode.setTextColor(Color.parseColor(color));
		txtVanName.setTextColor(Color.parseColor(color));
		txtVanPhone.setTextColor(Color.parseColor(color));
		txtCompanyOwnerName.setTextColor(Color.parseColor(color));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		LayoutInflater vi = (LayoutInflater) at.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = vi.inflate(R.layout.item_menu_left, null);
		txtCompanyNo = (TextView) v.findViewById(R.id.txtCompanyNo);
		txtMachineCode = (TextView) v.findViewById(R.id.txtMachineCode);
		txtVanName = (TextView) v.findViewById(R.id.txtVanname);
		txtVanPhone = (TextView) v.findViewById(R.id.txtVanphone);
		txtCompanyOwnerName = (TextView) v.findViewById(R.id.txtCompanyOwnerName);
		CompanyEntity comE = getItem(position);
		txtCompanyNo.setText(Helper.formatCompanyNo(comE.getF_CompanyNo()));
		txtMachineCode.setText(comE.getF_MachineCode());
		txtVanName.setText(comE.getF_VanName());
		txtVanPhone.setText(comE.getF_VanPhoneNo());
		txtCompanyOwnerName.setText(comE.getF_CompanyName());
		String f_idx = AppHelper.getCurrentVanID();
		if (!isShowList)
			if (position == pos || comE.getF_ID().equals(f_idx)) {
				setTexColor("#ffffff");
				v.setBackgroundColor(Color.parseColor("#32b1d1"));
			}else{
				setTexColor("#7f7f7f");
			}
		else{
			if (position == pos) {
				setTexColor("#ffffff");
				v.setBackgroundColor(Color.parseColor("#32b1d1"));
			}else{
				setTexColor("#7f7f7f");
			}
		}
		return v;
	}

}
