package com.devcrane.payfun.daou;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.devcrane.android.lib.emvreader.BlueToothListener;
import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.BTReaderInfo;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.BTHelper;
import com.devcrane.payfun.daou.utility.VersionUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConfigFragment extends Fragment implements BlueToothListener{
	private CheckBox cbWifi;
	private TextView tvVersion,tvBTReaderName;
//	Spinner spReaderType;
	RadioGroup rdgReadeType;
	RadioButton rdbBT;
	RadioButton rdbEarjack;
	EmvReader emvReader;
    FragmentActivity currentActivity;
	EditText edtServerIp, edtServerPort;
	Button btnSave;

	Context mContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_config, container, false);
		cbWifi = (CheckBox) v.findViewById(R.id.cbWifi);
		tvVersion = (TextView)v.findViewById(R.id.appVersion);
		rdgReadeType = (RadioGroup) v.findViewById(R.id.rdgReadeType);
		rdbBT = (RadioButton) rdgReadeType.findViewById(R.id.rdbBluetooth);
		rdbEarjack = (RadioButton) rdgReadeType.findViewById(R.id.rdbEarjack);
        tvBTReaderName = (TextView)v.findViewById(R.id.tvBTReaderName);
		edtServerIp  = (EditText)v.findViewById(R.id.edtVanIp);
		edtServerPort = (EditText)v.findViewById(R.id.edtVanPort);
		btnSave = (Button)v.findViewById(R.id.btnSaveVanServer);

		mContext = getContext();
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		currentActivity = getActivity();
		BHelper.setActivity(currentActivity);
		BHelper.setTypeface(getView());
		emvReader = MainActivity.getEmvReader();
