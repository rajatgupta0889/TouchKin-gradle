package com.touchKin.touchkinapp;


import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.touchKin.touckinapp.R;

public class AddParentActivity extends ActionBarActivity implements
		OnClickListener {
	EditText parentNameEditText, parentPhoneEditText, parentLocationEditText;
	CheckBox smartPhoneCB;
	Button addButton;
	private ProgressDialog pDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_parent_layout);
		init();
		addButton.setOnClickListener(this);
	}

	private void init() {
		// TODO Auto-generated method stub
		parentNameEditText = (EditText) findViewById(R.id.nameEditBox);
		parentPhoneEditText = (EditText) findViewById(R.id.phoneEditBox);
		parentLocationEditText = (EditText) findViewById(R.id.locationEditBox);
		smartPhoneCB = (CheckBox) findViewById(R.id.smartCB);
		addButton = (Button) findViewById(R.id.addButton);
		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Please wait...");
		pDialog.setCancelable(false);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.addButton:
			addParent();
			break;

		default:
			break;
		}

	}

	@SuppressLint("NewApi")
	private void addParent() {
		// TODO Auto-generated method stub
		String name = parentNameEditText.getText().toString();
		String phone = parentPhoneEditText.getText().toString();
		String location = parentLocationEditText.getText().toString();

		// if (!name.isEmpty() && !phone.isEmpty()) {
		Intent i = new Intent(AddParentActivity.this, DashBoardActivity.class);
		Bundle bndlanimation = ActivityOptions.makeCustomAnimation(
				getApplicationContext(), R.anim.animation, R.anim.animation2)
				.toBundle();

		startActivity(i, bndlanimation);
		finish();
//			showpDialog();
//
//			JSONObject params = new JSONObject();
//			try {
//				params.put("mobile", phone);
//				params.put("name", name);
//			} catch (JSONException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//
//			JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
//					"http://54.69.183.186:1340/kin/add-senior", params,
//					new Response.Listener<JSONObject>() {
//
//						@Override
//						public void onResponse(JSONObject response) {
//							Intent i = new Intent(AddParentActivity.this,
//									DashBoardActivity.class);
//							Bundle bndlanimation = ActivityOptions
//									.makeCustomAnimation(
//											getApplicationContext(),
//											R.anim.animation, R.anim.animation2)
//									.toBundle();
//
//							startActivity(i, bndlanimation);
//							// Log.d(TAG, response.toString());
//							// VolleyLog.v("Response:%n %s",
//							// response.toString(4));
//							hidepDialog();
//						}
//					}, new Response.ErrorListener() {
//						@Override
//						public void onErrorResponse(VolleyError error) {
//							Log.d("Error", "" + error);
//							VolleyLog.e("Error: ", error.getMessage());
//							Toast.makeText(getApplicationContext(),
//									error.getMessage(), Toast.LENGTH_SHORT)
//									.show();
//							hidepDialog();
//						}
//
//					});
//
//			AppController.getInstance().addToRequestQueue(req);
//
//		} else {
//			Toast.makeText(this, "Phone and name cannot be empty",
//					Toast.LENGTH_SHORT).show();
//
//		}
	}

	private void showpDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hidepDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}
}
