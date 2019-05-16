package com.devcrane.payfun.daou.utility;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;

public class VersionUtils {
	public static  String getVersionName(Context context, Class cls) 
	{
	  try {
	    ComponentName comp = new ComponentName(context, cls);
	    PackageInfo pinfo = context.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
	    return pinfo.versionName;
	  } catch (android.content.pm.PackageManager.NameNotFoundException e) {
	    return null;
	  }
	}
	public static  String getVersionCode(Context context, Class cls) 
	{
	  try {
	    ComponentName comp = new ComponentName(context, cls);
	    PackageInfo pinfo = context.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
	    return String.valueOf(pinfo.versionCode);
	  } catch (android.content.pm.PackageManager.NameNotFoundException e) {
	    return null;
	  }
	}
}
