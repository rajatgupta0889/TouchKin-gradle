package com.touchKin.touchkinapp.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;

public class MessageAndCallReadingService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;

	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Toast.makeText(getApplicationContext(), "Message and read CreATED",
				Toast.LENGTH_LONG).show();

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		fetchMessageCount();
		FetchCallCount();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public int fetchMessageCount() {

		Cursor cursor = getContentResolver().query(
				Uri.parse("content://sms/conversations/"), null, null, null,
				null);

		// if (cursor.moveToFirst()) { // must check the result to prevent
		// // exception
		// do {
		// String msgData = "";
		// for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
		// msgData += " " + cursor.getColumnName(idx) + ":"
		// + cursor.getString(idx);
		// }
		//
		// // use msgData
		// } while (cursor.moveToNext());
		// } else {
		// // empty box, no SMS
		// }
		// Log.d("Count Message ", cursor.getColumnCount() + "");
		Log.d("Count Message ", cursor.getCount() + "");
		return cursor.getCount();
	}

	public int FetchCallCount() {

		Cursor managedCursor = getContentResolver().query(
				CallLog.Calls.CONTENT_URI, null, null, null, null);

		// if (cursor.moveToFirst()) { // must check the result to prevent
		// // exception
		// do {
		// String msgData = "";
		// for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
		// msgData += " " + cursor.getColumnName(idx) + ":"
		// + cursor.getString(idx);
		// }
		//
		// // use msgData
		// } while (cursor.moveToNext());
		// } else {
		// // empty box, no SMS
		// }
		// Log.d("Count Message ", cursor.getColumnCount() + "");
		Log.d("Count Calll ", managedCursor.getCount() + "");

		return managedCursor.getCount();
	}

}
