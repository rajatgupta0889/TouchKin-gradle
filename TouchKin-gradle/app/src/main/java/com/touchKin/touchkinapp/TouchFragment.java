package com.touchKin.touchkinapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
import com.touchKin.touchkinapp.Interface.ButtonClickListener;
import com.touchKin.touchkinapp.Interface.FragmentInterface;
import com.touchKin.touchkinapp.Interface.ViewPagerListener;
import com.touchKin.touchkinapp.custom.CustomRequest;
import com.touchKin.touchkinapp.custom.HoloCircularProgressBar;
import com.touchKin.touchkinapp.custom.ImageLoader;
import com.touchKin.touchkinapp.custom.PieSlice;
import com.touchKin.touchkinapp.model.AppController;
import com.touchKin.touchkinapp.model.ParentListModel;
import com.touchKin.touckinapp.R;

public class TouchFragment extends Fragment implements FragmentInterface,
		ViewPagerListener, ButtonClickListener {
	private HoloCircularProgressBar mHoloCircularProgressBar;
	private ObjectAnimator mProgressBarAnimator;
	String serverPath = "https://s3-ap-southeast-1.amazonaws.com/touchkin-dev/avatars/";
	ImageView parentImage;
	TextView parentName, parentBotton;
	ParentListModel parent;
	int resID;
	Vibrator vib;
	String backData;
	String touchTime = "";
	ImageButton next;
	String topData = "";
	int lastDAta = 0;

	// newInstance constructor for creating fragment with arguments
	public static TouchFragment newInstance(int page, String title) {
		TouchFragment touchFragment = new TouchFragment();
		Bundle args = new Bundle();
		args.putInt("someInt", page);
		args.putString("someTitle", title);
		touchFragment.setArguments(args);

		return touchFragment;

	}

	// Store instance variables based on arguments passed
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		vib = (Vibrator) this.getActivity().getSystemService(
				Context.VIBRATOR_SERVICE);
		Fragment1.listener = TouchFragment.this;
		((DashBoardActivity) getActivity()).setCustomButtonListner(this);
	}

	// Inflate the view for the fragment based on layout XML
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dashboard_touch_screen,
				container, false);
		// final PieGraph pg = (PieGraph) view.findViewById(R.id.piegraph);
		mHoloCircularProgressBar = (HoloCircularProgressBar) view
				.findViewById(R.id.holoCircularProgressBar);

		// ArrayList<PieSlice> slices = new ArrayList<PieSlice>();
		// PieSlice slice = new PieSlice();
		parentName = (TextView) view.findViewById(R.id.parentNameTV);
		parentImage = (ImageView) view.findViewById(R.id.profile_pic);
		parentBotton = (TextView) view.findViewById(R.id.parentBottonTouch);
		// ((DashBoardActivity) getActivity()).setCustomButtonListner(this);
		next = (ImageButton) view.findViewById(R.id.imageButton2);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((Fragment1) getParentFragment()).getNextItem(1);

			}
		});
		parentImage.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (parent != null) {
					if (parent.getIsPendingTouch()) {
						vib.vibrate(500);
						SharedPreferences pendingTouch = getActivity()
								.getSharedPreferences("pendingTouch", 0);

						if (parent.getIsTouchMedia()) {
							((DashBoardActivity) getActivity()).goToKinbook();

						}
						String array = pendingTouch.getString("touch", null);
						try {
							JSONArray arrayObj = new JSONArray(array);
							if (arrayObj != null && arrayObj.length() > 0) {
								for (int i = 0; i < arrayObj.length(); i++) {
									JSONObject obj = arrayObj.getJSONObject(i);
									if (obj.getString("id").equalsIgnoreCase(
											parent.getParentId())) {
										arrayObj.remove(i);
									}
								}

							}
							Editor tokenedit = pendingTouch.edit();
							tokenedit.putString("touch", arrayObj + "");
							tokenedit.commit();
							parent.setIsPendingTouch(false);
							setText();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				return false;

			}
		});
		return view;
	}

	/**
	 * Animate.
	 *
	 * @param progressBar
	 *            the progress bar
	 * @param listener
	 *            the listener
	 */
	// private void animate(final HoloCircularProgressBar progressBar,
	// final AnimatorListener listener) {
	// final float progress = (float) (Math.random() * 2);
	// int duration = 3000;
	// animate(progressBar, listener, progress, duration);
	// }

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
		// animate(mHoloCircularProgressBar, null, 0.05f, 3000);
		// Toast.makeText(getActivity(), "Resume", Toast.LENGTH_SHORT).show();
		mHoloCircularProgressBar.setProgress(0.0f);
		animate(mHoloCircularProgressBar, null, (float) (1.0f / 30), 1000);
		SetImage();
		//
		// ImageLoader imageLoader = new ImageLoader(getActivity());
		// parent = ((DashBoardActivity) getActivity()).getSelectedParent();
		// Log.d("Parent", "" + parent);
		// if (parent != null) {
		// imageLoader.DisplayImage(serverPath + parent.getParentId()
		// + ".jpeg", R.drawable.ic_user_image, parentImage);
		// }
	}

	@Override
	public void fragmentBecameVisible() {
		// TODO Auto-generated method stub
		mHoloCircularProgressBar.setProgress(0.0f);
		animate(mHoloCircularProgressBar, null, (float) (1.0f / 30), 1000);

	}

	private void SetImage() {
		// TODO Auto-generated method stub
		ImageLoader imageLoader = new ImageLoader(getActivity());
		parent = ((DashBoardActivity) getActivity()).getSelectedParent();

		Log.d("Parent", "" + parent);
		if (parent != null) {
			getCurrent(parent.getParentId());

			String cut = parent.getParentName().substring(0, 1).toLowerCase();
			resID = getActivity().getResources().getIdentifier(cut, "drawable",
					getActivity().getPackageName());
			Log.d("cut", cut + " " + resID);
			imageLoader.DisplayImage(serverPath + parent.getParentId()
					+ ".jpeg", resID, parentImage);
			setText();

		}

	}

	@Override
	public void onButtonClickListner(int position, String value,
			Boolean isAccept) {
		// TODO Auto-generated method stub
		SetImage();
	}

	private void setText() {
		// TODO Auto-generated method stub
		if (parent.getIsPendingTouch()) {
			parentName.setText(parent.getParentName().substring(0, 1)
					.toUpperCase()
					+ parent.getParentName().substring(1)
					+ " is thinking of you");
			if (parent.getIsMale()) {
				parentBotton.setText("Tap above for a touch from him");
			} else {
				parentBotton.setText("Tap above for a touch from her");
			}
		} else {
			if (lastDAta > 1) {
				parentName.setText(parent.getParentName().substring(0, 1)
						.toUpperCase()
						+ parent.getParentName().substring(1) + " is ok today");
			} else {
				parentName.setText(parent.getParentName().substring(0, 1)
						.toUpperCase()
						+ parent.getParentName().substring(1)
						+ " is little low today");
			}
			if (touchTime != null && touchTime.equalsIgnoreCase("1"))
				parentBotton.setText("Last touch was " + touchTime
						+ " hour ago");
			else {
				if (!touchTime.equalsIgnoreCase("")
						&& !touchTime.equalsIgnoreCase("just now")
						&& Integer.parseInt(touchTime) > 48) {
					parentName.setText("You should probably call "
							+ parent.getParentName());
					parentBotton.setText("Last kin contact was 2 days ago");
				} else {
					if (touchTime.equalsIgnoreCase("just now")) {
						parentBotton.setText("Last touch was " + touchTime);
					} else {
						parentBotton.setText("Last touch was " + touchTime
								+ " hours ago");
					}
				}
			}
		}
	}

	public void getCurrent(String id) {
		Log.d("id ", id);
		JsonObjectRequest req = new JsonObjectRequest(Method.GET,
				getResources().getString(R.string.url) + "/activity/current/"
						+ id, null, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject responseArray) {
						// TODO Auto-generated method stub
						Log.d("Response Array Current",
								responseArray.toString());
						if (getActivity() != null) {
							setSlices(responseArray);
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

	@SuppressLint("SimpleDateFormat")
	public void setSlices(JSONObject slicesObject) {
		ArrayList<PieSlice> slices = new ArrayList<PieSlice>();
		final Resources resources = getResources();
		try {
			JSONObject sliceObj = slicesObject
					.getJSONObject("current_month_activity");
			JSONObject lastTouchObj = slicesObject.optJSONObject("last_touch");
			if (lastTouchObj != null) {
				String createdTime = lastTouchObj.optString("createdAt");

				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss");
				sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
				SimpleDateFormat output = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date d = null;
				try {
					d = sdf.parse(createdTime);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int updateHour = d.getHours();
				Date currentDate = new Date();
				int currentHour = currentDate.getHours();
				int diff = currentHour - updateHour;
				if (diff < 1) {
					touchTime = "just now";
				} else {
					touchTime = diff + "";
				}
			}

			Log.d("SLicce object ", sliceObj.toString());
			Iterator<String> iter = sliceObj.keys();
			String key = "";
			while (iter.hasNext()) {
				key = iter.next();
				PieSlice slice = new PieSlice();
				int value = sliceObj.optInt(key, 0);
				if (value == 1) {
					slice.setColor(resources.getColor(R.color.daily_prog_done));
				} else {
					slice.setColor(resources.getColor(R.color.daily_prog_left));
				}
				slices.add(slice);

			}
			lastDAta = sliceObj.optInt(key, 0);
			setText();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		mHoloCircularProgressBar.setSlices(slices);
		mHoloCircularProgressBar.setProgress(0.0f);
		animate(mHoloCircularProgressBar, null, (float) (1.0f / 30), 1000);
	}

	@Override
	public void sendTouchCLicked(Boolean isFirstTime) {
		// TODO Auto-generated method stub

		if (isFirstTime) {
			backData = parentBotton.getText().toString();
			topData = parentName.getText().toString();
			parentBotton.setText("Share a moment with video?");
			parentName.setText("Sending a touch...");
		} else {
			parentBotton.setText(backData);
			parentName.setText(topData);
		}
	}
}
