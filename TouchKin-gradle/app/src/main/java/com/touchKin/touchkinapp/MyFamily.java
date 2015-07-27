package com.touchKin.touchkinapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.touchKin.touchkinapp.Interface.ButtonClickListener;
import com.touchKin.touchkinapp.adapter.ExpandableListAdapter;
import com.touchKin.touchkinapp.model.AppController;
import com.touchKin.touchkinapp.model.ExpandableListGroupItem;
import com.touchKin.touchkinapp.model.ParentListModel;
import com.touchKin.touchkinapp.model.RequestModel;
import com.touchKin.touckinapp.R;

public class MyFamily extends ActionBarActivity implements OnClickListener,
		ButtonClickListener {

	ExpandableListAdapter adapter;
	HashMap<String, ArrayList<ParentListModel>> careGiver;
	ArrayList<RequestModel> requests;
	ArrayList<ExpandableListGroupItem> CareReciever;
	ArrayList<ExpandableListGroupItem> pendingReq;
	ExpandableListView expandListView;
	List<String> item;
	LinearLayout footerView;
	ExpandableListGroupItem me;
	JSONObject mySelf;
	private Toolbar toolbar;
	TextView mTitle;
//	Button next;
	Boolean isLoggedIn;
	ProgressBar myfamilyprogressbar;
	Boolean isFromNotification;
	String phone, device_id;
	String user;
	ImageView next_tool_button, previous_tool_button;
	String token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_family);
		init();
		isLoggedIn = getIntent().getExtras().getBoolean("isLoggedIn");
		isFromNotification = getIntent().getExtras().getBoolean("Flag");
		mTitle.setText("My Family");
		SharedPreferences userPref = getApplicationContext()
				.getSharedPreferences("userPref", 0);

		user = userPref.getString("user", null);

		// if (isFromNotification != null && isFromNotification) {
		// SignUp(phone, device_id);
		// } else {
		fetchDataFromServer();
		// }
		// parents.add(new ParentListModel("", false, "", "", ""));
		// parents.add(new ParentListModel("", false, "", "", ""));
		// parents.add(new ParentListModel("", false, "", "", ""));
		// CareReciever.add(new ExpandableListGroupItem("1", "", "", ""));
		// CareReciever.add(new ExpandableListGroupItem("2", "", "", ""));
		// careGiver.put(CareReciever.get(0).getUserId(), parents);
		// careGiver.put(CareReciever.get(1).getUserId(), parents);
		// careGiver.put(CareReciever.get(2).getUserId(), parents);
		// requests.add(new RequestModel("", "", "", ""));
		// requests.add(new RequestModel("", "", "", ""));

		expandListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// TODO Auto-generated method stub
				if (groupPosition > 0 && groupPosition < CareReciever.size()) {
					// ExpandableListGroupItem item = CareReciever
					// .get(groupPosition);
					// if (careGiver.get(item.getUserId()) == null)
					// fetchMyCRFamily(item.getUserId(), groupPosition);
				} else if (groupPosition != 0) {
					LayoutInflater li = LayoutInflater.from(MyFamily.this);
					View custom = li.inflate(R.layout.pending_dialog, null);
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							MyFamily.this);
					final ExpandableListGroupItem pendingParent = pendingReq
							.get(groupPosition - CareReciever.size());
					alertDialogBuilder.setView(custom);
					alertDialogBuilder.setCancelable(true);

					// create alert dialog
					final AlertDialog alertDialog = alertDialogBuilder.create();
					Button withdrawbtn = (Button) custom
							.findViewById(R.id.withDrawBtn);
					withdrawbtn.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							alertDialog.cancel();
						}
					});
					TextView name = (TextView) custom
							.findViewById(R.id.nameParent);
					name.setText(pendingParent.getUserName());
					Button resendButton = (Button) custom
							.findViewById(R.id.reSendBtn);
					resendButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent sendIntent = new Intent();
							sendIntent.setAction(Intent.ACTION_SEND);
							sendIntent.putExtra(
									Intent.EXTRA_TEXT,
									"Hi "
											+ pendingParent.getUserName()
											+ ", I would like you to care for me on Touchkin."
											+ " Click on the link below to install the app."
											+ " www.etsdfdfvfs.com");
							sendIntent.setType("text/plain");
							startActivity(sendIntent);

							alertDialog.cancel();
						}
					});

					alertDialog.show();

				}
				return false;
			}
		});
		expandListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			int previousGroup = -1;

			@Override
			public void onGroupExpand(int groupPosition) {
				if (groupPosition < CareReciever.size()) {
					if (groupPosition != previousGroup)
						expandListView.collapseGroup(previousGroup);
					previousGroup = groupPosition;
				}
			}
		});
		// pendingReq.add(new ExpandableListGroupItem());
		// pendingReq.add(new ExpandableListGroupItem());
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		footerView = (LinearLayout) inflater.inflate(
				R.layout.expand_list_footer, null);
		expandListView.addFooterView(footerView);
		footerView.setOnClickListener(this);
		adapter = new ExpandableListAdapter(MyFamily.this);
		adapter.setButtonListener(this);
		expandListView.setAdapter(adapter);
