package com.touchKin.touchkinapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.touchKin.touchkinapp.Interface.ButtonClickListener;
import com.touchKin.touchkinapp.Interface.ViewPagerListener;
import com.touchKin.touchkinapp.adapter.MyDashbaordAdapter;
import com.touchKin.touchkinapp.model.AppController;
import com.touchKin.touchkinapp.model.ParentListModel;
import com.touchKin.touckinapp.R;

public class Fragment2 extends Fragment implements OnClickListener,
		AnimationListener {
	List<ParentListModel> list;
	ViewPager myPager;
	ParentListModel parent;
	Vibrator vib;
	TextView sendTouch, getService, touch1;
	TextView sendTouchTextview;
	Boolean withoutMsg = false;
	Boolean isSendTouchAlreadyClicked = false;
	public static ViewPagerListener listener;
	MyDashbaordAdapter adapter;

	String prevtop, prevBottom;

	Animation animMove1, animMove2, animMove3, animMove4;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View v = inflater.inflate(R.layout.mydashboard, null);
		getActivity();
		vib = (Vibrator) this.getActivity().getSystemService(
				Context.VIBRATOR_SERVICE);
		myPager = (ViewPager) v.findViewById(R.id.myPager);
		list = new ArrayList<ParentListModel>();
		// ((DashBoardActivity) getActivity()).setCustomButtonListner(this);
		sendTouch = (TextView) v.findViewById(R.id.sendTouch);
		getService = (TextView) v.findViewById(R.id.getService);
		touch1 = (TextView) v.findViewById(R.id.touch);
		sendTouchTextview = (TextView) v.findViewById(R.id.textToSendTouch);

		return v;
	}

	// @Override
	// public void onButtonClickListner(int position, String value,
	// Boolean isAccept) {
	// // TODO Auto-generated method stub
	//
	//
	// }

	@SuppressWarnings("deprecation")
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		list = ((DashBoardActivity) getActivity()).getParentList();
		if (list != null && list.size() > 0) {
			adapter = new MyDashbaordAdapter(getActivity(), list);
			myPager.setAdapter(adapter);
		}
		animMove1 = AnimationUtils.loadAnimation(getActivity(), R.anim.move1);
		animMove3 = AnimationUtils.loadAnimation(getActivity(), R.anim.move3);
		animMove2 = AnimationUtils.loadAnimation(getActivity(), R.anim.move2);
		animMove4 = AnimationUtils.loadAnimation(getActivity(), R.anim.move4);
		animMove1.setAnimationListener(this);
		animMove2.setAnimationListener(this);
		animMove4.setAnimationListener(this);
		animMove3.setAnimationListener(this);
		sendTouch.setVisibility(View.GONE);
		getService.setVisibility(View.GONE);
		sendTouch.setOnClickListener(this);
		getService.setOnClickListener(this);
		touch1.setOnClickListener(this);
		sendTouchTextview.setOnClickListener(this);
		list = ((DashBoardActivity) getActivity()).getParentList();
		Log.d("Parent lIst", list + "");
		adapter = new MyDashbaordAdapter(getActivity(), list);

		myPager.setAdapter(adapter);
		myPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				sendTouch.setText("Send a Touch");
				sendTouch.setCompoundDrawablesWithIntrinsicBounds(0,
						R.drawable.ic_icon_send_touch, 0, 0);
				isSendTouchAlreadyClicked = false;

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.touch:
			touch1.startAnimation(animMove1);

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					sendTouch.startAnimation(animMove3);
				}
			}, 300);

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {

					sendTouch.startAnimation(animMove4);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							touch1.setVisibility(View.INVISIBLE);
							getService.startAnimation(animMove2);
						}
					}, 300);

				}
			}, 7000);

			break;
		case R.id.sendTouch:
			if (!isSendTouchAlreadyClicked) {
				vib.vibrate(500);
				// sendTouch.setText("Add a video");
				// sendTouch.setCompoundDrawablesWithIntrinsicBounds(0,
				// R.drawable.video_cam, 0, 0);
				TextView top = ((TextView) myPager.findViewWithTag(
						myPager.getCurrentItem()).findViewById(
						R.id.parentNameTV));
				prevtop = top.getText().toString();
				top.setText("Sending a touch...");
				TextView bottom = ((TextView) myPager.findViewWithTag(
						myPager.getCurrentItem()).findViewById(
						R.id.parentBottonTouch));
				prevBottom = bottom.getText().toString();
				bottom.setText("Share a moment with video?");

				listener.sendTouchCLicked(true);
				isSendTouchAlreadyClicked = true;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// This method will be executed once the timer is
						// over
						// Start your app main activity
						if (!withoutMsg)
							sendTouchWithoutMessage();
						isSendTouchAlreadyClicked = false;
						((TextView) myPager.findViewWithTag(
								myPager.getCurrentItem()).findViewById(
								R.id.parentBottonTouch)).setText(prevBottom);
						((TextView) myPager.findViewWithTag(
								myPager.getCurrentItem()).findViewById(
								R.id.parentNameTV)).setText(prevtop);
						// sendTouch.setText("Send a Touch");
						// sendTouch.setCompoundDrawablesWithIntrinsicBounds(
						// 0, R.drawable.ic_icon_send_touch, 0, 0);
						listener.sendTouchCLicked(false);

					}
				}, 10000);
			} else {
				withoutMsg = true;
				sendTouch();
				((TextView) myPager.findViewWithTag(myPager.getCurrentItem())
						.findViewById(R.id.parentBottonTouch))
						.setText(prevBottom);
				((TextView) myPager.findViewWithTag(myPager.getCurrentItem())
						.findViewById(R.id.parentNameTV)).setText(prevtop);
			}
			break;
		case R.id.getService:
			if (list != null)
				parent = list.get(myPager.getCurrentItem());

			if (parent != null) {

				Intent callIntent = new Intent(Intent.ACTION_DIAL);
				callIntent
						.setData(Uri.parse("tel:" + parent.getMobilenumber()));
				Intent chooser = Intent.createChooser(callIntent, "Call using");
				startActivity(chooser);

			} else {
				Toast.makeText(getActivity(),
						"Please Select Your parent to call", Toast.LENGTH_SHORT)
						.show();
			}

			break;
		case R.id.textToSendTouch:
			withoutMsg = true;
			sendTouch();
			break;
		default:
			break;
		}
	}

	private void sendTouch() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), SendTouchActivity.class);
		ParentListModel model = parent = list.get(myPager.getCurrentItem());
		intent.putExtra("userId", model.getParentId());
		intent.putExtra("token", ((DashBoardActivity) getActivity()).getToken());
		startActivity(intent);
	}

	private void sendTouchWithoutMessage() {
		JSONObject params = new JSONObject();
		try {
			params.put("receivingUserId", list.get(myPager.getCurrentItem())
					.getParentId());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
				"http://54.69.183.186:1340/touch/add", params,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("Activity Result", response.toString());
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
				headers.put("Authorization", "Bearer "
						+ ((DashBoardActivity) getActivity()).getToken());

				return headers;

			};
		};
		AppController.getInstance().addToRequestQueue(req);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if (animation == animMove1) {
			touch1.setVisibility(View.GONE);
			sendTouch.setVisibility(View.VISIBLE);
			getService.setVisibility(View.VISIBLE);
			Log.d("Animation1", animation.toString());
		}
		if (animation == animMove3) {
			Log.d("Animation3", animation.toString());
		}

		if (animation == animMove4) {
			sendTouch.setVisibility(View.GONE);
			getService.setVisibility(View.GONE);
			Log.d("Animation4", animation.toString());
		}
		if (animation == animMove2) {
			getService.setVisibility(View.GONE);
			touch1.setVisibility(View.VISIBLE);
			Log.d("Animation2", animation.toString());
		}

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

}
