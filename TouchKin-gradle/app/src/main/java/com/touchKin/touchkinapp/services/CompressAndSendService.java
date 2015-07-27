package com.touchKin.touchkinapp.services;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.webkit.MimeTypeMap;
import com.netcompss.ffmpeg4android.GeneralUtils;
import com.netcompss.ffmpeg4android.Prefs;
import com.netcompss.loader.LoadJNI;
import com.touchKin.touchkinapp.SendTouchPreview;
import com.touchKin.touchkinapp.custom.AndroidMultiPartEntity;
import com.touchKin.touchkinapp.custom.AndroidMultiPartEntity.ProgressListener;
import com.touchKin.touchkinapp.model.AppController;
import com.touchKin.touckinapp.R;

public class CompressAndSendService extends Service {

	NotificationManager mNotifyManager;
	Notification.Builder mBuilder;
	int id = 1;
	private File videoDirectory = null;
	String videoFolder = null, vkLogPath = null, workFolder = null;
	String videoPath = null;
	private boolean commandValidationFailedFlag = false;
	Context _act;
	Intent intent;
	long totalSize = 0;
	String token;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	protected File getVideoPath() {
		File dir = getVideoDirectory();

		dir.mkdirs();

		return (new File(dir, getVideoFilename()));
	}

	protected File getVideoDirectory() {
		if (videoDirectory == null) {
			initVideoDirectory();
		}

		return (videoDirectory);
	}

	private void initVideoDirectory() {
		videoDirectory = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
	}

	protected String getVideoFilename() {
		String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());

