package com.JTNetSecuerLibrary;

public class ICSecuer {
	static {
		System.loadLibrary("JTNetSecuerLibrary");
	}

	private native byte[] SignEncrypt(byte[] baSignInData);

	private native byte[] PinEncrypt(String strCardInData, String strPinInData);

	private native byte[] Auth(String strIPAddr, int iPortNo, int iSendLen,
			byte[] baSendData);

	private native int AuthCo(String strIPAddr, int iPortNo, int iSendLen,
			byte[] baSendData);

	public byte[] Sign_Enc(byte[] baSignInData) {
		byte[] baSignOutData = SignEncrypt(baSignInData);

		return baSignOutData;
	}

	public byte[] Pin_Enc(String strCardInData, String strPinInData) {
		byte[] baPinOutData = PinEncrypt(strCardInData, strPinInData);
		return baPinOutData;
	}

	public byte[] AuthProc(String strIPAddr, int iPortNo, int iSendLen,
			byte[] baSendData) {
		byte[] baRecvData = Auth(strIPAddr, iPortNo, iSendLen, baSendData);
		return baRecvData;
	}

	public int AuthProcCo(String strIPAddr, int iPortNo, int iSendLen,
			byte[] baSendData) {
		int iRet = AuthCo(strIPAddr, iPortNo, iSendLen, baSendData);
		return iRet;
	}
}
