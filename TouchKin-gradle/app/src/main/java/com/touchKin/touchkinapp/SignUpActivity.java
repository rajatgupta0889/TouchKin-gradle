package com.touchKin.touchkinapp;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.touchKin.touchkinapp.custom.ErrorHandlingInformation;
import com.touchKin.touchkinapp.model.AppController;
import com.touchKin.touchkinapp.model.Validation;
import com.touchKin.touckinapp.R;

public class SignUpActivity extends ActionBarActivity {

	String[] country;
	String[] code;
	Spinner spinner;
	Button otp_button;
	private EditText phone_number;
	private ProgressDialog pDialog;
	String areaCode;
	String phoneNumber;
	VideoView exam;
	private static String TAG = SignUpActivity.class.getSimpleName();
	private Toolbar toolbar;
	TextView mTitle;
	ImageView next_tool_button;
	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started."
	 */
	String SENDER_ID = "588149057277";

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	Context context;
	String regid;
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	ErrorHandlingInformation errorHandle;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up_activity);
		WindowManager wm = (WindowManager) SignUpActivity.this.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int width = display.getWidth(); 
		int height = display.getHeight();
//		Toast.makeText(context, ""+height+" "+width, Toast.LENGTH_SHORT).show();

		context = SignUpActivity.this;
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(SignUpActivity.this);
			regid = getRegistrationId(context);

			if (regid.isEmpty()) {
				registerInBackground();
			}
			/*
			 * if(Session.getActiveSession() != null){ startActivity(new
			 * Intent(getActivity(),LoginActivity.class)); }
			 */

		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
		next_tool_button = (ImageView) toolbar.findViewById(R.id.next);
		// Error handler
		errorHandle = new ErrorHandlingInformation(getApplicationContext());

		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Please wait...");
		pDialog.setCancelable(false);

		country = getResources().getStringArray(R.array.list_name);
		code = getResources().getStringArray(R.array.country_code);
		mTitle.setText("Login");
		Spinner spinner = (Spinner) findViewById(R.id.spinner);

		otp_button = (Button) findViewById(R.id.otp_Genrate_Button);
		phone_number = (EditText) findViewById(R.id.phone_number);
		spinner.setAdapter(new MyAdapter(this, R.layout.custom_spinner, country));
		spinner.setSelection(17);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				int index = parent.getSelectedItemPosition();
				areaCode = code[index];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		next_tool_button.setVisibility(View.VISIBLE);
		next_tool_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkValidation()) {
					// new LongOperation().execute("");
					if (errorHandle.InternetAvailable())
						sendIntent();
					else {
						Toast.makeText(SignUpActivity.this,
								" Please Check your connection",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(SignUpActivity.this,
							" Mobile Number is not valid", Toast.LENGTH_LONG)
							.show();
				}

			}
		});

		otp_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (checkValidation()) {
					// new LongOperation().execute("");
					if (errorHandle.InternetAvailable())
						sendIntent();
					else {
						Toast.makeText(SignUpActivity.this,
								" Please Check your connection",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(SignUpActivity.this,
							" Mobile Number is not valid", Toast.LENGTH_LONG)
							.show();
				}

			}
		});

		phone_number.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				Validation.isPhoneNumber(phone_number, false);
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		phone_number.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)) {
					// Toast.makeText(MainActivity.this, "enter press",
					// Toast.LENGTH_LONG).show();
					if (checkValidation()) {
						if (errorHandle.InternetAvailable())
							sendIntent();
						else {
							Toast.makeText(SignUpActivity.this,
									" Please Check your connection",
									Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(SignUpActivity.this, " contains error",
								Toast.LENGTH_LONG).show();
					}
				}
				return false;
			}
		});

		// RotateAnimation anim = new RotateAnimation(0f, 0f, 0f, 15f);
		// anim.setInterpolator(new LinearInterpolator());
		// anim.setRepeatCount(Animation.INFINITE);
		// anim.setDuration(100);
		// exam.startAnimation(anim);

		// Animation rotate =
		// AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
		// exam.startAnimation(rotate);

		// Uri uri = new Uri.Builder()
		// .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
		// .authority(getPackageName())
		// .path(Integer.toString(R.raw.trailer)).build();
		// exam.setVideoURI(uri);
		//
		// exam.setOnPreparedListener(new OnPreparedListener() {
		// @Override
		// public void onPrepared(MediaPlayer mp) {
		// mp.setLooping(true);
		// }
		// });
	}

	// @Override
	// protected void onResume() {
	// super.onResume();
	// exam.start();
	// }

	/*
	 * private class LongOperation extends AsyncTask<String, Void, String> {
	 * 
	 * @Override protected String doInBackground(String... params) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override protected void onPostExecute(String result) {
	 * 
	 * Log.d("value os", "wow nothing happen"); if (result != null) { Intent i =
	 * new Intent(MainActivity.this, Login.class); Bundle bndlanimation =
	 * ActivityOptions.makeCustomAnimation( getApplicationContext(),
	 * R.anim.animation, R.anim.animation2).toBundle(); startActivity(i,
	 * bndlanimation); } // might want to change "executed" for the returned
	 * string // passed // into onPostExecute() but that is upto you }
	 * 
	 * @Override protected void onPreExecute() { }
	 * 
	 * @Override protected void onProgressUpdate(Void... values) { } }
	 */

	private boolean checkValidation() {
		boolean ret = true;
		if (!Validation.isPhoneNumber(phone_number, true)) {
			ret = false;
		}
		return ret;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.toolbar_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		// /int id = item.getItemId();
		// if (id == R.id.) {
		// return true;
		// }
		return super.onOptionsItemSelected(item);
	}

	private void showpDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hidepDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}

	public void sendIntent() {
		// Intent i = new Intent(SignUpActivity.this,
		// DashBoardActivity.class);
		// Bundle bndlanimation = ActivityOptions
		// .makeCustomAnimation(getApplicationContext(),
		// R.anim.animation, R.anim.animation2)
		// .toBundle();
		// startActivity(i,bndlanimation);
		//

		showpDialog();
		phoneNumber = areaCode + phone_number.getText().toString();
		JSONObject params = new JSONObject();
		final String id = getRegistrationId(context);
		Log.d("reg id ", id);
		try {
			params.put("mobile", phoneNumber);
			params.put("mobile_device_id", id);
			params.put("mobile_os", "android");
			Log.d("reg id ", params.getString("mobile_device_id"));

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
				"http://54.69.183.186:1340/user/signup", params,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Intent i = new Intent(SignUpActivity.this,
								Details.class);
						Bundle bndlanimation = ActivityOptions
								.makeCustomAnimation(getApplicationContext(),
										R.anim.animation, R.anim.animation2)
								.toBundle();
						i.putExtra("device_id", id);
						i.putExtra("device_os", "android");
						i.putExtra("isLoggedin", false);
						i.putExtra("isdifferentId", false);
						startActivity(i, bndlanimation);
						SharedPreferences userPref = getApplicationContext()
								.getSharedPreferences("userPref", 0);
						Editor edit = userPref.edit();
						edit.putString("user", response.toString());
						if (response.has("token")) {
							SharedPreferences token = getApplicationContext()
									.getSharedPreferences("token", 0);
							Editor tokenedit = token.edit();
							try {
								tokenedit.putString("token",
										response.getString("token"));
								tokenedit.apply();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						// edit.commit();
						edit.apply();
						Log.d(TAG, response.toString());

						hidepDialog();
						finish();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("Error", "" + error.networkResponse);
						VolleyLog.e("Error: ", error.getMessage());
						String json = null;
						hidepDialog();
						NetworkResponse response = error.networkResponse;
						if (!InternetAvailable()) {
							Toast.makeText(context,
									"Please Check your intenet connection",
									Toast.LENGTH_SHORT).show();

						}

						// Log.d("Response", response.data.toString());
						if (response != null && response.data != null) {
							switch (response.statusCode) {
							case 400:
								json = new String(response.data);
								json = trimMessage(json, "message");
								if (json != null)
									displayMessage(json, 400);

								Log.d("Response", response.data.toString());
								Intent i = new Intent(SignUpActivity.this,
										Details.class);
								Bundle bndlanimation = ActivityOptions
										.makeCustomAnimation(
												getApplicationContext(),
												R.anim.animation,
												R.anim.animation2).toBundle();
								i.putExtra("phone_number", phoneNumber);
								i.putExtra("device_id", id);
								i.putExtra("device_os", "android");
								i.putExtra("isLoggedin", false);
								i.putExtra("isdifferentId", true);
								startActivity(i, bndlanimation);
								finish();
								break;
							}
							// Additional cases
						}
					}

				});

		AppController.getInstance().addToRequestQueue(req);
	}

	private boolean InternetAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public class MyAdapter extends ArrayAdapter<String> {

		public MyAdapter(Context ctx, int txtViewResourceId, String[] objects) {
			super(ctx, txtViewResourceId, objects);
		}

		@Override
		public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
			return getCustomView(position, cnvtView, prnt);
		}

		@Override
		public View getView(int pos, View cnvtView, ViewGroup prnt) {
			return getCustomView1(pos, cnvtView, prnt);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View mySpinner = inflater.inflate(R.layout.custom_spinner, parent,
					false);
			TextView main_text = (TextView) mySpinner
					.findViewById(R.id.text_main_seen);
			main_text.setText(country[position]);

			TextView subSpinner = (TextView) mySpinner
					.findViewById(R.id.sub_text_seen);
			subSpinner.setText(code[position]);

			return mySpinner;
		}

		public View getCustomView1(int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View mySpinner = inflater.inflate(R.layout.custom_spinner1, parent,
					false);

			TextView subSpinner = (TextView) mySpinner
					.findViewById(R.id.sub_text_seen);
			subSpinner.setText(code[position]);

			return mySpinner;
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(SignUpActivity.this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode,
						SignUpActivity.this, PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			} else {
				Log.i(TAG, "This device is not supported.");
				Toast.makeText(context, "This device is not supported",
						Toast.LENGTH_LONG).show();
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(SignUpActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */

	private void registerInBackground() {
		new AsyncTask<Object, Object, Object>() {
			@Override
			protected Object doInBackground(Object... params) {
				// TODO Auto-generated method stub
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;
					Log.d(TAG, msg);
					// You should send the registration ID to your server over
					// HTTP,
					// so it can use GCM/HTTP or CCS to send messages to your
					// app.
					// The request to your server should be authenticated if
					// your app
					// is using accounts.
					sendRegistrationIdToBackend();

					// For this demo: we don't need to send it because the
					// device
					// will send upstream messages to a server that echo back
					// the
					// message using the 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					// msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return null;
			}

		}.execute(null, null, null);
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
		// Your implementation here.
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
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
}
