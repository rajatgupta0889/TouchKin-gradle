package com.touchKin.touchkinapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.layout;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.netcompss.ffmpeg4android.GeneralUtils;
import com.touchKin.touchkinapp.adapter.MyAdapter.ViewHolder.IMyViewHolderClicks;
import com.touchKin.touchkinapp.adapter.SendTouchParentListAdapter;
import com.touchKin.touchkinapp.custom.HorizontalListView;
import com.touchKin.touchkinapp.model.AppController;
import com.touchKin.touchkinapp.model.ParentListModel;
import com.touchKin.touchkinapp.services.CompressAndSendService;
import com.touchKin.touckinapp.R;

public class SendTouchPreview extends ActionBarActivity implements
		OnClickListener, OnItemClickListener, IMyViewHolderClicks {

	ImageView previewImage;
	Bitmap bm;
	VideoView videoPreview;
	// public static EditText sendmessage;
	Button sendButton;
	Handler handler;
	private SendTouchParentListAdapter imageAdapter;
	Button thumbnailplaybutton;
	public static int type;
	NotificationManager mNotifyManager;
	Notification.Builder mBuilder;
	Uri previewFilePath;
	Boolean keepPrivate = false;
	public static int imagetype;
	HorizontalListView listview;
	RelativeLayout parentRelativeLayout;
	static List<ParentListModel> list;
	Bitmap thumbnail, bitmap;
	SendTouchActivity sendtouch;
	ProgressDialog pDialog;
	static String userId;
	String videoPath = null;
	int id = 1;
	String token;

	// private ParentListModel selectedParent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_touch_preview_video);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		init();
		Intent intent = getIntent();

		Typeface latofont = Typeface.createFromAsset(getAssets(),
				"fonts/Lato-LightItalic.ttf");
		// sendmessage.setTypeface(latofont);
		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Sending the touch...");
		pDialog.setCancelable(false);
		userId = intent.getExtras().getString("userId");
		token = intent.getExtras().getString("token");
		// adding toolbar
		// Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		// TextView mTitle = (TextView)
		// toolbar.findViewById(R.id.toolbar_title);
		// mTitle.setText("");
		// toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
		// toolbar.setNavigationOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// NavUtils.navigateUpFromSameTask(SendTouchPreview.this);
		//
		// }
		// });
		// toolbar.setNavigationIcon(R.drawable.back);
		// setSupportActionBar(toolbar);
		// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// toolbar.setNavigationOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent(SendTouchPreview.this,
		// SendTouchActivity.class);
		// startActivity(intent);
		//
		// }
		// });
		// sendmessage.setOnEditorActionListener(new OnEditorActionListener() {
		//
		// @Override
		// public boolean onEditorAction(TextView v, int actionId,
		// KeyEvent event) {
		// // TODO Auto-generated method stub
		// boolean handled = false;
		// if (actionId == EditorInfo.IME_ACTION_SEND) {
		// // sendMedia(type);
		// handled = true;
		// }
		// return handled;
		// }
		// });
		// sendmessage.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// sendmessage.requestLayout();
		// SendTouchPreview.this
		// .getWindow()
		// .setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
		//
		// return false;
		// }
		// });

		if (intent != null) {

			Bundle bundle = intent.getExtras();
			if (bundle != null) {

				type = bundle.getInt("Media_Type");
				if (type == SendTouchActivity.MEDIA_TYPE_IMAGE) {
					previewImage.setVisibility(View.VISIBLE);
					videoPreview.setVisibility(View.INVISIBLE);
					previewFilePath = (Uri) bundle.get(MediaStore.EXTRA_OUTPUT);
					String tempPath = getPath(previewFilePath);
					// setPic(tempPath);
					BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
					bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
					previewImage.setImageBitmap(bm);
				} else {
					previewImage.setVisibility(View.VISIBLE);
					videoPreview.setVisibility(View.INVISIBLE);
					previewFilePath = (Uri) bundle.get(MediaStore.EXTRA_OUTPUT);

					thumbnail = ThumbnailUtils.createVideoThumbnail(
							getPath(previewFilePath), Thumbnails.MINI_KIND);

					Log.d("previewFilePath", previewFilePath.toString());
					videoPath = previewFilePath.toString().substring(8);
					previewImage.setImageBitmap(thumbnail);

				}

				fetchParentList();
				listview.setOnItemClickListener(this);
				sendButton.setOnClickListener(this);

			}

		}
		thumbnailplaybutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SendTouchPreview.this,
						VideoFullScreen.class);
				intent.putExtra("thumbnail", thumbnail);
				intent.putExtra("videopath", previewFilePath);
				startActivity(intent);
			}
		});

	}

	public String getPath(Uri uri) {
		// just some safety built in
		if (uri == null) {
			// TODO perform some logging or show user feedback
			return null;
		}
		// try to retrieve the image from the media store first
		// this will only work for images selected from gallery
		String[] projection = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		// this is our fallback here
		return uri.getPath();
	}

	private void init() {
		previewImage = (ImageView) findViewById(R.id.imagePreview);
		videoPreview = (VideoView) findViewById(R.id.videoPreview);
		sendButton = (Button) findViewById(R.id.sendbutton);
		// sendmessage = (EditText) findViewById(R.id.phone_number_detail);
		listview = (HorizontalListView) findViewById(R.id.parentListView);
		thumbnailplaybutton = (Button) findViewById(R.id.play_btn);
		parentRelativeLayout = (RelativeLayout) findViewById(R.id.parentListLayoutDashboard);
		list = new ArrayList<ParentListModel>();
	}

	private void setPic(String file) {

		/*
		 * There isn't enough memory to open up more than a couple camera photos
		 */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = previewImage.getWidth();
		int targetH = previewImage.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		final Bitmap bitmap = BitmapFactory.decodeFile(file, bmOptions);

		// Matrix matrix = new Matrix();
		// Camera.CameraInfo info;
		// if (this.getResources().getConfiguration().orientation !=
		// Configuration.ORIENTATION_LANDSCAPE) {
		//
		// matrix.postRotate(90);
		// } else {
		// // This is an undocumented although widely known feature
		// matrix.postRotate(0);
		// }

		// Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, photoW,
		// photoH, matrix, true);
		// // TODO Auto-generated method stub
		//
		// /* Associate the Bitmap to the ImageView */
		// previewImage.setImageBitmap(rotatedBitmap);
		previewImage.setImageBitmap(bitmap);
		previewImage.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.yesButton:
			disCardVideo();
			break;

		case R.id.sendbutton:
			if (GeneralUtils.checkIfFileExistAndNotEmpty(videoPath)) {
				Intent intent = new Intent(SendTouchPreview.this,
						CompressAndSendService.class);
				intent.putExtra("videoPath", videoPath);
				intent.putExtra("token", token);
				startService(intent);
				finish();
			} else {
				Toast.makeText(getApplicationContext(),
						videoPath + " not found", Toast.LENGTH_LONG).show();
			}

			break;
		default:
			break;
		}

	}

	private void disCardVideo() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, SendTouchActivity.class);
		startActivity(intent);
		finish();
	}

	public static String getCheckedParentId() {
		String array = "";
		for (ParentListModel item : list) {
			if (item.getIsSelected()) {
				if (array.isEmpty()) {
					array = item.getParentId();
				} else {
					array = array + "," + item.getParentId();
				}
			}
		}

		return array;

	}

	public void fetchParentList() {
		list = new ArrayList<ParentListModel>();
		JsonObjectRequest req = new JsonObjectRequest(Method.GET,
				"http://54.69.183.186:1340/user/family", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject responseArray) {
						// TODO Auto-generated method stub
						JSONArray careRecievers;
						try {
							careRecievers = responseArray
									.getJSONArray("care_receivers");

							int crCount = careRecievers.length();
							for (int i = 0; i < crCount; i++) {
								JSONObject cr;

								cr = careRecievers.getJSONObject(i);

								if (cr != null) {
									ParentListModel item = new ParentListModel();
									item.setParentId(cr.getString("id"));
									item.setParentName(cr.optString("nickname"));
									item.setMobilenumber(cr.optString("mobile"));
									if (cr.has("care_receiver_status")
											&& cr.getString(
													"care_receiver_status")
													.equalsIgnoreCase("pending")) {
										item.setReqStatus(false);
									} else {
										item.setReqStatus(true);
									}
									if (item.getParentId().equalsIgnoreCase(
											userId))
										item.setIsSelected(true);
									else {
										item.setIsSelected(false);
									}
									list.add(item);
								}
							}
							JSONArray careGivers = responseArray
									.getJSONArray("care_givers");
							int cgCount = careGivers.length();
							for (int i = 0; i < cgCount; i++) {
								JSONObject cg = careGivers.getJSONObject(i);
								if (cg != null) {
									ParentListModel item = new ParentListModel();
									item.setParentId(cg.getString("id"));
									item.setParentName(cg
											.optString("first_name"));
									if (cg.has("care_receiver_status")
											&& cg.getString(
													"care_receiver_status")
													.equalsIgnoreCase("pending")) {
										item.setReqStatus(false);
									} else {
										item.setReqStatus(true);
									}
									item.setMobilenumber(cg.optString("mobile"));
									if (item.getParentId().equalsIgnoreCase(
											userId))
										item.setIsSelected(true);
									else {
										item.setIsSelected(false);
									}
									if (!containsId(list, item.getParentId())) {
										list.add(item);
									}
								}
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// else {
						// setMenuTitle(null);
						// }
						imageAdapter = new SendTouchParentListAdapter(
								SendTouchPreview.this, list);
						listview.setAdapter(imageAdapter);

					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

						Log.d("Error", "" + error.networkResponse);
						VolleyLog.e("Error: ", error.getMessage());
						String json = null;

						NetworkResponse response = error.networkResponse;
						if (!InternetAvailable()) {
							Toast.makeText(SendTouchPreview.this,
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
						Toast.makeText(SendTouchPreview.this,
								error.getMessage(), Toast.LENGTH_SHORT).show();

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

	private boolean InternetAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public void displayMessage(String toastString, int code) {
		Toast.makeText(getApplicationContext(),
				toastString + " code error: " + code, Toast.LENGTH_LONG).show();
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

	@Override
	public void onItemTouch(int position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onImageTouch() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		ParentListModel item = list.get(position);

		if (item.getIsSelected()) {
			item.setIsSelected(false);
		} else {
			item.setIsSelected(true);
		}
		// listview.setAdapter(imageAdapter);
		imageAdapter.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed() {
		Log.d("CDA", "onBackPressed Called");
		Intent setIntent = new Intent(this, SendTouchActivity.class);
		setIntent.putExtra("userId", userId);
		setIntent.putExtra("token", token);
		startActivity(setIntent);
		finish();
	}

	public static boolean containsId(List<ParentListModel> list, String id) {
		for (ParentListModel object : list) {
			if (object.getParentId().equalsIgnoreCase(id)) {
				return true;
			}
		}
		return false;
	}
}
