package com.devcrane.payfun.daou.utility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;

public class AndroidBmpUtil_1Bit {

	public static Bitmap createBlankWhiteBitmap(int width, int height) {
		Bitmap mBlankBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(mBlankBitmap);
		canvas.drawColor(Color.WHITE);

		return mBlankBitmap;
	}
	public static String makeImageInBase64(byte[] data){
		if(data==null || data.length==0)
			return "";
		String result="";
		result+="ESMS";
		result+="00";
		result+="96";

		String signData = Base64Utils.base64Encode(data);
		result+= String.format("%04d", signData.length());
		result+=signData;
		BHelper.db("signature real:"+ result);
//		result="ESMS00960240AhD5ACeAwMAAXQQO7T784AAUwOD4PJ7//x8AYgE//+AFgALAAuDq8HB4PBweDg8HBwEAAeD/PwAvgIDAwAng/MAAKQEHAwIB3wALwP5/AwAkgMDg8Hg4PBweDg8HBwMDAQEABwEDDx48eA9w1SAAKv//AB2AwODweDweDg8HAwEAVf//ABSAwODweDweDg8HAwEAXj9/8MAGgALAAuDy8Hg4PB4ODwcHAwEAaQEIAwIB/gBQ";
		BHelper.db("signature sample		:"+ result);
		return result;

	}
	public static Bitmap mergeBitmap(Bitmap baseBmp, Bitmap srcBmp) {
		int width = baseBmp.getWidth();
		int height = baseBmp.getHeight();
		Config bmpConfig = baseBmp.getConfig();

		Bitmap bmOverlay = Bitmap.createBitmap(width, height, bmpConfig);

		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(baseBmp, new Matrix(), null);
		canvas.drawBitmap(srcBmp, 0, 0, null);

		return bmOverlay;
	}

	public boolean bmpSave(Bitmap mBitmap, String mPath) {

		boolean bSucc = false;

		if ((mBitmap == null) || (mPath == null)) {
			return bSucc;
		}

		ByteBuffer mBuffer = getBitmapBuffer(mBitmap);

		if (mBuffer == null) {
			bSucc = false;
		} else {
			try {
				FileOutputStream fos;
				fos = new FileOutputStream(mPath);
				fos.write(mBuffer.array());
				fos.close();

				bSucc = true;
			} catch (IOException e) {
				bSucc = false;
			}

		}

		return bSucc;
	}

	public String bmpData_Header(Bitmap mBitmap) {
		String bmpDat = "";

		if (mBitmap == null) {
			return bmpDat;
		}

		ByteBuffer mBuffer = getBitmapBuffer(mBitmap);

		if (mBuffer == null) {
			bmpDat = "";
		} else {
			bmpDat = convHexStyle(mBuffer.array(), mBuffer.array().length - 62);
		}

		return bmpDat;
	}

	public String bmpData_NoHeader(Bitmap mBitmap) {
		String bmpDat = "";

		if (mBitmap == null) {
			return bmpDat;
		}

		ByteBuffer mBuffer = getBitmapBuffer_NoHeader(mBitmap);

		if (mBuffer == null) {
			bmpDat = "";
		} else {
			bmpDat = convHexStyle(mBuffer.array(), mBuffer.array().length - 62);
		}

		return bmpDat;
	}

