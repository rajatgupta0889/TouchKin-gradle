package com.touchKin.touchkinapp.services;

import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.touchKin.touchkinapp.DashBoardActivity;
import com.touchKin.touchkinapp.model.AppController;

public class DeviceAcivityService extends Service {

	TelephonyManager Tel;
	MyPhoneStateListener MyListener;
	public int level;
	public int battery;
	public int wifi;
	int outGoingCount = 0, incomingCount = 0, missedCount = 0;
	int msessageCount;
	JSONObject mySelf;
	String phone, mobile_device_id;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// Toast.makeText(getApplicationContext(), "Message and read CreATED",
		// Toast.LENGTH_LONG).show();
		Log.d("Device Activity serivce",
				"Created Time " + new Date().toGMTString());

		wifi = fetchwifi();
		battery = fetchBattery();
		// level = fetchSignal();
		SharedPreferences userPref = getApplicationContext()
				.getSharedPreferences("userPref", 0);
		Tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String user = userPref.getString("user", null);
		if (user != null) {
			try {
				mySelf = new JSONObject(user);

				phone = mySelf.getString("mobile");
				mobile_device_id = mySelf.getString("mobile_device_id");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		MyListener = new MyPhoneStateListener();
		Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		Log.d("Device Activity serivce",
				"Start Time " + new Date().toGMTString());

		msessageCount = fetchMessageCount();
		FetchCallCount();

		// Toast.makeText(
		// getApplicationContext(),
		// fetchsignal() + " " + battery + " " + fetchwifi() + " "
		// + fetchMessageCount() + " " + FetchCallCount(),
		// Toast.LENGTH_SHORT).show();
		if (mySelf != null) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					sendActivityData();
				}
			}, 2000);
		}

	}

	private int fetchBattery() {
		// TODO Auto-generated method stub
		registerReceiver(this.mBatInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
		return battery;
	}

	//
	// private int fetchSignal() {
	//
	//
	// return level;
	// }

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctxt, Intent intent) {
			battery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			// Toast.makeText(getApplicationContext(), battery + "",
			// Toast.LENGTH_SHORT);
		}
	};

	private class MyPhoneStateListener extends PhoneStateListener {
		/*
		 * Get the Signal strength from the provider, each tiome there is an
		 * update
		 */

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);
			level = signalStrength.getGsmSignalStrength();

		}
	}

	private int fetchwifi() {
		// TODO Auto-generated method stub
		WifiManager wifi = (WifiManager) getSystemService(getApplicationContext().WIFI_SERVICE);
		int numberOfLevels = 5;
		WifiInfo wifiInfo = wifi.getConnectionInfo();
		int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(),
				numberOfLevels);
		// float val = ((float) level / numberOfLevels) * 100f;
		// Toast.makeText(getApplicationContext(), val + "",
		// Toast.LENGTH_SHORT);
		return level;

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

		int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

		while (managedCursor.moveToNext()) {
			// String phNumber = managedCursor.getString(number);
			String callType = managedCursor.getString(type);
			String callDate = managedCursor.getString(date);
			Date callDayTime = new Date(Long.valueOf(callDate));
			// String callDuration = managedCursor.getString(duration);
			String dir = null;
			Date todayDate = new Date();
			int dircode = Integer.parseInt(callType);
			if (callDayTime.getDate() == todayDate.getDate()
					&& callDayTime.getMonth() == todayDate.getMonth()) {
				switch (dircode) {
				case CallLog.Calls.OUTGOING_TYPE:
					outGoingCount++;
					break;

				case CallLog.Calls.INCOMING_TYPE:
					incomingCount++;
					break;

				case CallLog.Calls.MISSED_TYPE:
					missedCount++;
					break;
				}
			}
		}
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
		Log.d("Count Calll ", outGoingCount + " " + incomingCount + " "
				+ missedCount + " " + managedCursor.getCount());

		return managedCursor.getCount();
	}

	private void sendActivityData() {
		// TODO Auto-generated method stub

		JSONObject params = new JSONObject();
		try {
			params.put("mobile", phone);
			params.put("mobile_device_id", mobile_device_id);
			params.put("mobile_os", "android");
			JSONObject data = new JSONObject();
			data.put("battery", battery);
			data.put("wifi_strength", wifi);
			data.put("3g", level);
			data.put("incoming_call_count", incomingCount);
			data.put("outgoing_call_count", outGoingCount);
			data.put("outgoing_skype_call_count", 20);
			params.put("data", data);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
				"http://54.69.183.186:1340/activity/add", params,
				new Response.Listener<JSONObject>() {
					@SuppressLint("NewApi")
					@Override
					public void onResponse(JSONObject response) {
						Log.d("Activity Result", response.toString());
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("Error", error.getMessage() + " ");

					}

				});
		AppController.getInstance().addToRequestQueue(req);
	}

}
