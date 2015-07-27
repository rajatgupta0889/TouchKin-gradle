package com.touchKin.touchkinapp.custom;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

public class ErrorHandlingInformation {
	Context context;

	public ErrorHandlingInformation(Context context) {
		super();
		this.context = context;
	}

	public boolean InternetAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public void displayMessage(String toastString, int code) {
		Toast.makeText(context, toastString + " code error: " + code,
				Toast.LENGTH_LONG).show();
	}

	public String trimMessage(String json, String key) {
		String trimmedString = null;

		try {
			JSONObject obj = new JSONObject(json);
			trimmedString = obj.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return trimmedString;
	}

}
