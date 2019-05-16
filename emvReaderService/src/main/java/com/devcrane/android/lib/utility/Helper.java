package com.devcrane.android.lib.utility;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import android.telephony.TelephonyManager;

public class Helper {
	// public static boolean TEST = false;

	public static String getStringKRByLength(String str, int start, int length) {
		byte[] byteArr = null;
		byte[] byteArrTemp;
		int strLen;

		try {
			byteArrTemp = str.getBytes("EUC-KR");
			strLen = byteArrTemp.length;
		} catch (UnsupportedEncodingException e) {
			return "";
		}

		String result = "";
		byteArr = new byte[length];
		if (strLen < start + length) {

		} else {
			for (int i = 0, j = start; i < length; i++, j++)
				byteArr[i] = byteArrTemp[j];
			try {
				result = new String(byteArr, "EUC-KR").trim();
			} catch (UnsupportedEncodingException e) {
				result = "";
			}
		}
		return result;
	}

	public static String formatCompanyNo(String coCode) {
		if (coCode.equals("") || coCode == null || coCode.length() < 5) {
			return "";
		}
		String v = "";
		String temp1 = coCode.substring(0, 3);
		String temp2 = coCode.substring(3, 5);
		String temp3 = coCode.substring(5);
		v = temp1 + "-" + temp2 + "-" + temp3;
		return v;
	}

	public static String cutString(String input, int length) {
		String result = input;
		if (input.length() > length)
			result = input.substring(0, length);
		return result;
	}

	public static String formatCompanyNoNew(String str) {
		str = (str.contains("-") ? str.replace("-", "") : str);
		if (str.length() > 3 && str.length() < 6) {
			str = str.substring(0, 3) + "-" + str.substring(3);
		} else if (str.length() >= 6) {
			str = str.substring(0, 3) + "-" + str.substring(3, 5) + "-" + str.substring(5);
		}
		return str;
	}

	public static String formatCompanyPhone(String phoneNo) {
		if (phoneNo.equals("") || phoneNo == null || phoneNo.length() < 6) {
			return phoneNo;
		}
		String v = "";
		String temp1 = phoneNo.substring(0, 4);
		String temp2 = phoneNo.substring(4);
		String temp3 = "";
		if (phoneNo.length() >= 11) {
			temp1 = phoneNo.substring(0, 3);
			temp2 = phoneNo.substring(3, 7);
			temp3 = phoneNo.substring(7);
			v = temp1 + "-" + temp2 + "-" + temp3;
		} else if (phoneNo.length() >= 9) {
			temp1 = phoneNo.substring(0, 2);
			temp2 = phoneNo.substring(2, 6);
			temp3 = phoneNo.substring(6);
			v = temp1 + "-" + temp2 + "-" + temp3;
		} else {
			v = temp1 + "-" + temp2;
		}
		return v;
	}

	public static String appenZeroNumber(String value, int length) {
		for (int i = value.length(); i < length; i++) {
			value = "0" + value;
		}
		return value;
	}

	public static String formatCardNo(String cardNo) {
		String v = "";
		int count = 0;
		if (cardNo.length() > 8 && cardNo != null) {
			String temp1 = cardNo.substring(0, 4);
			String temp2 = cardNo.substring(4, 8);
			String temp3 = "";
			if (cardNo.length() >= 16) {
				temp3 = cardNo.substring(12, 16);
				v = temp1 + "-" + temp2 + "-xxxx-" + temp3;
			} else if (cardNo.length() == 10) {
				v = temp1 + "-" + temp2 + "-xx";
			} else if (cardNo.length() == 11) {
				v = temp1 + "-" + temp2 + "-xxx";
			} else if (cardNo.length() == 13 || cardNo.length() == 14 || cardNo.length() == 15) {
				temp3 = cardNo.substring(12);
				v = temp1 + "-" + temp2 + "-xxxx-" + temp3;
			} else {
				for (int i = 0; i < cardNo.length(); i++) {
					count += 1;
					String temp = cardNo.substring(i, i + 1);
					if (i >= 8 && i <= 12)
						temp = "x";
					v += temp;
					if (count == 4) {
						v += "-";
						count = 0;
					}
				}
			}
		} else {
			v = cardNo;
		}
		return v;
	}

