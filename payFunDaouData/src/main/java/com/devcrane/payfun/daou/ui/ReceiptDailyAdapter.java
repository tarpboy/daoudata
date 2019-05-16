package com.devcrane.payfun.daou.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.utility.Helper;

public class ReceiptDailyAdapter extends BaseAdapter {
	private Activity activity;
	private List<ReceiptEntity> mList;
	private int pos = -1;

	public ReceiptDailyAdapter(Activity activity, List<ReceiptEntity> mList) {
		super();
		this.activity = activity;
		this.mList = mList;
	}

	@Override
	public int getCount() {
		if(mList!=null)
		return mList.size();
		return 0;
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
		v = vi.inflate(R.layout.item_receipt_daily, null);
		TextView txtReqDate = (TextView) v.findViewById(R.id.txtitemReceiptReqDate);
		ImageView imgIcon = (ImageView) v.findViewById(R.id.icon);
		TextView txtBuyerName = (TextView) v.findViewById(R.id.txtitemReceiptBuyerName);
		TextView txtTAmount = (TextView) v.findViewById(R.id.txtitemReceiptTAmount);
		TextView txtStaffName = (TextView) v.findViewById(R.id.txtItemReceiptStaffName);
		ReceiptEntity mReEntity = getItem(position);
		try {
			txtReqDate.setText(mReEntity.getF_revDate().split(" ")[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String amount = mReEntity.getF_TotalAmount();
		txtTAmount.setText(Helper.formatNumberExcel(amount));
		txtBuyerName.setText(mReEntity.getF_BuyerName());
		txtStaffName.setText(mReEntity.getF_StaffName());
		if (mReEntity.getF_revStatus().equals("0")) {
			txtReqDate.setTextColor(Color.RED);
			txtTAmount.setTextColor(Color.RED);
			txtBuyerName.setTextColor(Color.RED);
			txtStaffName.setTextColor(Color.RED);
//			imgIcon.setImageResource(R.drawable.iconcancel);
		} 
//		else 
		if (mReEntity.getF_Type().equals(StaticData.paymentTypeCredit)) {
			imgIcon.setImageResource(R.drawable.iconcredit);
		}else if (mReEntity.getF_Type().equals(StaticData.paymentTypeCash)) {
			imgIcon.setImageResource(R.drawable.iconcash);
		}
		// if(mReEntity.getF_revStatus().equals("0")){
		// txtReqDate.setTextColor(Color.RED);
		// txtTAmount.setTextColor(Color.RED);
		// txtBuyerName.setTextColor(Color.RED);
		// }

		return v;
	}

}
