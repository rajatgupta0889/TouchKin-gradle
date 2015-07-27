package com.touchKin.touchkinapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.touchKin.touchkinapp.Interface.FragmentInterface;
import com.touchKin.touchkinapp.custom.HoloCircularProgressBar;
import com.touchKin.touchkinapp.custom.PieSlice;
import com.touchKin.touchkinapp.model.AppController;
import com.touchKin.touchkinapp.model.ParentListModel;
import com.touchKin.touckinapp.R;

public class DashboardLocationFragment extends Fragment implements
		FragmentInterface, OnMarkerClickListener {
	private HoloCircularProgressBar mHoloCircularProgressBar;
	private ObjectAnimator mProgressBarAnimator;
	TextView parentName, parentNameBottom, parentLocPos;
	ParentListModel parent, lastSelectedParent;
	String serverPath = "https://s3-ap-southeast-1.amazonaws.com/touchkin-dev/avatars/";
	JSONObject obj = null;
	Marker googleMarker = null;
	Boolean isTapOnMap = false;
	String updatedTime;
	ImageButton next, prev;
	/**
	 * Provides the entry point to Google Play services.
	 */
	protected GoogleApiClient mGoogleApiClient;
	protected Location mLastLocation;
	int staticSince;
	String loc = "";

	public static DashboardLocationFragment newInstance(int page, String title) {
		DashboardLocationFragment locationFragment = new DashboardLocationFragment();
		Bundle args = new Bundle();
		args.putInt("someInt", page);
		args.putString("someTitle", title);
		locationFragment.setArguments(args);

		return locationFragment;
	}

	private GoogleMap googleMap;
	private static View view;

	// Store instance variables based on arguments passed
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (googleMap != null)
			googleMap.clear();
		// if (mGoogleApiClient.isConnected()) {
		// mGoogleApiClient.disconnect();
		// }

	}

	// Inflate the view for the fragment based on layout XML
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.dashboard_location_screen,
					container, false);
			parent = ((DashBoardActivity) getActivity()).getSelectedParent();
			parentName = (TextView) view.findViewById(R.id.ParentLocTV);
			parentNameBottom = (TextView) view
					.findViewById(R.id.parentBottonLocation);
			parentLocPos = (TextView) view.findViewById(R.id.parentLocPos);
			next = (ImageButton) view.findViewById(R.id.imageButton2);
			next.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					((Fragment1) getParentFragment()).getNextItem(2);

				}
			});
			prev = (ImageButton) view.findViewById(R.id.imageButton1);
			prev.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					((Fragment1) getParentFragment()).getNextItem(0);

				}
			});
			if (parent != null)
				parentName.setText(parent.getParentName() + " is in ");
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}

		if (googleMap == null) {

			googleMap = ((MapFragment) getActivity().getFragmentManager()
					.findFragmentById(R.id.map)).getMap();
		}

		mHoloCircularProgressBar = (HoloCircularProgressBar) view
				.findViewById(R.id.holoCircularProgressBar);

		return view;
	}

	private boolean isGooglePlayServicesAvailable() {
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getActivity());
		if (ConnectionResult.SUCCESS == status) {
			return true;
		} else {
			GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 0)
					.show();
			return false;
		}
	}

	public static Bitmap CustomMarkerView(Context context, View view) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.layout(0, 0, displayMetrics.widthPixels,
				displayMetrics.heightPixels);
		view.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
				view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);

		return bitmap;
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

	Builder dialog;

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		// parent = ((DashBoardActivity) getActivity()).getSelectedParent();
		// Log.d("Parent", parent + "");
		if (isGooglePlayServicesAvailable()) {
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			googleMap.getUiSettings().setZoomControlsEnabled(false);
			googleMap.getUiSettings().setScrollGesturesEnabled(false);
			googleMap.getUiSettings().setZoomGesturesEnabled(false);
			googleMap.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng arg0) {
					// TODO Auto-generated method stub
					isTapOnMap = true;
					Intent intent = new Intent(getActivity(), MapActivity.class);
					intent.putExtra("token",
							((DashBoardActivity) getActivity()).getToken());
					intent.putExtra("id", ((DashBoardActivity) getActivity())
							.getSelectedParent().getParentId());
					startActivity(intent);
				}
			});

		}
		parent = ((DashBoardActivity) getActivity()).getSelectedParent();
		Log.d("Parent", parent + "");
		if (parent != null) {
			// if (lastSelectedParent == null) {
			// lastSelectedParent = parent;
			// getLocation(parent.getParentId());
			// }
			// // parentName.setText(parent.getParentName().substring(0, 1)
			// // .toUpperCase()
			// // + parent.getParentName().substring(1) + " is in ");
			// // parentNameBottom.setText("Its been " + 2 + " hours since "
			// // + parent.getParentName() + " last left home");
			// if (!lastSelectedParent.equals(parent))
			// getLocation(parent.getParentId());
			// else {
			setText();
			mHoloCircularProgressBar.setProgress(0.0f);
			animate(mHoloCircularProgressBar, null, (float) (1.0f / 30), 1000);

		}
		// animate(mHoloCircularProgressBar, null, 0.05f, 3000);
		// Toast.makeText(getActivity(), "Resume", Toast.LENGTH_SHORT).show();

		super.onResume();
	}

	private void setText() {
		// TODO Auto-generated method stub
		parent = ((DashBoardActivity) getActivity()).getSelectedParent();
		if (parent != null) {

			if (!loc.isEmpty()) {
				parentName.setText(parent.getParentName().substring(0, 1)
						.toUpperCase()
						+ parent.getParentName().substring(1) + " is at ");
				parentNameBottom.setText(parent.getParentName().substring(0, 1)
						.toUpperCase()
						+ parent.getParentName().substring(1)
						+ " last left home "
						+ staticSince
						+ (staticSince > 1 ? " hours" : " hour") + " ago");
			} else {
				parentName.setText(parent.getParentName().substring(0, 1)
						.toUpperCase()
						+ parent.getParentName().substring(1) + " is in ");
				parentNameBottom.setText(parent.getParentName().substring(0, 1)
						.toUpperCase()
						+ parent.getParentName().substring(1)
						+ " left home "
						+ staticSince
						+ (staticSince > 1 ? " hours" : " hour")
						+ " ago");
			}
		}
	}

	@Override
	public void fragmentBecameVisible() {
		// TODO Auto-generated method stub

		parent = ((DashBoardActivity) getActivity()).getSelectedParent();
		Log.d("Parent", parent + "");
		if (parent != null) {
			// if (lastSelectedParent == null) {
			// lastSelectedParent = parent;
			// getLocation(parent.getParentId());
			// }
			// parentName.setText(parent.getParentName().substring(0, 1)
			// .toUpperCase()
			// + parent.getParentName().substring(1) + " is in ");
			// parentNameBottom.setText("Its been " + 2 + " hours since "
			// + parent.getParentName() + " last left home");
			getLocation(parent.getParentId());
			mHoloCircularProgressBar.setProgress(0.0f);
			animate(mHoloCircularProgressBar, null, (float) (1.0f / 30), 1000);
			setText();
		}

	}

	private void setLocation(JSONObject obj) {
		// TODO Auto-generated method stub
		String longitude = null;
		String latitude = null;
		LatLng latLng = null;

		try {
			JSONObject point = obj.getJSONObject("point");
			longitude = point.getString("y");
			latitude = point.getString("x");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loc = obj.optString("name", "");
		if (!loc.equalsIgnoreCase("")) {
			parentName.setText(parent.getParentName().substring(0, 1)
					.toUpperCase()
					+ parent.getParentName().substring(1) + " is at ");
			parentLocPos.setText(obj.optString("name"));
			isTapOnMap = true;
			parentNameBottom.setText(parent.getParentName().substring(0, 1)
					.toUpperCase()
					+ parent.getParentName().substring(1)
					+ " last left home "
					+ staticSince
					+ (staticSince > 1 ? " hours" : " hour")
					+ " ago");
		} else {
			Geocoder geocoder = new Geocoder(getActivity());
			try {
				List<Address> addresses = geocoder.getFromLocation(
						Double.parseDouble(latitude),
						Double.parseDouble(longitude), 1);
				if (addresses != null && addresses.get(0) != null)
					parentLocPos.setText(addresses.get(0).getLocality());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			parentNameBottom.setText(parent.getParentName().substring(0, 1)
					.toUpperCase()
					+ parent.getParentName().substring(1)
					+ " left home"
					+ staticSince
					+ (staticSince > 1 ? " hours" : " hour")
					+ " ago");
		}
		if (longitude != null & latitude != null) {
			latLng = new LatLng(Double.parseDouble(latitude),
					Double.parseDouble(longitude));

			View marker = ((LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE)).inflate(
					R.layout.custom_marker, null);
			if (googleMarker != null)
				googleMarker.remove();
			googleMarker = googleMap.addMarker(new MarkerOptions().position(
					latLng).icon(
					BitmapDescriptorFactory.fromBitmap(CustomMarkerView(
							getActivity(), marker))));
			googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(8));
			googleMap.setOnMarkerClickListener(this);

		}
	}

	public void getLocation(String id) {
		Log.d("id ", id);
		JsonObjectRequest req = new JsonObjectRequest(Method.GET,
				getResources().getString(R.string.url) + "/location/current/"
						+ id, null, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject responseArray) {
						// TODO Auto-generated method stub
						Log.d("Response Array Location", " " + responseArray);
						try {
							if (responseArray.length() > 0) {
								obj = responseArray
										.getJSONObject("lastUpdatedLocation");
								setLocation(obj);
								setText();
								setSlices(responseArray
										.getJSONObject("movements"));
								staticSince = responseArray.optInt(
										"static_since", 1);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("Error", "" + error + " ");
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

		req.setRetryPolicy(new DefaultRetryPolicy(0, 5,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), MapActivity.class);
		intent.putExtra("token", ((DashBoardActivity) getActivity()).getToken());
		intent.putExtra("id", ((DashBoardActivity) getActivity())
				.getSelectedParent().getParentId());
		isTapOnMap = true;
		startActivity(intent);

		return false;
	}

}
