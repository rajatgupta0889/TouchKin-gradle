package com.touchKin.touchkinapp.adapter;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.touchKin.touchkinapp.DashBoardActivity;
import com.touchKin.touchkinapp.Fragment2;
import com.touchKin.touchkinapp.Interface.ViewPagerListener;
import com.touchKin.touchkinapp.custom.HoloCircularProgressBar;
import com.touchKin.touchkinapp.custom.ImageLoader;
import com.touchKin.touchkinapp.custom.RoundedImageView;
import com.touchKin.touchkinapp.model.ParentListModel;
import com.touchKin.touckinapp.R;

public class MyDashbaordAdapter extends PagerAdapter implements
		ViewPagerListener {
	Context context;
	List<ParentListModel> parentList;
	LayoutInflater inflater;
	String serverPath = "https://s3-ap-southeast-1.amazonaws.com/touchkin-dev/avatars/";
	Vibrator vib;
	Boolean isFirst = false;
	TextView parentTop, parentBottom;

	public MyDashbaordAdapter(Context context, List<ParentListModel> parentList) {
		this.parentList = parentList;
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		Fragment2.listener = MyDashbaordAdapter.this;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager) container).removeView((LinearLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		// TODO Auto-generated method stub

		RoundedImageView imageView;
		final ParentListModel parent = parentList.get(position);
		View view = inflater.inflate(R.layout.dashboard_touch_screen,
				container, false);
		view.setTag(position);

		parentTop = (TextView) view.findViewById(R.id.parentNameTV);
		parentBottom = (TextView) view.findViewById(R.id.parentBottonTouch);
		Date d = new Date();
		HoloCircularProgressBar bar = (HoloCircularProgressBar) view
				.findViewById(R.id.holoCircularProgressBar);
		bar.setVisibility(View.INVISIBLE);
		imageView = (RoundedImageView) view.findViewById(R.id.profile_pic);
		// if (position + 1 != parentList.size()) {
		if (parent.getIsMale() != null) {
			if (parent.getIsMale()) {
				if (!parent.getIsPendingTouch()) {
					parentTop
							.setText("It is "
									+ (d.getHours() > 12 ? d.getHours() - 12
											: d.getHours())
									+ ":"
									+ (d.getMinutes() < 10 ? "0"
											+ d.getMinutes() : d.getMinutes())
									+ (d.getHours() > 12 ? " pm" : " am")
									+ " for "
									+ parent.getParentName().substring(0, 1)
											.toUpperCase()
									+ parent.getParentName().substring(1)
									+ " in India");
					parentBottom.setText("Send him a touch now?");
				} else {
					parentTop.setText(parent.getParentName().substring(0, 1)
							.toUpperCase()
							+ parent.getParentName().substring(1)
							+ " is thinking of you");
					parentBottom.setText("Tap above for a touch from him.");
				}
			} else {
				if (!parent.getIsPendingTouch()) {
					parentTop
							.setText("It is "
									+ (d.getHours() > 12 ? d.getHours() - 12
											: d.getHours())
									+ ":"
									+ (d.getMinutes() < 10 ? "0"
											+ d.getMinutes() : d.getMinutes())
									+ (d.getHours() > 12 ? " pm" : " am")
									+ " for "
									+ parent.getParentName().substring(0, 1)
											.toUpperCase()
									+ parent.getParentName().substring(1)
									+ " in India");
					parentBottom.setText("Send her a touch now?");
				} else {
					parentTop.setText(parent.getParentName().substring(0, 1)
							.toUpperCase()
							+ parent.getParentName().substring(1)
							+ " is thinking of you");
					parentBottom.setText("Tap above for a touch from her.");
				}
			}
		} else {
			parentTop.setText("It's " + d.getHours() + ":" + d.getMinutes()
					+ " for "
					+ parent.getParentName().substring(0, 1).toUpperCase()
					+ parent.getParentName().substring(1) + " in India");
			parentBottom.setText("Send them a touch now?");
		}

		ImageLoader imageLoader = new ImageLoader(context);
		String name = parent.getParentName();
		int resID = 0;
		if (!name.equalsIgnoreCase("")) {
			String cut = name.substring(0, 1).toLowerCase();
			resID = context.getResources().getIdentifier(cut, "drawable",
					context.getPackageName());
			Log.d("cut", cut + " " + resID);
		}
		imageLoader.DisplayImage(serverPath + parent.getParentId() + ".jpeg",
				resID, imageView);
		((ViewPager) container).addView(view);
		imageView.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (parent.getIsPendingTouch()) {
					vib.vibrate(500);
					if (parent.getIsTouchMedia()) {
						((DashBoardActivity) context).goToKinbook();
					}
					SharedPreferences pendingTouch = context
							.getSharedPreferences("pendingTouch", 0);
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
						notifyDataSetChanged();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				return true;
			}
		});
		// } else {
		// imageView.setImageResource(R.drawable.add_kin);
		// }
		return view;

	}

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return super.saveState();
	}

	public int getCount() {
		return parentList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == ((LinearLayout) arg1);

	}

	@Override
	public void sendTouchCLicked(Boolean isFirstTime) {
		// TODO Auto-generated method stub
		if (isFirstTime) {
			isFirstTime = true;
			notifyDataSetChanged();
		} else {
			isFirstTime = false;
			notifyDataSetChanged();
		}
	}

}
