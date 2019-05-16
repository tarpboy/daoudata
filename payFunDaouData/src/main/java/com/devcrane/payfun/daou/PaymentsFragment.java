package com.devcrane.payfun.daou;

import android.widget.EditText;
import android.widget.TextView;

import com.devcrane.payfun.cardreader.ReaderFragment;
import com.devcrane.payfun.daou.caller.ParaConstant;
import com.devcrane.payfun.daou.caller.ReqPara;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.InCompleteDataEntity;
import com.devcrane.payfun.daou.entity.TerminalInfo;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.van.DaouData;
import com.devcrane.payfun.daou.van.DaouDataContants;
import com.devcrane.payfun.daou.van.IPayment;
import com.devcrane.payfun.daou.van.IPaymentEmv;

public abstract class PaymentsFragment extends ReaderFragment{
	protected static TextView txtCompanyNo, txtMachineCode, txtCompanyOwerName, txtVanName,tvTax;
	protected static CompanyEntity comEntity;
	protected EditText edTAmount;
	protected String diviMonth = "00";
	protected String approvalNoCancel = "";
	protected IPayment payment;
	protected IPaymentEmv paymentEmv;
	@Override
	public void onStart() {
		super.onStart();
		BHelper.setTypeface(getView());
	}

	protected void checkNetworkResult(String networkResult){
		switch (networkResult){
			case DaouData.NETWORK_RESULT_DLE:
				showFallbackDlg(R.string.msg_network_result_dle);
				break;
			case DaouData.NETWORK_RESULT_SOCKET_ERROR:
				showFallbackDlg(R.string.msg_network_result_socket_error);
				break;
			case DaouData.NETWORK_RESULT_NO_EOT:
				InCompleteDataEntity dataEntity = AppHelper.getInCompleteData();
				if(dataEntity.getRespCode().equals(DaouDataContants.VAL_RESP_CODE_SUCCESS))
					showFallbackDlg(R.string.msg_network_result_no_eot);
				break;
		}

	}
	protected void onInitView() {
		initCard();
		txtCompanyNo = (TextView) getActivity().findViewById(R.id.txtCard_reading_CompanyNo);
		txtMachineCode = (TextView) getActivity().findViewById(R.id.txtCard_reading_MachineCode);
		txtCompanyOwerName = (TextView) getActivity().findViewById(R.id.txtCard_reading_CompanyOwerName);
		txtVanName = (TextView) getActivity().findViewById(R.id.txtCard_reading_VanName);
		CancelListFragment.isChartList = false;
		CancelDailyListFragment.isDailyChart = false;
	}
	public static void setCompany(CompanyEntity companyEntity) {
		onSetCompany(companyEntity);
	}
	
	@Override
	protected void checkEmvCard() {
		// TODO Auto-generated method stub
		super.checkEmvCard();
		
	}
	
	protected void loadFromCaller(){
		String isCalled = "";
		String reqParaJson="";
		if(getArguments()!=null){
			isCalled = getArguments().getString("isCalled","");
			reqParaJson =getArguments().getString("reqParaJson","");
		}
		BHelper.db("isCalled:" + isCalled);
		BHelper.db("reqParaJson:"+reqParaJson);
		StaticData.setToExit(false);
		if (reqParaJson!=null && !reqParaJson.equals("") && isCalled != null && isCalled.equals("true")) {
			
			StaticData.setIsCalled(true);
			ReqPara reqPara = ReqPara.fromJsonString(reqParaJson);
			String CoCode = reqPara.getCompanyNo();
			String machineCode = reqPara.getMachineNo();
			CompanyEntity companyEntity = CompanyManger.getCompany(CoCode, machineCode);
			companyEntity.setF_TaxRate(reqPara.getTaxRate());
			setCompany(companyEntity);
			setDisplaySignature(reqPara.getTotalAmount(),true);
			if(reqPara.getTransType().equals(ParaConstant.TRANS_TYPE_APPROVE)){
				int calledAmount = Integer.parseInt(reqPara.getTotalAmount());
//				edTAmount.setText(String.format("%,d", calledAmount));
				edTAmount.setText(String.valueOf(calledAmount));
				diviMonth = reqPara.getDivideMonth();
				if(reqPara.getPaymentType().equals(ParaConstant.PAYMENT_TYPE_CASH)){
					tvTax.setText(reqPara.getTaxRate());
				}
			}else if(reqPara.getTransType().equals(ParaConstant.TRANS_TYPE_CANCEL)){
				approvalNoCancel = reqPara.getApprovalNo();
				//doCard();
			}
			MainActivity.lockLeftRightMenu(true);
		} else {
			StaticData.setIsCalled(false);
			setCompany(null);
		}
	}
	protected static void onSetCompany(CompanyEntity companyEntity) {
		comEntity = companyEntity;
		if (comEntity == null)
			comEntity = CompanyManger.getCompanyByID(AppHelper.getCurrentVanID());
		if (comEntity != null) {
            TerminalInfo info = new TerminalInfo();
            info.setTerNumber(comEntity.getF_MachineCode());
            info.setTerCompanyNo(comEntity.getF_CompanyNo());
//            DaouDataHelper.openTerminal(at,info);
			BHelper.db("comEntity:"+comEntity.toString());
			try {
				if (txtCompanyNo != null)
					txtCompanyNo.setText(Helper.cutString(comEntity.getF_CompanyName(), 16));
				if (txtMachineCode != null)
					txtMachineCode.setText(comEntity.getF_CompanyOwnerName());
				if (txtCompanyOwerName != null)
					txtCompanyOwerName.setText(Helper.formatCompanyNo(comEntity.getF_CompanyNo()));
				if (txtVanName != null)
					txtVanName.setText(comEntity.getF_VanName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doCard() {

	}

	@Override
	protected void doReset() {

	}

}
