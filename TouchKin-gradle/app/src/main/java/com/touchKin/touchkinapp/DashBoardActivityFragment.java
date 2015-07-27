package com.touchKin.touchkinapp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.touchKin.touchkinapp.Interface.FragmentInterface;
import com.touchKin.touchkinapp.broadcastReciever.AirplaneModeReceiver;
import com.touchKin.touchkinapp.custom.HoloCircularProgressBar;
import com.touchKin.touchkinapp.custom.PieSlice;
import com.touchKin.touchkinapp.model.AppController;
import com.touchKin.touchkinapp.model.ParentListModel;
import com.touchKin.touckinapp.R;

public class DashBoardActivityFragment extends Fragment implements
		FragmentInterface {
	private HoloCircularProgressBar mHoloCircularProgressBar;
	private ObjectAnimator mProgressBarAnimator;
	TextView batteryTV;
	ImageView battery5, wifi4, network4;
	TelephonyManager Tel;

	TextView parentName, parentNameBottom;
	ParentListModel parent, lastSelectedParent;
	AirplaneModeReceiver rec;
	ImageButton prev;
	Context context;
	String LastUpdateTime = "0";

	// newInstance constructor for creating fragment with arguments
	public static DashBoardActivityFragment newInstance(int page, String title) {
		DashBoardActivityFragment dashBoardActivityFragment = new DashBoardActivityFragment();
		Bundle args = new Bundle();
		args.putInt("someInt", page);
		args.putString("someTitle", title);
		dashBoardActivityFragment.setArguments(args);
		return dashBoardActivityFragment;
	}

	// Store instance variables based on arguments passed
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// savedInstanceState.putString("WORKAROUND_FOR_BUG_19917_KEY",
		// "WORKAROUND_FOR_BUG_19917_VALUE");
		super.onCreate(savedInstanceState);

	}

	// Inflate the view for the fragment based on layout XML
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dashboard_activity_screen,
				container, false);

		parentName = (TextView) view.findViewById(R.id.parentNameTV);
		parentNameBottom = (TextView) view
				.findViewById(R.id.parentBottonActivity);
		mHoloCircularProgressBar = (HoloCircularProgressBar) view
				.findViewById(R.id.holoCircularProgressBar);

		batteryTV = (TextView) view.findViewById(R.id.battery);
		battery5 = (ImageView) view.findViewById(R.id.battery5);
		wifi4 = (ImageView) view.findViewById(R.id.wifi4);
		network4 = (ImageView) view.findViewById(R.id.ImageView05);
		prev = (ImageButton) view.findViewById(R.id.imageButton1);
		prev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((Fragment1) getParentFragment()).getNextItem(1);

			}
		});
		return view;
	}

	private void animate(final HoloCircularProgressBar progressBar,
			final AnimatorListener listener, final float progress,
			final int duration) {

		mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress",
				progress);
		mProgressBarAnimator.setDuration(duration);

		mProgressBarAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(final Animator animation) {
			}

			@Override
			public void onAnimationEnd(final Animator animation) {
				progressBar.setProgress(progress);
			}

			@Override
			public void onAnimationRepeat(final Animator animation) {
			}

			@Override
			public void onAnimationStart(final Animator animation) {
			}
		});
		if (listener != null) {
			mProgressBarAnimator.addListener(listener);
		}
		mProgressBarAnimator.reverse();
		mProgressBarAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				progressBar.setProgress((Float) animation.getAnimatedValue());
			}
		});
		progressBar.setMarkerProgress(progress);
		mProgressBarAnimator.start();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mHoloCircularProgressBar.setProgress(0.0f);
		animate(mHoloCircularProgressBar, null, (float) (1.0f / 30), 1000);
		parent = ((DashBoardActivity) getActivity()).getSelectedParent();
		Log.d("Parent", parent + "");
		setText();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		lastSelectedParent = null;

	}

	@Override
	public void onStart() {
		super.onStart();

	};

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		lastSelectedParent = null;

	}

	@Override
	public void fragmentBecameVisible() {
		// TODO Auto-generated method stub
		parent = ((DashBoardActivity) getActivity()).getSelectedParent();
		if (parent != null) {

			if (lastSelectedParent == null) {
				lastSelectedParent = parent;
				getConnectivity(parent.getParentId());
			}
			if (!lastSelectedParent.equals(parent))
				getConnectivity(parent.getParentId());
			else {
				mHoloCircularProgressBar.setProgress(0.0f);
				animate(mHoloCircularProgressBar, null, (float) (1.0f / 30),
						1000);
			}
		}
	}

	public void getConnectivity(String id) {
		Log.d("id ", id);
		JsonObjectRequest req = new JsonObjectRequest(Method.GET,
				getResources().getString(R.string.url)
						+ "/connectivity/current/" + id, null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject responseObject) {
						// TODO Auto-generated method stub
						Log.d("Response Array Activity",
								responseObject.toString());
						try {
							if (responseObject.has("lastUpdatedConnectivity")) {

								JSONObject lastUpdatedConnectivity = responseObject
										.getJSONObject("lastUpdatedConnectivity");
								int battery = Integer
										.parseInt(lastUpdatedConnectivity
												.getJSONObject("data")
												.getString("battery"));
								int signal = Integer
										.parseInt(lastUpdatedConnectivity
												.getJSONObject("data")
												.getString("3g"));
								int wifi = Integer
										.parseInt(lastUpdatedConnectivity
												.getJSONObject("data")
												.getString("wifi_strength"));
								String updateTime = lastUpdatedConnectivity
										.getString("updatedAt");
								DateFormat df1 = new SimpleDateFormat(
										"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
								try {
									Date result1 = df1.parse(updateTime);
									Log.d("time ", result1 + " ");
									int myHours = result1.getHours();
									Date currentDate = new Date();
									int curHour = currentDate.getHours();
									LastUpdateTime = (curHour - myHours) + "";

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								setData(signal, wifi, battery);
								setText();
							}
							setSlices(responseObject.getJSONObject("stats"));

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// if (responseArray.length() > 0) {
						// // setLocation(responseArray.getJSONObject(
						// // responseArray.length() - 1)
						// // .getJSONObject("point"));
						// }

					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("Error", "" + error.networkResponse);
						VolleyLog.e("Error: ", error.getMessage());
						String json = null;

						NetworkResponse response = error.networkResponse;
						if (!InternetAvailable()) {
							Toast.makeText(getActivity(),
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
						Toast.makeText(getActivity(), error.getMessage(),
								Toast.LENGTH_SHORT).show();
					}

				}) {
			public java.util.Map<String, String> getHeaders()
					throws com.android.volley.AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Authorization", "Bearer "
						+ ((DashBoardActivity) getActivity()).getToken());
				return headers;

			};
		};

		AppController.getInstance().addToRequestQueue(req);

	}

	private boolean InternetAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public void displayMessage(String toastString, int code) {
		Toast.makeText(getActivity(), toastString + " code error: " + code,
				Toast.LENGTH_LONG).show();
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

	public void setData(int level, int wifi, int battery) {

		if (level >= 24) {
			network4.setImageResource(R.drawable.network4);

		} else if (level >= 16 && level < 24) {
			network4.setImageResource(R.drawable.network3);

		} else if (level >= 8 && level < 16) {
			network4.setImageResource(R.drawable.network2);

		} else if (level > 0 && level < 8) {
			network4.setImageResource(R.drawable.network1);

		} else if (level == 0) {
			network4.setImageResource(R.drawable.network0);

		}
		if (wifi == 5) {
			wifi4.setImageResource(R.drawable.wifi4);
		} else if (wifi == 4) {
			wifi4.setImageResource(R.drawable.wifi3);

		} else if (wifi == 3) {
			wifi4.setImageResource(R.drawable.wifi3);

		} else if (wifi == 2) {
			wifi4.setImageResource(R.drawable.wifi2);

		} else if (wifi == 1) {
			wifi4.setImageResource(R.drawable.wifi1);

		} else if (wifi == 0) {
			wifi4.setImageResource(R.drawable.wifi0);

		}
		batteryTV.setText("" + battery + "%");
		if (battery == 100) {
			battery5.setImageResource(R.drawable.battery100);
		} else if (battery >= 80 && battery < 100) {
			battery5.setImageResource(R.drawable.battery80);

		} else if (battery >= 60 && battery < 80) {
			battery5.setImageResource(R.drawable.battery60);

		} else if (battery >= 40 && battery < 60) {
			battery5.setImageResource(R.drawable.battery40);

		} else if (battery >= 20 && battery < 40) {
			battery5.setImageResource(R.drawable.battery20);

		} else if (battery < 20) {
			battery5.setImageResource(R.drawable.battery0);

		}
	}

	public void setSlices(JSONObject slicesObject) {
		ArrayList<PieSlice> slices = new ArrayList<PieSlice>();
		final Resources resources = getResources();

		Iterator<String> iter = slicesObject.keys();
		while (iter.hasNext()) {
			String key = iter.next();
			PieSlice slice = new PieSlice();
			int value = slicesObject.optInt(key, 0);

			if (value == 1) {
				slice.setColor(resources.getColor(R.color.daily_prog_done));
			} else {
				slice.setColor(resources.getColor(R.color.daily_prog_left));
			}
			slices.add(slice);

		}

		mHoloCircularProgressBar.setSlices(slices);
		mHoloCircularProgressBar.setProgress(0.0f);
		animate(mHoloCircularProgressBar, null, (float) (1.0f / 30), 1000);
	}

	public void setText() {
		// parentName.setText(text);
		if (parent != null) {
			if (Integer.parseInt(LastUpdateTime) < 1) {
				parentName.setText(parent.getParentName().substring(0, 1)
						.toUpperCase()
						+ parent.getParentName().substring(1)
						+ " is connected ");
				parentNameBottom.setText("Last updated now");
			} else {
				parentName.setText(parent.getParentName().substring(0, 1)
						.toUpperCase()
						+ parent.getParentName().substring(1)
						+ " is not connected ");
				parentNameBottom.setText("Last updated "
						+ LastUpdateTime
						+ (LastUpdateTime.equalsIgnoreCase("1") ? "  hour"
								: " hours"));
			}
			// getConnectivity(parent.getParentId());
			if (lastSelectedParent == null) {
				lastSelectedParent = parent;
				getConnectivity(parent.getParentId());
			}
			if (lastSelectedParent != null
					&& !lastSelectedParent.equals(parent))
				getConnectivity(parent.getParentId());
			else {
				mHoloCircularProgressBar.setProgress(0.0f);
				animate(mHoloCircularProgressBar, null, (float) (1.0f / 30),
						1000);
			}
		}
	}
}
