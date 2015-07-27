package com.touchKin.touchkinapp;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.touchKin.touchkinapp.Interface.ButtonClickListener;
import com.touchKin.touchkinapp.adapter.ExpandableListAdapter;
import com.touchKin.touchkinapp.adapter.ImageAdapter;
import com.touchKin.touchkinapp.adapter.MyAdapter;
import com.touchKin.touchkinapp.adapter.MyAdapter.ViewHolder.IMyViewHolderClicks;
import com.touchKin.touchkinapp.custom.HorizontalListView;
import com.touchKin.touchkinapp.model.AppController;
import com.touchKin.touchkinapp.model.ParentListModel;
import com.touchKin.touchkinapp.services.DeviceAcivityService;
import com.touchKin.touchkinapp.services.LocationSendingService;
import com.touchKin.touckinapp.R;

public class DashBoardActivity extends ActionBarActivity implements
		AnimationListener, OnItemClickListener, IMyViewHolderClicks,
		ButtonClickListener {
	public FragmentTabHost mTabHost;
	String NAME = "Rajat Gupta ";
	// String EMAIL = "akash.bangad@android4devs.com";
	// int PROFILE = R.drawable.mom;
	String TITLES[] = { "My Family", "My Accounts", "Upgrade", "Terms of Use",
			"Contact Us", "Sign out" };
	private Toolbar toolbar; // Declaring the Toolbar Object
	
	RecyclerView mRecyclerView; // Declaring RecyclerView
	RecyclerView.Adapter<MyAdapter.ViewHolder> mAdapter; // Declaring Adapte
	// For Recycler View
	RelativeLayout relative;
	RecyclerView.LayoutManager mLayoutManager; // Declaring Layout Manager as a
	TextView mTitle;
	DrawerLayout Drawer; // Declaring DrawerLayout
	RelativeLayout parentRelativeLayout;
	HorizontalListView listview;
	Animation animSlideUp, animSlideDown;
	List<ParentListModel> list, careGiverList;
	private ParentListModel selectedParent;
	ActionBarDrawerToggle mDrawerToggle; // Declaring Action Bar Drawer Toggle
	private ImageAdapter imageAdapter;
	public static Boolean isCancel = true;
	public String userId, userName, phoneNo;
	ButtonClickListener listener;
	JSONObject userObj;
	static Button notifCount;
	String token;
	Boolean isFromNotification;
	JSONArray touchArray = null;
	View anchor;
	List<String> notificationList;
	ListPopupWindow popup;
	Boolean popupIsShowing = false;
	TextView notification;
	public static int width, height;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dash_board);

		// mTabHost = new FragmentTabHost(this);
		// mTabHost.setup(this, getSupportFragmentManager(),
		// R.id.menu_settings);
		// lLayout = new MyLinearLayout(this);
		DisplayMetrics metrics = DashBoardActivity.this.getResources().getDisplayMetrics();
		width = metrics.widthPixels;
		height = metrics.heightPixels;
		
		

		InitView();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			isFromNotification = extras.getBoolean("Flag");
		}
		setSupportActionBar(toolbar);
		toolbar.inflateMenu(R.menu.toolbar_menu);
		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// Handle the menu item
				int id = item.getItemId();
				if (id == R.id.bell) {
					Toast.makeText(DashBoardActivity.this, "Menu",
							Toast.LENGTH_LONG).show();
					showListPopup();
					// toggleVissibility();
					return true;
				}
				Toast.makeText(getApplicationContext(), "ifds",
						Toast.LENGTH_LONG).show();
				return true;
			}
		});

		mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);

		mRecyclerView.setHasFixedSize(true);

		MyAdapter.mListener = DashBoardActivity.this;
		SharedPreferences userPref = getApplicationContext()
				.getSharedPreferences("userPref", 0);

		String user = userPref.getString("user", null);
		try {
			userObj = new JSONObject(user);
			userId = userObj.getString("id");
			userName = userObj.getString("first_name");
			phoneNo = userObj.getString("mobile");
			token = userObj.optString("token");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		SharedPreferences pendingTouch = getApplicationContext()
				.getSharedPreferences("pendingTouch", 0);
		String array = pendingTouch.getString("touch", null);
		if (array != null) {
			try {
				touchArray = new JSONArray(array);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		notificationList = new ArrayList<String>();

		SharedPreferences pendingReq = getApplicationContext()
				.getSharedPreferences("pedingReq", 0);
		String str = pendingReq.getString("req", null);
		if (str != null) {
			try {
				JSONArray notifArray = new JSONArray(str);
				int count = notifArray.length();
				for (int i = 0; i < count; i++) {
					notificationList.add(notifArray.getString(i));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		fetchParentList();
		mAdapter = new MyAdapter(TITLES, userName, userId,
				DashBoardActivity.this); // Creating
		mRecyclerView.setAdapter(mAdapter); // Setting the adapter to
		// RecyclerView

		mLayoutManager = new LinearLayoutManager(this); // Creating a layout
		// Manager

		mRecyclerView.setLayoutManager(mLayoutManager); // Setting the layout
		Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout); // Drawer
		relative.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (parentRelativeLayout.getVisibility() == View.VISIBLE) {
					parentRelativeLayout.startAnimation(animSlideUp);

				}
			}
		});
		parentRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toggleVissibility();
			}
		});
		// object
		// Assigned
		// Assigned
		// to the
		// view
		toolbar.setNavigationIcon(R.drawable.ic_drawer);
		mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, null,
				R.string.navigation_drawer_open,
				R.string.navigation_drawer_close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				// code here will execute once the drawer is opened( As I dont
				// want anything happened whe drawer is
				// open I am not going to put anything here)

			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				// Code here will execute once drawer is closed

			}

		}; // Drawer Toggle Object Made
		Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the
		// Drawer toggle

		toolbar.setTitle("");
		mDrawerToggle.setDrawerIndicatorEnabled(false);
		toolbar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				Drawer.openDrawer(Gravity.LEFT);

			}
		});
		mTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toggleVissibility();

			}
		});

		mDrawerToggle.syncState(); // Finally we set the drawer toggle sync
		// State
		// mTitle.setText("TouchKin");

		listview.setOnItemClickListener(this);
		animSlideUp.setAnimationListener(this);
		animSlideDown.setAnimationListener(this);

		// AlarmManager alarm = (AlarmManager)
		// getSystemService(Context.ALARM_SERVICE);
		// Intent intent1 = new Intent(DashBoardActivity.this,
		// DeviceAcivityService.class);
		// PendingIntent pending = PendingIntent.getService(
		// DashBoardActivity.this, 0, intent1, 0);
		// // alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
		// // 10000, pending);
		// alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
		// AlarmManager.INTERVAL_FIFTEEN_MINUTES,
		// AlarmManager.INTERVAL_FIFTEEN_MINUTES, pending);
		startLocationService();
		startActivityService();
		// startService(intent1);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (userName == null || userName.isEmpty())
			try {
				userName = userObj.getString("first_name");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		mAdapter.notifyDataSetChanged();
		popup = new ListPopupWindow(DashBoardActivity.this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		getMenuInflater().inflate(R.menu.toolbar_menu, menu);
		anchor = menu.findItem(R.id.bell).getActionView();
		MenuItem item = menu.getItem(0);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				if (item.getItemId() == R.id.bell)
					showListPopup();
				return false;
			}
		});
		anchor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showListPopup();
			}
		});

		notification = (TextView) anchor.findViewById(R.id.hotlist_hot);
		setCount();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// if (id == R.id.bell) {
		// // Not implemented here
		// Toast.makeText(getApplicationContext(), "ifds", Toast.LENGTH_LONG)
		// .show();
		// showListPopup();
		// return true;
		// }
		return super.onOptionsItemSelected(item);
	}

	private void setTabColor(FragmentTabHost mTabHost) {
		for (int i = 0; i < this.mTabHost.getTabWidget().getChildCount(); i++) {
			this.mTabHost
					.getTabWidget()
					.getChildAt(i)
					.setBackgroundColor(getResources().getColor(R.color.tab_bg)); // unselected

		}
		this.mTabHost
				.getTabWidget()
				.getChildAt(this.mTabHost.getCurrentTab())
				.setBackgroundColor(
						getResources().getColor(R.color.tab_selected));
		if (this.mTabHost.getCurrentTab() == 0) {
			if (list.size() > 1) {
				ParentListModel item = list.get(1);
				for (ParentListModel data : list) {

					if (data.equals(item)) {
						data.setIsSelected(true);
					} else {
						data.setIsSelected(false);
					}
				}
				setMenuTitle(item);
				selectedParent = item;
			}

			getSupportFragmentManager().executePendingTransactions();
			((Fragment1) getSupportFragmentManager().findFragmentByTag(
					"DashBoard")).notifyFrag();
		}
	}

	private void InitView() {
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		Bundle b = new Bundle();
		b.putString("key", "Fake");
		mTabHost.addTab(
				setIndicator(this, mTabHost.newTabSpec("DashBoard"),
						R.color.tab_bg, "Dashboard", R.drawable.dashboard),
				Fragment1.class, b);
		mTabHost.setCurrentTab(0);
		// Added tab for mydashboard
		b = new Bundle();
		b.putString("key", "MyDashboard");
		mTabHost.addTab(
				mTabHost.newTabSpec("MyDashBoard").setIndicator(" MyDashBoard"),
				Fragment2.class, b);
		mTabHost.getTabWidget().getChildAt(1).setVisibility(View.GONE);
		mTabHost.setCurrentTab(0);
		b = new Bundle();
		b.putString("key", "TouchKin");
		mTabHost.addTab(
				setIndicator(this, mTabHost.newTabSpec("KinBook"),
						R.color.tab_bg, "Kinbook", R.drawable.kinbook),
				TouchKinBookFragment.class, b);

		b = new Bundle();
		b.putString("key", "Messages");
		mTabHost.addTab(
				setIndicator(this, mTabHost.newTabSpec("Live Advisor"),
						R.color.tab_bg, "Live Advisor",
						R.drawable.ic_action_live), SettingsFragment.class, b);

		b = new Bundle();
		b.putString("key", "ER Plan");
		mTabHost.addTab(
				setIndicator(this, mTabHost.newTabSpec("More"), R.color.tab_bg,
						"More", R.drawable.ic_action_more),
				TouchKinBookFragment.class, b);

		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				setTabColor(mTabHost);

			}

		});
		listview = (HorizontalListView) findViewById(R.id.parentListView);

		parentRelativeLayout = (RelativeLayout) findViewById(R.id.parentListLayoutDashboard);
		animSlideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
		animSlideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
		relative = (RelativeLayout) findViewById(R.id.relativeDashboard);
	}

	private TabSpec setIndicator(Context ctx, TabSpec spec, int resid,
			String string, int genresIcon) {
		View v = LayoutInflater.from(ctx).inflate(R.layout.tab_item, null);
		v.setBackgroundResource(resid);
		TextView tv = (TextView) v.findViewById(R.id.txt_tabtxt);
		ImageView img = (ImageView) v.findViewById(R.id.img_tabtxt);

		tv.setText(string);
		img.setBackgroundResource(genresIcon);

		return spec.setIndicator(v);
	}

	private void toggleVissibility() {
		// TODO Auto-generated method stub
		if (parentRelativeLayout.getVisibility() == View.VISIBLE) {
			listview.startAnimation(animSlideUp);

		} else {
			parentRelativeLayout.setVisibility(View.VISIBLE);
			listview.startAnimation(animSlideDown);
		}
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		if (animation == animSlideDown) {
			parentRelativeLayout.setVisibility(View.VISIBLE);
			parentRelativeLayout.setClickable(true);

		} else if (animation == animSlideUp) {
			parentRelativeLayout.setVisibility(View.GONE);
			parentRelativeLayout.setClickable(false);
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	public void fetchParentList() {
		list = new ArrayList<ParentListModel>();
		careGiverList = new ArrayList<ParentListModel>();

		JsonObjectRequest req = new JsonObjectRequest(Method.GET,
				"http://54.69.183.186:1340/user/family", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject responseArray) {
						// TODO Auto-generated method stub
						Log.d("Response Array", " " + responseArray);
						try {
							JSONArray careRecievers = responseArray
									.getJSONArray("care_receivers");

							list.add(new ParentListModel(userId, false,
									"My Circle", userId, "", true, userObj
											.getString("mobile"), true, false,
									(userObj.getString("gender")
											.equalsIgnoreCase("male")) ? true
											: false));

							int crCount = careRecievers.length();
							for (int i = 0; i < crCount; i++) {
								JSONObject cr;

								cr = careRecievers.getJSONObject(i);

								if (cr != null) {
									ParentListModel item = new ParentListModel();
									item.setParentId(cr.getString("id"));
									item.setParentName(cr.optString("nickname"));
									item.setMobilenumber(cr.optString("mobile"));
									if (cr.has("care_receiver_status")
											&& cr.getString(
													"care_receiver_status")
													.equalsIgnoreCase("pending")) {
										item.setReqStatus(false);
									} else {
										item.setReqStatus(true);
									}
									if (cr.has("gender")) {
										item.setIsMale((cr.getString("gender")
												.equalsIgnoreCase("male")) ? true
												: false);
									} else {
										item.setIsMale(null);
									}
									if (touchArray != null
											&& touchArray.length() > 0) {
										for (int j = 0; j < touchArray.length(); j++) {
											try {
												JSONObject obj = touchArray
														.getJSONObject(j);
												if (obj.getString("id")
														.equalsIgnoreCase(
																item.getParentId())) {
													item.setIsPendingTouch(true);
													if (obj.getString("type")
															.equalsIgnoreCase(
																	"media")) {
														item.setIsTouchMedia(true);
													} else {
														item.setIsTouchMedia(false);
													}
												} else {
													item.setIsPendingTouch(false);
												}

											} catch (JSONException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}
									} else {
										item.setIsPendingTouch(false);

									}
									if (i != 0) {
										item.setIsSelected(false);
									} else {
										item.setIsSelected(true);
									}
									list.add(item);

								}
							}
							JSONArray careGivers = responseArray
									.getJSONArray("care_givers");
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
										item.setReqStatus(false);
									} else {
										item.setReqStatus(true);
									}
									if (cg.has("gender")) {
										item.setIsMale((cg.getString("gender")
												.equalsIgnoreCase("male")) ? true
												: false);
									} else {
										item.setIsMale(null);
									}
									if (touchArray != null
											&& touchArray.length() > 0) {
										for (int j = 0; j < touchArray.length(); j++) {
											try {
												JSONObject obj = touchArray
														.getJSONObject(j);
												if (obj.getString("id")
														.equalsIgnoreCase(
																item.getParentId())) {
													item.setIsPendingTouch(true);
													if (obj.getString("type")
															.equalsIgnoreCase(
																	"media")) {
														item.setIsTouchMedia(true);
													} else {
														item.setIsTouchMedia(false);
													}
												} else {
													item.setIsPendingTouch(false);
												}
											} catch (JSONException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}
									} else {
										item.setIsPendingTouch(false);
									}
									item.setMobilenumber(cg.optString("mobile"));
									careGiverList.add(item);
								}
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// else {
						// setMenuTitle(null);
						// }
						if (list.size() > 1) {
							Collections.sort(list,
									new Comparator<ParentListModel>() {
										@Override
										public int compare(ParentListModel lhs,
												ParentListModel rhs) {
											// TODO Auto-generated method stub
											return rhs.getReqStatus()
													.compareTo(
															lhs.getReqStatus());
										}
									});
							Collections.sort(list,
									new Comparator<ParentListModel>() {
										@Override
										public int compare(ParentListModel lhs,
												ParentListModel rhs) {
											// TODO Auto-generated method stub
											return rhs
													.getIsPendingTouch()
													.compareTo(
															lhs.getIsPendingTouch());
										}
									});
							selectedParent = list.get(1);
							list.get(1).setIsSelected(true);
						} else {
							selectedParent = list.get(0);
							list.get(0).setIsSelected(true);
							mTabHost.setCurrentTab(1);

						}
						list.add(new ParentListModel("", false, "Add kin", "",
								"", false, "", false, false, false));

						imageAdapter = new ImageAdapter(DashBoardActivity.this,
								list);
						// if (selectedParent == null) {
						// selectedParent = list.get(0);
						// }
						setTabColor(mTabHost);
						if (listener != null)
							listener.onButtonClickListner(0, null, false);
						setMenuTitle(selectedParent);
						listview.setAdapter(imageAdapter);

					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

						Log.d("Error", "" + error.networkResponse);
						VolleyLog.e("Error: ", error.getMessage());
						String json = null;

						NetworkResponse response = error.networkResponse;
						if (!InternetAvailable()) {
							Toast.makeText(DashBoardActivity.this,
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
						Toast.makeText(DashBoardActivity.this,
								error.getMessage(), Toast.LENGTH_SHORT).show();

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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		ParentListModel item = list.get(position);

		if (!item.getParentId().equals("")) {
			listview.setAdapter(imageAdapter);
			selectedParent = item;
			mTabHost.setVisibility(View.VISIBLE);
			for (ParentListModel data : list) {

				if (data.equals(item)) {
					data.setIsSelected(true);
				} else {
					data.setIsSelected(false);
				}
			}
			setMenuTitle(item);
			if (item.getParentId().equalsIgnoreCase(userId)) {
				mTabHost.setCurrentTab(1);
			} else {

				if (mTabHost.getCurrentTab() != 0) {
					mTabHost.setCurrentTab(0);
					getSupportFragmentManager().executePendingTransactions();
				}
				((Fragment1) getSupportFragmentManager().findFragmentByTag(
						"DashBoard")).notifyFrag();
			}

		} else {
			DialogFragment newFragment = new ContactDialogFragment();
			newFragment.setCancelable(true);
			Bundle args = new Bundle();
			args.putInt("num", ExpandableListAdapter.ADD_CR);
			args.putString("mobile", phoneNo);
			args.putString("token", token);
			newFragment.setArguments(args);
			newFragment.show(getSupportFragmentManager(), "TAG");
			((ContactDialogFragment) newFragment).SetButtonListener(this);
		}
		toggleVissibility();

	}

	public void setMenuTitle(ParentListModel item) {

		if (item != null) {
			mTitle.setText(item.getParentName());

		} else {
			mTitle.setText("Add");
		}
		mTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				R.drawable.ic_action_down, 0);

	}

	public ParentListModel getSelectedParent() {
		return selectedParent;
	}

	@Override
	public void onItemTouch(int caller) {
		// TODO Auto-generated method stub
		Toast.makeText(DashBoardActivity.this, "View " + caller,
				Toast.LENGTH_SHORT).show();

		if (caller == 6) {

			SharedPreferences userPref = getApplicationContext()
					.getSharedPreferences("userPref", 0);
			Editor edit = userPref.edit();
			edit.putString("user", null);
			edit.apply();
			Intent intent = new Intent(this, SignUpActivity.class);
			startActivity(intent);
			finish();

		}
		if (caller == 1) {
			Intent intent = new Intent(DashBoardActivity.this, MyFamily.class);
			intent.putExtra("isLoggedIn", true);

			startActivity(intent);
		}
		Drawer.closeDrawer(Gravity.LEFT);
	}

	public void setCustomButtonListner(ButtonClickListener listener) {
		this.listener = listener;
	}

	@Override
	public void onImageTouch() {
		// TODO Auto-generated method stub
		Toast.makeText(DashBoardActivity.this, "View image ",
				Toast.LENGTH_SHORT).show();
		Intent i = new Intent(DashBoardActivity.this, Details.class);
		i.putExtra("isLoggedin", true);
		startActivity(i);
		Drawer.closeDrawer(Gravity.LEFT);

	}

	public FragmentTabHost getTabHost() {
		return mTabHost;
	}

	@Override
	public void onButtonClickListner(int position, String value,
			Boolean isAccept) {
		// TODO Auto-generated method stub
		fetchParentList();
	}

	public void startLocationService() {
		Intent mServiceIntent = new Intent(DashBoardActivity.this,
				LocationSendingService.class);
		mServiceIntent.putExtra("token", token);
		DashBoardActivity.this.startService(mServiceIntent);
		Log.d("Service run: %s", "Service is started");
		Calendar cal = Calendar.getInstance();
		Intent intent = new Intent(DashBoardActivity.this,
				LocationSendingService.class);
		intent.putExtra("token", token);
		// Intent intent = new Intent("com.my.package.MY_UNIQUE_ACTION");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				DashBoardActivity.this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// AlarmManager alarmManager = (AlarmManager)
		// applicationContext.getSystemService(Context.ALARM_SERVICE);
		// alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
		// cal.getTimeInMillis(), 1000 * 60, pendingIntent);
		// PendingIntent pintent = PendingIntent.getService(
		// applicationContext, 0, intent, 0);
		boolean alarmUp = (PendingIntent.getBroadcast(DashBoardActivity.this,
				0, intent, PendingIntent.FLAG_NO_CREATE) != null);

		if (alarmUp) {
			Log.d("myTag", "Alarm is already active");
		} else {
			AlarmManager alarm = (AlarmManager) DashBoardActivity.this
					.getSystemService(Context.ALARM_SERVICE);
			// Start service every hour
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
					3600 * 1000, pendingIntent);
		}
	}

	public void startActivityService() {
		Intent mServiceIntent = new Intent(DashBoardActivity.this,
				DeviceAcivityService.class);
		mServiceIntent.putExtra("token", token);
		DashBoardActivity.this.startService(mServiceIntent);

		Calendar cal = Calendar.getInstance();
		Intent intent = new Intent(DashBoardActivity.this,
				DeviceAcivityService.class);
		intent.putExtra("token", token);
		// Intent intent = new Intent("com.my.package.MY_UNIQUE_ACTION");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				DashBoardActivity.this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// AlarmManager alarmManager = (AlarmManager)
		// applicationContext.getSystemService(Context.ALARM_SERVICE);
		// alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
		// cal.getTimeInMillis(), 1000 * 60, pendingIntent);
		// PendingIntent pintent = PendingIntent.getService(
		// applicationContext, 0, intent, 0);
		boolean alarmUp = (PendingIntent.getBroadcast(DashBoardActivity.this,
				0, intent, PendingIntent.FLAG_NO_CREATE) != null);

		if (alarmUp) {
			Log.d("myTag", "Alarm is already active");
		} else {
			AlarmManager alarm = (AlarmManager) DashBoardActivity.this
					.getSystemService(Context.ALARM_SERVICE);
			// Start service every hour
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
					3600 * 1000, pendingIntent);
		}
	}

	public List<ParentListModel> getParentList() {
		if (careGiverList.size() > 1) {
			Collections.sort(careGiverList, new Comparator<ParentListModel>() {
				@Override
				public int compare(ParentListModel lhs, ParentListModel rhs) {
					// TODO Auto-generated method stub
					return rhs.getReqStatus().compareTo(lhs.getReqStatus());
				}
			});
			Collections.sort(careGiverList, new Comparator<ParentListModel>() {
				@Override
				public int compare(ParentListModel lhs, ParentListModel rhs) {
					// TODO Auto-generated method stub
					return rhs.getIsPendingTouch().compareTo(
							lhs.getIsPendingTouch());
				}
			});
		}

		return careGiverList;
	}

	public String getToken() {
		return token;
	}

	public void goToKinbook() {
		mTabHost.setCurrentTab(2);
	}

	public void showListPopup() {
		if (!popupIsShowing) {
			popup.setAnchorView(anchor);
			popup.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.notification_pop));
			popup.setWidth(600);
			final ListAdapter adapter = new MyAdapterPopup(this,
					notificationList);
			popup.setOnItemClickListener(new OnItemClickListener() {

				@SuppressLint("NewApi")
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					if (notificationList.get(position).contains("touch")) {
						goToKinbook();
					} else {
						Intent intent = new Intent(DashBoardActivity.this,
								MyFamily.class);
						intent.putExtra("isLoggedIn", true);

						startActivity(intent);
					}
					notificationList.remove(position);
					SharedPreferences pendingReq = getApplicationContext()
							.getSharedPreferences("pedingReq", 0);
					String str = pendingReq.getString("req", null);

					try {
						JSONArray notifArray = new JSONArray(str);
						notifArray.remove(position);
						Editor tokenedit = pendingReq.edit();
						tokenedit.putString("req", notifArray + "");
						tokenedit.commit();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					popup.dismiss();
					setCount();

				}
			});

			popup.setAdapter(adapter);
			popup.show();
			popupIsShowing = true;
		} else {
			popup.dismiss();
			popupIsShowing = false;
		}

	}

	public static class MyAdapterPopup extends BaseAdapter implements
			ListAdapter {
		private Activity activity;
		List<String> notificationList;

		public MyAdapterPopup(Activity activity, List<String> notificationList) {
			this.activity = activity;
			this.notificationList = notificationList;
		}

		@Override
		public int getCount() {
			return notificationList.size();
		}

		@Override
		public Object getItem(int position) {
			return notificationList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView text = null;

			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.textview, null);

			text = (TextView) convertView.findViewById(R.id.textList);

			text.setText(notificationList.get(position));
			return convertView;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (popup != null)
			popup.dismiss();
	}

	@Override
	public void onBackPressed() {
		if (mTabHost.getCurrentTab() != 1) {
			mTabHost.setVisibility(View.VISIBLE);
			mTabHost.setCurrentTab(1);
			if (list.size() > 0) {
				selectedParent = list.get(0);
				ParentListModel item = list.get(0);
				for (ParentListModel data : list) {

					if (data.equals(item)) {
						data.setIsSelected(true);
					} else {
						data.setIsSelected(false);
					}
				}
				setMenuTitle(selectedParent);
				if (toolbar.getVisibility() == View.GONE) {
					toolbar.setVisibility(View.VISIBLE);
				}

			} else {
				finish();
			}
			// if (mTabHost.getCurrentTab() != 0) {
			// mTabHost.setCurrentTab(1);
			// //getSupportFragmentManager().executePendingTransactions();
			// }
			// ((Fragment1) getSupportFragmentManager().findFragmentByTag(
			// "DashBoard")).notifyFrag();
		} else {
			if (!Drawer.isDrawerOpen(Gravity.LEFT))
				finish();
			else {
				Drawer.closeDrawer(Gravity.LEFT);
			}
		}
	}

	public void setCount() {
		if (notification != null && notificationList.size() > 0) {
			notification.setText(notificationList.size() + "");
		} else {
			notification.setVisibility(View.INVISIBLE);
		}
	}

	public void gotoTabZero() {

		mTabHost.setCurrentTab(0);
		getSupportFragmentManager().executePendingTransactions();

		((Fragment1) getSupportFragmentManager().findFragmentByTag("DashBoard"))
				.notifyFrag();

	}
}
