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
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.utility.Helper;

public class ReceiptAdapter extends BaseAdapter {
	private Activity activity;
	private List<ReceiptEntity> mList;
	private int pos = -1;

	public ReceiptAdapter(Activity activity, List<ReceiptEntity> mList) {
		super();
		this.activity = activity;
		this.mList = mList;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public ReceiptEntity getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void setItemSelected(int position) {
		this.pos = position;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = vi.inflate(R.layout.item_receipt, null);
		TextView txtReqDate = (TextView) v.findViewById(R.id.txtitemReceiptReqDate);
		TextView txtReqTime = (TextView) v.findViewById(R.id.txtitemReceiptReqTime);
		TextView txtBuyerName = (TextView) v.findViewById(R.id.txtitemReceiptBuyerName);
		TextView txtTAmount = (TextView) v.findViewById(R.id.txtitemReceiptTAmount);
		TextView txtUser = (TextView) v.findViewById(R.id.txtitemReceiptUser);
		ReceiptEntity mReEntity = getItem(position);
		try {
			txtReqDate.setText(mReEntity.getF_revDate().split(" ")[0]);
			txtReqTime.setText(mReEntity.getF_revDate().split(" ")[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String tAmount = Helper.formatNumberExcel(mReEntity.getF_TotalAmount())+"원";
		txtTAmount.setText(tAmount);
		txtUser.setText(mReEntity.getF_StaffName());
		txtBuyerName.setText(mReEntity.getF_BuyerName());
		if(mReEntity.getF_revStatus().equals("0")){
			txtReqDate.setTextColor(Color.RED);
			txtReqTime.setTextColor(Color.RED);
			txtTAmount.setTextColor(Color.RED);
			txtBuyerName.setTextColor(Color.RED);
		}

		return v;
	}

}
