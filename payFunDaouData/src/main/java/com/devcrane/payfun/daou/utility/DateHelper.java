package com.devcrane.payfun.daou.utility;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.text.format.DateFormat;

public class DateHelper {
//	public static Locale sCountry = new Locale(Locale.KOREA.getDisplayLanguage(), Locale.KOREA.getCountry());
	public static Locale sCountry = new Locale("en-US");

	public static String getYMD(Date date) {
		String sResult = "";
		try {
			sResult = new SimpleDateFormat("yyyy-MM-dd", sCountry).format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}

	public static String getYMD(Calendar date) {
		String sResult = "";
		try {
			sResult = DateFormat.format("yyyy-MM-dd", date).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}

	public static String getHM(Date date) {
		String sResult = "";
		try {
			sResult = new SimpleDateFormat("HH:mm", sCountry).format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}

	public static String gethhmm(Date date) {
		String sResult = "";
		try {
			sResult = new SimpleDateFormat("a hh:mm", sCountry).format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}

	public static String getCurrentYMD() {
		String sResult = "";
		try {
			Calendar calendar = Calendar.getInstance();
			sResult = getYMD(calendar.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;

	}

	public static String getCurrentDateFull() {
		String sResult = "";
		try {
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", sCountry);
			sResult = df.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}

	public static String getCurrenthhmma() {
		String sResult = "";
		try {
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("a hh:mm", sCountry);
			sResult = df.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}
	public static String getCurrenthhmmss() {
		String sResult = "";
		try {
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("hhmmss", sCountry);
			sResult = df.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}
	public static String getdateFull() {
		String result = "";
		try {
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd a hh:mm:ss", sCountry);
			result = df.format(c.getTime());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;

	}

	/*--------------------Date & Time------------------------*/

	public static String add(int value) {
		String date1 = "";
		if (value < 10)
			date1 = "0" + String.valueOf(value);
		else
			date1 = String.valueOf(value);
		return date1;
	}

	public static ArrayList<String> getDateList() {
		Calendar cal = Calendar.getInstance();
		Date currentDate = new Date();
		Date firstDate = new Date();
		ArrayList<String> dlist = new ArrayList<String>();
		firstDate = getFirstDay_Return_Date(currentDate);

		while (firstDate.compareTo(currentDate) < 0) {
			currentDate = cal.getTime();
			dlist.add(getDate(currentDate));
			cal.add(Calendar.DATE, -1);
		}
		return dlist;
	}

	public static ArrayList<String> getAllDayInMonth() {
		Calendar cal = Calendar.getInstance();
		Date currentDate = new Date();
		Date firstDate = new Date();
		Date lastDate = new Date();
		ArrayList<String> dlist = new ArrayList<String>();
		firstDate = getFirstDay_Return_Date(currentDate);
		lastDate = getLastDay_Return_Date(currentDate);
		while (firstDate.compareTo(lastDate) < 0) {
			// firstDate = cal.getTime();
			dlist.add(getDate(firstDate));
			cal.add(Calendar.DATE, 1);
			// firstDate.
		}
		return dlist;
	}

	private static Date getFirstDay_Return_Date(Date d) {
		Date dResult = new Date();
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			dResult = calendar.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (dResult);
	}

	private static Date getLastDay_Return_Date(Date d) {
		Date dResult = new Date();
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			dResult = calendar.getTime();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return (dResult);
	}

	public static int getWeekOfDate(Date d) {
		int sResult = 0;
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			sResult = calendar.get(Calendar.WEEK_OF_YEAR);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sResult;
	}

	public static String getFirstDay_Return_String(Date d) {
		String sResult = "";
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date fistDate = calendar.getTime();
		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy", sCountry);
			sResult = sdf1.format(fistDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}

	public static String getLastDay(Date d) {
		String sResult = "";
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date lastDate = calendar.getTime();
		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy", sCountry);
			sResult = sdf1.format(lastDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sResult;
	}

	public static String formatDateToStringYYMM(Date mytime) {
		String sResult = "";
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", sCountry);
			sResult = dateFormat.format(mytime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;

	}

	public static String getDate(Date d) {
		String sResult = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", sCountry);
			sResult = sdf.format(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}
	public static Date parse(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(date);

		} catch (ParseException pe) {
			myDate = new Date();
		}

		return myDate;
	}
	public static Date formatReq_Date(String mytime) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", sCountry);
		Date myDate = null;
		try {
			myDate = dateFormat.parse(mytime);

		} catch (ParseException pe) {
			myDate = new Date();
		}

		return myDate;
	}

	public static String formatStringtoDate(String mytime) {
		String sResult = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd a hh:mm", sCountry);
		Date myDate = null;
		try {
			myDate = dateFormat.parse(mytime);

		} catch (ParseException pe) {
			myDate = new Date();
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", sCountry);
			sResult = sdf.format(myDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}

	public static String getNextDay(String sDate, boolean isNextDate) {
		String sResult = "";
		int next = 1;
		if (!isNextDate)
			next = -1;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", sCountry);
		Date myDate = null;
		try {
			myDate = dateFormat.parse(sDate);

		} catch (ParseException pe) {
			myDate = new Date();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(myDate);
		cal.add(Calendar.DAY_OF_YEAR, next);
		Date tomorrow = cal.getTime();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", sCountry);
			sResult = sdf.format(tomorrow);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}

	public static String formatReq_date(String mytime) {
		String sResult = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", sCountry);
		Date myDate = null;
		try {
			myDate = dateFormat.parse(mytime);

		} catch (ParseException pe) {
			myDate = new Date();
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", sCountry);
			sResult = sdf.format(myDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}

	public static String formatReq_Date(String mytime, boolean isWorking) {
		String sResult = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", sCountry);
		Date myDate = null;
		try {
			myDate = dateFormat.parse(mytime);

		} catch (ParseException pe) {
			myDate = new Date();
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", sCountry);
			if (isWorking)
				sdf = new SimpleDateFormat("a hh:mm:ss", sCountry);
			sResult = sdf.format(myDate);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sResult;
	}

	public static Date formatStringToDatehhmm(String mytime) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("a hh:mm", sCountry);
		Date myDate = null;
		try {
			myDate = dateFormat.parse(mytime);

		} catch (ParseException pe) {
			myDate = new Date();
		}

		return myDate;
	}

	public static String getUnique10Num() {

		String result = "";
		/*
		 * SimpleDateFormat indfm = new SimpleDateFormat("yyyyMMddHHmmss"); result = indfm.format(new Date()); SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss"); Date myDate =df.format(date) new Date(); Log.i("nguu","long date:"+myDate.getTime());
		 */
		long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
		result = String.valueOf(number);
		BHelper.db("long date:" + result);
		return result;

		// return result.substring(3, 13);
	}

	public static String getUnique12Num() {
		String result = "";
		try {
			SimpleDateFormat indfm = new SimpleDateFormat("yyMMddHHmmss", sCountry);
			result = indfm.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static String getCurrentDateStringNice() {
		String result = "";
		try {
			SimpleDateFormat indfm = new SimpleDateFormat("MMddHHmmss", DateHelper.sCountry);
			result = indfm.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		BHelper.db("long date:" + result);
		return result;
	}

//	public static int getCurrentMonth() {
//		Calendar cal = Calendar.getInstance();
//		return cal.get(Calendar.MONTH + 1);
//	}

	public static String currentDate() {
		String sResult = "";
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", DateHelper.sCountry);
			sResult = df.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;

	}
	public static String getYYYYMMDD(){
		String sResult = "";
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", DateHelper.sCountry);
			sResult = df.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}
	public static String getYear() {
		Calendar cal = Calendar.getInstance();
		return String.valueOf(cal.get(Calendar.YEAR));
	}
	public static String getMonth() {
		String sResult = "";
		try {
			SimpleDateFormat df = new SimpleDateFormat("MM", DateHelper.sCountry);
			sResult = df.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}

	public static String formatRevDateKSNET(String sDate) {
		Date date = null;
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
		try {
			date = formatter.parse(sDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return format.format(date);
	}

	public static String formatRevDateDaouData(String sDate) {
		Date date = null;
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			date = formatter.parse(sDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return format.format(date);
	}
	public static String formatCancelDateDaouData(String sDate) {
		Date date = null;
		Format format = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = formatter.parse(sDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return format.format(date);
	}

	public static String currentDateTimeFully() {
		String sResult = "";
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss", DateHelper.sCountry);
			sResult = df.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}
	@SuppressWarnings("deprecation")
	public static String getDateBeforeMonthNo(int monthNo){
		String sResult = "";
		Date dateBefore6Month = new Date();
		dateBefore6Month.setMonth(dateBefore6Month.getMonth()- monthNo);
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			sResult = format.format(dateBefore6Month);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sResult;
	}
	public static String currentyyMMdd() {
		String sResult = "";
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyMMdd", DateHelper.sCountry);
			sResult = df.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}
	
	public static String formatyyMMdd(String sDate) {
		Date date = null;
		SimpleDateFormat formatterIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Format formatterOut = new SimpleDateFormat("yyMMdd");
		try {
			date = formatterIn.parse(sDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formatterOut.format(date);
	}
	public static String formatyyMMddHHmmss(String sDate) {
		Date date = null;
		SimpleDateFormat formatterIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Format formatterOut = new SimpleDateFormat("yyMMddHHmmss");
		try {
			date = formatterIn.parse(sDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formatterOut.format(date);
	}
	public static String formatDatetoString(String date) {
		String v = "";
		BHelper.db("date " + date);
		String date1 = date.split(" ")[0];
		String[] date2 = date1.split("-");
		return date2[0] + date2[1] + date2[2];
	}

	public static String formatRevDate(String revDate) {
		if(revDate==null)
			return "";
		
		if (revDate.trim().length() < 10)
			return revDate;
		return ("20"+revDate.substring(0, 2) + "-" + revDate.substring(2, 4) + "-" + revDate.substring(4, 6) + " " + revDate.substring(6, 8) + ":" + revDate.substring(8, 10) + ":" + revDate.substring(10));

	}

	public static String formatRevDate2(String date) {
		if(date==null)
			return "";
		if (date.trim().length() < 13)
			return date;
		return (date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " " + date.substring(8, 10) + ":" + date.substring(10, 12));

	}
	public static double getCurrJulianDate(){
		Calendar cal = Calendar.getInstance();
		return dateToJulian(cal);
	}
	 public static double dateToJulian(Calendar date) {
		    int year = date.get(Calendar.YEAR);
		    int month = date.get(Calendar.MONTH)+1;
		    int day = date.get(Calendar.DAY_OF_MONTH);
		    int hour = date.get(Calendar.HOUR_OF_DAY);
		    int minute = date.get(Calendar.MINUTE);
		    int second = date.get(Calendar.SECOND);

		    double extra = (100.0 * year) + month - 190002.5;
		    return (367.0 * year) -
		      (Math.floor(7.0 * (year + Math.floor((month + 9.0) / 12.0)) / 4.0)) + 
		      Math.floor((275.0 * month) / 9.0) +  
		      day + ((hour + ((minute + (second / 60.0)) / 60.0)) / 24.0) +
		      1721013.5 - ((0.5 * extra) / Math.abs(extra)) + 0.5;
		  }
}
