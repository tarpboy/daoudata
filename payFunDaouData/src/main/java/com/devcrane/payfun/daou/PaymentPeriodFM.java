package com.devcrane.payfun.daou;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devcrane.payfun.daou.billing.util.IabHelper;
import com.devcrane.payfun.daou.billing.util.IabHelper.OnConsumeFinishedListener;
import com.devcrane.payfun.daou.billing.util.IabHelper.OnIabPurchaseFinishedListener;
import com.devcrane.payfun.daou.billing.util.IabHelper.OnIabSetupFinishedListener;
import com.devcrane.payfun.daou.billing.util.IabHelper.QueryInventoryFinishedListener;
import com.devcrane.payfun.daou.billing.util.IabResult;
import com.devcrane.payfun.daou.billing.util.Inventory;
import com.devcrane.payfun.daou.billing.util.Purchase;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.UserBalanceEntity;
import com.devcrane.payfun.daou.manager.UserBalanceManager;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.BHelper.CalendarHelper;
import com.devcrane.payfun.daou.utility.MyTask;

public class PaymentPeriodFM extends Fragment {
	private Activity at;
	private TextView tvDigitalClock;
	private RadioGroup radioPurchase;
	private Button btnPaymentHistory;
	UserBalanceEntity e;
	private ProgressDialog dialog;

	protected static final String SKU = "android.test.purchased";
	private static final String PUBKEY = "0011223344";
	public static final int BUY_REQUEST_CODE = 12345;
	public static IabHelper buyHelper;
	private Purchase purchase;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_payment_period, container, false);
		tvDigitalClock = (TextView) v.findViewById(R.id.tvDigitalClock);
		radioPurchase = (RadioGroup) v.findViewById(R.id.radioPurchase);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		BHelper.setActivity(at = getActivity());
		BHelper.setTypeface(getView());
		btnPaymentHistory = (Button) at.findViewById(R.id.btnPaymentHistory);
		initComponent();
		dialog = ProgressDialog.show(at, null, "Loading...", true);
		dialog.setCancelable(false);
		buyHelper = new IabHelper(at, PUBKEY);
		buyHelper.startSetup(new OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {
				update();
			}
		});
	}

	private void initComponent() {
		tvDigitalClock.setText(ProfileFragment.getDigitalClock(at));
		btnPaymentHistory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doPaymentHistory();
			}
		});
	}

	private void update() {
		ArrayList<String> moreSkus = new ArrayList<String>();
		moreSkus.add(SKU);
		buyHelper.queryInventoryAsync(true, moreSkus, new QueryInventoryFinishedListener() {
			@Override
			public void onQueryInventoryFinished(IabResult result, Inventory inv) {
				if (result.isSuccess()) {
					purchase = inv.getPurchase(SKU);
					if (purchase != null) {
						buyHelper.consumeAsync(purchase, new OnConsumeFinishedListener() {
							@Override
							public void onConsumeFinished(Purchase purchase, IabResult result) {
								if (result.isSuccess()) {
									try {
										Thread.sleep(1000);
										update();
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									Toast.makeText(at, "Error consuming: " + result.getMessage(), Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				} else {
					Toast.makeText(at, "Error getting inventory!", Toast.LENGTH_SHORT).show();
				}
				if(dialog!=null||dialog.isShowing()){
					dialog.cancel();
				}
			}
		});
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		buyHelper.dispose();
	}

	private void doPaymentHistory() {
		StaticData.GETCOUPON = true;
		List<PurchaseEntity> list = new ArrayList<PurchaseEntity>();
		list.add(new PurchaseEntity(R.id.radio0, "1", "2000"));
		list.add(new PurchaseEntity(R.id.radio1, "3", "5800"));
		list.add(new PurchaseEntity(R.id.radio2, "6", "10000"));
		e = new UserBalanceEntity();
		e.setF_UserID(AppHelper.getCurrentUserID());
		e.setUPDATE_UID(AppHelper.getUpdateUserID());

		for (PurchaseEntity pe : list) {
			if (radioPurchase.getCheckedRadioButtonId() == pe.radioID) {
				e.setF_PurchaseMonthNo(pe.monthNo);
				e.setF_PurchaseAmount(pe.amount);
				break;
			}
		}

		buyHelper.launchPurchaseFlow(at, SKU, BUY_REQUEST_CODE, new OnIabPurchaseFinishedListener() {
			@Override
			public void onIabPurchaseFinished(IabResult result, Purchase info) {
				BHelper.db("result:" + result.isSuccess());
				if (result.isSuccess()) {
					new MyTask(at) {
						@Override
						public boolean run() {
							e.setF_ServiceStartDate(UserBalanceManager.getServiceStartDate(e.getF_UserID()));
							e.setF_ServiceBeExpiredDate(calcServiceBeExpiredDate());
							return UserBalanceManager.insert(e);
						}

						private String calcServiceBeExpiredDate() {
							Calendar calendar = CalendarHelper.getCalendar(e.getF_ServiceStartDate());
							int nextMonth = Integer.valueOf(e.getF_PurchaseMonthNo());
							calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + nextMonth);
							return CalendarHelper.getDateTime(calendar);
						}

						@Override
						public boolean res(boolean result) {
							Toast.makeText(at, "Thanks for buying!", Toast.LENGTH_SHORT).show();
							return false;
						}
					};

				}
				update();
			}
		});

	}

}

class PurchaseEntity {
	int radioID;
	String monthNo;
	String amount;

	public PurchaseEntity(int radioID, String monthNo, String amount) {
		super();
		this.radioID = radioID;
		this.monthNo = monthNo;
		this.amount = amount;
	}
}
