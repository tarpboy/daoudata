package com.devcrane.android.lib.emvreader;

import com.devcrane.android.lib.utility.BHelper;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class EmvReaderService extends Service {
	static private EmvReader emvReader;
	private IBinder   emvBinder = new EmvReaderServiceBinder();
	
	@Override
	public IBinder onBind(Intent intent) {
		return emvBinder;
	}
	public class EmvReaderServiceBinder extends Binder{
		EmvReaderService getService(){
			return EmvReaderService.this;
		}
	}
	public EmvReader getEmvReader(){
		return emvReader;
	}
	public void releaseEmvReader(){
		try{
			if(emvReader!=null)
				emvReader.releaseController();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	@Override
    public void onCreate() {
		BHelper.db("create EmvReaderService with static");
    	try {
    		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	if(emvReader==null)
    		emvReader = new EmvReader(this);
    	else{
    		BHelper.db("emvReader is existed, dont need init");
    	}
	}
}
