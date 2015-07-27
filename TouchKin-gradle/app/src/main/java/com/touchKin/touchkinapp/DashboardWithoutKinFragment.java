package com.touchKin.touchkinapp;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.touchKin.touchkinapp.Interface.FragmentInterface;
import com.touchKin.touchkinapp.custom.HoloCircularProgressBar;
import com.touchKin.touchkinapp.custom.PieSlice;
import com.touchKin.touckinapp.R;

public class DashboardWithoutKinFragment extends Fragment implements
		FragmentInterface {
	private HoloCircularProgressBar mHoloCircularProgressBar;
	private ObjectAnimator mProgressBarAnimator;

	// newInstance constructor for creating fragment with arguments
	public static DashboardWithoutKinFragment newInstance(int page, String title) {
		DashboardWithoutKinFragment DashboardWithoutKinFragment = new DashboardWithoutKinFragment();
		Bundle args = new Bundle();
		args.putInt("someInt", page);
		args.putString("someTitle", title);
		DashboardWithoutKinFragment.setArguments(args);
		return DashboardWithoutKinFragment;
	}

	// Store instance variables based on arguments passed
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	// Inflate the view for the fragment based on layout XML
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dashboard_withoutkin, container,
				false);
		final Resources resources = getResources();
		// final PieGraph pg = (PieGraph) view.findViewById(R.id.piegraph);
		mHoloCircularProgressBar = (HoloCircularProgressBar) view
				.findViewById(R.id.holoCircularProgressBar);
		ArrayList<PieSlice> slices = new ArrayList<PieSlice>();
		PieSlice slice = new PieSlice();

		slice.setColor(resources.getColor(R.color.daily_prog_done));
		slices.add(slice);
		// slice.setValue(1);
		// slice.setTitle("first");
		// pg.addSlice(slice);
		slice = new PieSlice();
		slice.setColor(resources.getColor(R.color.daily_prog_left));
		slices.add(slice);
		// slice.setValue(1);
		// pg.addSlice(slice);
		slice = new PieSlice();
		slice.setColor(resources.getColor(R.color.daily_prog_done));
		slices.add(slice);
		// slice.setValue(1);
		// pg.addSlice(slice);
		slice = new PieSlice();
		slice.setColor(resources.getColor(R.color.daily_prog_done));
		slices.add(slice);
		// slice.setValue(1);
		// pg.addSlice(slice);

		slice = new PieSlice();
		slice.setColor(resources.getColor(R.color.daily_prog_done));
		slices.add(slice);
		// slice.setValue(1);
		// pg.addSlice(slice);

		slice = new PieSlice();
		slice.setColor(resources.getColor(R.color.daily_prog_done));
		slices.add(slice);
		// slice.setValue(1);
		// pg.addSlice(slice);

		slice = new PieSlice();
		slice.setColor(resources.getColor(R.color.daily_prog_done));
		slices.add(slice);
		// slice.setValue(1);
		// pg.addSlice(slice);

		slice = new PieSlice();
		slice.setColor(resources.getColor(R.color.daily_prog_done));
		slices.add(slice);
		// slice.setValue(1);
		slice = new PieSlice();
		slice.setColor(resources.getColor(R.color.daily_prog_done));
		slices.add(slice);
		// slice.setValue(1);
		// pg.addSlice(slice);

		slice = new PieSlice();
		slice.setColor(resources.getColor(R.color.daily_prog_done));
		slices.add(slice);
		// slice.setValue(1);
		// pg.addSlice(slice);

		slice = new PieSlice();
		slice.setColor(resources.getColor(R.color.daily_prog_done));
		slices.add(slice);
		// slice.setValue(1);
		// pg.addSlice(slice);

		slice = new PieSlice();
		slice.setColor(resources.getColor(R.color.daily_prog_left));
		slices.add(slice);
		// slice.setValue(1);
		// pg.addSlice(slice);

		slice = new PieSlice();
		slice.setColor(resources.getColor(R.color.daily_prog_done));
		slices.add(slice);
		// slice.setValue(1);
		// pg.addSlice(slice);

		slice = new PieSlice();
		slice.setColor(resources.getColor(R.color.daily_prog_left));
		slices.add(slice);
		// slice.setValue(1);
		// pg.addSlice(slice);

		slice = new PieSlice();
		slice.setColor(resources.getColor(R.color.daily_prog_done));
		slices.add(slice);
		// slice.setValue(1);
		// pg.addSlice(slice);

		slice = new PieSlice();
		slice.setColor(resources.getColor(R.color.daily_prog_left));
		slices.add(slice);
		// slice.setValue(15);
		//
		// pg.addSlice(slice);
		mHoloCircularProgressBar.setSlices(slices);
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
		mHoloCircularProgressBar.setProgress(0.0f);
		// animate(mHoloCircularProgressBar, null, 0.05f, 3000);
		// Toast.makeText(getActivity(), "Resume", Toast.LENGTH_SHORT).show();
		mHoloCircularProgressBar.setProgress(0.0f);
		animate(mHoloCircularProgressBar, null, (float) (1.0f / 30), 1000);
		super.onResume();
	}

	@Override
	public void fragmentBecameVisible() {
		// TODO Auto-generated method stub
		mHoloCircularProgressBar.setProgress(0.0f);
		animate(mHoloCircularProgressBar, null, (float) (1.0f / 30), 1000);

	}

}
