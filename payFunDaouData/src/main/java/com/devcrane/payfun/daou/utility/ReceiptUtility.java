package com.devcrane.payfun.daou.utility;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.ReceiptEntity;

public class ReceiptUtility {
	
	private static byte[] INIT = {0x1B,0x40};
	private static byte[] POWER_ON = {0x1B,0x3D,0x01};
	private static byte[] POWER_OFF = {0x1B,0x3D,0x02};
	private static byte[] NEW_LINE = {0x0A};
	private static byte[] ALIGN_LEFT = {0x1B,0x61,0x00};
	private static byte[] ALIGN_CENTER = {0x1B,0x61,0x01};
	private static byte[] ALIGN_RIGHT = {0x1B,0x61,0x02};
	private static byte[] EMPHASIZE_ON = {0x1B,0x45,0x01};
	private static byte[] EMPHASIZE_OFF = {0x1B,0x45,0x00};
	private static byte[] FONT_5X8 = {0x1B,0x4D,0x00};
	private static byte[] FONT_5X12 = {0x1B,0x4D,0x01};
	private static byte[] FONT_8X12 = {0x1B,0x4D,0x02};
	private static byte[] FONT_10X18 = {0x1B,0x4D,0x03};
	private static byte[] FONT_SIZE_0 = {0x1D,0x21,0x00};
	private static byte[] FONT_SIZE_1 = {0x1D,0x21,0x11};
	private static byte[] CHAR_SPACING_0 = {0x1B,0x20,0x00};
	private static byte[] CHAR_SPACING_1 = {0x1B,0x20,0x01};
	
