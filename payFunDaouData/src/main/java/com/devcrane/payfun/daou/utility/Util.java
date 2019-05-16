package com.devcrane.payfun.daou.utility;


public class Util {
	public static String sTrack1;
	public static String sTrack2;
	public static String sDate;

	public static final byte[] PSE_APDU_SELECT = { (byte) 0x00, // CLA (class of command)
			(byte) 0xA4, // INS (instruction); A4 = select
			(byte) 0x04, // P1 (parameter 1) (0x04: select by name)
			(byte) 0x00, // P2 (parameter 2)
			(byte) 0x0E, // LC (length of data) 14 (0x0E) = length("2PAY.SYS.DDF01")
			'1', 'P', 'A', 'Y', '.', 'S', 'Y', 'S', '.', 'D', 'D', 'F', '0', '1', (byte) 0x00 // LE (max length of expected result, 0 implies 256)
	};
	public static final byte[] PPSE_APDU_SELECT = { (byte) 0x00, // CLA (class of command)
			(byte) 0xA4, // INS (instruction); A4 = select
			(byte) 0x04, // P1 (parameter 1) (0x04: select by name)
			(byte) 0x00, // P2 (parameter 2)
			(byte) 0x0E, // LC (length of data) 14 (0x0E) = length("2PAY.SYS.DDF01")
			'2', 'P', 'A', 'Y', '.', 'S', 'Y', 'S', '.', 'D', 'D', 'F', '0', '1', (byte) 0x00 };
	public static final byte[] Get_Processing_Options = { (byte) 0x00, // CLA Class
			(byte) 0xB2, // INS Instruction
			(byte) 0x01, // P1 Parameter 1
			(byte) 0x0C, // P2 Parameter 2
			(byte) 0x00 };
	public static final byte[] READ_TRACK2 = { (byte) 0x00, // CLA Class
			(byte) 0xB2, // INS Instruction
			(byte) 0x01, // P1 Parameter 1
			(byte) 0x0C, // P2 Parameter 2
			(byte) 0x00 };
	public static final byte[] READ_RECORD = { (byte) 0x00, // CLA Class
			(byte) 0xB2, // INS Instruction
			(byte) 0x01, // P1 Parameter 1
			(byte) 0x14, // P2 Parameter 2
			(byte) 0x00 };
	public static final byte[] READ_RECORD2 = { (byte) 0x00, // CLA Class
			(byte) 0xB2, // INS Instruction
			(byte) 0x02, // P1 Parameter 1
			(byte) 0x14, // P2 Parameter 2
			(byte) 0x00 };
	public static final byte[] READ_RECORD3 = { (byte) 0x00, // CLA Class
			(byte) 0xB2, // INS Instruction
			(byte) 0x03, // P1 Parameter 1
			(byte) 0x14, // P2 Parameter 2
			(byte) 0x00 };
	public static final byte[] VISA_MSD_SELECT = { (byte) 0x00, // CLA
		(byte) 0xA4, // INS
		(byte) 0x04, // P1
		(byte) 0x00, // P2
		(byte) 0x07, // LC (data length = 7)
		(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10  , (byte) 0x00
	};

	public static String getString(byte[] info, int start, int length, boolean toHex) {
		byte[] byteArr = null;
		byte[] byteArrTemp = info;
		int strLen = byteArrTemp.length;
		byteArr = new byte[length];
		if (strLen < start + length) {

		} else {
			for (int i = 0, j = start; i < length; i++, j++)
				byteArr[i] = byteArrTemp[j];
		}
		if (toHex)
			return SharedUtils.Byte2Hex(byteArr);
		else
			return new String(byteArr);
	}

	public static byte[] HexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static byte[] getstatusWord(byte[] result) {
		int resultLength = result.length;
		byte[] statusWord = { result[resultLength - 2], result[resultLength - 1] };
		return statusWord;
	}

	public static byte[] convert2Byte(String input) {
		String[] hexbytes = input.split("\\s");
		byte[] bytes = new byte[hexbytes.length];
		for (int i = 0; i < hexbytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(hexbytes[i], 16);
		}
		return bytes;
	}

	public static String getCardInfo(byte[] sInfo) {
		int start = 0;
		String recordTemplate = getString(sInfo, start, 2, true);
		start = start + 2;
		String track2EquivalenData = getString(sInfo, start, 2, true);
		start = start + 2;
		int lengTrack2 = sInfo[3];
		String track2 = getString(sInfo, 4, lengTrack2, true);
		String track2Data = track2.replace(" ", "");
		track2Data = track2Data.replace("D", "=");
		start = start + lengTrack2;
		String cardholdername = getString(sInfo, start, 3, true);
		start = start + 3;
		int lengName = sInfo[start - 1];
		String cardname = getString(sInfo, start, lengName, true);
		start = start + lengName;
		String Magnetic = getString(sInfo, start, 3, true);
		start = start + 3;
		int lengTrack1 = sInfo[start - 1];
		String Track1 = getString(sInfo, start, lengTrack1, true);

		String sCardInfo = "RecordTemplate:" + recordTemplate + "\n" + //
				"Track2EquivalenData:" + track2EquivalenData + "\n" + //
				"Track2:" + track2 + "\n" + //
				"Track2Data:" + track2Data + "\n" + //
				"Cardholdername:" + cardholdername + "\n" + //
				"Cardname:" + cardname + "\n" + //
				"Magnetic:" + Magnetic + "\n" + //
				"Track1:" + Track1;
		BHelper.db(sCardInfo);
		Track1.replace(" ", "");
		String Track1_Data = Track1.replace(" ", "");

		String Track2_Full = track2Data.replace("F", "");
		sTrack1 = convertHexToString(Track1_Data.trim());
		sTrack2 = Track2_Full;
		return Track2_Full;
	}

	public static String getAIDInfo(byte[] sInfo) {
		int start = 0;
		String FileControll = getString(sInfo, start, 2, true);
		start = start + 2;
		String Dedicated_File = getString(sInfo, start, 2, true);
		start = start + 2;
		int DF_Name_lenght = sInfo[start - 1];
		String File_Name = getString(sInfo, start, DF_Name_lenght, true);
		start = start + DF_Name_lenght;
		String BINARY = getString(sInfo, start, 2, true);
		start = start + 2;
		String FILE_CONTROL_INFORM = getString(sInfo, start, 3, true);
		start = start + 3;
		String APP_Template = getString(sInfo, start, 2, true);
		start = start + 2;
		String APP_Indentifier = getString(sInfo, start, 2, true);
		start = start + 2;
		int AID_lenght = sInfo[start - 1];
		String AID_FULL = getString(sInfo, start - 1, AID_lenght + 1, true);
		String AID = getString(sInfo, start, AID_lenght, true);
		start = start + AID_lenght;
		String APP_Label = getString(sInfo, start, 2, true);
		start = start + 2;
		int Label_lenght = sInfo[start - 1];
		String Type_Card = getString(sInfo, start, Label_lenght, true);
		String info = "FileControll:" + FileControll + "\n" + //
				"Dedicated_File:" + Dedicated_File + "\n" + //
				"File_Name:" + File_Name + "\n" + //
				"BINARY:" + BINARY + "\n" + //
				"FILE_CONTROL_INFORM:" + FILE_CONTROL_INFORM + "\n" + //
				"APP_Template:" + APP_Template + "\n" + //
				"APP_Indentifier:" + APP_Indentifier + "\n" + //
				"AID_FULL:" + AID_FULL + "\n" + //
				"AID:" + AID + "\n" + //
				"APP_Label:" + APP_Label + "\n" + //
				"Type_Card:" + Type_Card + "\n";
		BHelper.db(info);
		return AID_FULL;
	}

	public static String readRecord2(byte[] sInfo) {
		int start = 0;
		int key_lenght = 0;
		String RecordTemplate = getString(sInfo, start, 3, true);
		start = start + 3;
		String ICCPublicKey = getString(sInfo, start, 4, true);
		start = start + 4;
		String hex_lenght = getString(sInfo, start - 1, 1, true).trim();
		key_lenght = Integer.parseInt(hex_lenght, 16);
		BHelper.db("int:" + key_lenght + "Length:" + sInfo.length);
		String ICCPublicDATA = getString(sInfo, start, key_lenght, true);
		start = start + key_lenght;
		String ICCPublicKeyEX = getString(sInfo, start, 3, true);
		start = start + 3;
		key_lenght = sInfo[start - 1];
		String ICCPublicKeyEXData = getString(sInfo, start, key_lenght, true);
		start = start + key_lenght;
		String ICCPublicKeyRemainder = getString(sInfo, start, 3, true);
		start = start + 3;
		key_lenght = sInfo[start - 1];
		BHelper.db("int:" + key_lenght + "Start:" + start + "|:" + getString(sInfo, start - 1, 1, true));
		String ICCPublicKeyRemainder_Data = getString(sInfo, start, key_lenght, true);
		start = start + key_lenght;
		String Dynamic = getString(sInfo, start, 3, true);
		start = start + 3;
		key_lenght = sInfo[start - 1];
		String Dynamic_Data = getString(sInfo, start, key_lenght, true);
		start = start + key_lenght;
		String EXP_Date = getString(sInfo, start, 3, true);
		start = start + 3;
		key_lenght = sInfo[start - 1];
		String eXP_Date_Data = getString(sInfo, start, key_lenght, true);
		start = start + key_lenght;
		String EFF_Date = getString(sInfo, start, 3, true);
		start = start + 3;
		key_lenght = sInfo[start - 1];
		String EFF_Date_Data = getString(sInfo, start, key_lenght, true);
		start = start + key_lenght;
		String USE_CONTROL = getString(sInfo, start, 3, true);
		start = start + 3;
		key_lenght = sInfo[start - 1];
		String USE_CONTROL_DATA = getString(sInfo, start, key_lenght, true);

		sDate = eXP_Date_Data.replace(" ", "");
		if (sDate.length() > 4)
			sDate = sDate.substring(0, 4);
		String data = "RecordTemplate:" + RecordTemplate + "\n" + //
				"ICCPublicKey:" + ICCPublicKey + "\n" + //
				"ICCPublicDATA:" + ICCPublicDATA + "\n" + //
				"ICCPublicKeyEX:" + ICCPublicKeyEX + "\n" + //
				"ICCPublicKeyEXData:" + ICCPublicKeyEXData + "\n" + //
				"ICCPublicKeyRemainder:" + ICCPublicKeyRemainder + "\n" + //
				"ICCPublicKeyRemainder_Data:" + ICCPublicKeyRemainder_Data + "\n" + //
				"Dynamic:" + Dynamic + "\n" + //
				"Dynamic_Data:" + Dynamic_Data + "\n" + //
				"EXP_Date:" + EXP_Date + "\n" + //
				"eXP_Date_Data:" + eXP_Date_Data + "\n" + //
				"EFF_Date:" + EFF_Date + "\n" + //
				"EFF_Date_Data:" + EFF_Date_Data + "\n" + //
				"USE_CONTROL:" + USE_CONTROL + "\n" + //
				"USE_CONTROL_DATA:" + USE_CONTROL_DATA + "\n";
		BHelper.db(data);
		return data;
	}

	public static String ByteArrayToHexString(byte[] bytes) {
		final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static String convertHexToString(String hex) {

		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();

		for (int i = 0; i < hex.length() - 1; i += 2) {

			// grab the hex in pairs
			String output = hex.substring(i, (i + 2));
			// convert hex to decimal
			int decimal = Integer.parseInt(output, 16);
			// convert the decimal to character
			sb.append((char) decimal);

			temp.append(decimal);
		}
		System.out.println("Decimal : " + temp.toString());

		return sb.toString();
	}
}
