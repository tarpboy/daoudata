package com.devcrane.payfun.daou;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.devcrane.android.lib.msreader.PayfunApplication;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.NoticeEntity;
import com.devcrane.payfun.daou.entity.TerminalInfo;
import com.devcrane.payfun.daou.entity.UserEntity;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.manager.NoticeManager;
import com.devcrane.payfun.daou.manager.UserManager;
import com.devcrane.payfun.daou.ui.MyDialog;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.MyTask;
import com.devcrane.payfun.daou.utility.MyTaskStr;
import com.devcrane.payfun.daou.van.DaouData;
import com.devcrane.payfun.daou.van.OpenTerminal;

import static com.devcrane.payfun.daou.MainActivity.isBTReaderConnected;

public class HomeFragment extends Fragment {
	private Activity at = getActivity();
	private ListView lvNotice;
	public static ImageButton btnLogin;
	public static TextView tvUserName;
	MyDialog dialog;
	UserEntity key = new UserEntity(0);
	static int[] ids = { R.id.btnMMCancelList,R.id.btnMMCancelPayment,R.id.btnMMCashCard,R.id.btnMMCoupon,
			R.id.btnMMCreditCard,R.id.btnMMMemberShip};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_home, container, false);
		lvNotice = (ListView) v.findViewById(R.id.lvNotice);
		return v;
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		checkToReturnCallerApp();
	}
	@Override
	public void onStart() {
		super.onStart();
		initComponent();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	private void checkToReturnCallerApp() {
		Log.d("callApp","check to return data to caller app");
		if(!StaticData.getIsCalled())
			AppHelper.resetReturnToCaller();
		String retData =AppHelper.getReturnToCaller();
		if(StaticData.getIsCalled() && retData!=""){
			at = getActivity();
			Log.d("callApp","return data to caller app");
			String[] data = retData.split(";");
			String comNo,approvalNo,amount,totalAmount;
			comNo = approvalNo = amount = totalAmount = "";
			if(data.length>=4){
				comNo = data[0];
				approvalNo = data[1];
				amount = data[2];
				totalAmount = data[3];
			}
			Log.d("callApp","reset data after return data to caller app");
			AppHelper.resetReturnToCaller();
			Intent i = new Intent();
			i.putExtra("COMPANY_NO", comNo);
			i.putExtra("APPROVAL_NO", approvalNo);
			i.putExtra("AMOUNT", amount);
			i.putExtra("TOTAL_AMOUNT", totalAmount);
			at.setResult(Activity.RESULT_OK,i);
			at.finish();
		}

	}
	void lockMenu(){
		if(StaticData.getIsCalled()){
			BHelper.db("remove all listener for caller");
			for (int id : ids) {
				at.findViewById(id).setOnClickListener(null);
			}
		}

	}

	private void doCheckLogin() {
		if (!AppHelper.getIsLogin()) {
			final String f_Passwd = AppHelper.prefGet(key.getF_Password(), "");
			if (!f_Passwd.equals("")) {
				BHelper.db("Use old way to login first time.");
				final String f_Email = AppHelper.prefGet(key.getF_Email(), "");
				new MyTask(at) {
					@Override
					public boolean run() {
						String userID = UserManager.checkLoginV1(at,f_Email, f_Passwd);
						AppHelper.setCurrentUserID(userID);
						return userID != null;
					}

					@Override
					public boolean res(boolean result) {
						if (result) {
							AppHelper.setIsLogin(true);
							doLoginSuccess();
						}
						return false;
					}
				};
			} else {
				MainActivity main = (MainActivity) getActivity();
				main.initMenuLeft();
			}
		} else {
			String name = AppHelper.getCurrentUserName();
			tvUserName.setText(name.equals("") ? "" : name + " 로그인 하셨습니다.");
			MainActivity main = (MainActivity)getActivity();
			main.initMenuLeft();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		doCheckLogin();
		lockMenu();
	}

	private void initComponent() {
		BHelper.setActivity(at = getActivity());
		BHelper.setTypeface(getView());
		CancelListFragment.isDaily = false;

		setAdapter();

		final OnClickListener onMenuListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity main = (MainActivity) at;
				if (v.getId() == R.id.btnMenuLeft) {
					main.mSlidingMenu.showMenu();
				} else {
					main.mSlidingMenu.showSecondaryMenu();
				}
			}
		};
		at.findViewById(R.id.btnMenuLeft).setOnClickListener(onMenuListener);
		at.findViewById(R.id.btnMenuRight).setOnClickListener(onMenuListener);

		final Map<Integer, Fragment> map = new HashMap<Integer, Fragment>();
		map.put(R.id.btnMMCreditCard, new PaymentsCreditFragment());
		map.put(R.id.btnMMCashCard, new PaymentsCashFragment());
		map.put(R.id.btnMMCoupon, new CouponFragment());
		map.put(R.id.btnMMCancelPayment, new CancelSelectorFragment());
		map.put(R.id.btnMMCancelList, new CancelDailyListFragment());
		map.put(R.id.btnMMMemberShip, new ProfileFragment());

		for (final Integer id : map.keySet()) {
			at.findViewById(id).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if (AppHelper.getIsLogin()){
						if(id == R.id.btnMMCoupon){
							BHelper.showToast(R.string.msg_unavailable_function);
						}else{

							if(id == R.id.btnMMCreditCard)
							{
								if (!isBTReaderConnected && !Helper.isHeadsetConnected(at))
								{
//									showRequestDevice();
//									return;
									BHelper.showToast("단말기 연결이 필요합니다.");
								}
							}

							MainActivity.setFragment(map.get(id));
						}

					}

					else{
						BHelper.showToast(R.string.msg_not_login_yet);
					}
				}
			});
		}
		btnLogin = (ImageButton) at.findViewById(R.id.btnMMLogin);
		tvUserName = (TextView) at.findViewById(R.id.tvUserName);
		if (AppHelper.getIsLogin()) {
			btnLogin.setImageResource(R.drawable.unlock);
			tvUserName.setText(AppHelper.getCurrentUserName()+ "로그인 하셨습니다.");
		}
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (AppHelper.getIsLogin()) {
//					AppHelper.prefSeBoolean(StaticData.Login,false);
//					AppHelper.prefSet(key.getF_ID(), "");
					AppHelper.prefSet(CompanyManger.TABLE, "");

					AppHelper.setIsLogin(false);
					tvUserName.setText(getString(R.string.logout_message));
//					AppHelper.prefSet(StaticData.USERNAME, "");
					AppHelper.setCurrentUserName("'");
					btnLogin.setImageResource(R.drawable.lock);
					MainActivity main = (MainActivity) getActivity();
					main.initMenuLeft();
					Toast.makeText(at, getString(R.string.msg_log_out), Toast.LENGTH_SHORT).show();
				} else {
					MainActivity.setFragment(new LoginFragment());
				}

			}
		});
	}



	void showRequestDevice(){

		new AlertDialog.Builder(getContext())
				.setTitle("단말기 연결이 필요합니다.")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("예", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						RegistervanNewFragment fragment = new RegistervanNewFragment();
						MainActivity.setFragment(fragment);
					}
				})