	private static byte[] hexToByteArray(String s) {
		if(s == null) {
			s = "";
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		for(int i = 0; i < s.length() - 1; i += 2) {
			String data = s.substring(i, i + 2);
			bout.write(Integer.parseInt(data, 16));
		}
		return bout.toByteArray();
	}
	
	private static byte[] convertBitmap(Bitmap bitmap, int targetWidth, int threshold) {
		int targetHeight = (int)Math.round((double)targetWidth / (double)bitmap.getWidth() * (double)bitmap.getHeight());
		
		byte[] pixels = new byte[targetWidth * targetHeight];
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
		for(int j = 0; j < scaledBitmap.getHeight(); ++j) {
			for(int i = 0; i < scaledBitmap.getWidth(); ++i) {
				int pixel = scaledBitmap.getPixel(i, j);
				int alpha = (pixel >> 24) & 0xFF;
				int r = (pixel >> 16) & 0xFF;
				int g = (pixel >> 8) & 0xFF;
				int b = pixel & 0xFF;
				if(alpha < 50) {
					pixels[i + j * scaledBitmap.getWidth()] = 0;
				} else if((r + g + b) / 3 >= threshold) {
					pixels[i + j * scaledBitmap.getWidth()] = 0;
				} else {
					pixels[i + j * scaledBitmap.getWidth()] = 1;
				}
			}
		}
		
		byte[] output = new byte[scaledBitmap.getWidth() * (int)Math.ceil((double)scaledBitmap.getHeight() / (double)8)];
		
		for(int i = 0; i < scaledBitmap.getWidth(); ++i) {
			for(int j = 0; j < (int)Math.ceil((double)scaledBitmap.getHeight() / (double)8); ++j) {
				for(int n = 0; n < 8; ++n) {
					if(j * 8 + n < scaledBitmap.getHeight()) {
						output[i + j * scaledBitmap.getWidth()] |= pixels[i + (j * 8 + n) * scaledBitmap.getWidth()] << (7 - n);
					}
				}
			}
		}
		
		return output;
	}
	
	private static byte[] convertBarcode(Bitmap bitmap, int targetWidth, int threshold) {
		int targetHeight = (int)Math.round((double)targetWidth / (double)bitmap.getWidth() * (double)bitmap.getHeight());
		
		byte[] pixels = new byte[targetWidth];
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
		for(int i = 0; i < scaledBitmap.getWidth(); ++i) {
			int pixel = scaledBitmap.getPixel(i, scaledBitmap.getHeight() / 2);
			int alpha = (pixel >> 24) & 0xFF;
			int r = (pixel >> 16) & 0xFF;
			int g = (pixel >> 8) & 0xFF;
			int b = pixel & 0xFF;
			if(alpha < 50) {
				pixels[i] = 0;
			} else if((r + g + b) / 3 >= threshold) {
				pixels[i] = 0;
			} else {
				pixels[i] = 1;
			}
		}
		
		byte[] output = new byte[(int)Math.ceil((double)scaledBitmap.getWidth() / 8.0)];
		
		for(int i = 0; i < scaledBitmap.getWidth(); ++i) {
			output[i / 8] |= pixels[i] << (7 - (i % 8));
		}
		
		return output;
	}

	public static byte[] genReceipt(Context context,ReceiptEntity receiptE,String path,String rightV1,CompanyEntity comE) {
		int lineWidth = 384;
		int size0NoEmphasizeLineWidth = 384 / 8; //line width / font width
		String singleLine = "";
		for(int i = 0; i < size0NoEmphasizeLineWidth; ++i) {
			singleLine += "-";
		}
		String doubleLine = "";
		for(int i = 0; i < size0NoEmphasizeLineWidth; ++i) {
			doubleLine += "=";
		}
		String scr = "";
		
		boolean isCancel = receiptE.getF_revStatus().equals("0");
		if (isCancel)
			scr = "-";
		String tax = Helper.formatNumberExcel(receiptE.getF_Tax());
		if (tax.equals("0"))
			tax = "0";
		else
			tax = scr + tax;
		String point = Helper.formatNumberExcel(receiptE.getF_CouponDiscountAmount());
		if (point.equals("0"))
			point = "0";
		else
			point = scr + point;
		
		
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(INIT);
			baos.write(POWER_ON);
			baos.write(NEW_LINE);
			baos.write(ALIGN_LEFT);
			
			
			baos.write(NEW_LINE);
			baos.write(receiptE.getF_TypeSub().getBytes());
			baos.write(FONT_SIZE_0);
			baos.write(EMPHASIZE_ON);
			baos.write(FONT_5X12);
			baos.write(rightV1.getBytes());
			baos.write(NEW_LINE);
			baos.write(EMPHASIZE_OFF);
			
			baos.write(FONT_SIZE_0);
			baos.write(FONT_5X12);
			baos.write("거래일시".getBytes());
			baos.write(EMPHASIZE_ON);
			baos.write(receiptE.getF_RequestDate().getBytes());
			baos.write(NEW_LINE);
			baos.write(EMPHASIZE_OFF);
			
			baos.write(FONT_SIZE_0);
			baos.write(FONT_5X12);
			baos.write("카드번호".getBytes());
			baos.write(EMPHASIZE_ON);
			baos.write(Helper.formatCardNo(receiptE.getF_CardNo()).getBytes());
			baos.write(NEW_LINE);
			baos.write(EMPHASIZE_OFF);
			
			baos.write(FONT_SIZE_0);
			baos.write(FONT_5X12);
			baos.write("가맹점번호".getBytes());
			baos.write(EMPHASIZE_ON);
			baos.write(receiptE.getF_revCoCode().getBytes());
			baos.write(NEW_LINE);
			baos.write(EMPHASIZE_OFF);
			
			baos.write(FONT_SIZE_0);
			baos.write(FONT_5X12);
			baos.write("승인번호".getBytes());
			baos.write(EMPHASIZE_ON);
			baos.write(receiptE.getF_ApprovalCode().getBytes());
			baos.write(NEW_LINE);
			baos.write(EMPHASIZE_OFF);
			
			
			baos.write(FONT_SIZE_0);
			baos.write(FONT_5X12);
			baos.write("매입사".getBytes());
			baos.write(EMPHASIZE_ON);
			baos.write(receiptE.getF_BuyerName().getBytes());
			baos.write(NEW_LINE);
			baos.write(EMPHASIZE_OFF);
			
			baos.write(FONT_SIZE_0);
			baos.write(FONT_5X12);
			baos.write("판매금액".getBytes());
			baos.write(EMPHASIZE_ON);
			baos.write((scr + Helper.formatNumberExcel(receiptE.getF_Amount())).getBytes());
			baos.write(NEW_LINE);
			baos.write(EMPHASIZE_OFF);
			
			baos.write(FONT_SIZE_0);
			baos.write(FONT_5X12);
			baos.write("부가가치세".getBytes());
			baos.write(EMPHASIZE_ON);
			baos.write(tax.getBytes());
			baos.write(NEW_LINE);
			baos.write(EMPHASIZE_OFF);
			
			baos.write(FONT_SIZE_0);
			baos.write(FONT_5X12);
			baos.write("포인트".getBytes());
			baos.write(EMPHASIZE_ON);
			baos.write(point.getBytes());
			baos.write(NEW_LINE);
			baos.write(EMPHASIZE_OFF);
			
			baos.write(FONT_SIZE_0);
			baos.write(FONT_5X12);
			baos.write("합 계".getBytes());
			baos.write(EMPHASIZE_ON);
			baos.write((scr + Helper.formatNumberExcel(receiptE.getF_TotalAmount())).getBytes());
			baos.write(NEW_LINE);
			baos.write(EMPHASIZE_OFF);
			
			
			
			baos.write(FONT_SIZE_0);
			baos.write(FONT_5X12);
			baos.write("가맹점명".getBytes());
			baos.write(EMPHASIZE_ON);
			baos.write(comE.getF_CompanyName().getBytes());
			baos.write(NEW_LINE);
			baos.write(EMPHASIZE_OFF);
			
			
			
			baos.write(FONT_SIZE_0);
			baos.write(FONT_5X12);
			baos.write("사업자번호".getBytes());
			baos.write(EMPHASIZE_ON);
			baos.write(Helper.formatCompanyNo(comE.getF_CompanyName()).getBytes());
			baos.write(NEW_LINE);
			baos.write(EMPHASIZE_OFF);
			
			baos.write(FONT_SIZE_0);
			baos.write(FONT_5X12);
			baos.write("대표자명".getBytes());
			baos.write(EMPHASIZE_ON);
			baos.write(comE.getF_CompanyOwnerName().getBytes());
			baos.write(NEW_LINE);
			baos.write(EMPHASIZE_OFF);
			
			baos.write(FONT_SIZE_0);
			baos.write(FONT_5X12);
			baos.write("TEL".getBytes());
			baos.write(EMPHASIZE_ON);
			baos.write(comE.getF_CompanyPhoneNo().getBytes());
			baos.write(NEW_LINE);
			baos.write(EMPHASIZE_OFF);
			
			baos.write(NEW_LINE);
			baos.write(comE.getF_CompanyAddress().getBytes());
			baos.write(NEW_LINE);
			baos.write(NEW_LINE);

			baos.write(hexToByteArray("1D6B21"));
			if(path!=null){
				Bitmap barcodeBitmap = BitmapFactory.decodeFile(path);
				int barcodeTargetHeight = 64;
				int barcodeTargetWidth = 384;
				byte[] d2 = convertBitmap(barcodeBitmap, barcodeTargetWidth, 160);
				baos.write(barcodeTargetHeight);
				baos.write((byte)barcodeTargetWidth);
				baos.write((byte)(barcodeTargetWidth >> 8));
				baos.write(d2);
				baos.write(NEW_LINE);
			}
			
			baos.write(NEW_LINE);
			baos.write(NEW_LINE);
			baos.write(NEW_LINE);
			baos.write(NEW_LINE);
			baos.write(NEW_LINE);
			baos.write(NEW_LINE);
			baos.write(NEW_LINE);

			baos.write(POWER_OFF);
			
			return baos.toByteArray();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
