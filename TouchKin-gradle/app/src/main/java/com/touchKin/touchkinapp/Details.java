package com.touchKin.touchkinapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.touchKin.touchkinapp.broadcastReciever.IncomingSMS;
import com.touchKin.touchkinapp.custom.ImageLoader;
import com.touchKin.touchkinapp.custom.RoundedImageView;
import com.touchKin.touchkinapp.model.AppController;
import com.touchKin.touchkinapp.model.RequestModel;
import com.touchKin.touchkinapp.model.Validation;
import com.touchKin.touckinapp.R;

public class Details extends ActionBarActivity implements OnClickListener {

	final int PIC_CROP = 2;
	private Uri selectedImageUri;
	ImageView next_tool_butoon, previous_tool_button;
	Button next;
	TextView otptext;
	TextView phone_detail;
	EditText name;
	String name_detail, phone;
	boolean hasFocus = false;
	private static int RESULT_LOAD_IMG = 1;
	String imgDecodableString;
	RoundedImageView imgView;
	ImageView addImageView;
	String previewFilePath;
	String userID, userName = null;
	String serverPath = "https://s3-ap-southeast-1.amazonaws.com/touchkin-dev/avatars/";
	Boolean exists;
	ImageLoader imgLoader;
	EditText userAge;
	RadioGroup radioGroup;
	private Intent pictureActionIntent = null;
	String image_url;
	private ProgressDialog pDialog;
	private Toolbar toolbar;
	List<String> list;
	TextView mTitle, textTv;
	List<RequestModel> requestList;
	final String TAG = "Details";
	Boolean male = true;
	String yob = null;
	IncomingSMS reciever;
	String oneTimePass;
	String deviceId, mobile_os;
	String code;
	Spinner year_spinner;
	int[] age_year;
	int yob_from_server;
	String gender_server;
	String server_name, server_age;
	EditText otp;
	Boolean verified = true;
	Button enterManually, resendOTP;
	Boolean isLoggedIn;
	ArrayAdapter<String> dataAdapter;
	String selectedImagePath;
	String tokenString;
	protected static final int CAMERA_REQUEST = 0;
	protected static final int GALLERY_PICTURE = 1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info);
		init();

		isLoggedIn = getIntent().getExtras().getBoolean("isLoggedin");
		if (!isLoggedIn) {
			deviceId = getIntent().getExtras().getString("device_id");
			if (getIntent().getExtras().getBoolean("isdifferentId")) {
				phone = getIntent().getExtras().getString("phone_number");
			}
			Log.d("Data ", "Phone " + phone + " mobile_os " + mobile_os + "id "
					+ deviceId);
			verified = false;
		} else {
			SharedPreferences token = getApplicationContext()
					.getSharedPreferences("token", 0);
			tokenString = token.getString("token", null);
		}
		year_spinner.setEnabled(false);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		year_spinner.setAdapter(dataAdapter);
		// if (getIntent() != null) {
		// if (getIntent().getExtras().getBoolean("fromOtp")) {
		// phone = getIntent().getExtras().getString("phoneNumber");
		// userID = getIntent().getExtras().getString("id");
		// if (getIntent().getExtras().getString("first_name") != null)
		// userName = getIntent().getExtras().getString("first_name");
		// } else {
		// getUserInfo();
		// }
		// }

		// getUserInfo();
		pDialog.setMessage("Updating info");
		pDialog.setCancelable(false);

		SharedPreferences userPref = getApplicationContext()
				.getSharedPreferences("userPref", 0);

		String user = userPref.getString("user", null);

		// Log.d("User", user + " ");
		if (user != null) {
			try {
				JSONObject obj = new JSONObject(user);
				Log.d("User", obj + "");

				phone = obj.optString("mobile");
				if (obj.has("first_name")) {

					name.setText(obj.optString("first_name"));
					server_name = obj.optString("first_name");
					verified = true;
					String yob = obj.getString("yob");
					if (yob != null) {
						Log.d("YOB", yob);
						year_spinner.setEnabled(true);
						yob_from_server = Integer.parseInt(yob);
						gender_server = obj.getString("gender");
						Log.d("yob from server", " " + yob_from_server);
						age_year[0] = Integer.parseInt(yob) - 1;
						age_year[1] = Integer.parseInt(yob);
						age_year[2] = Integer.parseInt(yob) + 1;
						list.clear();
						list.add("" + age_year[0]);
						list.add("" + age_year[1]);
						list.add("" + age_year[2]);
						year_spinner.setSelection(1);
						dataAdapter.notifyDataSetChanged();
						Calendar calendar = Calendar.getInstance();
						int year = calendar.get(Calendar.YEAR);
						userAge.setText("" + (year - Integer.parseInt(yob)));
						server_age = userAge.getText().toString();

						// if (!isLoggedIn) {
						// otp.setText(obj
						// .optString("mobile_verification_code"));
						// sendIntent();
						// }

					}
					String gender = obj.optString("gender");
					if (!gender.equalsIgnoreCase("male"))
						male = false;
				}
				userID = obj.optString("id");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (verified) {
			enterManually.setVisibility(View.INVISIBLE);
			resendOTP.setVisibility(View.INVISIBLE);
			textTv.setVisibility(View.INVISIBLE);
		}

		name.setImeOptions(EditorInfo.IME_ACTION_DONE);
		phone_detail.setText(phone);
		// Image url
		image_url = serverPath + userID + ".jpeg";
		mTitle.setText("Profile");
		// ImageLoader class instance
		imgLoader = new ImageLoader(getApplicationContext());
		imgLoader.DisplayImage(image_url, R.drawable.ic_user_image, imgView);
		// new MyTask().execute(image_url);
		// whenever you want to load an image from url
		// call DisplayImage function
		// url - image url to load
		// loader - loader image, will be displayed before getting image
		// image - ImageView

		next.setOnClickListener(this);
		next_tool_butoon.setVisibility(View.VISIBLE);
		previous_tool_button.setVisibility(View.VISIBLE);
		previous_tool_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent backActivity = new Intent(Details.this,
						SignUpActivity.class);
				startActivity(backActivity);

			}
		});
		next_tool_butoon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!name.getText().toString().isEmpty()
						&& !userAge.getText().toString().isEmpty()) {
					if (verified) {
						String userName = name.getText().toString();
						String gender = "male";
						if (!male) {
							gender = "female";
						}

						// String yob = userYear.getText().toString();

						if (!name.getText().toString().equals(server_name)
								|| Integer.parseInt(year_spinner
										.getSelectedItem().toString()) != yob_from_server
								|| !gender_server.equalsIgnoreCase(gender)) {
							// Log.d("here", "come");
							updateUser(userName, gender, yob);
						} else {
							if (isLoggedIn)
								finish();
							else {
								Intent intent = new Intent(Details.this,
										MyFamily.class);
								intent.putExtra("isLoggedIn", false);
								startActivity(intent);
								finish();
							}
						}
					} else {
						if (!otp.getText().toString().equals(""))
							sendIntent();
						Toast.makeText(Details.this,
								"PLease wait while we verify you",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(Details.this,
							"PLease Add your Name and age", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
		// detail.setOnClickListener(this);
		// if (userName != null && !userName.isEmpty()) {
		// detail.setText(userName);
		// name.setText(userName);
		// }
		// if (yob != null) {
		// userYear.setText(yob);
		// Calendar calendar = Calendar.getInstance();
		// int year = calendar.get(Calendar.YEAR);
		// userAge.setText(year - Integer.parseInt(yob));
		// }
		enterManually.setOnClickListener(this);
		resendOTP.setOnClickListener(this);

		userAge.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)) {
					// Toast.makeText(MainActivity.this, "enter press",
					// Toast.LENGTH_LONG).show();
					year_spinner.setEnabled(true);
					String age = userAge.getText().toString();
					Calendar calendar = Calendar.getInstance();
					int year = calendar.get(Calendar.YEAR);
					int yob = year - Integer.parseInt(age);
					age_year[0] = yob - 1;
					age_year[1] = yob;
					age_year[2] = yob + 1;
					list.clear();
					list.add("" + age_year[0]);
					list.add("" + age_year[1]);
					list.add("" + age_year[2]);
					dataAdapter.notifyDataSetChanged();
					year_spinner.setSelection(1);
				}
				return false;
			}
		});
		year_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				yob = parent.getSelectedItem().toString();
				Log.d("yob", yob);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		radioGroup.check(R.id.radioMale);
		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup arg0, int id) {
						switch (id) {
						case R.id.radioFemale:
							Log.v(TAG, "female");
							male = false;
							break;
						default:
							Log.v(TAG, "Male?");
							male = true;
							break;
						}
					}
				});

	}

	private void init() {
		// TODO Auto-generated method stub
		next = (Button) findViewById(R.id.next_detail_button);
		// detail = (TextView) findViewById(R.id.add_name);
		phone_detail = (TextView) findViewById(R.id.phn_number_detail);
		name = (EditText) findViewById(R.id.edit_name);
		addImageView = (ImageView) findViewById(R.id.profile_pic);
		imgView = (RoundedImageView) findViewById(R.id.change_profile_pic);
		pDialog = new ProgressDialog(this);
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
		next_tool_butoon = (ImageView) toolbar.findViewById(R.id.next);
		previous_tool_button = (ImageView) toolbar.findViewById(R.id.previous);
		requestList = new ArrayList<RequestModel>();
		userAge = (EditText) findViewById(R.id.userAge);
		otptext = (TextView) findViewById(R.id.textTv);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		otp = (EditText) findViewById(R.id.otp_editText);
		enterManually = (Button) findViewById(R.id.enter_otp);
		resendOTP = (Button) findViewById(R.id.resendOtp);
		textTv = (TextView) findViewById(R.id.textTv);
		age_year = new int[3];
		year_spinner = (Spinner) findViewById(R.id.year_spinner);
		list = new ArrayList<String>();
		dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.next_detail_button:
			if (!name.getText().toString().isEmpty()
					&& !userAge.getText().toString().isEmpty()) {
				if (verified) {
					String userName = name.getText().toString();
					String gender = "male";
					if (!male) {
						gender = "female";
					}

					// String yob = userYear.getText().toString();

					if (!name.getText().toString().equals(server_name)
							|| Integer.parseInt(year_spinner.getSelectedItem()
									.toString()) != yob_from_server
							|| !gender_server.equalsIgnoreCase(gender)) {
						// Log.d("here", "come");
						updateUser(userName, gender, yob);
					} else {
						if (isLoggedIn)
							finish();
						else {
							Intent intent = new Intent(Details.this,
									MyFamily.class);
							intent.putExtra("isLoggedIn", false);
							startActivity(intent);
							finish();
						}
					}
				} else {
					if (!otp.getText().toString().equals(""))
						sendIntent();
					Toast.makeText(Details.this,
							"PLease wait while we verify you",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(Details.this, "PLease Add your Name and age",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.add_name:
			// detail.setVisibility(View.GONE);
			name.setVisibility(View.VISIBLE);
			// name_detail = name.getText().toString();
			// detail.setText(name_detail);
			break;
		case R.id.enter_otp:
			otp.setVisibility(View.VISIBLE);
			textTv.setVisibility(View.GONE);
			break;
		case R.id.resendOtp:
			resendOtp();
			resendOTP.setEnabled(false);
			break;
		default:
			break;
		}
	}

	private void resendOtp() {
		// TODO Auto-generated method stub
		pDialog.setMessage("Resending OTP");
		showpDialog();

		JSONObject params = new JSONObject();
		try {
			params.put("mobile", phone);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
				"http://54.69.183.186:1340/user/send-mobile-verification-code",
				params, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.d(TAG, response.toString());
						hidepDialog();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("Error", "" + error.networkResponse);
						VolleyLog.e("Error: ", error.getMessage());
						String json = null;
						hidepDialog();
						NetworkResponse response = error.networkResponse;
						if (!InternetAvailable()) {
							Toast.makeText(Details.this,
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
				});

		AppController.getInstance().addToRequestQueue(req);
	}

	private boolean InternetAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public void loadImagefromGallery(View view) {

		// startDialog();
		selectImage();
		// Create intent to Open Image applications like Gallery, Google Photos
		// Intent galleryIntent = new Intent(Intent.ACTION_PICK,
		// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// // Start the Intent
		// startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
	}

	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					pictureActionIntent = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					// File f = new File(android.os.Environment
					// .getExternalStorageDirectory(), "temp.jpg");
					// pictureActionIntent.putExtra(MediaStore.EXTRA_OUTPUT,
					// Uri.fromFile(f));

					startActivityForResult(pictureActionIntent, CAMERA_REQUEST);

				}

				else if (items[item].equals("Choose from Library")) {
					pictureActionIntent = new Intent(Intent.ACTION_PICK, null);
					pictureActionIntent.setType("image/*");
					pictureActionIntent.putExtra("return-data", true);
					startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	// private void startDialog() {
	// AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
	// myAlertDialog.setTitle("Choose Your Option");
	// myAlertDialog.setMessage("How do you want to set your picture?");
	//
	// myAlertDialog.setPositiveButton("Gallery",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface arg0, int arg1) {
	// pictureActionIntent = new Intent(
	// Intent.ACTION_GET_CONTENT, null);
	// pictureActionIntent.setType("image/*");
	// pictureActionIntent.putExtra("return-data", true);
	// startActivityForResult(pictureActionIntent,
	// GALLERY_PICTURE);
	// }
	// });
	//
	// myAlertDialog.setNegativeButton("Camera",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface arg0, int arg1) {
	// pictureActionIntent = new Intent(
	// android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	// startActivityForResult(pictureActionIntent,
	// CAMERA_REQUEST);
	//
	// }
	// });
	// myAlertDialog.show();
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			// When an Image is picked
			if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
					&& null != data) {
				// Get the Image from data

				selectedImageUri = data.getData();

				performCrop();
			} else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK
					&& data.hasExtra("data")) {

				// File f = new
				// File(Environment.getExternalStorageDirectory().toString());
				// for (File temp : f.listFiles()) {
				// if (temp.getName().equals("temp.jpg")) {
				// f = temp;
				// break;
				// }
				// selectedImageUri = Uri.fromFile(f);
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				selectedImageUri = data.getData();
				//
				// // Bitmap thePic = (Bitmap) data.getExtras().get("data");
				// ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				// thePic.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

				// you can create a new file name "test.jpg" in sdcard folder.
				// File f = new File(Environment.getExternalStorageDirectory()
				// + File.separator + "abc.jpg");
				// f.createNewFile();
				// selectedImageUri = Uri.fromFile(f);
				Log.d("path", " " + selectedImageUri);
				Cursor cursor = getContentResolver().query(
						Media.EXTERNAL_CONTENT_URI,
						new String[] { Media.DATA, Media.DATE_ADDED,
								MediaStore.Images.ImageColumns.ORIENTATION },
						Media.DATE_ADDED, null, "date_added ASC");
				if (cursor != null && cursor.moveToFirst()) {
					do {
						Uri uri = Uri.parse(cursor.getString(cursor
								.getColumnIndex(Media.DATA)));
						selectedImagePath = uri.toString();
					} while (cursor.moveToNext());
					cursor.close();
				}

				Log.e("path of the image from camera ====> ", selectedImagePath);
				performCrop();
				// bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
				// update the image view with the bitmap
				// imgView.setImageBitmap(bitmap);

			} else if (requestCode == PIC_CROP) {

				// get the returned data
				Bundle extras = data.getExtras();
				// get the cropped bitmap
				Bitmap thePic = extras.getParcelable("data");
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				thePic.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

				// you can create a new file name "test.jpg" in sdcard folder.
				File f = new File(Environment.getExternalStorageDirectory()
						+ File.separator + "test.jpg");
				f.createNewFile();
				previewFilePath = f.getAbsolutePath();
				// write the bytes in file
				FileOutputStream fo = new FileOutputStream(f);
				fo.write(bytes.toByteArray());

				// remember close de FileOutput
				fo.close();
				// String tempPath = getPath(selectedImageUri, this);
				// BitmapFactory.Options btmapOptions = new
				// BitmapFactory.Options();
				// Bitmap bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
				// // Set the Image in ImageView after decoding the String
				// previewFilePath = tempPath;

				imgView.setImageBitmap(thePic);
				new ImageUploadTask(this).execute();

			} else {
				Toast.makeText(this, "You haven't picked Image",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {

			Toast.makeText(this, "Something went wrong " + e, Toast.LENGTH_LONG)
					.show();
			Log.d("Message", "" + e);
		}

	}

	private void performCrop() {

		try {
			// call the standard crop action intent (the user device may not
			// support it)
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			// indicate image type and Uri
			cropIntent.setDataAndType(selectedImageUri, "image/*");
			// set crop properties
			cropIntent.putExtra("crop", "true");
			// indicate aspect of desired crop
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			// indicate output X and Y
			cropIntent.putExtra("outputX", 256);
			cropIntent.putExtra("outputY", 256);
			// retrieve data on return
			cropIntent.putExtra("return-data", true);
			// start the activity - we handle returning in onActivityResult
			startActivityForResult(cropIntent, PIC_CROP);

		} catch (ActivityNotFoundException anfe) {
			// display an error message
			String errorMessage = "Whoops - your device doesn't support the crop action!";
			Toast toast = Toast
					.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}

	}

	class ImageUploadTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (previewFilePath != null) {
				File f = new File(previewFilePath);
				if (f != null) {
					if (f.delete())
						Log.d("file", "Update and delete");

				}
			}
		}

		Context context;

		public ImageUploadTask(Context context) {
			// TODO Auto-generated constructor stub
			this.context = context;
			imgLoader.clearCache();
		}

		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(Void... unsued) {
			try {

				File file = new File(previewFilePath);

				// this is storage overwritten on each iteration with bytes
				AppController.mHttpClient.getParams().setParameter(
						CoreProtocolPNames.PROTOCOL_VERSION,
						HttpVersion.HTTP_1_1);

				HttpClient httpClient = AppController.mHttpClient;

				HttpPost httpPost = new HttpPost(
						"http://54.69.183.186:1340/user/avatar");
				httpPost.setHeader("Authorization", "Bearer " + tokenString);
				MultipartEntity entity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);

				ContentBody cbFile = new FileBody(
						file,
						ContentType.create(getMimeType(file.getAbsolutePath())),
						file.getName());

				entity.addPart("avatar", cbFile);

				// entity.addPart("media", fileBody);

				// entity.addPart("photoCaption", new
				// StringBody(caption.getText()
				// .toString()));
				httpPost.setEntity(entity);
				Log.d("HttpPost", httpPost.getEntity() + "");
				httpClient.execute(httpPost);

				// if (resEntity != null) {
				// System.out.println(EntityUtils.toString(resEntity));
				// }
				// if (resEntity != null) {
				// resEntity.consumeContent();
				// }
				return null;
			} catch (Exception e) {
				Log.e(e.getClass().getName(), e.getMessage(), e);
				return null;
			}

			// (null);
		}

		// @Override
		// protected void onProgressUpdate(Void... unsued) {
		//
		// }

	}

	public String getPath(Uri uri, Activity activity) {
		String[] projection = { MediaColumns.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = activity
				.managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
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

	private void showpDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hidepDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}

	private void updateUser(final String name, final String gender,
			final String yob) {
		// TODO Auto-generated method stub
		showpDialog();
		pDialog.setMessage("Updating User");
		JSONObject params = new JSONObject();
		try {
			params.put("first_name", name);
			params.put("gender", gender);
			params.put("yob", yob);

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
				"http://54.69.183.186:1340/user/complete-profile", params,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						hidepDialog();

						SharedPreferences userPref = getApplicationContext()
								.getSharedPreferences("userPref", 0);
						Editor edit = userPref.edit();
						SharedPreferences token = getApplicationContext()
								.getSharedPreferences("token", 0);
						try {
							response.put("token",
									token.getString("token", null));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						edit.putString("user", response.toString());
						edit.apply();
						if (!isLoggedIn) {
							Intent intent = new Intent(Details.this,
									MyFamily.class);
							intent.putExtra("isLoggedIn", false);
							startActivity(intent);
						}

						finish();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

						Log.d("Error", "" + error.networkResponse);
						VolleyLog.e("Error: ", error.getMessage());
						String json = null;
						hidepDialog();
						NetworkResponse response = error.networkResponse;
						if (!InternetAvailable()) {
							Toast.makeText(Details.this,
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

						Toast.makeText(Details.this, "PLease Error" + error,
								Toast.LENGTH_SHORT).show();

					}

				}) {
			public java.util.Map<String, String> getHeaders()
					throws com.android.volley.AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Authorization", "Bearer " + tokenString);
				return headers;

			};
		};

		AppController.getInstance().addToRequestQueue(req);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		reciever = new IncomingSMS();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(reciever, filter);

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Details.this.unregisterReceiver(reciever);
			}
		}, 0);
		super.onStop();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d("YourActivity", "onNewIntent is called!");

		code = intent.getStringExtra("code");
		Log.d("code", code);
		otp.setText(code);
		sendIntent();
	} // End of onNewIntent(Intent intent)

	private boolean checkValidation() {
		boolean ret = true;

		if (!Validation.isOTPNumber(otp, true)) {
			ret = false;
		}
		return ret;
	}

	public void sendIntent() {
		showpDialog();
		oneTimePass = otp.getText().toString();
		pDialog.setMessage("Verifying User");
		JSONObject params = new JSONObject();
		try {
			params.put("mobile", phone);
			params.put("code", Integer.parseInt(oneTimePass));
			params.put("mobile_device_id", deviceId);
			params.put("mobile_os", "android");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
				"http://54.69.183.186:1340/user/verify-mobile", params,
				new Response.Listener<JSONObject>() {
					@SuppressLint("NewApi")
					@Override
					public void onResponse(JSONObject response) {

						SharedPreferences userPref = getApplicationContext()
								.getSharedPreferences("userPref", 0);
						if (!response.has("code")) {
							try {
								response.put("code",
										Integer.parseInt(oneTimePass));
								SharedPreferences token = getApplicationContext()
										.getSharedPreferences("token", 0);
								Editor tokenedit = token.edit();
								try {
									tokenedit.putString("token",
											response.getString("token"));
									tokenedit.apply();
									tokenString = response.optString("token");
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
						Editor edit = userPref.edit();
						edit.putString("user", response.toString());
						edit.apply();
						Log.d("Response", "" + response);
						// Log.d("mobile", "" + pref.getString("mobile",
						// null));
						// Log.d("otp", "" + pref.getString("mobile",
						// null));
						otptext.setText("Your phone number has been verified");
						verified = true;

						//
						// Intent i = new Intent(Details.this, Details.class);
						// Bundle bndlanimation = ActivityOptions
						// .makeCustomAnimation(getApplicationContext(),
						// R.anim.animation, R.anim.animation2)
						// .toBundle();
						// i.putExtra("phoneNumber", phone);
						// i.putExtra("id", userID);
						// i.putExtra("fromOtp", true);
						// if (userName != null)
						// i.putExtra("first_name", userName);
						//
						// startActivity(i, bndlanimation);

						// Log.d(TAG, response.toString());
						// VolleyLog.v("Response:%n %s",
						// response.toString(4));
						hidepDialog();
						// finish();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						String json = null;

						NetworkResponse response = error.networkResponse;

						Log.d("Error", "" + error.networkResponse);
						VolleyLog.e("Error: ", error.getMessage());

						if (!InternetAvailable()) {
							Toast.makeText(Details.this,
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

						if (response != null && response.data != null) {
							int code = response.statusCode;
							json = new String(response.data);
							json = trimMessage(json, "message");
							if (json != null)
								displayMessage(json, code);

						}
						hidepDialog();
					}
				}) {
			public java.util.Map<String, String> getHeaders()
					throws com.android.volley.AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("token", "Bearer " + tokenString);
				return headers;

			};
		};

		AppController.getInstance().addToRequestQueue(req);
	}

	public void displayMessage(String toastString, int code) {
		Toast.makeText(getApplicationContext(),
				toastString + " code error: " + code, Toast.LENGTH_LONG).show();
	}

	public String trimMessage(String json, String key) {
		String trimmedString = null;

		try {
			JSONObject obj = new JSONObject(json);
			trimmedString = obj.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return trimmedString;
	}

}