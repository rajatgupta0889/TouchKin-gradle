package com.touchKin.touchkinapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraFragment;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraUtils;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.google.android.gms.tagmanager.Container;
import com.touchKin.touckinapp.R;

public class DemoCameraFragment extends CameraFragment {
	private static final String KEY_USE_FFC = "com.commonsware.cwac.camera.demo.USE_FFC";
	public static final int MEDIA_TYPE_IMAGE = 1;

	static DemoCameraFragment newInstance(boolean useFFC) {
		DemoCameraFragment f = new DemoCameraFragment();
		Bundle args = new Bundle();

		args.putBoolean(KEY_USE_FFC, useFFC);
		f.setArguments(args);

		return (f);
	}public DemoCameraFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		SimpleCameraHost.Builder builder = new SimpleCameraHost.Builder(
				new DemoCameraHost(getActivity()));

		setHost(builder.useFullBleedPreview(true).build());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View cameraView = super.onCreateView(inflater, container,
				savedInstanceState);
		View results = inflater.inflate(R.layout.fragment, container, false);

		((ViewGroup) results.findViewById(R.id.camera)).addView(cameraView);
		return (results);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return (super.onOptionsItemSelected(item));
	}

	Contract getContract() {
		return ((Contract) getActivity());
	}

	void takeSimplePicture() {
		PictureTransaction xact = new PictureTransaction(getHost());
		takePicture(xact);

	}

	interface Contract {
		boolean isSingleShotMode();

		void setSingleShotMode(boolean mode);
	}

	class DemoCameraHost extends SimpleCameraHost {
		boolean supportsFaces = false;

		public DemoCameraHost(Context _ctxt) {
			super(_ctxt);
		}

		
		@Override
		public boolean useFrontFacingCamera() {
			if (getArguments() == null) {
				return (false);
			}

			return (getArguments().getBoolean(KEY_USE_FFC));
		}
		
		@Override
		protected File getVideoPath() {
			// TODO Auto-generated method stub
			((SendTouchActivity) getActivity()).setPath(super.getVideoPath());
			return super.getVideoPath();
		}

		@Override
		protected File getPhotoPath() {
			// TODO Auto-generated method stub
			((SendTouchActivity) getActivity()).setImagepath(super
					.getPhotoPath());
			return super.getPhotoPath();
		}

		@Override
		public void saveImage(PictureTransaction xact, byte[] image) {
			// TODO Auto-generated method stub
			super.saveImage(xact, image);
//			Intent intent = new Intent(getActivity(), SendTouchPreview.class);
//			intent.putExtra("Media_Type", MEDIA_TYPE_IMAGE);
//			intent.putExtra(MediaStore.EXTRA_OUTPUT,
//					Uri.fromFile(getPhotoPath()));
//
//			startActivity(intent);
		}
		
		@Override
		public void onCameraFail(CameraHost.FailureReason reason) {
			super.onCameraFail(reason);

			Toast.makeText(getActivity(),
					"Sorry, but you cannot use the camera now!",
					Toast.LENGTH_LONG).show();
		}
	}

}