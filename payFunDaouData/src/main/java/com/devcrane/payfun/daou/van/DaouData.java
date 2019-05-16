package com.devcrane.payfun.daou.van;

import com.devcrane.payfun.daou.entity.SessionInfo;
import com.devcrane.payfun.daou.entity.TerminalInfo;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Base64Utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Random;

public abstract class DaouData {

//	public static final String VAN_IP_ADDRESS = "222.106.99.137";
//	public static final int VAN_PORT = 20071;
//real server
	public static final String VAN_IP_ADDRESS = "222.106.99.133";
	public static final int VAN_PORT = 10071;


	
	protected static Random rand = new Random();

	protected static byte[] ACK = { 'C', 'T', 'A', 'C', 'K' };
	protected static byte[] EOT = { 'C', 'T', 'E', 'O', 'T' };

	protected String transType = "";
	protected String taskCode="";
	public static String keyInfo="";
	protected TerminalInfo terminalInfo =new TerminalInfo();
	public static String cardType = "J";

	public static final String NETWORK_RESULT_SUCCESS = "SUCCESS";
	public static final String NETWORK_RESULT_NO_EOT = "NO_EOT";
	public static final String NETWORK_RESULT_SOCKET_ERROR = "SOCKET_ERROR";
	public static final String NETWORK_RESULT_DLE = "DLE";
	private static String networkResult = NETWORK_RESULT_SOCKET_ERROR;

	public static String getNetworkResult() {
		return networkResult;
//		return NETWORK_RESULT_NO_EOT;
	}

	public static void setNetworkResult(String networkResult) {
		DaouData.networkResult = networkResult;
	}

	public String[] req(TerminalInfo terminalInfo){
		this.terminalInfo = terminalInfo;
		byte[] data = makeData();
		String[] recv = sendMsg(data, 0, data.length);
		BHelper.db("========RESP DATA======");
		BHelper.db(getResp(recv));
		return recv;
	}
	public String[] req(TerminalInfo terminalInfo, String vanIp, int vanPort){
		this.terminalInfo = terminalInfo;
		byte[] data = makeData();
		String[] recv = sendMsg(data, 0, data.length, vanIp, vanPort);
		BHelper.db("========RESP DATA======");
		BHelper.db(getResp(recv));
		return recv;
	}

	protected abstract byte[] makeData();

	protected String getModuleInfo(){
		return terminalInfo.getTerHwCert() + DaouDataHelper.appendChar(DaouDataContants.VAL_MODULE_ID,' ',10);
	}