	public static String formatCardNoKeyIn(String cardNo) {
		String v = "";
		if (cardNo.length() > 8 && cardNo != null) {
			String temp1 = cardNo.substring(0, 4);
			String temp2 = cardNo.substring(4, 8);
			String temp3 = "";
			String temp4 = "";
			v = temp1 + "-" + temp2;
			if (cardNo.length() >= 13) {
				temp3 = cardNo.substring(8, 12);
				temp4 = cardNo.substring(12);
				v += "-" + temp3 + "-" + temp4;
			} else if (cardNo.length() >= 9) {
				temp3 = cardNo.substring(8);
				v += "-" + temp3;
			}
		} else {
			v = cardNo;
		}
		return v;
	}

	


	static String formatValue(float value) {
		return new DecimalFormat("##").format(value);
	}

	public static int round(double inputVal) {
		int result = 0;
		try {
			double interVal = Math.floor(inputVal);
			result = (int) interVal;
			if (inputVal - interVal >= 0.5)
				result = (int) interVal + 1;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;

	}

	public static String appendspace(String res, int space) {
		int k = space - res.length();
		String value = "";
		for (int i = 0; i < k; i++) {
			value += " ";
		}
		return (res += value);
	}

	public static String lastString(String str, int space) {
		String result = "";
		int lenght = str.length();
		if (space <= lenght)
			result = str;
		result = str.substring(lenght - 2);
		return result;
	}

	public static String formatNumberExcel(String number) {
		try {
			if(number==null || number.isEmpty())
				return "0";
			double numberformat = Double.valueOf(number);
			return NumberFormat.getInstance(Locale.US).format(numberformat);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "0";
		}

	}

	public static byte[] fileToBytes(String path) throws IOException {
		File file = new File(path);
		java.io.FileInputStream fis = new java.io.FileInputStream(file);
		byte[] b = new byte[(int) file.length()];
		int read = fis.read(b);
		fis.close();
		return b;
	}

	public static String createMonochromeBitmapInBase64(Bitmap src) {
		String mImagePath = Environment.getExternalStorageDirectory() + "/androidpaint";
		File temp = new File(mImagePath);
		if (!temp.exists())
			temp.mkdirs();
		String filePath = mImagePath + "/tmp_bmp.bmp";
		int width = 128;// src.getWidth();
		int height = 64;// src.getHeight();
		// color information
		int A, R, G, B;
		int pixel;
		byte[] bmpData = new byte[width * height];
		Bitmap bmpMonochrome = Bitmap.createScaledBitmap(src, width, height, true);
		// scan through all pixels
		for (int i = 0; i < height; ++i) {
			for (int j = 0; j < width; ++j) {
				// get pixel color
				pixel = bmpMonochrome.getPixel(j, i);
				// pixel = SDcardHelper.BitmapResize(src, 128, 64).getPixel(j,
				// i);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);

				// use 128 as threshold, above -> white, below -> black;//230
				if (gray > 128)
					gray = 1;
				else
					gray = 0;

				bmpData[i * width + j] = (byte) gray;
			}
		}

		byte[] bmp1 = new byte[1024];
		for (int n = 0; n < 1024; n++) {
			final int k = (64 - (n / 16) - 1) * 16 + (n % 16);

			bmp1[k] = (byte) (((byte) bmpData[n * 8 + 7] & 0xff) == 0 ? 0x00 : 0x01);
			bmp1[k] = (byte) (((byte) bmpData[n * 8 + 6] & 0xff) == 0 ? bmp1[k] & 0xfd : bmp1[k] | 0x02);
			bmp1[k] = (byte) (((byte) bmpData[n * 8 + 5] & 0xff) == 0 ? bmp1[k] & 0xfb : bmp1[k] | 0x04);
			bmp1[k] = (byte) (((byte) bmpData[n * 8 + 4] & 0xff) == 0 ? bmp1[k] & 0xf7 : bmp1[k] | 0x08);
			bmp1[k] = (byte) (((byte) bmpData[n * 8 + 3] & 0xff) == 0 ? bmp1[k] & 0xef : bmp1[k] | 0x10);
			bmp1[k] = (byte) (((byte) bmpData[n * 8 + 2] & 0xff) == 0 ? bmp1[k] & 0xdf : bmp1[k] | 0x20);
			bmp1[k] = (byte) (((byte) bmpData[n * 8 + 1] & 0xff) == 0 ? bmp1[k] & 0xbf : bmp1[k] | 0x40);
			bmp1[k] = (byte) (((byte) bmpData[n * 8 + 0] & 0xff) == 0 ? bmp1[k] & 0x7f : bmp1[k] | 0x80);
		}

		byte[] outbuf = new byte[62];
		outbuf[0] = (byte) 0x42;
		outbuf[1] = (byte) 0x4D;
		outbuf[2] = (byte) 0x1E;
		outbuf[3] = (byte) 0x11;
		outbuf[4] = (byte) 0x00;
		outbuf[5] = (byte) 0x00;
		outbuf[6] = (byte) 0x00;
		outbuf[7] = (byte) 0x00;
		outbuf[8] = (byte) 0x00;
		outbuf[9] = (byte) 0x00;
		outbuf[10] = (byte) 0x3E;
		outbuf[11] = (byte) 0x00;
		outbuf[12] = (byte) 0x00;
		outbuf[13] = (byte) 0x00;
		outbuf[14] = (byte) 0x28;
		outbuf[15] = (byte) 0x00;
		outbuf[16] = (byte) 0x00;
		outbuf[17] = (byte) 0x00;
		outbuf[18] = (byte) 0x80;
		outbuf[19] = (byte) 0x00;
		outbuf[20] = (byte) 0x00;
		outbuf[21] = (byte) 0x00;
		outbuf[22] = (byte) 0x40;
		outbuf[23] = (byte) 0x00;
		outbuf[24] = (byte) 0x00;
		outbuf[25] = (byte) 0x00;
		outbuf[26] = (byte) 0x01;
		outbuf[27] = (byte) 0x00;
		outbuf[28] = (byte) 0x01;
		outbuf[29] = (byte) 0x00;
		outbuf[30] = (byte) 0x00;
		outbuf[31] = (byte) 0x00;
		outbuf[32] = (byte) 0x00;
		outbuf[33] = (byte) 0x00;
		outbuf[34] = (byte) 0xE0;
		outbuf[35] = (byte) 0x10;
		outbuf[36] = (byte) 0x00;
		outbuf[37] = (byte) 0x00;
		outbuf[38] = (byte) 0x00;
		outbuf[39] = (byte) 0x00;
		outbuf[40] = (byte) 0x00;
		outbuf[41] = (byte) 0x00;
		outbuf[42] = (byte) 0x00;
		outbuf[43] = (byte) 0x00;
		outbuf[44] = (byte) 0x00;
		outbuf[45] = (byte) 0x00;
		outbuf[46] = (byte) 0x00;
		outbuf[47] = (byte) 0x00;
		outbuf[48] = (byte) 0x00;
		outbuf[49] = (byte) 0x00;
		outbuf[50] = (byte) 0x00;
		outbuf[51] = (byte) 0x00;
		outbuf[52] = (byte) 0x00;
		outbuf[53] = (byte) 0x00;
		outbuf[54] = (byte) 0x00;
		outbuf[55] = (byte) 0x00;
		outbuf[56] = (byte) 0x00;
		outbuf[57] = (byte) 0x00;
		outbuf[58] = (byte) 0xFF;
		outbuf[59] = (byte) 0xFF;
		outbuf[60] = (byte) 0xFF;
		outbuf[61] = (byte) 0x00;

		FileOutputStream f = null;
		BufferedOutputStream buf = null;
		try {
			f = new FileOutputStream(filePath);
			buf = new BufferedOutputStream(f);
			buf.write(outbuf, 0, outbuf.length);
			buf.write(bmp1, 0, 1024);
			buf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// return bmOut;
		try {
			return Base64Utils.base64Encode(fileToBytes(filePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static boolean saveSMTSign(Context ctx, Bitmap bm, String dest_image_path) {
		boolean res = false;

		String path = ctx.getDir("", 0) + "/";
		BHelper.db("path => [" + path + "]");

		if (bm != null) {

			try {
				File f = new File(path);
				f.mkdir();
				File f2 = new File(dest_image_path);
				FileOutputStream fos = new FileOutputStream(f2);

				if (fos != null) {
					byte header[] = new byte[] { 0x42, 0x4d, 0x3e, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x3e, 0x00, 0x00, 0x00, 0x28, 0x00, 0x00, 0x00, (byte) 0x80, 0x00, 0x00, 0x00, 0x40, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x00 };
					byte payload[] = new byte[1024];
					int payload_idx = 0;

					int bitValIdx = 7;
					byte data = 0;
					int cnt = 0;
					Bitmap src = Bitmap.createScaledBitmap(bm, 128, 64, false);
					for (int y = 63; y >= 0; --y) {
						for (int x = 0; x < 128; ++x) {

							int p = src.getPixel(x, y);
							int r = Color.red(p);
							int g = Color.green(p);
							int b = Color.blue(p);

							byte bitVal;
							if (r < 0x77 || g < 0x77 || b < 0x77) {
								bitVal = 0;
								cnt++;
							} else {
								bitVal = 1;
							}
							data |= bitVal << bitValIdx--;
							if (bitValIdx == -1) {

								payload[payload_idx++] = data;

								bitValIdx = 7;
								data = 0;
							}
						}
					}
					fos.write(header);
					fos.write(payload);
					fos.flush();
					fos.close();
					if (cnt > 50) {
						res = true;
					}

				}
			} catch (Exception e) {
				BHelper.db("Exception: " + e.toString());
			}
		}

		return res;
	}

	public static boolean saveBitmap128_64_1_File(Bitmap img, String dest_image_path) {
		BHelper.db("start 126 x 64 1");
		String mImagePath = Environment.getExternalStorageDirectory() + "";// + "/androidpaint";
		String filePath = mImagePath + "/tmp_bmp.bmp";
		filePath = dest_image_path;

		Bitmap rsBmp = null;
		Bitmap bmp = (Bitmap.createScaledBitmap(img, 128, 64, true)).copy(Config.ARGB_8888, true);
		int[] data = new int[bmp.getHeight() * bmp.getWidth()];
		bmp.getPixels(data, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
		// BmpHeader fetched from the header 128 * 64 monochrome BMP images.

		// I need to learn more because I have not done this, but perhaps
		// resolution and color pixels? Value is defined to hold

		byte bmpHeader[] = { 66, 77, 62, 4, 0, 0, 0, 0, 0, 0, 62, 0, 0, 0, 40, 0, 0, 0, -128, 0, 0, 0, 64, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 4, 0, 0, -60, 14, 0, 0, -60, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, 0 };

		// FileOutputStream f = new FileOutputStream(filePath);
		FileOutputStream f = null;
		BufferedOutputStream buf = null;
		// try{
		// f= context.openFileOutput(filePath, Context.MODE_PRIVATE);
		try {
			f = new FileOutputStream(filePath);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BHelper.db("126 x 64 1 f:" + f.toString());
		buf = new BufferedOutputStream(f);
		try {
			buf.write(bmpHeader, 0, bmpHeader.length);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Of 1024 / / 128 * 64 8192 Pixel color information will have of 1Bit
		// monochrome bitmap 1Pixel 1Bit a kind, so

		// 8192/8 (1Byte = 8Bit) by dividing the value.

		byte[] bmp1 = new byte[1024];
		/*
		 * X, y-axis from 0.0 below the pixel value of 8192 put 1024 Byte plus a one-dimensional array of logic. For each iteration of the door once 1Byte (8 Pixel) on each bit and put it into operation puts a value in the order of Low Bit -> High Bit Insert. 64-1 y-axis upside down way down / / 128 (16 byte) * 64, so each 16 byte increments bmp array 0-1023. Ie. 0 stored in the array, rather than from the top of the image file is saved in the bottom left from goes up over line by line, is stored. (Up and down) reversely Saved
		 */
		BHelper.db("126 x 64 1 start loop");
		for (int n = 0; n < 1024; n++) {
			// int i = 64 - ( n / 16 ) - 1;
			// int j = n % 16;
			// LogCat.log("bitmapTest", "i="+i+",j="+j+",k="+k);
			final int k = (64 - (n / 16) - 1) * 16 + (n % 16);

			bmp1[k] = (byte) (((byte) data[n * 8 + 7] & 0xff) == 0 ? 0x00 : 0x01);
			bmp1[k] = (byte) (((byte) data[n * 8 + 6] & 0xff) == 0 ? bmp1[k] & 0xfd : bmp1[k] | 0x02);
			bmp1[k] = (byte) (((byte) data[n * 8 + 5] & 0xff) == 0 ? bmp1[k] & 0xfb : bmp1[k] | 0x04);
			bmp1[k] = (byte) (((byte) data[n * 8 + 4] & 0xff) == 0 ? bmp1[k] & 0xf7 : bmp1[k] | 0x08);
			bmp1[k] = (byte) (((byte) data[n * 8 + 3] & 0xff) == 0 ? bmp1[k] & 0xef : bmp1[k] | 0x10);
			bmp1[k] = (byte) (((byte) data[n * 8 + 2] & 0xff) == 0 ? bmp1[k] & 0xdf : bmp1[k] | 0x20);
			bmp1[k] = (byte) (((byte) data[n * 8 + 1] & 0xff) == 0 ? bmp1[k] & 0xbf : bmp1[k] | 0x40);
			bmp1[k] = (byte) (((byte) data[n * 8 + 0] & 0xff) == 0 ? bmp1[k] & 0x7f : bmp1[k] | 0x80);
		}
		BHelper.db("126 x 64 1 end loop");
		try {
			buf.write(bmp1, 0, 1024);
			buf.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public static boolean createMonochromeBitmap(Bitmap src, String dest_image_path) {
		String mImagePath = Environment.getExternalStorageDirectory() + "";// + "/androidpaint";
		// File temp = new File(mImagePath);
		// if (!temp.exists())
		// temp.mkdirs();
		String filePath = mImagePath + "/tmp_bmp.bmp";
		filePath = dest_image_path;
		int width = 128;// src.getWidth();
		int height = 64;// src.getHeight();
		// color information
		int A, R, G, B;
		int pixel;
		byte[] bmpData = new byte[width * height];
		Bitmap bmpMonochrome = Bitmap.createScaledBitmap(src, width, height, true);
		// scan through all pixels
		for (int i = 0; i < height; ++i) {
			for (int j = 0; j < width; ++j) {
				// get pixel color
				pixel = bmpMonochrome.getPixel(j, i);
				// pixel = SDcardHelper.BitmapResize(src, 128, 64).getPixel(j,
				// i);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);

				// use 128 as threshold, above -> white, below -> black
				if (gray > 230)
					gray = 1;
				else
					gray = 0;

				bmpData[i * width + j] = (byte) gray;
			}
		}

		byte[] bmp1 = new byte[1024];
		for (int n = 0; n < 1024; n++) {
			final int k = (64 - (n / 16) - 1) * 16 + (n % 16);

			bmp1[k] = (byte) (((byte) bmpData[n * 8 + 7] & 0xff) == 0 ? 0x00 : 0x01);
			bmp1[k] = (byte) (((byte) bmpData[n * 8 + 6] & 0xff) == 0 ? bmp1[k] & 0xfd : bmp1[k] | 0x02);
			bmp1[k] = (byte) (((byte) bmpData[n * 8 + 5] & 0xff) == 0 ? bmp1[k] & 0xfb : bmp1[k] | 0x04);
			bmp1[k] = (byte) (((byte) bmpData[n * 8 + 4] & 0xff) == 0 ? bmp1[k] & 0xf7 : bmp1[k] | 0x08);
			bmp1[k] = (byte) (((byte) bmpData[n * 8 + 3] & 0xff) == 0 ? bmp1[k] & 0xef : bmp1[k] | 0x10);
			bmp1[k] = (byte) (((byte) bmpData[n * 8 + 2] & 0xff) == 0 ? bmp1[k] & 0xdf : bmp1[k] | 0x20);
			bmp1[k] = (byte) (((byte) bmpData[n * 8 + 1] & 0xff) == 0 ? bmp1[k] & 0xbf : bmp1[k] | 0x40);
			bmp1[k] = (byte) (((byte) bmpData[n * 8 + 0] & 0xff) == 0 ? bmp1[k] & 0x7f : bmp1[k] | 0x80);
		}

		byte[] outbuf = new byte[62];
		outbuf[0] = (byte) 0x42;
		outbuf[1] = (byte) 0x4D;
		outbuf[2] = (byte) 0x1E;
		outbuf[3] = (byte) 0x11;
		outbuf[4] = (byte) 0x00;
		outbuf[5] = (byte) 0x00;
		outbuf[6] = (byte) 0x00;
		outbuf[7] = (byte) 0x00;
		outbuf[8] = (byte) 0x00;
		outbuf[9] = (byte) 0x00;
		outbuf[10] = (byte) 0x3E;
		outbuf[11] = (byte) 0x00;
		outbuf[12] = (byte) 0x00;
		outbuf[13] = (byte) 0x00;
		outbuf[14] = (byte) 0x28;
		outbuf[15] = (byte) 0x00;
		outbuf[16] = (byte) 0x00;
		outbuf[17] = (byte) 0x00;
		outbuf[18] = (byte) 0x80;
		outbuf[19] = (byte) 0x00;
		outbuf[20] = (byte) 0x00;
		outbuf[21] = (byte) 0x00;
		outbuf[22] = (byte) 0x40;
		outbuf[23] = (byte) 0x00;
		outbuf[24] = (byte) 0x00;
		outbuf[25] = (byte) 0x00;
		outbuf[26] = (byte) 0x01;
		outbuf[27] = (byte) 0x00;
		outbuf[28] = (byte) 0x01;
		outbuf[29] = (byte) 0x00;
		outbuf[30] = (byte) 0x00;
		outbuf[31] = (byte) 0x00;
		outbuf[32] = (byte) 0x00;
		outbuf[33] = (byte) 0x00;
		outbuf[34] = (byte) 0xE0;
		outbuf[35] = (byte) 0x10;
		outbuf[36] = (byte) 0x00;
		outbuf[37] = (byte) 0x00;
		outbuf[38] = (byte) 0x00;
		outbuf[39] = (byte) 0x00;
		outbuf[40] = (byte) 0x00;
		outbuf[41] = (byte) 0x00;
		outbuf[42] = (byte) 0x00;
		outbuf[43] = (byte) 0x00;
		outbuf[44] = (byte) 0x00;
		outbuf[45] = (byte) 0x00;
		outbuf[46] = (byte) 0x00;
		outbuf[47] = (byte) 0x00;
		outbuf[48] = (byte) 0x00;
		outbuf[49] = (byte) 0x00;
		outbuf[50] = (byte) 0x00;
		outbuf[51] = (byte) 0x00;
		outbuf[52] = (byte) 0x00;
		outbuf[53] = (byte) 0x00;
		outbuf[54] = (byte) 0x00;
		outbuf[55] = (byte) 0x00;
		outbuf[56] = (byte) 0x00;
		outbuf[57] = (byte) 0x00;
		outbuf[58] = (byte) 0xFF;
		outbuf[59] = (byte) 0xFF;
		outbuf[60] = (byte) 0xFF;
		outbuf[61] = (byte) 0x00;

		FileOutputStream f = null;
		BufferedOutputStream buf = null;
		try {
			f = new FileOutputStream(filePath);
			buf = new BufferedOutputStream(f);
			buf.write(outbuf, 0, outbuf.length);
			buf.write(bmp1, 0, 1024);
			buf.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static Bitmap rotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}

	public static void saveBitmap(Bitmap bitmap, String fileName) {
		File file = new File(getExSD());
		if (!file.exists())
			file.mkdirs();
		try {
			FileOutputStream stream = new FileOutputStream(getExSD() + fileName);
			bitmap.compress(CompressFormat.PNG, 100, stream);
			stream.flush();
			stream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getExSD() {
		String sdAppPath = Environment.getExternalStorageDirectory() + "/Payfun";
		File temp = new File(sdAppPath);
		if (!temp.exists())
			temp.mkdirs();
		return Environment.getExternalStorageDirectory() + "/Payfun/";
	}

	public static boolean fileCoppy(File from, File to) {
		try {
			FileInputStream in = new FileInputStream(from);
			FileOutputStream out = new FileOutputStream(to);
			FileChannel fromChannel = null, toChannel = null;
			try {
				fromChannel = in.getChannel();
				toChannel = out.getChannel();
				fromChannel.transferTo(0, fromChannel.size(), toChannel);
			} finally {
				if (fromChannel != null)
					fromChannel.close();
				if (toChannel != null)
					toChannel.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	public static String getPoint(String sPoint, String sTotal) {
		float total = Float.valueOf(sTotal);
		float p = Float.valueOf(sPoint);
		float point = (Float) (total * p) / 100;
		return formatValue(point);
	}

	public static String getTAmount(String sPoint, String sTotal) {
		float total = Float.valueOf(sTotal);
		float p = Float.valueOf(sPoint);
		float point = total - p;
		return formatValue(point);
	}

	
	
	public static int getQuater(String sMonth) {
		int month = Integer.valueOf(sMonth);
		if (month <= 3)
			return 0;
		if (month <= 6)
			return 1;
		if (month <= 9)
			return 2;
		if (month <= 12)
			return 3;
		return 0;
	}

	

	public static void writeLogFile(String logData) {
		String fileName = "error";
		writeLogFile(fileName, logData);
	}
	public static String readIntegrityLog(){
		File sdcard = Environment.getExternalStorageDirectory();

		//Get the text file
		File file = new File(sdcard,"PayFunLog/intergity.log");

		//Read text from file
		StringBuilder text = new StringBuilder();

		try {
		    BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;

		    while ((line = br.readLine()) != null) {
		        text.append(line);
		        text.append("\n");
		    }
		    br.close();
		}
		catch (IOException e) {
		    //You'll need to add proper error handling here
		}
		return text.toString();
		
	}
	public static void writeIntegrityLog(String logData) {
		String fileName = "intergity";
		writeLogFile(fileName, logData);
	}
	public static void writeLogFile(String fileName, String logData) {
		
		try {
			String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
			logData = timeStamp + "\n"+ logData + "\n*********\n";
			String filepath = Environment.getExternalStorageDirectory().getPath();
			File file = new File(filepath, "PayFunLog");
			if (!file.exists()) {
				file.mkdirs();
			}
			String fullFileName = file.getAbsolutePath() + "/"+fileName+".log";
			File myFile = new File(fullFileName);
			if(!myFile.exists()){
				myFile.createNewFile();
				BHelper.db("file :"+ fullFileName +" not existed");
			}
			FileWriter fileWritter = new FileWriter(fullFileName,true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write(logData);
	        bufferWritter.close();
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String GetMyPhone(Context ct) {
		TelephonyManager telephonyManager = (TelephonyManager) ct.getSystemService(Context.TELEPHONY_SERVICE);
		String simSerial = telephonyManager.getSimSerialNumber();
		if (simSerial == null || simSerial == "")
			simSerial = telephonyManager.getDeviceId();
		return simSerial;
	}

	public static Bitmap createTransparentBitmapFromBitmap(Bitmap bitmap, int replaceThisColor) {
		if (bitmap != null) {
			int picw = bitmap.getWidth();
			int pich = bitmap.getHeight();
			int[] pix = new int[picw * pich];
			bitmap.getPixels(pix, 0, picw, 0, 0, picw, pich);

			for (int y = 0; y < pich; y++) {
				// from left to right
				for (int x = 0; x < picw; x++) {
					int index = y * picw + x;
					int r = (pix[index] >> 16) & 0xff;
					int g = (pix[index] >> 8) & 0xff;
					int b = pix[index] & 0xff;

					if (pix[index] == replaceThisColor) {
						pix[index] = Color.TRANSPARENT;
					}
//					else {
//						pix[index] = Color.GRAY;
//					}
				}

//				// from right to left
				for (int x = picw - 1; x >= 0; x--) {
					int index = y * picw + x;
					int r = (pix[index] >> 16) & 0xff;
					int g = (pix[index] >> 8) & 0xff;
					int b = pix[index] & 0xff;

					if (pix[index] == replaceThisColor) {
						pix[index] = Color.TRANSPARENT;
					}
//					else {
//						pix[index] = Color.GRAY;
//					}
				}
			}

			Bitmap bm = Bitmap.createBitmap(pix, picw, pich, Bitmap.Config.ARGB_8888);

			return bm;
		}
		return null;
	}
	public static char[] appendSpace(String input,int length){
		String value = input;
		for (int i = input.length(); i < length; i++) {
			value = " "+value;
		}
		return value.toCharArray();
	}
	
	public static String byte2hex(byte[] bs) {
		StringBuilder str = new StringBuilder();
		int i = 1;
		//str.append("0x");
		for (byte b : bs) {
			str.append(String.format("%02X", b));
			if(i!=bs.length){
				str.append(i++ % 2 == 0 ? "" : "");
			}
		}
		return str.toString();
	}
	public static String byte2hex2(byte[] input) {
		String result = "";
		for (int i = 0; i < input.length; i++)
			result += String.format("0x%02x,", input[i]);
		return result;
	}
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}
	public static boolean isHeadsetConnected(Context tx){
		boolean isConnected =false;
		try{
			IntentFilter iFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
			Intent iStatus = tx.registerReceiver(null, iFilter);
			
			isConnected = iStatus.getIntExtra("state", 0) == 1;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return isConnected;
	}

}
