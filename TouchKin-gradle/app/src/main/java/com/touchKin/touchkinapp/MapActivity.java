package com.touchKin.touchkinapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.touchKin.touchkinapp.custom.Constants;
import com.touchKin.touchkinapp.custom.CustomRequest;
import com.touchKin.touchkinapp.custom.GeofenceErrorMessages;
import com.touchKin.touchkinapp.model.AppController;
import com.touchKin.touchkinapp.services.GeofenceTransitionsIntentService;
import com.touchKin.touckinapp.R;

public class MapActivity extends ActionBarActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, OnMarkerClickListener,
		ResultCallback<Status> {
	String text = "";
	private GoogleMap googleMap;
	protected GoogleApiClient mGoogleApiClient;
	protected Location mLastLocation;
	Marker googleMarker = null;
	protected ArrayList<Geofence> mGeofenceList;
	private boolean mGeofencesAdded;
	private PendingIntent mGeofencePendingIntent;
	String token;
	String id;
	Marker clickedMarker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_fragment);
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
		}
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			token = extras.getString("token");
			id = extras.getString("id");
		}
		getLocation(id);
		mGeofenceList = new ArrayList<Geofence>();
		mGeofencePendingIntent = null;
		populateGeofenceList();
		buildGoogleApiClient();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		googleMap.setMyLocationEnabled(true);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// Provides a simple way of getting a device's location and is well
		// suited for
		// applications that do not require a fine-grained location and that do
		// not need location
		// updates. Gets the best and most recent location currently available,
		// which may be null
		// in rare cases when a location is not available.
		// mLastLocation = LocationServices.FusedLocationApi
		// .getLastLocation(mGoogleApiClient);
		// LatLng latLng = new LatLng(mLastLocation.getLatitude(),
		// mLastLocation.getLongitude());
		// if (mLastLocation != null) {
		// View marker = ((LayoutInflater)
		// getSystemService(Context.LAYOUT_INFLATER_SERVICE))
		// .inflate(R.layout.custom_marker, null);
		//
		// if (googleMarker != null)
		// googleMarker.remove();
		// googleMarker = googleMap.addMarker(new MarkerOptions()
		// .position(latLng)
		// .title("randomlocation")
		// .icon(BitmapDescriptorFactory.fromBitmap(CustomMarkerView(
		// this, marker))));
		// googleMarker = googleMap.addMarker(new MarkerOptions()
		// .position(new LatLng(12.9667d, 77.5667d))
		// .title("randomlocation")
		// .icon(BitmapDescriptorFactory.fromBitmap(CustomMarkerView(
		// this, marker))));
		//
		// googleMarker = googleMap.addMarker(new MarkerOptions()
		// .position(new LatLng(12.9259d, 77.6229d))
		// .title("randomlocation")
		// .icon(BitmapDescriptorFactory.fromBitmap(CustomMarkerView(
		// this, marker))));
		//
		// googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		// googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
		// googleMap.setOnMarkerClickListener(this);
		//
		// } else {
		// Toast.makeText(this, "No Location Detected", Toast.LENGTH_LONG)
		// .show();
		// }
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Refer to the javadoc for ConnectionResult to see what error codes
		// might be returned in
		// onConnectionFailed.
		Log.i("Location",
				"Connection failed: ConnectionResult.getErrorCode() = "
						+ result.getErrorCode());
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// The connection to Google Play services was lost for some reason. We
		// call connect() to
		// attempt to re-establish the connection.
		Log.i("Location", "Connection suspended");
		mGoogleApiClient.connect();
	}

	@Override
	public void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	/**
	 * Builds a GoogleApiClient. Uses the addApi() method to request the
	 * LocationServices API.
	 */
	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
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

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		DialogFragment newFragment = new MapDialogFragment();
		newFragment.setCancelable(false);
		Bundle args = new Bundle();
		args.putDouble("lat", marker.getPosition().latitude);
		args.putDouble("long", marker.getPosition().longitude);
		newFragment.setArguments(args);
		newFragment.show(getSupportFragmentManager(), "TAG");
		clickedMarker = marker;
		marker.showInfoWindow();

		return false;
	}

	public class MapDialogFragment extends DialogFragment {

		// EditText nameBox;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			// Get the layout inflater
			LayoutInflater inflater = getActivity().getLayoutInflater();
			text = "";
			Bundle arguments = getArguments();
			Double x = arguments.getDouble("lat");
			Double y = arguments.getDouble("long");
			final JSONObject point = new JSONObject();
			try {
				point.put("x", x);
				point.put("y", y);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			View view = inflater.inflate(R.layout.set_location_dialog, null);
			final Button home = (Button) view.findViewById(R.id.customLoc);
			ImageView homeIV = (ImageView) view.findViewById(R.id.homeImage);
			ImageView customIV = (ImageView) view
					.findViewById(R.id.customImage);
			final EditText customlocation = (EditText) view
					.findViewById(R.id.editLoc);
			customlocation.setImeOptions(EditorInfo.IME_ACTION_DONE);
			homeIV.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (home.getVisibility() == View.INVISIBLE) {
						home.setVisibility(View.VISIBLE);
					}
					if (customlocation.getVisibility() == View.VISIBLE) {
						customlocation.setVisibility(View.INVISIBLE);
					}
				}
			});
			customIV.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (home.getVisibility() == View.VISIBLE) {
						home.setVisibility(View.INVISIBLE);
					}
					if (customlocation.getVisibility() == View.INVISIBLE) {
						customlocation.setVisibility(View.VISIBLE);
					}
				}
			});

			builder.setCancelable(true);
			builder.setView(view);

			// Add action buttons
			final AlertDialog dialog = builder.create();
			dialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));

			home.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					JSONObject place = new JSONObject();
					try {
						place.put("home", point);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					setNameToLocation(id, place, "home");
					dialog.dismiss();
				}
			});

			customlocation
					.setOnEditorActionListener(new OnEditorActionListener() {
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
									|| (actionId == EditorInfo.IME_ACTION_DONE)) {
								// Toast.makeText(MainActivity.this,
								// "enter press",
								// Toast.LENGTH_LONG).show();

								JSONObject place = new JSONObject();
								try {
									place.put(customlocation.getText()
											.toString(), point);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								setNameToLocation(id, place, customlocation
										.getText().toString());
								dialog.dismiss();
							}
							return false;
						}
					});
			dialog.show();
			return dialog;
		}

		@Override
		public boolean isCancelable() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void setCancelable(boolean cancelable) {
			// TODO Auto-generated method stub
			super.setCancelable(true);
		}
	}

	@Override
	public void onResult(Status status) {
		// TODO Auto-generated method stub
		if (status.isSuccess()) {
			// Update state and save in shared preferences.
			mGeofencesAdded = !mGeofencesAdded;

		} else {

			String errorMessage = GeofenceErrorMessages.getErrorString(this,
					status.getStatusCode());
			Log.e("Error", errorMessage);
		}
	}

	private GeofencingRequest getGeofencingRequest() {
		GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

		// The INITIAL_TRIGGER_ENTER flag indicates that geofencing service
		// should trigger a
		// GEOFENCE_TRANSITION_ENTER notification when the geofence is added and
		// if the device
		// is already inside that geofence.
		builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

		// Add the geofences to be monitored by geofencing service.
		builder.addGeofences(mGeofenceList);

		// Return a GeofencingRequest.
		return builder.build();
	}

	private PendingIntent getGeofencePendingIntent() {
		// Reuse the PendingIntent if we already have it.
		if (mGeofencePendingIntent != null) {
			return mGeofencePendingIntent;
		}
		Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
		// We use FLAG_UPDATE_CURRENT so that we get the same pending intent
		// back when calling
		// addGeofences() and removeGeofences().
		return PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	/**
	 * This sample hard codes geofence data. A real app might dynamically create
	 * geofences based on the user's location.
	 */
	public void populateGeofenceList() {
		for (Map.Entry<String, LatLng> entry : Constants.BAY_AREA_LANDMARKS
				.entrySet()) {

			mGeofenceList.add(new Geofence.Builder()
			// Set the request ID of the geofence. This is a string to identify
			// this
			// geofence.
					.setRequestId(entry.getKey())

					// Set the circular region of this geofence.
					.setCircularRegion(entry.getValue().latitude,
							entry.getValue().longitude,
							Constants.GEOFENCE_RADIUS_IN_METERS)

					// Set the expiration duration of the geofence. This
					// geofence gets automatically
					// removed after this period of time.
					.setExpirationDuration(
							Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

					// Set the transition types of interest. Alerts are only
					// generated for these
					// transition. We track entry and exit transitions in this
					// sample.
					.setTransitionTypes(
							Geofence.GEOFENCE_TRANSITION_ENTER
									| Geofence.GEOFENCE_TRANSITION_EXIT)

					// Create the geofence.
					.build());
		}
	}

	public void addGeofencesButtonHandler(View view) {
		if (!mGoogleApiClient.isConnected()) {
			Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();
			return;
		}

		try {
			LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,
			// The GeofenceRequest object.
					getGeofencingRequest(),
					// A pending intent that that is reused when calling
					// removeGeofences(). This
					// pending intent is used to generate an intent when a
					// matched geofence
					// transition is observed.
					getGeofencePendingIntent()).setResultCallback(this); // Result
																			// processed
																			// in
																			// onResult().
		} catch (SecurityException securityException) {
			// Catch exception generated if the app does not use
			// ACCESS_FINE_LOCATION permission.
			// logSecurityException(securityException);
		}
	}

	public void getLocation(String id) {
		Log.d("id ", id);
		JsonArrayRequest req = new JsonArrayRequest(getResources().getString(
				R.string.url)
				+ "/location/fetch-frequent-locations/" + id,
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray responseArray) {
						// TODO Auto-generated method stub
						Log.d("Response Array Location of user", " "
								+ responseArray);
						JSONArray array = new JSONArray();
						for (int i = 0; i < responseArray.length(); i++) {
							try {
								array.put(responseArray.get(i));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
						setLoction(array);

					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("Error", "" + error.networkResponse);
						VolleyLog.e("Error: ", error.getMessage());
						String json = null;

						NetworkResponse response = error.networkResponse;

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
						Toast.makeText(MapActivity.this, error.getMessage(),
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

	public void displayMessage(String toastString, int code) {
		Toast.makeText(this, toastString + " code error: " + code,
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

	public void setLoction(JSONArray array) {
		View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.custom_marker, null);

		if (googleMarker != null) {
			googleMarker.remove();
		}
		JSONObject obj;
		LatLng latLng = null;
		try {
			obj = array.getJSONObject(0);

			JSONObject point = obj.getJSONObject("point");
			latLng = new LatLng(Double.parseDouble(point.getString("x")),
					Double.parseDouble(point.getString("y")));
			googleMarker = googleMap.addMarker(new MarkerOptions()
					.position(latLng)
					.title(point.optString("name", ""))
					.icon(BitmapDescriptorFactory.fromBitmap(CustomMarkerView(
							this, marker))));
			Log.d("Length", array.length() + "");
			for (int i = 1; i < array.length(); i++) {
				obj = array.getJSONObject(i);
				point = obj.getJSONObject("point");

				googleMarker = googleMap.addMarker(new MarkerOptions()
						.position(
								new LatLng(Double.parseDouble(point
										.getString("x")), Double
										.parseDouble(point.getString("y"))))
						.title(point.optString("name", ""))
						.icon(BitmapDescriptorFactory
								.fromBitmap(CustomMarkerView(this, marker))));
				if (i == 4)
					break;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
		googleMap.setOnMarkerClickListener(this);

	}

	public void setNameToLocation(String id, JSONObject param, final String name) {
		CustomRequest req = new CustomRequest(Method.POST,
				"http://54.69.183.186:1340/user/add-places/" + id, param,
				new Response.Listener<JSONArray>() {
					@SuppressLint("NewApi")
					@Override
					public void onResponse(JSONArray response) {
						Log.d("Activity Result", response.toString());
						clickedMarker.setTitle(name);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("Error", error.getMessage() + " ");

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

}