//		emvReader.stopConnection();
		initComponent();
        attachService();
	}

    @Override
    public void onStop() {
        super.onStop();
        detachService();
    }

    private void initComponent() {
		cbWifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				AppHelper.prefSeBoolean(StaticData.UseWifi, isChecked);
			}
		});
		String CurVersion = VersionUtils.getVersionCode(currentActivity, this.getClass());
		cbWifi.setChecked(AppHelper.prefGetBoolean(StaticData.UseWifi, false));
		tvVersion.setText(CurVersion);
        initReaderType();
		loadDefaultReaderInfo();
        tvBTReaderName.setOnClickListener(clickListener);

		edtServerIp.setText(AppHelper.getDownloadVanIp());
		edtServerPort.setText(String.valueOf(AppHelper.getDownloadVanPort()));
		btnSave.setOnClickListener(clickListener);
	}
	void loadDefaultReaderInfo(){
		BTReaderInfo btReaderInfo = AppHelper.getBTReaderInfo();
		if(btReaderInfo.getName().equals("")){
			BTHelper.savePairedBT(currentActivity);
		}
		loadBTReaderInfo();

	}
    void initReaderType(){
		rdgReadeType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				String readerType = ((RadioButton)radioGroup.findViewById(i)).getText().toString();
				String readerTypeBT =getString(R.string.reader_type_bluetooth);
				BHelper.db("checked:"+ i+ readerType + "    "+ readerTypeBT);

				boolean isBT = false;
//				if(AppHelper.getReaderType()== EmvReader.READER_TYPE_BT){
//					isBT = true;
//				}
//
//				EmvReader.setIsBlueTooth(isBT);
				if(readerType.equals(readerTypeBT)){
					isBT = true;
					EmvReader.setIsBlueTooth(isBT);
					AppHelper.setReaderType(String.valueOf(EmvReader.READER_TYPE_BT));
					emvReader.stopAudio();
				}
				else{
					isBT = false;
					EmvReader.setIsBlueTooth(isBT);
					AppHelper.setReaderType(String.valueOf(EmvReader.READER_TYPE_EARJACK));
					emvReader.disconnectBT();
					emvReader.restartAudio();
				}

			}
		});
		if(AppHelper.getReaderType()== EmvReader.READER_TYPE_BT)
			rdbBT.setChecked(true);
		else
			rdbEarjack.setChecked(true);
    }
	//region WisePad
	static ArrayAdapter<String> arrayAdapter;
	static List<BluetoothDevice> foundDevices;
	static Dialog wiseDialog;
	static final String[] DEVICE_NAMES = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	public void dismissDialog() {
		if (wiseDialog != null) {
			wiseDialog.dismiss();
			wiseDialog = null;
		}
	}
    void loadBTReaderInfo(){
        BTReaderInfo btReaderInfo = AppHelper.getBTReaderInfo();
        tvBTReaderName.setText(btReaderInfo.getName());
    }
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tvBTReaderName:
					if(AppHelper.getReaderType()== EmvReader.READER_TYPE_BT)
					{
						promptForConnection();

					}
                    break;
				case R.id.btnSaveVanServer:
					if(edtServerIp.getText().toString().trim().equals("") || edtServerPort.getText().toString().trim().equals("")){
						BHelper.showToast(R.string.msg_van_info_required);
						break;
					}
					AppHelper.setDownloadVanIp(edtServerIp.getText().toString());
					AppHelper.setDownloadVanPort(edtServerPort.getText().toString());
					BHelper.showToast(R.string.success);
					break;
                default:
                    break;
            }
        }
    };
	public void promptForConnection() {
		dismissDialog();
		wiseDialog = new Dialog(currentActivity);
		wiseDialog.setContentView(R.layout.connection_dialog);
		wiseDialog.setTitle(this.getString(R.string.connection));

		String[] connections = new String[1];
		connections[0] = "Bluetooth";
//		connections[1] = "Audio";

//		ListView listView = (ListView) wiseDialog.findViewById(R.id.connectionList);
//		listView.setAdapter(new ArrayAdapter<String>(currentActivity, android.R.layout.simple_list_item_1, connections));
//		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				dismissDialog();
//				if (position == 0) {
					Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
					final BluetoothDevice[] pairedDevices = new BluetoothDevice[pairedObjects.length];
					for (int i = 0; i < pairedObjects.length; ++i) {
						pairedDevices[i] = (BluetoothDevice) pairedObjects[i];
					}

					final ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(currentActivity, android.R.layout.simple_list_item_1);
					for (int i = 0; i < pairedDevices.length; ++i) {
						String deviceName = pairedDevices[i].getName();
						if(deviceName.contains(getString(R.string.bt_device_prefix)))
						mArrayAdapter.add(deviceName);
					}

					dismissDialog();
					wiseDialog = new Dialog(currentActivity);
					wiseDialog.setContentView(R.layout.bluetooth_2_device_list_dialog);
					wiseDialog.setTitle(R.string.bluetooth_devices);

					ListView listView1 = (ListView) wiseDialog.findViewById(R.id.pairedDeviceList);
					listView1.setAdapter(mArrayAdapter);
					listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							BHelper.db("connecting..."+pairedDevices[position].getName());
                            BTReaderInfo btReaderInfo = new BTReaderInfo(pairedDevices[position].getName(),pairedDevices[position].getAddress());
//							emvReader.connectBT(pairedDevices[position]);


							//Jonathan 171122 수정
							AppHelper.setBTReaderInfo(btReaderInfo);
//							String btAddress = pairedDevices[position].getAddress();
//							String btNames = pairedDevices[position].getName();
//							ArrayList<String> savedBtAddress = new ArrayList<String>();
//							ArrayList<String> savedBtNames = new ArrayList<String>();
//							savedBtAddress =  AppHelper.getStringArrayPref(mContext,"BT_ADDRS");
//							savedBtNames =  AppHelper.getStringArrayPref(mContext,"BT_NAMES");
//							if(!savedBtNames.contains(btNames))
//							{
//								savedBtAddress.add(btAddress);
//								savedBtNames.add(btNames);
//							}
//							else
//							{
//								savedBtAddress.set(savedBtNames.indexOf(btNames) , btAddress);
//							}
//							Log.e("Jonathan", " bbb :: " + savedBtAddress.size());
//							Log.e("Jonathan", " bbb1 :: " + savedBtAddress.toString());
//							AppHelper.setStringArrayPref(mContext, "BT_ADDRS",savedBtAddress);
//							AppHelper.setStringArrayPref(mContext, "BT_NAMES",savedBtNames);





							loadBTReaderInfo();
							dismissDialog();
						}

					});

					arrayAdapter = new ArrayAdapter<String>(currentActivity, android.R.layout.simple_list_item_1);
					ListView listView2 = (ListView) wiseDialog.findViewById(R.id.discoveredDeviceList);
					listView2.setAdapter(arrayAdapter);
					listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							BHelper.db("connecting..."+ getString(R.string.connecting_bluetooth));
