package com.touchKin.touchkinapp.custom;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	private SurfaceHolder mHolder;

	private Camera mCamera;
	private boolean cameraConfigured = false;
	private Context mContext;
	private int mCameraId;
	List<Size> mSupportedPreviewSizes;
	Size mPreviewSize;

	public CameraPreview(Context context, Camera camera) {
		super(context);
		mCamera = camera;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mContext = context;
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {

			if (mCamera == null) {
				Camera.Parameters parameters = mCamera.getParameters();
				if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
					parameters.set("orientation", "portrait");
					mCamera.setDisplayOrientation(90);
					parameters.setRotation(90);
				} else {
					// This is an undocumented although widely known feature
					parameters.set("orientation", "landscape");
					// For Android 2.2 and above
					mCamera.setDisplayOrientation(0);
					// Uncomment for Android 2.0 and above
					parameters.setRotation(0);
				}

				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
			}
		} catch (IOException e) {
			Log.d(VIEW_LOG_TAG,
					"Error setting camera preview: " + e.getMessage());
		}
	}

	public void refreshCamera(Camera camera, int cameraid) {
		if (mHolder.getSurface() == null) {
			return;
		}
		try {
			mCamera = camera;
			mCamera.stopPreview();
			setCamera(camera);
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
			setCameraDisplayOrientation((Activity) mContext, cameraid, camera);
		} catch (Exception e) {
			Log.d(VIEW_LOG_TAG,
					"Error starting camera preview: " + e.getMessage());
		}
	}

	public static void setCameraDisplayOrientation(Activity activity,
			int cameraId, android.hardware.Camera camera) {

		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);

		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		initPreview(w, h);
		refreshCamera(mCamera, mCameraId);
	}

	private void initPreview(int w, int h) {
		if (mCamera != null && mHolder.getSurface() != null) {
			try {
				mCamera.setPreviewDisplay(mHolder);
			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
			}
			if (!cameraConfigured) {
				Camera.Parameters parameters = mCamera.getParameters();
				List<String> focusModes = parameters.getSupportedFocusModes();
				if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
					parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				}
				parameters.setPreviewSize(mPreviewSize.width,
						mPreviewSize.height);
				mCamera.setParameters(parameters);
				cameraConfigured = true;
			}
		}
	}

	public void setCamera(Camera camera) {
		mCamera = camera;
		mSupportedPreviewSizes = mCamera.getParameters()
				.getSupportedPreviewSizes();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.release();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = resolveSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		setMeasuredDimension(width, height);

		if (mSupportedPreviewSizes != null) {
			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width,
					height);
		}
	}

	private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w,
			int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) h / w;

		if (sizes == null)
			return null;

		Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		for (Camera.Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}
}