	protected byte[] makeHeader(String transType, String taskCode){
		byte[] header = new byte[30];
		int pos = 0;
		int src_len;
		byte[] src_data;
		header[pos] = DaouDataContants.VAL_STX;
		pos++;
		BHelper.db("==================start of header=================");
		src_data = "    ".getBytes();//packet's len
		src_len =src_data.length;
		System.arraycopy(src_data, 0, header, pos, src_len);
		pos+=src_len;

		src_data = DaouDataContants.VAL_VERSION_DIVISION_V2.getBytes();
		src_len =src_data.length; 
		System.arraycopy(src_data, 0, header, pos, src_len);
		pos+=src_len;
		showLog("version",new String(src_data));
		src_data = transType.getBytes();
		src_len = src_data.length;
		System.arraycopy(src_data, 0, header, pos, src_len);
		pos+=src_len;
		showLog("trans type",new String(src_data));
		src_data = taskCode.getBytes();
		src_len = src_data.length;
		System.arraycopy(src_data, 0, header, pos, src_len);
		pos+=src_len;
		showLog("task code",new String(src_data));

		src_data = terminalInfo.getModelCode().getBytes();
		src_len = src_data.length;
		System.arraycopy(src_data, 0, header, pos, src_len);
		pos+=src_len;
		showLog("model code",new String(src_data));

		src_data = terminalInfo.getTerNumber().getBytes();
		src_len = src_data.length;
		System.arraycopy(src_data, 0, header, pos, src_len);
		pos+=src_len;
		showLog("terninal number",new String(src_data));

		src_data = getSlipNumber().getBytes();
		src_len = src_data.length;
		System.arraycopy(src_data, 0, header, pos, src_len);
		pos+=src_len;
		showLog("slip number",new String(src_data));
		header[pos] = DaouDataContants.VAL_FS;
		src_data = null;
		src_len = 0;
		BHelper.db("==================end of header=================");
		return header;
	}
	protected String getSlipNumber(){
		SessionInfo info = AppHelper.getSessionInfo();
        BHelper.db("sessionInfo:"+info.toString());
		if(!info.getSlipNo().equals(""))
			return info.getSlipNo();
		return "0001";
	}
	protected String getTerminalFirmwareVers(){
		return "100";
	}
	protected String getPSTNGroupIndex(){
		SessionInfo info = AppHelper.getSessionInfo();
		if(!info.getSeedIndx().equals(""))
			return info.getSeedIndx();
		return "  ";
	}
	protected void showLog(String name, String data){
		BHelper.db(name+ ":"+ data);
		BHelper.db(name+ " size :"+ data.getBytes().length);
	}
	protected String makeTerminalInfo(){
		String res="";
		BHelper.db("==================start of terminal info=================");
		res+= terminalInfo.getTerProSerialNo();
		showLog("Production Serial Number",terminalInfo.getTerProSerialNo());
		res+=getTerminalFirmwareVers();
		showLog("firmware ver",getTerminalFirmwareVers());
		res+= getPSTNGroupIndex();
		showLog("PSTN Group Index",getPSTNGroupIndex());


//		res+=DaouDataContants.VAL_TERMINAL_DIVISION_GENERAL;
		res+=terminalInfo.getMachineDivision();
		showLog("Terminal Division",terminalInfo.getMachineDivision());
		res+= terminalInfo.geteSign();
		showLog("Electronic Signature",terminalInfo.geteSign());
		res+= " ";//DaouDataContants.VAL_DIVISION_DONGLE;
		showLog("Dongle Division"," ");
		res+=" ";//DaouDataContants.VAL_RF_CARD_VISA_WAVE;
		showLog("RF Card Division"," ");
		res+= DaouDataContants.VAL_NO_SLIP_DIVISION;
		showLog("No slip Division",DaouDataContants.VAL_NO_SLIP_DIVISION);
		res+=DaouDataContants.VAL_PC_POS_OTHER;
		showLog("PC-POS",DaouDataContants.VAL_PC_POS_OTHER);
		res+= DaouDataHelper.appendChar("", ' ', 8);//reserse
		showLog("reserse",DaouDataHelper.appendChar("", ' ', 8));
		BHelper.db("==================end of terminal info=================");
		return res;
	}
	protected String makeEncryptedInfo(String swCert16, String hwCert16){
		String res="";
		res+=DaouDataContants.VAL_KEY_DIVISION_DUKPT;
		BHelper.db("==================start of encrypted info=================");
		showLog("Key Division",res);
		swCert16 = terminalInfo.getTerSwCert();
		showLog("S/W Certification",swCert16);
		hwCert16 = terminalInfo.getTerHwCert();
		showLog("H/W Certification",hwCert16);
		res+=swCert16+ hwCert16;
		BHelper.db("==================end of encrypted info=================");
		return res;
	}
	static boolean isValidRecvChar(byte[] toCheck, byte needCheck, int count){
		int cnt = 0;
		if(toCheck.length>=count){
			for(int i=0;i<toCheck.length;i++){
				if(toCheck[i]==needCheck && cnt<count){
					cnt++;
				}else if(toCheck[i]!=needCheck && cnt<count){
					return false;
				}
			}
			return true;
		}else
			return false;
	}
	protected String[] sendMsg(byte[] msg) {
		int offset = 0;
		int length = msg.length;
		return sendMsg(msg,offset,length);

	}
	protected String[] sendMsg(byte[] msg, String vanIp, int vanPort) {
		int offset = 0;
		int length = msg.length;
		return sendMsg(msg,offset,length,vanIp,vanPort);

	}
	protected String[] sendMsg(byte[] msg, int offset, int length) {
		String vanIp = AppHelper.getVanIp();
		int vanPort = AppHelper.getVanPort();
		return sendMsg(msg,offset,length,vanIp,vanPort);

	}
	protected String[] sendMsg(byte[] msg, int offset, int length, String vanIP, int vanPort) {
		Socket sock = null;
		byte[] buffer = new byte[4096];
		String[] recv = null;
		int cnt = 0;
		boolean isReady = false, isFinish = false;
		networkResult = NETWORK_RESULT_SOCKET_ERROR;
		try {
			BHelper.db("SendMsg Start");
			sock = new Socket(vanIP,vanPort);
			OutputStream os = sock.getOutputStream();
			InputStream is = sock.getInputStream();

			BHelper.db("Listen data ");
			int send_count = 0;
			int real_received_count = 0;
			while ((cnt = is.read(buffer))>0){
				BHelper.db("received cnt:"+cnt);
				byte[] tmp = new byte[cnt];
				System.arraycopy(buffer,0,tmp,0,cnt);
				BHelper.db("received:"+ HexDump.toHexString(tmp));
                if(tmp[0]==DaouDataContants.VAL_STX){
                    real_received_count++;
                }
                BHelper.db("real_received_count:"+real_received_count);
                if(real_received_count==1){
                    byte[] ackBuffer = {0x06,0x06,0x06,0x06,0x06};
                    BHelper.db("Send ACK to finish transaction: "+HexDump.toHexString(ackBuffer));
                    os.write(ackBuffer,0,5);
                }
				if(isValidRecvChar(tmp,DaouDataContants.VAL_ENQ,5)){
					send_count++;
					os.write(msg, offset, length);
					BHelper.db("send data:"+ HexDump.toHexString(msg));
				}else if(isValidRecvChar(tmp,DaouDataContants.VAL_ACK,5)){
					isReady = true;
				}else if(isValidRecvChar(tmp,DaouDataContants.VAL_EOT,5)){
					networkResult = NETWORK_RESULT_SUCCESS;
					break;
				}else if(isValidRecvChar(tmp,DaouDataContants.VAL_DLE,5)){
					networkResult = NETWORK_RESULT_DLE;
				}
				else if(isValidRecvChar(tmp,DaouDataContants.VAL_NAK,5) && send_count<3){
					if(send_count==2){
						BHelper.db("send VAL_ENQ");
						os.write(DaouDataContants.VAL_ENQ);
						os.write(DaouDataContants.VAL_ENQ);
						os.write(DaouDataContants.VAL_ENQ);
						os.write(DaouDataContants.VAL_ENQ);
						os.write(DaouDataContants.VAL_ENQ);
					}else{

					}
					send_count++;
				}else if(isReady && !isFinish){
					byte []split = {0x1c};
					String recvStr="";
					try{
						recvStr = new String(tmp,"EUC-KR");
                        tmp = null;
					}catch (UnsupportedEncodingException ex){
						ex.printStackTrace();
					}
					recv =recvStr.split(new String(split));
					networkResult = NETWORK_RESULT_NO_EOT;
                    recvStr="";
					isFinish = true;
//					break;
				}
				buffer = new byte[4096];
			}
            os = null;
            is = null;
			sock.close();
			
			BHelper.db("Close socket");
		}catch(Exception ex){
			ex.printStackTrace();
		}finally {
			try {
				if (sock != null)
					sock.close();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		BHelper.db("network result:"+ getNetworkResult());

		return recv;
	}
	static protected String getRandKeyIdx() {
		String val = String.format("%02d", 1 + rand.nextInt(30));
		return val;
	}
	public static String getResp(String [] resp){
		String recvStr = "";
		int i=1;
		for (String item:resp
				) {
			recvStr+="item "+ i+ ": " + item+"\n";
			i++;
		}
		return recvStr;
	}
	public static String extractKeyInfo(String[] respData){
		String key = respData[15];
		key = HexDump.toHexString(Base64Utils.base64Decode(key));
		keyInfo = key;
		return key;
	}
}