//                .setNegativeButton("아니오(cancel)", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//              }
//           })
				.show();
	}
	private void setAdapter() {
		new MyTask(at) {
			private List<NoticeEntity> objects;

			@Override
			public boolean run() {
				objects = NoticeManager.get();
				return false;
			}

			@Override
			public boolean res(boolean result) {
				lvNotice.setAdapter(new NoticeAdapter(at, objects));
				return false;
			}
		};

		lvNotice.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				NoticeEntity ne = (NoticeEntity) arg0.getAdapter().getItem(arg2);
				dialog = new MyDialog(at);
				dialog.tvContent.setText(ne.getF_Content());
				dialog.tvContent.setTextColor(Color.BLACK);
				dialog.tvTitle.setText(ne.getF_Titile());
				dialog.tvTitle.setTextColor(Color.BLACK);
				dialog.btnOK.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();

					}
				});
				dialog.show();
			}
		});
	}

	private class NoticeAdapter extends ArrayAdapter<NoticeEntity> {

		private LayoutInflater mInflater;
		private List<NoticeEntity> objects;

		public NoticeAdapter(Context context, List<NoticeEntity> objects) {
			super(context, 0, objects);
			mInflater = LayoutInflater.from(context);
			this.objects = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.item_notice, null);
			final TextView tvType = (TextView) convertView.findViewById(R.id.tvType);
			final TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			// final TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			BHelper.setTypeface(tvType);

			final NoticeEntity ne = objects.get(position);
			tvType.setText(ne.getF_Type());
			tvDate.setText(ne.getUPDATE_DT().substring(0, 10));
			// tvTitle.setText(ne.getF_Titile());
			return convertView;
		}
	}
	void checkVanInfo(){

		if(!AppHelper.getVanIp().equals("")){
			BHelper.db("exist van info: dont need update");
			return;
		}
		CompanyEntity companyEntity = CompanyManger.getCompanyByID(AppHelper.getCurrentVanID());
		if(companyEntity ==null) return;

		final DaouData daouData = new OpenTerminal();
		final TerminalInfo terminalInfo = new TerminalInfo();
		terminalInfo.setTerCompanyNo(companyEntity.getF_CompanyNo());
		terminalInfo.setTerNumber(companyEntity.getF_MachineCode());
		new MyTaskStr(at){

			@Override
			public String[] run() {

				return daouData.req(terminalInfo);
			}

			@Override
			public boolean res(String[] result) {
				if(result[1].equals("0000")){
					String vanInfo = result[9];
					String vanIP = vanInfo.substring(24,39).trim();
					String vanPort = vanInfo.substring(39,45).trim();
					BHelper.db("vanIP:"+vanIP + ", vanPort:"+vanPort);
					AppHelper.setVanIp(vanIP);
					AppHelper.setVanPort(vanPort);
					BHelper.db(DaouData.getResp(result));
				}else {
					String msg =result[21];
					BHelper.showToast(msg);

				}
				return false;
			}
		};

	}
	private void doLoginSuccess() {
		new MyTask(at) {

			@Override
			public boolean run() {
				CompanyManger.getCompanyByUserID(AppHelper.getCurrentUserID());
				return true;
			}

			@Override
			public boolean res(boolean result) {

				BHelper.db("doLoginSuccess:");
				checkVanInfo();
				AppHelper.setIsLogin(true);
				MainActivity main = (MainActivity) getActivity();
				main.initMenuLeft();
				btnLogin.setImageResource(R.drawable.unlock);
				String name = AppHelper.getCurrentUserName();
				tvUserName.setText(name.equals("") ? "" : name + " 로그인 하셨습니다.");
				return false;
			}
		};
	}
}