		return ("Video_" + ts + ".mp4");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		this.intent = intent;
		videoPath = intent.getExtras().getString("videoPath");
		token = intent.getExtras().getString("token");
		videoFolder = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		workFolder = getApplicationContext().getFilesDir().getAbsolutePath()
				+ "/";
		vkLogPath = workFolder + "vk.log";
		GeneralUtils.copyLicenseFromAssetsToSDIfNeeded((Activity) _act,
				workFolder);
		Log.d("path", videoFolder);
		new TranscdingBackground(getApplicationContext()).execute();
	}

	String val;

	public class TranscdingBackground extends
			AsyncTask<String, Integer, Integer> {
		public TranscdingBackground(Context context) {
			_act = context;
		}

		@Override
		protected void onPreExecute() {
			mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mBuilder = new Notification.Builder(getApplicationContext());
			mBuilder.setContentTitle("Touchkin")
					.setContentText("Wait for a sec")
					.setSmallIcon(R.drawable.ic_launcher)
					.setProgress(0, 0, true);
			mNotifyManager.notify(id, mBuilder.build());

		}

		protected Integer doInBackground(String... paths) {
			Log.i(Prefs.TAG, "doInBackground started...");

			PowerManager powerManager = (PowerManager) _act
					.getSystemService(Activity.POWER_SERVICE);
			WakeLock wakeLock = powerManager.newWakeLock(
					PowerManager.PARTIAL_WAKE_LOCK, "VK_LOCK");
			Log.d(Prefs.TAG, "Acquire wake lock");
			wakeLock.acquire();
			val = getVideoPath().toString();
			Log.d("value", val);

			String[] commandStr = { "ffmpeg", "-y", "-i", videoPath, "-strict",
					"experimental", "-r", "30", "-ac", "2", "-ar", "22050",
					"-b", "2000k", "-preset", "ultrafast", val };
			Log.d("cmd", "");

			LoadJNI vk = new LoadJNI();
			try {

				// complex command
				vk.run(commandStr, workFolder, _act);
				GeneralUtils.copyFileToFolder(vkLogPath, videoFolder);
			} catch (Throwable e) {
				Log.e(Prefs.TAG, "vk run exeption.", e);
			} finally {
				if (wakeLock.isHeld())
					wakeLock.release();
				else {
					Log.i(Prefs.TAG,
							"Wake lock is already released, doing nothing");
				}
			}
			Log.i(Prefs.TAG, "doInBackground finished");
			return Integer.valueOf(0);
		}

		protected void onProgressUpdate(Integer... progress) {
		}

		@Override
		protected void onCancelled() {
			Log.i(Prefs.TAG, "onCancelled");
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			Log.i(Prefs.TAG, "onPostExecute");
			// Removes the progress bar
			mBuilder.setContentText("").setProgress(0, 0, false);

			mNotifyManager.notify(id, mBuilder.build());
			sendMedia(SendTouchPreview.type);
			super.onPostExecute(result);

			// finished Toast
			String rc = null;
			if (commandValidationFailedFlag) {
				rc = "Command Vaidation Failed";
			} else {
				rc = GeneralUtils.getReturnCodeFromLog(vkLogPath);
			}
		}
	}

	public void sendMedia(int type) {

		new ImageUploadTask(this).execute();

	}

	class ImageUploadTask extends AsyncTask<Void, Integer, String> {

		Context context;

		public ImageUploadTask(Context context) {
			this.context = context;
		}

		@Override
		protected String doInBackground(Void... unsued) {
			return uploadFile();
		}

		@SuppressWarnings("deprecation")
		private String uploadFile() {
			String responseString = null;

			HttpClient httpclient = new DefaultHttpClient();
			AppController.mHttpClient.getParams().setParameter(
					CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

			HttpClient httpClient = AppController.mHttpClient;
			HttpPost httppost = new HttpPost(
					"http://54.69.183.186:1340/kinbook/message/add");
			httppost.setHeader("Authorization", "Bearer " + token);
			try {
				AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
						new ProgressListener() {

							@Override
							public void transferred(long num) {
								publishProgress((int) ((num / (float) totalSize) * 100));
							}
						});

				File sourceFile = new File(val);
				entity.addPart("shared_with",
						new StringBody(SendTouchPreview.getCheckedParentId()));
//				entity.addPart("message", new StringBody(
//						SendTouchPreview.sendmessage.getText().toString()));
				ContentBody cbFile = new FileBody(sourceFile,
						ContentType.create(getMimeType(sourceFile
								.getAbsolutePath())), sourceFile.getName());
				entity.addPart("media", cbFile);

				// entity.addPart("media", fileBody);
				Log.d("ParentId", SendTouchPreview.getCheckedParentId()
						.toString());

				totalSize = entity.getContentLength();
				httppost.setEntity(entity);

				// Making server call
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity r_entity = response.getEntity();

				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					// Server response
					responseString = EntityUtils.toString(r_entity);
				} else {
					responseString = "Error occurred! Http Status Code: "
							+ statusCode;
				}

			} catch (ClientProtocolException e) {
				responseString = e.toString();
			} catch (IOException e) {
				responseString = e.toString();
			}

			return responseString;

		}

		@Override
		protected void onProgressUpdate(Integer... unsued) {

			if (unsued[0] != 0 && unsued[0] < 100) {

				mBuilder.setProgress(100, unsued[0], false).setContentText("Sending your touch "+
						String.valueOf(unsued[0])+"% complete");
				Log.i(Prefs.TAG, "setting progress notification: " + unsued[0]);
				try {
					mNotifyManager.notify(id, mBuilder.build());
				} catch (Exception e) {
					Log.i(Prefs.TAG, "Android 2.3 or below? " + e.getMessage());
				}
			} else if (unsued[0] == 100) {
				Log.i(Prefs.TAG,
						"==== progress is 100, exiting Progress update thread");

				mBuilder.setContentText("Sent")
				// Removes the progress bar
						.setProgress(0, 0, false);
				try {
					mNotifyManager.notify(id, mBuilder.build());
				} catch (Exception e) {
					Log.i(Prefs.TAG, "Android 2.3 or below? " + e.getMessage());
				}

			}
		}

		@Override
		protected void onPostExecute(String sResponse) {
			stopService(intent);
		}

		@Override
		protected void onPreExecute() {
		}

	}

	public static String getMimeType(String url) {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
		}
		Log.d("Type", type);
		return type;
	}
}
