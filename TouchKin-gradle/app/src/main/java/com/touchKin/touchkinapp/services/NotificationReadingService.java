package com.touchKin.touchkinapp.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class NotificationReadingService extends NotificationListenerService {
	Context context;
	public static boolean isNotificationAccessEnabled = false;

	@Override
	public void onCreate() {

		super.onCreate();
		context = getApplicationContext();

	}

	@SuppressLint("NewApi")
	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {

		String pack = sbn.getPackageName();
		// String ticker = sbn.getNotification().tickerText.toString();
		Bundle extras = sbn.getNotification().extras;
		String title = extras.getString("android.title");
		String text = extras.getCharSequence("android.text").toString();

		Log.i("Package", pack);
		// Log.i("Ticker", ticker);
		Log.i("Title", title);
		Log.i("Text", text);
		
		Intent msgrcv = new Intent("Msg");
		msgrcv.putExtra("package", pack);
		// msgrcv.putExtra("ticker", ticker);
		msgrcv.putExtra("title", title);
		msgrcv.putExtra("text", text);
		
		LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);

	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		Log.i("Msg", "Notification Removed");

	}

	@Override
	public IBinder onBind(Intent mIntent) {
		IBinder mIBinder = super.onBind(mIntent);
		isNotificationAccessEnabled = true;
		return mIBinder;
	}

	@Override
	public boolean onUnbind(Intent mIntent) {
		boolean mOnUnbind = super.onUnbind(mIntent);
		isNotificationAccessEnabled = false;
		return mOnUnbind;
	}
}
