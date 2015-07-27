package com.touchKin.touchkinapp.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.touchKin.touchkinapp.DashBoardActivity;
import com.touchKin.touchkinapp.MyFamily;
import com.touchKin.touchkinapp.broadcastReciever.GcmBroadcastReceiver;
import com.touchKin.touckinapp.R;

public class GcmIntentService extends IntentService {

	public GcmIntentService() {
		super("GcmIntentService");
		// TODO Auto-generated constructor stub
	}

	public static String TAG = "GCMIntentService";
	private NotificationManager mNotificationManager;
	public static final int NOTIFICATION_ID = 1;
	NotificationCompat.Builder builder;

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver

		String messageType = gcm.getMessageType(intent);
		Log.d("Intent", messageType);
		if (extras != null && !extras.isEmpty() && extras.containsKey("txt")) {
			/*
			 * filter message based on message Type. Since it is likely that GCM
			 * will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send Error: " + extras.toString(), "", "");
				Log.d(TAG, messageType);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification(
						"Deleted messagess on server: " + extras.toString(),
						"", "");
				Log.d(TAG, messageType);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				Log.d("Extras", extras.getString("txt"));
				if (extras != null) {
					String message = extras.getString("txt");
					sendNotification(message, extras.getString("type"),
							extras.getString("id"));
				}

			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg, String resultType, String id) {
		// TODO Auto-generated method stub
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = null;
		String message = null;

		message = msg;
		if (resultType.equalsIgnoreCase("request")) {
			intent = new Intent(this, MyFamily.class);

		} else {
			intent = new Intent(this, DashBoardActivity.class);
			SharedPreferences pendingTouch = getApplicationContext()
					.getSharedPreferences("pendingTouch", 0);
			Editor tokenedit = pendingTouch.edit();
			JSONArray touch = null;
			if (pendingTouch.getString("touch", null) == null) {
				touch = new JSONArray();
			} else {
				String array = pendingTouch.getString("touch", null);
				if (array != null) {
					try {
						touch = new JSONArray(array);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			JSONObject touchObj = new JSONObject();
			try {
				touchObj.put("id", id);
				touchObj.put("type", resultType);
				touch.put(touchObj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			tokenedit.putString("touch", touch + "");
			tokenedit.putString("type", resultType);
			tokenedit.commit();
		}
		JSONArray request = null;
		SharedPreferences pendingReq = getApplicationContext()
				.getSharedPreferences("pedingReq", 0);
		Editor tokenedit = pendingReq.edit();
		if (pendingReq.getString("req", null) == null) {
			request = new JSONArray();
		} else {
			String array = pendingReq.getString("req", null);
			if (array != null) {
				try {
					request = new JSONArray(array);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		request.put(msg);
		tokenedit.putString("req", request + "");
		tokenedit.commit();
		intent.putExtra("type", resultType);
		intent.putExtra("Flag", true);
		intent.putExtra("id", id);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Touchkin")
				.setStyle(
						new NotificationCompat.BigTextStyle().bigText(message))
				.setContentText(message)
				.setAutoCancel(true)
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

	}
}
