package com.devcrane.payfun.daou;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import jxl.write.WriteException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.manager.ReceiptManager;
import com.devcrane.payfun.daou.ui.ReceiptDailyAdapter;
import com.devcrane.payfun.daou.ui.WriteExcel;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.DateHelper;
import com.devcrane.payfun.daou.utility.HandlerTask;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.JSonHelper;
import com.devcrane.payfun.daou.utility.MyTask;
import com.google.gson.reflect.TypeToken;

public class CancelDailyListFragment extends Fragment {

	Activity at;
	static TextView txtVan, txtCompanyName, txtCompanyNo;
	TextView txtDate;
	TextView txtTotalAmount;
	TextView txtChartdateDaily;
	static CompanyEntity comEntity;
	ReceiptDailyAdapter adapter;
	List<ReceiptEntity> list;
	String date = "2014-06-20";
	public static boolean isDaily = false;
	public static boolean isDailyChart = false;
	boolean isStart = false;
	boolean chartdaily = false;
	private ListView mListView;
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
	private final SimpleDateFormat textsdf = new SimpleDateFormat("yyyy-MM-dd");
	private int[] ids = { R.id.btnChartexcel, R.id.btnChartMonth,//
			R.id.btnChartDaily,//
			R.id.buttongetchartdaily, R.id.buttongetchartmontly };
	private String sMonth = "";
	String sTypeReceipt = "";
	List<ReceiptEntity> listTemp;
	private Spinner spType;
	private Spinner spQuarter;
	private Button btnDaily;
	private Button btnMonth;
	private int mYear, mMonth, mDay;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_chart_cancel_daily, container, false);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isStart = true;
		isDailyChart = false;
		chartdaily = true;
		if (!Helper.checkSelectCompany(getActivity()))
			MainActivity.setFragment(new HomeFragment());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		at = getActivity();
		BHelper.setActivity(at);
		BHelper.setTypeface(getView());
		adapter = null;
		initView();
	}

	private void initView() {
		btnMonth = (Button) at.findViewById(R.id.buttongetchartmontly);
		mListView = (ListView) at.findViewById(R.id.listChart);
		btnDaily = (Button) at.findViewById(R.id.buttongetchartdaily);
		txtVan = (TextView) at.findViewById(R.id.txtChartDailyVan);
		txtCompanyName = (TextView) at.findViewById(R.id.txtChartDailyVanCompanyName);
		txtCompanyNo = (TextView) at.findViewById(R.id.txtChartDailyVanCompanyNo);
		txtDate = (TextView) at.findViewById(R.id.chartdailydate);
		txtChartdateDaily = (TextView) at.findViewById(R.id.txtChartDailyDate);
		txtTotalAmount = (TextView) at.findViewById(R.id.chartdailytotalAmount);
		spType = (Spinner) at.findViewById(R.id.spinnergetcharttype);
		spQuarter = (Spinner) at.findViewById(R.id.spinnergetchartquarterly);
		initComponent();
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

	}

	private void initComponent() {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ReceiptEntity mREntity = (ReceiptEntity) mListView.getAdapter().getItem(position);
				Type type = new TypeToken<ReceiptEntity>() {
				}.getType();
				if (mREntity == null)
					return;
				StaticData.sResultPayment = JSonHelper.serializerJson(mREntity, type);
				isDailyChart = true;
				MainActivity.setFragment(new ReceiptCancelFragment());
			}
		});
		for (int i : ids) {
			at.findViewById(i).setOnClickListener(onButtonClick);
		}
		btnMonth.setText(sdf.format(Calendar.getInstance().getTime()));

		ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(getActivity(), R.array.type_arrays, R.layout.spinner_item_chartdaily);
		adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spType.setAdapter(adapterType);
		spType.setOnItemSelectedListener(mSelectType);
		ArrayAdapter<CharSequence> adapterQuarter = ArrayAdapter.createFromResource(getActivity(), R.array.quarterly_arrays, R.layout.spinner_item_chartdaily);
		adapterQuarter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spQuarter.setAdapter(adapterQuarter);
		spQuarter.setOnItemSelectedListener(mSelectQuarter);
		date = DateHelper.currentDate();
		if (isDaily)
			date = StaticData.sDay;
		setList(date);
		txtCompanyNo.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (chartdaily)
					if (comEntity != null)
						getList(date);

			}
		});

	}

	private OnClickListener onButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnChartexcel:
				String link = "";
				if (list == null || list.size() <= 0)
					return;
				try {
					link = WriteExcel.writeDailyChart(list, date);
				} catch (WriteException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (!link.equals("")) {
					new AlertDialog.Builder(getActivity()).setTitle("Excel")//
							.setMessage(link).setPositiveButton("OK", null).show();
				}
				break;
			case R.id.btnChartMonth:
				MainActivity.setFragment(new CancelListFragment());
				break;
			case R.id.btnChartDaily:

				break;

			case R.id.buttongetchartmontly:
				MainActivity.setFragment(new CancelListFragment());
				break;
//
//				DatePickerDialog dp = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
//					@Override
//					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//						setMonth(year + "-" + Helper.appenZeroNumber(String.valueOf(monthOfYear + 1), 2) + "-" + Helper.appenZeroNumber(String.valueOf(dayOfMonth), 2));
//						mYear = year;
//						mMonth = monthOfYear;
//						mDay = dayOfMonth;
//					}
//				}, mYear, mMonth, mDay);
//				try {
//					((ViewGroup) dp.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
//				}catch (Exception ex){
//					ex.printStackTrace();
//				}
//
//				dp.show();
//				break;
			case R.id.buttongetchartdaily:
				BHelper.db("show DatePicker Dialog");
				DatePickerDialog dpd = new DatePickerDialog(getActivity(),R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						setList(year + "-" + Helper.appenZeroNumber(String.valueOf(monthOfYear + 1), 2) + "-" + Helper.appenZeroNumber(String.valueOf(dayOfMonth), 2));
						mYear = year;
						mMonth = monthOfYear;
						mDay = dayOfMonth;
					}
				}, mYear, mMonth, mDay);
				dpd.show();
				//dpd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
				break;
			default:
				break;
			}

		}
	};

	private OnItemSelectedListener mSelectType = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			setReceiptType(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};
	private OnItemSelectedListener mSelectQuarter = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (!isStart)
				setReceiptQuater(position);
			isStart = false;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	public void setList(final String sDate) {
		date = sDate;
		btnDaily.setText(sDate);
		txtChartdateDaily.setText(sDate);
		txtDate.setText("(" + sDate + ")");
		comEntity = CompanyManger.getCompanyByID(AppHelper.getCurrentVanID());
		if (comEntity == null) {
			Toast.makeText(at, R.string.please_select_company_first, Toast.LENGTH_LONG).show();
			return;
		}
		setVantext(comEntity);
		getList(sDate);

	}

	private void getList(final String date) {
		BHelper.db("CancelDailyListFragment:"+isDailyChart);
		new MyTask(getActivity()) {

			@Override
			public boolean run() {
				list = ReceiptManager.getReceiptByDate(comEntity.getF_CompanyNo(), comEntity.getF_MachineCode(), date);
				return list != null;
			}

			@Override
			public boolean res(boolean result) {
				setListView(list);
				return true;
			}
		};
	}

	public static void setVantext(CompanyEntity comE) {
		if (comE == null)
			comE = CompanyManger.getCompanyByID(AppHelper.getCurrentVanID());
		comEntity =comE;
		if (txtVan != null)
			txtVan.setText("대표:" + comE.getF_CompanyOwnerName());
		if (txtCompanyName != null)
			txtCompanyName.setText("상호 :" + comE.getF_CompanyName());
		if (txtCompanyNo != null)
			txtCompanyNo.setText(Helper.formatCompanyNo(comE.getF_CompanyNo()));
	}

	private void setListView(final List<ReceiptEntity> mList) {
		new Runnable() {
			public void run() {
				adapter = new ReceiptDailyAdapter(at, mList);
				mListView.setAdapter(adapter);
				long Total = 0;
				for (ReceiptEntity receiptEntity : mList) {
					String amount = receiptEntity.getF_TotalAmount();
					if (receiptEntity.getF_revStatus().equals("0"))
						Total = Total - 0;
					else
						Total = Total + Long.valueOf(amount);
				}
				txtTotalAmount.setText(NumberFormat.getInstance(Locale.US).format(Total));
			}
		}.run();
	}

	private void setReceiptType(final int type) {
		listTemp = new ArrayList<ReceiptEntity>();
		if (list == null)
			return;
		new HandlerTask(at) {

			@Override
			public String run() {
				if (type == 1)
					sTypeReceipt = StaticData.paymentTypeCredit;
				else if (type == 2)
					sTypeReceipt = StaticData.paymentTypeCash;
				else
					sTypeReceipt = "";
				if (sTypeReceipt.equals(""))
					listTemp = list;
				else {
					for (ReceiptEntity reEntity : list) {
						if (reEntity.getF_Type().equals(sTypeReceipt))
							listTemp.add(reEntity);
					}
				}
				return null;
			}

			@Override
			public boolean res() {
				adapter = new ReceiptDailyAdapter(at, listTemp);
				mListView.setAdapter(adapter);
				return false;
			}
		};

	}

	private void setReceiptQuater(final int quater) {
		spType.setSelection(0);
		new MyTask(at) {

			@Override
			public boolean run() {
				list = Helper.getReceiptByQuater(comEntity, quater);
				return true;
			}

			@Override
			public boolean res(boolean result) {
				setListView(list);
				return false;
			}
		};

	}
	@Override
	public void onPause() {
		super.onPause();
		chartdaily = false;
	}

	private void setMonth(String month) {
		spType.setSelection(0);
		btnMonth.setText(month.substring(0, month.lastIndexOf("-")));
		sMonth = month;
		new MyTask(getActivity()) {

			@Override
			public boolean run() {
				list = ReceiptManager.getReceiptByMonth(comEntity.getF_CompanyNo(), comEntity.getF_MachineCode(), sMonth);
				return list != null;
			}

			@Override
			public boolean res(boolean result) {
				setListView(list);
				return false;
			}
		};
	}

	class FlingDetector extends GestureDetector.SimpleOnGestureListener {

		private int swipeMinDistance;
		private int swipeMinVelocity;
		private static final int swipeMaxDeviation = 250;

		public FlingDetector(Context context) {
			final ViewConfiguration vc = ViewConfiguration.get(context);
			swipeMinDistance = vc.getScaledWindowTouchSlop();
			swipeMinVelocity = vc.getScaledMinimumFlingVelocity();
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			swipeMinDistance = 160;
			try {
				if (Math.abs(e1.getY() - e2.getY()) > swipeMaxDeviation)
					return false;

				if (e1.getX() - e2.getX() > swipeMinDistance && Math.abs(velocityX) > swipeMinVelocity) {
					date = DateHelper.getNextDay(date, true);
					setList(date);
					return true;
				} else if (e2.getX() - e1.getX() > swipeMinDistance && Math.abs(velocityX) > swipeMinVelocity) {
					date = DateHelper.getNextDay(date, false);
					setList(date);
					return true;
				}
			} catch (Exception e) {

			}
			return false;
		}
	}

}
