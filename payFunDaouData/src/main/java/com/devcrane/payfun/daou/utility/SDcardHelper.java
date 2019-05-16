package com.devcrane.payfun.daou.utility;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapPrimitive;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;

public class SDcardHelper {

	public static boolean hasStorage1() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	public static boolean hasStorage2() {
		return android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.MEDIA_MOUNTED);
	}

	public static boolean hasStorage3(boolean requireWriteAccess) {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		} else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	public static String get_FileName(String path) {
		if (path == "" || path == null) {
			return "No Image";
		}
		String rs = "";
		int c = path.lastIndexOf("/") + 1;
		rs = path.substring(c);
		return rs;
	}

	public static byte[] get_Byte_Image(String filepath) {
		if (filepath == null || filepath == "")
			return null;
		byte[] b = null;
		try {
			File imagefile = new File(filepath);
			b = new byte[(int) imagefile.length()];
			FileInputStream fis = null;
			fis = new FileInputStream(imagefile);
			fis.read(b, 0, (int) imagefile.length());

			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return (b);
	}

	// -------------------------Image Bitmap----------------------------------
	public static byte[] BitmaptoByte(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
		byte[] data = baos.toByteArray();
		return data;
	}

	public static String BytetoString(byte[] bytes) {
		String strbytes = Base64.encode(bytes);
		SoapPrimitive oPrimitive = new SoapPrimitive(SoapEnvelope.ENC, "base64", strbytes);
		return oPrimitive.toString();
	}

	public static String BitmaptoString(Bitmap bitmap) {
		byte[] bytes = BitmaptoByte(bitmap);
		return BytetoString(bytes);
	}

	public static String BitmaptoBase64(Bitmap bitmap) {
		byte[] bytes = BitmaptoByte(bitmap);

		// return Base64.encode(bytes);
		return Base64Utils.base64Encode(bytes);

	}
	public static void bitmap2File(String fileName, Bitmap bmp){
		byte[] data  = BitmaptoByte(bmp);
		String filePath = Helper.getExSD()+ fileName;
		File file = new File(filePath);
		FileOutputStream out;

		// bm.compress(Bitmap.CompressFormat.PNG, 100, out);
		try {
			out = new FileOutputStream(file);
			out.write(data);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void DecodeBitmapSample() throws FileNotFoundException {
		String a = "Qk0+BAAAAAAAAD4AAAAoAAAAgAAAAEAAAAABAAEAAAAAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP///wD///////////////////////////////////////////////////////////////////////////////8f//////////////////gAAAP////////////////AAEAAf///////////////g///+B///////////////h////8H//////////////g/////wf/////////////w//////D/////////////w//////4f////////////8f//////j////////////8P//////4/////////////H///////H////////////z///////x////////////4///////+f///////////+f///////n////////////H///////4////////////z////////P///////////8////////z////////////P///////8////////////z////////P///////////8////////z///////////+P///////8////////////j////////P///////////4////////j////////////P///////4////////////z///////+f///////////8////////n////////////H///////x////////////5///////8////////////+f//////+P////////////j///+A//H////////////8f///AD/x/////////////H///h4P4/////////////4///x/h+f/////////////H//8/+GH/////////////4f/+P/wD//////////////D//n/+B//////////////4P/5//g///////////////gf+f/wf//////////////+A/n/wP///////////////4A5/AH////////////////4AAAD/////////////////4ABx//////////////////4/4//////////////////+P8f//////////////////z+P//////////////////8fD///////////////////Hx///////////////////54////////////////////8f////////////////////P////////////////////j////////////////////4////////////////////+f////////////////////H////////////////////x////////////////////8////////////////////+P////////////////////n////////////////////x////////////////////8f////////////////////P////";

		byte[] bytes = Base64Utils.base64Decode(a);
		String mImagePath = Environment.getExternalStorageDirectory() + "/androidpaint";

		File file = new File(mImagePath + "/sample_bmp.bmp");
		FileOutputStream out;

		// bm.compress(Bitmap.CompressFormat.PNG, 100, out);
		try {
			out = new FileOutputStream(file);
			out.write(bytes);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String EncodeBitmapSample() throws FileNotFoundException {
		String result = "";

		String mImagePath = Environment.getExternalStorageDirectory() + "/androidpaint";
		Bitmap bmp = SDcardHelper.BitmapFromFilePath(mImagePath + "/sample_bmp.bmp");
		result = SDcardHelper.BitmaptoBase64(bmp);

		return result;

	}

	public static Bitmap saveBitmap128_64_1_File(Bitmap img) throws IOException {
		Log.i("nguu", "start 126 x 64 1");
		String mImagePath = Environment.getExternalStorageDirectory() + "/androidpaint";
		File temp = new File(mImagePath);
		if (!temp.exists())
			temp.mkdirs();
		String filePath = mImagePath + "/tmp_128_64.bmp";

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
		f = new FileOutputStream(filePath);
		Log.i("nguu", "126 x 64 1 f:" + f.toString());
		buf = new BufferedOutputStream(f);
		buf.write(bmpHeader, 0, bmpHeader.length);

		// Of 1024 / / 128 * 64 8192 Pixel color information will have of 1Bit
		// monochrome bitmap 1Pixel 1Bit a kind, so

		// 8192/8 (1Byte = 8Bit) by dividing the value.

		byte[] bmp1 = new byte[1024];
		/*
		 * X, y-axis from 0.0 below the pixel value of 8192 put 1024 Byte plus a one-dimensional array of logic. For each iteration of the door once 1Byte (8 Pixel) on each bit and put it into operation puts a value in the order of Low Bit -> High Bit Insert. 64-1 y-axis upside down way down / / 128 (16 byte) * 64, so each 16 byte increments bmp array 0-1023. Ie. 0 stored in the array, rather than from the top of the image file is saved in the bottom left from goes up over line by line, is stored. (Up and down) reversely Saved
		 */
		Log.i("nguu", "126 x 64 1 start loop");
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
		Log.i("nguu", "126 x 64 1 end loop");
		buf.write(bmp1, 0, 1024);
		buf.close();
		// rsBmp =SDcardHelper.BitmapFromFilePath(filePath);
		// convert byte[] to bitmap
		rsBmp = BitmapFactory.decodeByteArray(bmp1, 0, bmp1.length);

		// }catch(Exception e){
		// buf.flush();
		// buf.close();
		// return false;
		// }
		return rsBmp;
	}

	public static Bitmap createBlackAndWhite(Bitmap src) {
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int A, R, G, B;
		int pixel;

		// scan through all pixels
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);

				// use 128 as threshold, above -> white, below -> black
				if (gray > 128)
					gray = 255;
				else
					gray = 0;
				// set new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
			}
		}
		return bmOut;
	}

	public static Bitmap createMonochromeBitmap(Bitmap src) {
		String mImagePath = Environment.getExternalStorageDirectory() + "/androidpaint";
		File temp = new File(mImagePath);
		if (!temp.exists())
			temp.mkdirs();
		String filePath = mImagePath + "/tmp_bmp.bmp";
		int width = 128;// src.getWidth();
		int height = 64;// src.getHeight();

		Bitmap bmpMonochrome = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmpMonochrome);
		ColorMatrix ma = new ColorMatrix();
		ma.setSaturation(0);
		Paint paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(ma));
		canvas.drawBitmap(src, 0, 0, paint);

		// create output bitmap
		Bitmap bmOut = null;
		// color information
		int A, R, G, B;
		int pixel;
		byte[] bmpData = new byte[width * height];
		byte[] bmpData2 = new byte[width * height];
		bmpMonochrome = Bitmap.createScaledBitmap(src, width, height, true);
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
			// int i = 64 - ( n / 16 ) - 1;
			// int j = n % 16;
			// LogCat.log("bitmapTest", "i="+i+",j="+j+",k="+k);
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
		// 128*64 1비트 bmp 헤더
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

		// outbuf[22] = (byte)0xB4;
		// outbuf[18] = (byte)0xAE;
		byte bmpHeader[] = { 66, 77, 62, 4, 0, 0, 0, 0, 0, 0, 62, 0, 0, 0, 40, 0, 0, 0, -128, 0, 0, 0, 64, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 4, 0, 0, -60, 14, 0, 0, -60, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, 0 };

		FileOutputStream f = null;
		BufferedOutputStream buf = null;
		try {
			f = new FileOutputStream(filePath);
			buf = new BufferedOutputStream(f);
			buf.write(outbuf, 0, outbuf.length);
			// buf.write(bmpHeader, 0, bmpHeader.length);
			// buf.write(bmpData2, 0, bmpcnt);
			buf.write(bmp1, 0, 1024);
			buf.close();
			bmOut = SDcardHelper.BitmapFromFilePath(filePath);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bmOut;

	}
	public static void createImage128_64_2(byte[] rawData, String fileName){
		String mImagePath = Environment.getExternalStorageDirectory() + "/JTNet";
		File temp = new File(mImagePath);
		if (!temp.exists())
			temp.mkdirs();
		String filePath = mImagePath + "/"+fileName;
		
		
		byte[] outbuf = new byte[62];
		// 128*64 1비트 bmp 헤더
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
			buf.write(rawData, 0, 1024);
			buf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void createImage128_64(Bitmap src, String fileName){
		String mImagePath = Environment.getExternalStorageDirectory() + "/JTNet";
		File temp = new File(mImagePath);
		if (!temp.exists())
			temp.mkdirs();
		String filePath = mImagePath + "/"+fileName;
		
		Bitmap bitmap = Bitmap.createScaledBitmap(src, 128, 64, true);
		int pixelCnt = 0;
		int[] data = new int[bitmap.getWidth()*bitmap.getHeight()];
		
		//1Â÷¿ø ¹è¿­¿¡ ÇÈ¼¿°ª ¾ò±â
		bitmap.getPixels(data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

		byte[] tmpChar = new byte[1];
		byte[] RawData = new byte[1024];
		
		for(int y=0; y<64; y++){
			for(int x=0; x<128; x++){
				if(data[y*128 + x] != -1){
					pixelCnt++;
					tmpChar[0] = 0x01;
					tmpChar[0] = (byte) (tmpChar[0] << (y % 8));
					RawData[((y/8) * 128) + x] |= tmpChar[0];
				}						
			}
		}
		byte[] outbuf = new byte[62];
		// 128*64 1비트 bmp 헤더
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
			buf.write(RawData, 0, 1024);
			buf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		// 128*64 1비트 bmp 헤더
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

			return Base64Utils.base64Encode(SDcardHelper.fileToBytes(filePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public static byte[] getRawDataImage128_64(Bitmap src) {
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
		return bmp1;
	}
	public static byte[] getRawDataImage128_64_2(Bitmap src) {
		Bitmap bitmap = Bitmap.createScaledBitmap(src, 128, 64, true);
		int pixelCnt = 0;
		int[] data = new int[bitmap.getWidth()*bitmap.getHeight()];
		
		//1Â÷¿ø ¹è¿­¿¡ ÇÈ¼¿°ª ¾ò±â
		bitmap.getPixels(data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

		byte[] tmpChar = new byte[1];
		byte[] rawData = new byte[1024];
		
		for(int y=0; y<64; y++){
			for(int x=0; x<128; x++){
				if(data[y*128 + x] != -1){
					pixelCnt++;
					tmpChar[0] = 0x01;
					tmpChar[0] = (byte) (tmpChar[0] << (y % 8));
					rawData[((y/8) * 128) + x] |= tmpChar[0];
				}						
			}
		}
		return rawData;
	}
	public static Bitmap StringtoBitmap(String strBitmap) {
		byte[] bytes = Base64.decode(strBitmap);
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return bitmap;
	}

	public static Bitmap BytetoBitmap(byte[] bytes) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return bitmap;
	}

	public static Bitmap BitmapFromFilePath(String filePath) {
		byte[] bytes = SDcardHelper.get_Byte_Image(filePath);
		return SDcardHelper.BytetoBitmap(bytes);
	}

	public static Bitmap getBitmapfromURL(String url) {
		try {
			HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
			conn.connect();
			return BitmapFactory.decodeStream(conn.getInputStream());
		} catch (Exception ex) {
			return null;
		}
	}

	public static byte[] fileToBytes(String path) throws IOException {

		File file = new File(path);
		java.io.FileInputStream fis = new java.io.FileInputStream(file);
		byte[] b = new byte[(int) file.length()];
		fis.read(b);
		fis.close();
		return b;
	}

	public static Bitmap BitmapResize(Bitmap bitmap, int newWidth, int newHeight) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// calculate the scale - in this case = 0.4f
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// createa matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return resizedBitmap;
	}

	public static Bitmap WidthBitmapResize(Bitmap bitmap, int newWidth) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// calculate the scale - in this case = 0.4f
		float scaleWidth = ((float) newWidth) / width;

		// createa matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleWidth);
		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return resizedBitmap;
	}

	public static BitmapDrawable BitmaptoDrawable(Bitmap bitmap) {
		BitmapDrawable bmd = new BitmapDrawable(bitmap);
		return bmd;
	}

	public static boolean SaveBitmap(Context context, Bitmap bitmap, String filename) {
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					return false;
				}
			}
		}

	}

	public static String StoreByteImage(Context mContext, byte[] imageData, int quality, String expName) {
		String ImageUrl = "";
		File sdImageMainDirectory = new File("/mnt/sdcard/myImages");
		FileOutputStream fileOutputStream = null;
		String nameFile = null;
		if (!sdImageMainDirectory.exists())
			sdImageMainDirectory.mkdirs();
		try {

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 5;

			Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
			Log.i("dung", "Image1");
			ImageUrl = sdImageMainDirectory.toString() + "/" + expName + ".jpg";
			fileOutputStream = new FileOutputStream(ImageUrl);
			Log.i("dung", "Image2");

			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

			myImage.compress(CompressFormat.JPEG, quality, bos);
			Log.i("dung", "Image3");
			bos.flush();
			bos.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("dung", "ERR " + String.valueOf(e));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("dung", "ERR1 " + e);
		}

		return ImageUrl;
	}

	public static Bitmap rotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}

	// ------------------------------- Create Folder----------------------
	public static boolean CreateFolder(String folderName) {
		File folder = new File(Environment.getExternalStorageDirectory() + "/" + folderName);
		boolean success = true;
		if (!folder.exists()) {
			success = folder.mkdirs();
		}
		if (!success) {
			// Do something on success
		} else {
			// Do something else on failure
		}
		return success;
	}
	public static boolean deleteFile(String filePath){
		try{
			File f = new File(filePath);
			if (f.exists())
				return f.delete();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
		
	}

}