//		next.setOnClickListener(this);
		next_tool_button.setVisibility(View.VISIBLE);
		previous_tool_button.setVisibility(View.VISIBLE);
		next_tool_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoNextScreen();

			}
		});
	}

	private void fetchDataFromServer() {
		// TODO Auto-generated method stub
		careGiver.clear();
		CareReciever.clear();
		pendingReq.clear();
		try {
			mySelf = new JSONObject(user);
			CareReciever.add(new ExpandableListGroupItem(
					mySelf.getString("id"), mySelf.getString("first_name"), "",
					"", mySelf.getString("mobile")));
			phone = mySelf.getString("mobile");
			device_id = mySelf.getString("mobile_device_id");
			token = mySelf.optString("token");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getConnectionRequest();
		fetchMyFamily();
	}

	private void getConnectionRequest() {
		// TODO Auto-generated method stub
		JsonArrayRequest req = new JsonArrayRequest(
				"http://54.69.183.186:1340/user/connection-requests",
				new Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray responseArray) {
						// TODO Auto-generated method stub

						Log.d("Response Array", " " + responseArray);
						requests.clear();
						if (responseArray.length() > 0) {
							for (int i = 0; i < responseArray.length(); i++) {
								try {
									RequestModel item = new RequestModel();
									JSONObject careRequest = responseArray
											.getJSONObject(i);
									JSONObject careInitiator = careRequest
											.getJSONObject("initiator");
									JSONObject careGiver = careRequest
											.getJSONObject("care_giver");
									JSONObject careReciever = careRequest
											.getJSONObject("care_receiver");
									if (careInitiator.getString("id").equals(
											careGiver.get("id"))) {
										if (careInitiator.has("nickname")) {
											item.setCare_reciever_name(careInitiator
													.getString("nickname"));
										} else if (careInitiator
												.has("first_name")) {
											item.setCare_reciever_name(careInitiator
													.getString("first_name"));
										} else {
											item.setCare_reciever_name(careInitiator
													.getString("mobile"));
										}
										item.setUserId(careGiver
												.getString("id"));
										item.setReqMsg(item
												.getCare_reciever_name()
												+ " wants to care for you");
									}
									if (careInitiator.get("id").equals(
											careReciever.get("id"))) {
										if (careInitiator.has("nickname")) {
											item.setCare_reciever_name(careInitiator
													.getString("nickname"));
										} else if (careInitiator
												.has("first_name")) {
											item.setCare_reciever_name(careInitiator
													.getString("first_name"));
										} else {
											item.setCare_reciever_name(careInitiator
													.getString("mobile"));
										}
										item.setUserId(careReciever
												.getString("id"));

										item.setReqMsg(item
												.getCare_reciever_name()
												+ " wants you to care for them");

									}
									if (!careInitiator.get("id").equals(
											careReciever.get("id"))
											&& !careInitiator
													.getString("id")
													.equals(careGiver.get("id"))) {
										if (careInitiator.has("nickname")) {
											item.setCare_reciever_name(careInitiator
													.getString("nickname"));
										} else if (careInitiator
												.has("first_name")) {
											item.setCare_reciever_name(careInitiator
													.getString("first_name"));
										} else {
											item.setCare_reciever_name(careInitiator
													.getString("mobile"));
										}
										item.setUserId(careInitiator
												.getString("id"));
										item.setReqMsg(item
												.getCare_reciever_name()
												+ " wants you to care for"
												+ careReciever
														.getInt("nickname"));
									}
									// RequestModel item = new RequestModel();
									//

									item.setRequestID(careRequest
											.getString("id"));
									requests.add(item);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						}
						CareReciever.get(0).setReqCount(
								responseArray.length() + "");

						adapter.notifyDataSetChanged();
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("Error", "" + error.networkResponse);
						VolleyLog.e("Error: ", error.getMessage());
						String json = null;

						NetworkResponse response = error.networkResponse;
						if (!InternetAvailable()) {
							Toast.makeText(MyFamily.this,
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
						Toast.makeText(MyFamily.this, error.getMessage(),
								Toast.LENGTH_SHORT).show();
					}

				}) {
			public java.util.Map<String, String> getHeaders()
					throws com.android.volley.AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Authorization", "Bearer " + token);
				return headers;

			};
		};

		AppController.getInstance().addToRequestQueue(req);

	}

	private void fetchMyFamily() {
		// TODO Auto-generated method stub
		JsonObjectRequest req = new JsonObjectRequest(Method.GET,
				"http://54.69.183.186:1340/user/family", null,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject responseObject) {
						// TODO Auto-generated method stub
						Log.d("Response Array", " " + responseObject);
						myfamilyprogressbar.setVisibility(View.INVISIBLE);
						// CareReciever.clear();

						try {
							JSONArray careGivers = responseObject
									.getJSONArray("care_givers");
							ArrayList<ParentListModel> parents = new ArrayList<ParentListModel>();

							JSONArray careRecievers = responseObject
									.getJSONArray("care_receivers");
							int crCount = careRecievers.length();
							for (int i = 0; i < crCount; i++) {

								JSONObject cr = careRecievers.getJSONObject(i);
								if (cr != null) {
									ExpandableListGroupItem item = new ExpandableListGroupItem();
									item.setUserId(cr.getString("id"));
									item.setUserName(cr.optString("nickname"));
									item.setMobileNo(cr.optString("mobile"));
									if (cr.has("care_receiver_status")
											&& cr.getString(
													"care_receiver_status")
													.equalsIgnoreCase("pending")) {
										pendingReq.add(item);
									} else {
										CareReciever.add(item);
									}
								}
							}
							int cgCount = careGivers.length();
							for (int i = 0; i < cgCount; i++) {
								JSONObject cg = careGivers.getJSONObject(i);
								if (cg != null) {
									ParentListModel item = new ParentListModel();
									item.setParentId(cg.getString("id"));
									item.setParentName(cg
											.optString("first_name"));
									if (cg.has("care_receiver_status")
											&& cg.getString(
													"care_receiver_status")
													.equalsIgnoreCase("pending")) {
									} else {
										parents.add(item);
									}
								}
							}
							CareReciever.get(0)
									.setKinCount(parents.size() + "");
							Log.d("Care reciever Length",
									"" + CareReciever.size());
							careGiver.put(CareReciever.get(0).getUserId(),
									parents);
							startFecthingCRFamily(CareReciever);
							adapter.setupTrips(careGiver, requests,
									CareReciever, pendingReq);
							if (isFromNotification != null
									&& isFromNotification) {
								expandListView.expandGroup(0);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

						Log.d("Error", "" + error.networkResponse);
						VolleyLog.e("Error: ", error.getMessage());
						String json = null;

						NetworkResponse response = error.networkResponse;
						if (!InternetAvailable()) {
							Toast.makeText(MyFamily.this,
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
						Toast.makeText(MyFamily.this, error.getMessage(),
								Toast.LENGTH_SHORT).show();

						VolleyLog.e("Error: ", error.getMessage());
						Toast.makeText(MyFamily.this, error.getMessage(),
								Toast.LENGTH_SHORT).show();
						myfamilyprogressbar.setVisibility(View.INVISIBLE);

					}

				}) {
			public java.util.Map<String, String> getHeaders()
					throws com.android.volley.AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Authorization", "Bearer " + token);
				return headers;

			};
		};

		AppController.getInstance().addToRequestQueue(req);

	}

	private void init() {
		// TODO Auto-generated method stub
		expandListView = (ExpandableListView) findViewById(R.id.familyList);
		careGiver = new HashMap<String, ArrayList<ParentListModel>>();
		CareReciever = new ArrayList<ExpandableListGroupItem>();
		pendingReq = new ArrayList<ExpandableListGroupItem>();
		requests = new ArrayList<RequestModel>();

		me = new ExpandableListGroupItem();
		myfamilyprogressbar = (ProgressBar) findViewById(R.id.myfamilyprogressbar);
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		next_tool_button = (ImageView) toolbar.findViewById(R.id.next);
		previous_tool_button = (ImageView) toolbar.findViewById(R.id.previous);
		mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

//		next = (Button) findViewById(R.id.next);

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.footerView:
			try {
				addContact(ExpandableListAdapter.ADD_CR,
						mySelf.getString("mobile"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.next:
			gotoNextScreen();
			break;
		default:
			break;
		}
	}

	private void gotoNextScreen() {
		// TODO Auto-generated method stub
		if (!isLoggedIn) {
			Intent intent = new Intent(MyFamily.this, DashBoardActivity.class);
			startActivity(intent);
		}
		finish();
	}

	@Override
	public void onButtonClickListner(int position, String value,
			Boolean isAccept) {
		// TODO Auto-generated method stub
		if (position == ExpandableListAdapter.ADD_CG) {
			addContact(ExpandableListAdapter.ADD_CG, value);
		}
		if (position == ExpandableListAdapter.CONN_REQ) {
			if (isAccept) {
				acceptRequest(value);
			} else {
				rejectRequest(value);
			}
		}
		if (position == 1000) {
			fetchDataFromServer();
		}

	}

	public void addContact(int reqTO, String ParentNo) {
		DialogFragment newFragment = new ContactDialogFragment();
		newFragment.setCancelable(true);
		Bundle args = new Bundle();
		args.putInt("num", reqTO);
		args.putString("mobile", ParentNo);
		args.putString("token", token);
		newFragment.setArguments(args);
		newFragment.show(getSupportFragmentManager(), "TAG");
		((ContactDialogFragment) newFragment).SetButtonListener(MyFamily.this);
	}

	private void rejectRequest(String requestID) {
		// TODO Auto-generated method stub
		JSONObject param = new JSONObject();
		JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
				"http://54.69.183.186:1340/user/connection-request/"
						+ requestID + "/reject", param,

				new Response.Listener<JSONObject>() {
					@SuppressLint("NewApi")
					@Override
					public void onResponse(JSONObject response) {

						Log.d("Response", "" + response);
						// requests.remove(position);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("Error", "" + error.networkResponse);
						VolleyLog.e("Error: ", error.getMessage());
						String json = null;

						NetworkResponse response = error.networkResponse;
						if (!InternetAvailable()) {
							Toast.makeText(MyFamily.this,
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
						Toast.makeText(MyFamily.this, error.getMessage(),
								Toast.LENGTH_SHORT).show();
					}

				}) {
			public java.util.Map<String, String> getHeaders()
					throws com.android.volley.AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Authorization", "Bearer " + token);
				return headers;

			};
		};

		AppController.getInstance().addToRequestQueue(req);

	}

	private void acceptRequest(String requestID) {
		// TODO Auto-generated method stub
		JSONObject param = new JSONObject();
		JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
				"http://54.69.183.186:1340/user/connection-request/"
						+ requestID + "/accept", param,

				new Response.Listener<JSONObject>() {
					@SuppressLint("NewApi")
					@Override
					public void onResponse(JSONObject response) {

						Log.d("Response", "" + response);
						// requests.remove(position);
						// adapter.notifyDataSetChanged();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("Error", "" + error.networkResponse);
						VolleyLog.e("Error: ", error.getMessage());
						String json = null;

						NetworkResponse response = error.networkResponse;
						if (!InternetAvailable()) {
							Toast.makeText(MyFamily.this,
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
						Toast.makeText(MyFamily.this, error.getMessage(),
								Toast.LENGTH_SHORT).show();
					}

				}) {
			public java.util.Map<String, String> getHeaders()
					throws com.android.volley.AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Authorization", "Bearer " + token);
				return headers;

			};
		};

		AppController.getInstance().addToRequestQueue(req);
	}

	private void fetchMyCRFamily(String id, final int position) {
		// TODO Auto-generated method stub
		JsonObjectRequest req = new JsonObjectRequest(Method.GET,
				"http://54.69.183.186:1340/user/family/" + id, null,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject responseObject) {
						// TODO Auto-generated method stub
						Log.d("Response Array", " " + responseObject);
						try {
							JSONArray careGivers = responseObject

							.getJSONArray("care_givers");
							ArrayList<ParentListModel> parents = new ArrayList<ParentListModel>();
							int cgCount = careGivers.length();
							for (int i = 0; i < cgCount; i++) {
								JSONObject cg = careGivers.getJSONObject(i);
								ParentListModel item = new ParentListModel();
								item.setParentId(cg.getString("id"));
								item.setParentName(cg.optString("first_name"));
								if (cg.has("care_receiver_status")
										&& cg.getString("care_receiver_status")
												.equalsIgnoreCase("pending")) {
								} else {
									parents.add(item);
								}
							}
							CareReciever.get(position).setKinCount(
									parents.size() + "");

							careGiver.put(CareReciever.get(position)
									.getUserId(), parents);
							adapter.setupTripsForCR(careGiver, position);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("Error", "" + error.networkResponse);
						VolleyLog.e("Error: ", error.getMessage());
						String json = null;

						NetworkResponse response = error.networkResponse;
						if (!InternetAvailable()) {
							Toast.makeText(MyFamily.this,
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
						Toast.makeText(MyFamily.this, error.getMessage(),
								Toast.LENGTH_SHORT).show();
					}

				}) {
			public java.util.Map<String, String> getHeaders()
					throws com.android.volley.AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Authorization", "Bearer " + token);
				return headers;

			};
		};

		AppController.getInstance().addToRequestQueue(req);

	}

	// public void SignUp(String phone, String device_id) {
	// JSONObject params = new JSONObject();
	// try {
	// params.put("mobile", phone);
	// params.put("mobile_device_id", device_id);
	// params.put("mobile_os", "android");
	// Log.d("reg id ", params.getString("mobile_device_id"));
	//
	// } catch (JSONException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	//
	// JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
	// "http://54.69.183.186:1340/user/signup", params,
	// new Response.Listener<JSONObject>() {
	//
	// @Override
	// public void onResponse(JSONObject response) {
	// Log.d("Response", response.toString());
	// fetchDataFromServer();
	//
	// }
	// }, new Response.ErrorListener() {
	// @Override
	// public void onErrorResponse(VolleyError error) {
	// Log.d("Error", "" + error.networkResponse);
	// VolleyLog.e("Error: ", error.getMessage());
	// String json = null;
	//
	// NetworkResponse response = error.networkResponse;
	// if (!InternetAvailable()) {
	// Toast.makeText(MyFamily.this,
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
	// Toast.makeText(MyFamily.this, error.getMessage(),
	// Toast.LENGTH_SHORT).show();
	// }
	//
	// }) {
	// public java.util.Map<String, String> getHeaders()
	// throws com.android.volley.AuthFailureError {
	// HashMap<String, String> headers = new HashMap<String, String>();
	// headers.put("Authorization", "Bearer " + token);
	// return headers;
	//
	// };
	// };
	//
	// AppController.getInstance().addToRequestQueue(req);
	// }

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

	private void startFecthingCRFamily(
			ArrayList<ExpandableListGroupItem> careReciever) {
		// TODO Auto-generated method stub

		for (int i = 1; i < careReciever.size(); i++) {

			fetchMyCRFamily(careReciever.get(i).getUserId(), i);

		}
	}
}
