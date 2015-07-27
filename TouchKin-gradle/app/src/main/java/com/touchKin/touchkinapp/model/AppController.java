package com.touchKin.touchkinapp.model;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.touchKin.touchkinapp.custom.LruBitmapCache;

@SuppressWarnings("deprecation")
public class AppController extends Application {

	/**
	 * Log or request TAG
	 */
	public static DefaultHttpClient mHttpClient = getThreadSafeClient();
	public static final String TAG = AppController.class.getSimpleName();

	/**
	 * Global request queue for Volley
	 */
	private RequestQueue mRequestQueue;

	/**
	 * A singleton instance of the application class for easy access in other
	 * places
	 */
	private static AppController mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		// initialize the singleton
		mInstance = this;

	}

	/**
	 * @return ApplicationController singleton instance
	 */

	public static synchronized AppController getInstance() {
		return mInstance;

	}

	/**
	 * @return The Volley Request queue, the queue will be created if it is null
	 */

	public RequestQueue getRequestQueue() {
		// lazy initialize the request queue, the queue instance will be
		// created when it is accessed for the first time
		// lazy initialize the request queue, the queue instance will be
		// created when it is accessed for the first time
		if (mRequestQueue == null) {
			// Create an instance of the Http client.
			// We need this in order to access the cookie store
			// create the request queue
			mRequestQueue = Volley.newRequestQueue(this, new HttpClientStack(
					mHttpClient));
		}
		mRequestQueue.getCache().clear();
		return mRequestQueue;
	}

	/**
	 * Adds the specified request to the global queue, if tag is specified then
	 * it is used else Default TAG is used.
	 * 
	 * @param req
	 * @param tag
	 */

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		VolleyLog.d("Adding request to queue: %s", req.getUrl());
		req.setShouldCache(true);
		getRequestQueue().add(req);
	}

	/**
	 * Adds the specified request to the global queue using the Default TAG.
	 * 
	 * @param req
	 * @param tag
	 */

	public <T> void addToRequestQueue(Request<T> req) {
		// set the default tag if tag is empty
		req.setTag(TAG);
		req.setShouldCache(false);
		getRequestQueue().add(req);
	}

	/**
	 * Cancels all pending requests by the specified TAG, it is important to
	 * specify a TAG so that the pending/ongoing requests can be cancelled.
	 * 
	 * @param tag
	 */

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

	public static DefaultHttpClient getThreadSafeClient() {

		DefaultHttpClient client = new DefaultHttpClient();

		ClientConnectionManager mgr = client.getConnectionManager();

		HttpParams params = client.getParams();

		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params,

		mgr.getSchemeRegistry()), params);

		return client;

	}
}