	private ByteBuffer getBitmapBuffer(Bitmap mBitmap) {

		ByteBuffer mBuffer = null;

		try {
			mBitmap = Bitmap.createScaledBitmap(mBitmap, 128, 64, true);

			int mWidth = mBitmap.getWidth();
			int mHeight = mBitmap.getHeight();
			int[] mPixel = new int[mWidth * mHeight];
			int imgSize = mPixel.length / 8;
			int imgOffset = 0x3E;
			int fileSize = imgSize + imgOffset;

			mBitmap.getPixels(mPixel, 0, mWidth, 0, 0, mWidth, mHeight);
			mBuffer = ByteBuffer.allocate(fileSize);

			mBuffer.put((byte) 0x42);
			mBuffer.put((byte) 0x4D);
			mBuffer.put(writeInt(fileSize));
			mBuffer.put(putShort((short) 0));
			mBuffer.put(putShort((short) 0));
			mBuffer.put(writeInt(imgOffset));
			mBuffer.put(writeInt(0x28));
			mBuffer.put(writeInt(mWidth));
			mBuffer.put(writeInt(mHeight));
			mBuffer.put(putShort((short) 1));
			mBuffer.put(putShort((short) 1));
			mBuffer.put(writeInt(0));
			mBuffer.put(writeInt(imgSize));
			mBuffer.put(writeInt(0));
			mBuffer.put(writeInt(0));
			mBuffer.put(writeInt(0));
			mBuffer.put(writeInt(0));
			mBuffer.put((byte) 0x00);
			mBuffer.put((byte) 0x00);
			mBuffer.put((byte) 0x00);
			mBuffer.put((byte) 0x00);
			mBuffer.put((byte) 0xff);
			mBuffer.put((byte) 0xff);
			mBuffer.put((byte) 0xff);
			mBuffer.put((byte) 0xff);

			int height = mHeight;
			int width = mWidth;
			int startPosition = 0;
			int endPosition = 0;

			while (height > 0) {
				startPosition = (height - 1) * width;
				endPosition = height * width;
				int[] iTemp = new int[8];
				for (int i = startPosition; i < endPosition;) {
					if ((i + 7) <= endPosition) {
						for (int j = 7; j >= 0; j--) {
							iTemp[j] = mPixel[i++];
						}
					} else {
						int mLength = endPosition - i;

						for (int j = 7; j >= (8 - mLength); j++) {
							iTemp[j] = mPixel[i++];
						}

						for (int j = (7 - mLength); j >= 0; j++) {
							iTemp[j] = 0x00FFFFFF;
						}
					}

					mBuffer.put(putIntToBit(iTemp));
				}
				height--;
			}
		} catch (IOException e) {
			mBuffer = null;
		}

		return mBuffer;
	}

	public byte[] getSignature(Bitmap mBitmap){
		ByteBuffer mBuffer = getBitmapBuffer_NoHeader(mBitmap);
		int length = mBuffer.array().length - 64;
		if(length<=0)
			return new byte[0];
		byte[] result = new byte[length];
		for(int i=0;i<length;i++){
			result[i] = mBuffer.get(i);
		}
		return result;
	}
	private ByteBuffer getBitmapBuffer_NoHeader(Bitmap mBitmap) {

		ByteBuffer mBuffer = null;

		try {
			mBitmap = Bitmap.createScaledBitmap(mBitmap, 128, 64, true);

			int mWidth = mBitmap.getWidth();
			int mHeight = mBitmap.getHeight();
			int[] mPixel = new int[mWidth * mHeight];
			int imgSize = mPixel.length / 8;
			int imgOffset = 0x3E;
			int fileSize = imgSize + imgOffset;

			mBitmap.getPixels(mPixel, 0, mWidth, 0, 0, mWidth, mHeight);
			mBuffer = ByteBuffer.allocate(fileSize);

			int height = mHeight;
			int width = mWidth;
			int startPosition = 0;
			int endPosition = 0;

			while (height > 0) {
				startPosition = (height - 1) * width;
				endPosition = height * width;
				int[] iTemp = new int[8];
				for (int i = startPosition; i < endPosition;) {
					if ((i + 7) <= endPosition) {
						for (int j = 7; j >= 0; j--) {
							iTemp[j] = mPixel[i++];
						}
					} else {
						int mLength = endPosition - i;

						for (int j = 7; j >= (8 - mLength); j++) {
							iTemp[j] = mPixel[i++];
						}

						for (int j = (7 - mLength); j >= 0; j++) {
							iTemp[j] = 0x00FFFFFF;
						}
					}

					mBuffer.put(putIntToBit(iTemp));
				}
				height--;
			}
		} catch (IOException e) {
			mBuffer = null;
		}

		return mBuffer;
	}

	private byte[] writeInt(int value) throws IOException {
		byte[] b = new byte[4];

		b[0] = (byte) (value & 0x000000FF);
		b[1] = (byte) ((value & 0x0000FF00) >> 8);
		b[2] = (byte) ((value & 0x00FF0000) >> 16);
		b[3] = (byte) ((value & 0xFF000000) >> 24);

		return b;
	}

	private byte[] putShort(short value) throws IOException {
		byte[] b = new byte[2];

		b[0] = (byte) (value & 0x00FF);
		b[1] = (byte) ((value & 0xFF00) >> 8);

		return b;
	}

	private byte putIntToBit(int[] value) throws IOException {
		byte bReturn = (byte) 0xFF;
		byte[] b = new byte[3];

		for (int i = 0; i < value.length; i++) {
			b[0] = (byte) (value[i] & 0x000000FF);

			if (b[0] != (byte) 0xFF) {
				bReturn -= Math.pow(2, i);
			}

		}
		return (byte) bReturn;
	}

	private String convHexStyle(byte[] arrByte, int len) {
		String sRet = "";

		for (int i = 0; i < len; i++) {
			sRet += String.format("%02X", arrByte[i]);
		}

		return sRet;
	}
}
