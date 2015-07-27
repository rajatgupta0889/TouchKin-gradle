package com.touchKin.touchkinapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.touchKin.touchkinapp.model.AppController;
import com.touchKin.touckinapp.R;

public class SplashActivity extends Activity {
	// Splash screen timer
	private static int SPLASH_TIME_OUT = 2000;
	ProgressBar progBar;
	// private int mProgressStatus = 0;
	// private Handler mHandler = new Handler();
	String applicationId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		SharedPreferences userPref = getApplicationContext()
				.getSharedPreferences("userPref", 0);
		progBar = (ProgressBar) findViewById(R.id.progressBar1);
		String user = userPref.getString("user", null);
		String mobile = null;
		String code = null;
		String mobile_device_id = null;
		Log.d("USer", user + " ");
		if (user != null) {

			try {
				JSONObject userObj = new JSONObject(user);
				mobile = userObj.getString("mobile");
				code = userObj.getString("mobile_verification_code");
				mobile_device_id = userObj.getString("mobile_device_id");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// pref.edit().putString(GetUserLogin.UserTom, null);
			// pref.edit().commit();
			// System.out.println(pref.getString(GetUserLogin.UserTom, null));
			// new Thread(new Runnable() {
			// public void run() {
			// while (mProgressStatus < 100) {
			//
			// progBar.incrementProgressBy(1);
			//
			// // Update the progress bar
			// mHandler.post(new Runnable() {
			// public void run() {
			// progBar.setProgress(mProgressStatus);
			// }
			// });
			// }
			// }
			//
			// }).start();
			// Log.d("Mobile", "" + pref.getString("mobile", null));
			// Log.d("otp", "" + pref.getString("otp", null));
		}
		if (mobile != null && code != null) {
			sendIntent(mobile, code, mobile_device_id);
			// new SendRequest().execute(params)
		} else {
			new Handler().postDelayed(new Runnable() {

				/*
				 * Showing splash screen with a timer. This will be useful when
				 * you want to show case your app logo / company
				 */
				@Override
				public void run() {
					// This method will be executed once the timer is over
					// Start your app main activity
					Intent i = new Intent(SplashActivity.this,
							SignUpActivity.class);
					Bundle bndlanimation = ActivityOptions.makeCustomAnimation(
							getApplicationContext(), R.anim.animation,
							R.anim.animation2).toBundle();
					startActivity(i, bndlanimation);
					finish();
					// close this activity
					finish();
				}
			}, SPLASH_TIME_OUT);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		return true;
	}

	public void sendIntent(String phone, String otp, String id) {
		if (phone != null && otp != null) {

			// JSONObject params = new JSONObject();
			// try {
			// params.put("mobile", phone);
			// params.put("code", Integer.parseInt(otp));
			// params.put("mobile_device_id", id);
			// params.put("mobile_os", "android");
			// } catch (JSONException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }
			//
			// JsonObjectRequest req = new
			// JsonObjectRequest(Request.Method.POST,
			// "http://54.69.183.186:1340/user/verify-mobile", params,
			// new Response.Listener<JSONObject>() {
			// @SuppressLint("NewApi")
			// @Override
			// public void onResponse(JSONObject response) {
			Intent i = new Intent(SplashActivity.this, DashBoardActivity.class);
			Bundle bndlanimation = ActivityOptions.makeCustomAnimation(
					getApplicationContext(), R.anim.animation,
					R.anim.animation2).toBundle();
			startActivity(i, bndlanimation);
			finish();
			// Log.d(TAG, response.toString());
			// VolleyLog.v("Response:%n %s",
			// response.toString(4));
			// }
			// }, new Response.ErrorListener() {
			// @SuppressLint("NewApi")
			// @Override
			// public void onErrorResponse(VolleyError error) {
			//
			//
			// Log.d("Error", "" + error.networkResponse);
			// VolleyLog.e("Error: ", error.getMessage());
			// String json = null;
			//
			// NetworkResponse response = error.networkResponse;
			// if (!InternetAvailable()) {
			// Toast.makeText(SplashActivity.this,
			// "Please Check your intenet connection",
			// Toast.LENGTH_SHORT).show();
			//
			// }
			//
			// // Log.d("Response", response.data.toString());
			// if (response != null && response.data != null) {
			// switch (response.statusCode) {
			// case 400:
			// json = new String(response.data);
			// json = trimMessage(json, "message");
			// if (json != null)
			// displayMessage(json, 400);
			//
			// Log.d("Response", response.data.toString());
			// }
			// }
			//
			// VolleyLog.e("Error: ", error.getMessage());
			// Toast.makeText(SplashActivity.this,
			// error.getMessage(), Toast.LENGTH_SHORT).show();
			//
			// Toast.makeText(getApplicationContext(),
			// error.getMessage(), Toast.LENGTH_SHORT)
			// .show();
			// Intent i = new Intent(SplashActivity.this,
			// SignUpActivity.class);
			// Bundle bndlanimation = ActivityOptions
			// .makeCustomAnimation(
			// getApplicationContext(),
			// R.anim.animation, R.anim.animation2)
			// .toBundle();
			//
			// startActivity(i, bndlanimation);
			// finish();
			// }
			//
			// });
			//
			// AppController.getInstance().addToRequestQueue(req);
			// }
		}
	}

	private boolean InternetAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public void displayMessage(String toastString, int code) {
		Toast.makeText(getApplicationContext(),
				toastString + " code error: " + code, Toast.LENGTH_LONG).show();
	}

	public String trimMessage(String json, String key) {
		String trimmedString = null;

		try {
			JSONObject obj = new JSONObject(json);
			Log.d("JSOn", " " + obj);
			trimmedString = obj.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return trimmedString;
	}
	// private class SendRequest extends AsyncTask<String, Void, Void> {
	// /**
	// * The system calls this to perform work in a worker thread and delivers
	// * it the parameters given to AsyncTask.execute()
	// */
	// protected void onPostExecute(Void result) {
	//
	// }
	//
	// @Override
	// protected Void doInBackground(String... params) {
	// // TODO Auto-generated method stub
	// sendIntent(params[0], params[1]);
	// return null;
	// }
	// }

}