//                            statusEditText.setText(at.getString(R.string.connecting_bluetooth));
//							emvReader.connectBT(foundDevices.get(position));
                            BTReaderInfo btReaderInfo = new BTReaderInfo(foundDevices.get(position).getName(),foundDevices.get(position).getAddress());
//							emvReader.connectBT(pairedDevices[position]);



							//Jonathan 171122 수정
							AppHelper.setBTReaderInfo(btReaderInfo);
//							String btAddress = pairedDevices[position].getAddress();
//							String btNames = pairedDevices[position].getName();
//							ArrayList<String> savedBtAddress = new ArrayList<String>();
//							ArrayList<String> savedBtNames = new ArrayList<String>();
//							savedBtAddress =  AppHelper.getStringArrayPref(mContext,"BT_ADDRS");
//							savedBtNames =  AppHelper.getStringArrayPref(mContext,"BT_NAMES");
//							if(!savedBtNames.contains(btNames))
//							{
//								savedBtAddress.add(btAddress);
//								savedBtNames.add(btNames);
//							}
//							else
//							{
//								savedBtAddress.set(savedBtNames.indexOf(btNames) , btAddress);
//							}
//							Log.e("Jonathan", " bbb :: " + savedBtAddress.size());
//							Log.e("Jonathan", " bbb1 :: " + savedBtAddress.toString());
//							AppHelper.setStringArrayPref(mContext, "BT_ADDRS",savedBtAddress);
//							AppHelper.setStringArrayPref(mContext, "BT_NAMES",savedBtNames);





                            loadBTReaderInfo();
							dismissDialog();
						}

					});

					wiseDialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							emvReader.stopBTScan();
							dismissDialog();
						}
					});
					wiseDialog.setCancelable(false);
					wiseDialog.show();
					BHelper.db("start BT Scan.....");
					emvReader.startBTScan(DEVICE_NAMES, 120);
//				}
//                else if (position == 1) {
//					emvReader.startAudio();
//				}
//			}
//
//		});

		wiseDialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissDialog();
			}
		});

		wiseDialog.show();
	}
    void attachService(){

        if(emvReader!=null){
            emvReader.attachBlueToothListener(ConfigFragment.this);
        }
    }
    void detachService(){
        if(emvReader!=null){
            emvReader.detachBluetoothListener(ConfigFragment.this);

        }
    }
	@Override
	public void onBTReturnScanResults(List<BluetoothDevice> list) {
        BHelper.db("onBTReturnScanResults in Config: "+ list.size());
//		ConfigFragment.foundDevices = list;
		ConfigFragment.foundDevices = new LinkedList<BluetoothDevice>();
		if (arrayAdapter != null) {
			arrayAdapter.clear();
			for (int i = 0; i < list.size(); ++i) {
				BHelper.db("found device: "+ list.get(i).getName());
				String deviceName = list.get(i).getName();
				if(deviceName.contains(getString(R.string.bt_device_prefix))){
					arrayAdapter.add(deviceName);
					ConfigFragment.foundDevices.add(list.get(i));
				}

			}
			arrayAdapter.notifyDataSetChanged();
		}else {
            BHelper.db("arrayAdapter is null");
        }
	}

	@Override
	public void onBTScanTimeout() {

	}

	@Override
	public void onBTScanStopped() {

	}

	@Override
	public void onBTConnected(BluetoothDevice bluetoothDevice) {

	}

	@Override
	public void onBTDisconnected() {

	}
}
