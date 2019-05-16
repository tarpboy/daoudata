package com.devcrane.payfun.daou;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import jxl.write.WriteException;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.manager.ReceiptManager;
import com.devcrane.payfun.daou.ui.ReceiptAdapter;
import com.devcrane.payfun.daou.ui.WriteExcel;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.DateHelper;
import com.devcrane.payfun.daou.utility.HandlerTask;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.JSonHelper;
import com.devcrane.payfun.daou.utility.MyTask;
import com.google.gson.reflect.TypeToken;

public class CancelListFragment extends Fragment {

	Activity at;
	static TextView txtVan;
	TextView txtDate;
	static CompanyEntity comEntity;
	ReceiptAdapter adapter;
	List<ReceiptEntity> list;
	String date = "2014-06-20";
	public static boolean isDaily = false;
	public static boolean isChartList = false;
	private GestureDetector mGestureDetector;
	private boolean isChart = false;
	private ListView mListView;
	private TextView tvMonth, tvQuarter, tvType;
	private int countQuarter = 0;
	private final int countTypeCredit = 0;
	private final int countTypeCash = 1;
	private final int countTypeAll = 2;
	private int countType = countTypeAll;

	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
	private final SimpleDateFormat textsdf = new SimpleDateFormat("yyyy-MM-dd");

