package com.touchKin.touchkinapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.touchKin.touchkinapp.broadcastReciever.IncomingSMS;
import com.touchKin.touchkinapp.model.AppController;
import com.touchKin.touchkinapp.model.Validation;
import com.touchKin.touckinapp.R;

public class OtpRequestActivity extends ActionBarActivity {
	EditText otp, phoneNo;
	String OneTimePass;
	private ProgressDialog pDialog;
	String phone;
	private Toolbar toolbar;
	TextView mTitle;
	String phoneNumber, userID, userName = null;
	IncomingSMS reciever;
	String code;
	String deviceId, mobile_os;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.otp_layout);

		phone = getIntent().getExtras().getString("phoneNumber");
		deviceId = getIntent().getExtras().getString("device_id");
		mobile_os = getIntent().getExtras().getString("device_os");
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
		mTitle.setText("Verify your number");
		phoneNo = (EditText) findViewById(R.id.mobileNumberEditText);
		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Please wait...");
		pDialog.setCancelable(false);
		phoneNo.setText(phone);
		Button submit_button = (Button) findViewById(R.id.submitButton);
		otp = (EditText) findViewById(R.id.otp_editText);

		submit_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkValidation()) {
					sendIntent();
				} else {

					Toast.makeText(OtpRequestActivity.this, " contains error",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		otp.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				Validation.isOTPNumber(otp, false);
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		otp.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)) {
					// Toast.makeText(MainActivity.this, "enter press",
					// Toast.LENGTH_LONG).show();
					if (checkValidation()) {
						sendIntent();
					} else {
						Toast.makeText(OtpRequestActivity.this,
								" contains error", Toast.LENGTH_LONG).show();
					}
				}
				return false;
			}
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		reciever = new IncomingSMS();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(reciever, filter);

	}

	protected void onResume() {
		super.onResume();

	};

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		new Handler().postDelayed(new Runnable() {
			public void run() {
				OtpRequestActivity.this.unregisterReceiver(reciever);
			}
		}, 0);
		super.onStop();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d("YourActivity", "onNewIntent is called!");

		code = intent.getStringExtra("code");
		Log.d("code", code);
		otp.setText(code);
		super.onNewIntent(intent);
	} // End of onNewIntent(Intent intent)

	private boolean checkValidation() {
		boolean ret = true;

		if (!Validation.isOTPNumber(otp, true)) {
			ret = false;
		}
		return ret;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		// if (id == R.id.action_settings) {
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
		showpDialog();
		OneTimePass = otp.getText().toString();
		JSONObject params = new JSONObject();
		try {
			params.put("mobile", phone);
			params.put("code", Integer.parseInt(OneTimePass));
			params.put("mobile_device_id", deviceId);
			params.put("mobile_os", mobile_os);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
				"http://54.69.183.186:1340/user/verify-mobile", params,
				new Response.Listener<JSONObject>() {
					@SuppressLint("NewApi")
					@Override
					public void onResponse(JSONObject response) {

						SharedPreferences pref = getApplicationContext()
								.getSharedPreferences("loginPref", 0);
						try {

							Editor edit = pref.edit();
							edit.putString("mobile",
									response.getString("mobile"));
							edit.putString("otp", response
									.getString("mobile_verification_code"));
							edit.putString("id", response.getString("id"));
							edit.apply();
							if (response.has("first_name")) {
								userName = response.getString("first_name");
							}
							userID = response.getString("id");
							// Log.d("Response", "" + response);
							// Log.d("mobile", "" + pref.getString("mobile",
							// null));
							// Log.d("otp", "" + pref.getString("mobile",
							// null));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						Intent i = new Intent(OtpRequestActivity.this,
								Details.class);
						Bundle bndlanimation = ActivityOptions
								.makeCustomAnimation(getApplicationContext(),
										R.anim.animation, R.anim.animation2)
								.toBundle();
						i.putExtra("phoneNumber", phone);
						i.putExtra("id", userID);
						i.putExtra("fromOtp", true);
						if (userName != null)
							i.putExtra("first_name", userName);

						startActivity(i, bndlanimation);

						// Log.d(TAG, response.toString());
						// VolleyLog.v("Response:%n %s",
						// response.toString(4));
						hidepDialog();
						finish();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

						Log.d("Error", "" + error.networkResponse);
						VolleyLog.e("Error: ", error.getMessage());
						String json = null;
						
						NetworkResponse response = error.networkResponse;
						if (!InternetAvailable()) {
							Toast.makeText(OtpRequestActivity.this,
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
							}
						}
					
						VolleyLog.e("Error: ", error.getMessage());
						Toast.makeText(OtpRequestActivity.this,
								error.getMessage(), Toast.LENGTH_SHORT).show();

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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		// unregisterReceiver(reciever);

	}

}
