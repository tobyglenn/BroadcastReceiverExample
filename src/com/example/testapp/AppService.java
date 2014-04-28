package com.example.testapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class AppService extends Service {
	private final IBinder mBinder = new LocalBinder();
	public static final String NOTIFICATION = "com.example.serviceCalled";
	
	public class LocalBinder extends Binder {
		AppService getService() {
            // Return this instance of LocalService so clients can call public methods
            return AppService.this;
        }
    }
	
	@Override
	 public void onCreate() {
	        super.onCreate();
	        Log.d(AppService.class.getName(), "Created");
	        Intent intent1 = new Intent(NOTIFICATION);
			  // You can also include some extra data.
			  intent1.putExtra("saying", "This is my message for Local Broadcast only");
			  LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
	 } 
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(AppService.class.getName(), "Bound");
		return mBinder;
	}

}
