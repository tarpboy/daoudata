package com.devcrane.payfun.daou;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.ui.CompanyAdapter;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.SoundSearcher;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class CompanyListFragment extends Fragment {
	ListView listCompany;
	EditText edSearch;
	Activity at;
	CompanyAdapter adapter;
	List<CompanyEntity> objects;
	private PullToRefreshListView mPullRefreshListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_companylist, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		at = getActivity();
		BHelper.setTypeface(getView());
	}
	@Override
	public void onResume() {
		super.onResume();
		onInitView();
		
	}

	private void onInitView() {
//		listCompany = (ListView) at.findViewById(R.id.lvListCompany);
		edSearch = (EditText) at.findViewById(R.id.txtListSearch);
		edSearch.addTextChangedListener(textSearch);
		mPullRefreshListView = (PullToRefreshListView) at.findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(at.getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				doSyncCompany();
			
			}
		});
		listCompany = mPullRefreshListView.getRefreshableView();
		registerForContextMenu(listCompany);
		oninitData();
	}

	private void oninitData() {
		objects = CompanyManger.getAllCompany(AppHelper.getCurrentUserID());
		adapter = new CompanyAdapter(getActivity(), objects,true);
		listCompany.setAdapter(adapter);
		listCompany.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				adapter.setPosition(position-1);
				CompanyEntity comE = (CompanyEntity) listCompany.getAdapter().getItem(position);
				StaticData.sCompanyID = comE.getF_ID();
				AppHelper.setCurrentVanID(comE.getF_ID());
				MainActivity main = (MainActivity) getActivity();
				main.initMenuLeft();
				MainActivity.setFragment(new DetailsCompanyFragment());
			}
		});
	}
	private void doSyncCompany(){
		new SyncCompany().execute();
//		new MyTask(at) {
//			
//			@Override
//			public boolean run() {
//				CompanyManger.getCompanyByUserID(LoginFragment.F_USERID);
//				return true;
//			}
//			
//			@Override
//			public boolean res(boolean result) {
//				mPullRefreshListView.onRefreshComplete();
//				oninitData();
//				MainActivity main = (MainActivity) getActivity();
//				main.initMenuLeft();
//				return false;
//			}
//		};
	}
	
	private class SyncCompany extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			CompanyManger.getCompanyByUserID(AppHelper.getCurrentUserID());
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mPullRefreshListView.onRefreshComplete();
			oninitData();
			MainActivity main = (MainActivity) getActivity();
			main.initMenuLeft();
			edSearch.setText("");
			
		}
		
	}

	private TextWatcher textSearch = new TextWatcher() {

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
			if (s.toString().equals("")) {
				listCompany.setAdapter(adapter);
				return;
			}
			List<CompanyEntity> list = new ArrayList<CompanyEntity>();
			for (CompanyEntity obj : objects) {
				String keyword = s.toString();
				if (SoundSearcher.matchString(obj.getF_CompanyName(), keyword) || obj.getF_CompanyNo().indexOf(keyword) >-1 || obj.getF_MachineCode().indexOf(keyword) >-1) {
					list.add(obj);
				}
			}
			listCompany.setAdapter(new CompanyAdapter(getActivity(), list,true));

		}
	};
}