	private int[] ids = { R.id.btnChartexcel, R.id.btnChartMonth,//
			R.id.btnChartDaily, R.id.chart_btn_quarter_next, R.id.chart_btn_quarter_pre,//
			R.id.chart_btn_month_next, R.id.chart_btn_month_pre, R.id.chart_btn_type_next, R.id.chart_btn_type_pre };
	private String sMonth = "";
	private LinkedList<String> lstType;
	private LinkedList<String> lstQuarter;
	String sTypeReceipt = "";
	List<ReceiptEntity> listTemp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_chart_daily, container, false);
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
		CancelDailyListFragment.isDailyChart = false;
		isChartList = true;
		isChart = true;
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

	@SuppressWarnings("deprecation")
	private void initView() {
		tvMonth = (TextView) at.findViewById(R.id.chart_tv_month);
		tvQuarter = (TextView) at.findViewById(R.id.chart_tv_quarter);
		tvType = (TextView) at.findViewById(R.id.chart_tv_type);
		mListView = (ListView) at.findViewById(R.id.listChart);
		txtDate = (TextView) at.findViewById(R.id.txtChartDailyDate);
		txtVan = (TextView) at.findViewById(R.id.txtChartDailyVan);
		mGestureDetector = new GestureDetector(new FlingDetector(at));
		initComponent();
		lstType = new LinkedList<String>();
		lstType.add(0, "Credit");
		lstType.add(1, "Cash");
		lstType.add(2, "All");
		setReceiptType(countType);
		lstQuarter = new LinkedList<String>();
		lstQuarter.add(0, "1 분기");
		lstQuarter.add(1, "2 분기");
		lstQuarter.add(2, "3 분기");
		lstQuarter.add(3, "4 분기");
		countQuarter = Helper.getQuater(DateHelper.getMonth());
		tvQuarter.setText(lstQuarter.get(countQuarter).toString());
	}

	private void initComponent() {
		mListView.setOnTouchListener(onTouch);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ReceiptEntity mREntity = (ReceiptEntity) mListView.getAdapter().getItem(position);
				Type type = new TypeToken<ReceiptEntity>() {
				}.getType();
				if (mREntity == null)
					return;
				StaticData.sResultPayment = JSonHelper.serializerJson(mREntity, type);
				MainActivity.setFragment(new ReceiptCancelFragment());
			}
		});
		for (int i : ids) {
			at.findViewById(i).setOnClickListener(onButtonClick);
		}
		tvMonth.setText(sdf.format(Calendar.getInstance().getTime()));
		date = DateHelper.currentDate();
		if (isDaily)
			date = StaticData.sDay;
		setList(date);
		txtVan.addTextChangedListener(new TextWatcher() {

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
				if (isChart)
					if (comEntity != null)
						getList(date);

			}
		});
	}

	private void getList(final String sDate) {
		BHelper.db("CancelListFragment:"+isChart);
		new MyTask(getActivity()) {

			@Override
			public boolean run() {
				list = ReceiptManager.getReceiptByMonth(comEntity.getF_CompanyNo(), comEntity.getF_MachineCode(), sDate);
				return list != null;
			}

			@Override
			public boolean res(boolean result) {
				setListView(list);
				return true;
			}
		};
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
				break;
			case R.id.btnChartDaily:
				MainActivity.setFragment(new CancelDailyListFragment());
				break;

			case R.id.chart_btn_month_next:
				setMonth(true);
				break;
			case R.id.chart_btn_month_pre:
				setMonth(false);
				break;
			case R.id.chart_btn_type_next:
				countType += 1;
				if (countType > countTypeAll) {
					countType = 2;
				} else {
					setReceiptType(countType);
				}
				break;

			case R.id.chart_btn_type_pre:
				countType -= 1;
				if (countType < countTypeCredit) {
					countType = 0;
				} else {
					setReceiptType(countType);
				}
				break;
			case R.id.chart_btn_quarter_next:
				countQuarter += 1;
				if (countQuarter > 3)
					countQuarter = 4;
				else {
					setReceiptQuater(countQuarter);
				}
				break;
			case R.id.chart_btn_quarter_pre:
				countQuarter -= 1;
				if (countQuarter < 0)
					countQuarter = 0;
				else {
					setReceiptQuater(countQuarter);
				}
				break;
			default:
				break;
			}

		}
	};

	private void setList(final String sDate) {
		BHelper.db("setList: date:"+ sDate);
		mListView.setOnScrollListener(null);
		txtDate.setText(sDate);
		comEntity = CompanyManger.getCompanyByID(AppHelper.getCurrentVanID());
		if (comEntity == null) {
			Toast.makeText(at, R.string.please_select_company_first, Toast.LENGTH_LONG).show();
			return;
		}
		setVantext(comEntity);
		getList(sDate);

	}

	public static void setVantext(CompanyEntity comE) {
		comEntity = comE;
		if (comEntity == null)
			comEntity = CompanyManger.getCompanyByID(AppHelper.getCurrentVanID());
		if (txtVan != null)
			txtVan.setText(comEntity.getF_CompanyOwnerName() + "\n" + Helper.formatCompanyNo(comEntity.getF_CompanyNo()));
	}

	private OnTouchListener onTouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mGestureDetector.onTouchEvent(event))
				return true;
			return false;
		}
	};

	

	private void setListView(final List<ReceiptEntity> mList) {
		new Runnable() {
			public void run() {
				adapter = new ReceiptAdapter(at, mList);
				mListView.setAdapter(adapter);
			}
		}.run();
	}

	private void setReceiptType(final int type) {
		mListView.setOnScrollListener(null);
		tvType.setText(lstType.get(type).toString());
		listTemp = new ArrayList<ReceiptEntity>();
		if (list == null)
			return;
		new HandlerTask(at) {

			@Override
			public String run() {
				if (type == 0)
					sTypeReceipt = StaticData.paymentTypeCredit;
				else if (type == 1)
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
				adapter = new ReceiptAdapter(at, listTemp);
				mListView.setAdapter(adapter);
				return false;
			}
		};

	}
	@Override
	public void onPause() {
		super.onPause();
		isChart = false;
	}

	private void setReceiptQuater(final int quater) {
		countType = countTypeAll;
		tvType.setText(lstType.get(countType));
		tvQuarter.setText(lstQuarter.get(quater).toString());
		new MyTask(at) {

			@Override
			public boolean run() {
				BHelper.db("get by Quater");
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

	private void setMonth(boolean isNext) {
		countType = countTypeAll;
		tvType.setText(lstType.get(countType));
		Calendar calendar = getCalendar();
		int iMonth = calendar.get(Calendar.MONTH);
		iMonth = iMonth + (isNext ? 1 : -1);
		calendar.set(Calendar.MONTH, iMonth);
		tvMonth.setText(sdf.format(calendar.getTime()));
		sMonth = textsdf.format(calendar.getTime());
		new MyTask(getActivity()) {

			@Override
			public boolean run() {
				BHelper.db("getReceiptByMonth: " + comEntity.getF_CompanyNo() + " machine code: "+ comEntity.getF_MachineCode() + " month: "+ sMonth);
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

	private Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(tvMonth.getText().toString()));
		} catch (ParseException ex) {
			BHelper.ex(ex);
		}
		return calendar;
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
