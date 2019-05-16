package com.devcrane.payfun.daou.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import android.os.Environment;

import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.SDcardHelper;

public class WriteExcel {
	private WritableCellFormat timesBoldUnderline;
	private WritableCellFormat times;
	private String inputFile;

	public void setOutputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public void write() throws IOException, WriteException {
		File file = new File(inputFile);
		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("en", "EN"));

		WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
		workbook.createSheet("Report", 0);
		WritableSheet excelSheet = workbook.getSheet(0);
		createLabel(excelSheet);
		createContent(excelSheet);

		workbook.write();
		workbook.close();
	}

	private void createLabel(WritableSheet sheet) throws WriteException {
		// Lets create a times font
		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
		// Define the cell format
		times = new WritableCellFormat(times10pt);
		// Lets automatically wrap the cells
		times.setWrap(true);

		// Create create a bold font with unterlines
		WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false, UnderlineStyle.SINGLE);
		timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
		// Lets automatically wrap the cells
		timesBoldUnderline.setWrap(true);

		CellView cv = new CellView();
		cv.setFormat(times);
		cv.setFormat(timesBoldUnderline);
		cv.setAutosize(true);

		// Write a few headers
		addCaption(sheet, 0, 0, "Header 1");
		addCaption(sheet, 1, 0, "This is another header");

	}

	public void createContent(WritableSheet sheet) throws WriteException, RowsExceededException {
		// Write a few number
		for (int i = 1; i < 10; i++) {
			// First column
			addNumber(sheet, 0, i, i + 10);
			// Second column
			addNumber(sheet, 1, i, i * i);
		}
		// Lets calculate the sum of it
		StringBuffer buf = new StringBuffer();
		buf.append("SUM(A2:A10)");
		Formula f = new Formula(0, 10, buf.toString());
		sheet.addCell(f);
		buf = new StringBuffer();
		buf.append("SUM(B2:B10)");
		f = new Formula(1, 10, buf.toString());
		sheet.addCell(f);

		// Now a bit of text
		for (int i = 12; i < 20; i++) {
			// First column
			addLabel(sheet, 0, i, "Boring text " + i);
			// Second column
			addLabel(sheet, 1, i, "Another text");
		}
	}

	public void addCaption(WritableSheet sheet, int column, int row, String s) throws RowsExceededException, WriteException {
		Label label;
		label = new Label(column, row, s, timesBoldUnderline);
		sheet.addCell(label);
	}

	public void addNumber(WritableSheet sheet, int column, int row, Integer integer) throws WriteException, RowsExceededException {
		Number number;
		number = new Number(column, row, integer, times);
		sheet.addCell(number);
	}

	public void addLabel(WritableSheet sheet, int column, int row, String s) throws WriteException, RowsExceededException {
		Label label;
		label = new Label(column, row, s, times);
		sheet.addCell(label);
	}

	public static String writeDailyChart(List<ReceiptEntity> list, String title) throws WriteException, IOException {
		int sumAmount = 0;
		String link = "";
		SDcardHelper.CreateFolder("PayFun");
		BHelper.db("Create folder report " + SDcardHelper.CreateFolder("PayFun/report"));
		if (SDcardHelper.CreateFolder("PayFun/report")) {
			File file = new File(Environment.getExternalStorageDirectory() + "/PayFun/report", title + ".xls");
			link = "sdcard/PayFun/report" + "/" + title + ".xls";
			WritableWorkbook wb = Workbook.createWorkbook(file);
			WritableSheet ws = wb.createSheet("" + title + "", 0);
			Label datetime = new Label(0, 0, "날짜시간");
			ws.addCell(datetime);
			Label amount = new Label(1, 0, "금액");
			ws.addCell(amount);
			Label tax = new Label(2, 0, "부가세");
			ws.addCell(tax);
			Label service = new Label(3, 0, "서비스");
			ws.addCell(service);
			Label tAmount = new Label(4, 0, "총금액");
			ws.addCell(tAmount);
			Label tMonth = new Label(5, 0, "할부개월");
			ws.addCell(tMonth);
			Label sAmount = new Label(6, 0, "누적금액");
			ws.addCell(sAmount);
			Label uPoint = new Label(7, 0, "사용포인트");
			ws.addCell(uPoint);

			Label sPoint = new Label(8, 0, "적립포인트");
			ws.addCell(sPoint);
			Label sStatus = new Label(9, 0, "지위");
			ws.addCell(sStatus);
			for (int i = 0; i < list.size(); i++) {
				Label datetime1 = new Label(0, i + 1, String.format("%s", list.get(i).getF_RequestDate()).toString());
				ws.addCell(datetime1);
				Label amount1 = new Label(1, i + 1, String.format("%,d", Integer.valueOf(list.get(i).getF_Amount())));
				ws.addCell(amount1);
				Label tax1 = new Label(2, i + 1, String.format("%,d", Integer.valueOf(list.get(i).getF_Tax())));
				ws.addCell(tax1);
				Label service1 = new Label(3, i + 1, String.format("%,d", Integer.valueOf(list.get(i).getF_Service())));
				ws.addCell(service1);
				Label tAmount1 = new Label(4, i + 1, String.format("%,d", Integer.valueOf(list.get(i).getF_TotalAmount())));
				ws.addCell(tAmount1);
				Label tMonth1 = new Label(5, i + 1, list.get(i).getF_Month());
				ws.addCell(tMonth1);
				Label sAmount1;
				Label sStatus1;
				if (list.get(i).getF_revStatus().equals("1")) {
					sumAmount += Integer.valueOf(list.get(i).getF_TotalAmount());
					sAmount1 = new Label(6, i + 1, String.format("%,d", sumAmount));
					sStatus1 = new Label(9, i + 1, "Normal");
				} else {
					sAmount1 = new Label(6, i + 1, String.format("%,d", 0));
					sStatus1 = new Label(9, i + 1, "Canceled");
				}
				ws.addCell(sAmount1);
				String sCouponDiscountAmoun = list.get(i).getF_CouponDiscountAmount();
				if (sCouponDiscountAmoun == null)
					sCouponDiscountAmoun = "0";
				String sCouponDiscountRate = list.get(i).getF_CouponDiscountRate();
				if (sCouponDiscountRate == null)
					sCouponDiscountRate = "0";
				Label uPoint1 = new Label(7, i + 1, String.format("%,d",Integer.valueOf(sCouponDiscountAmoun)));
				ws.addCell(uPoint1);
				Label sPoint1 = new Label(8, i + 1, String.format("%,d", Integer.valueOf(sCouponDiscountRate)));
				ws.addCell(sPoint1);
				ws.addCell(sStatus1);
			}
			wb.write();
			wb.close();
		}
		return link;
	}

	public static void main(String[] args) throws WriteException, IOException {
		// WriteExcel test = new WriteExcel();
		WritableWorkbook wb = Workbook.createWorkbook(new File("/data/payfun/devcrane.payfun.jtnet/databases/output.xls"));
		WritableSheet ws = wb.createSheet("First", 0);
		Label label = new Label(0, 2, "A label record");
		ws.addCell(label);

		Number number = new Number(3, 4, 3.1459);
		ws.addCell(number);
		wb.write();
		wb.close();
		System.out.println("Please check the result file under c:/temp/lars.xls ");
	}
}
