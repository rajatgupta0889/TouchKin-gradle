package com.touchKin.touchkinapp.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONStringer;

import android.util.Log;

public class ServiceHandler {
	static String response = null;
	public final static int GET = 1;
	public final static int POST = 2;
	public final static DefaultHttpClient httpClient = new DefaultHttpClient();

	public ServiceHandler() {

	}

	/**
	 * Making service call
	 * 
	 * @url - url to make request
	 * @method - http request method
	 * */
	public String makeServiceCall(String url, int method) {
		return this.makeServiceCall(url, method, null);
	}

	/**
	 * Making service call
	 * 
	 * @url - url to make request
	 * @method - http request method
	 * @params - http request params
	 * */
	public String makeServiceCall(String url, int method, JSONStringer params) {
		try {
			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;
			if (method == POST) {
				HttpPost httpPost = new HttpPost(url);
				// adding post params
				if (params != null) {
					httpPost.setEntity(new StringEntity(params.toString()));
				}
				// Log.d("Post",httpPost.toString());

				// Log.d("LocalCOntext",localContext.getAttribute(ClientContext.COOKIE_STORE).toString());
				httpResponse = httpClient.execute(httpPost);

			} else if (method == GET) {
				// appending params to url
				if (params != null) {
					/*
					 * String paramString = URLEncodedUtils
					 * .format(params.toString(), "utf-8");
					 */
					url += "?" + params.toString();
				}
				HttpGet httpGet = new HttpGet(url);
				Log.d("Get", httpGet.toString());
				httpResponse = httpClient.execute(httpGet);

			}
			httpEntity = httpResponse.getEntity();
			response = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return response;
	}
}
