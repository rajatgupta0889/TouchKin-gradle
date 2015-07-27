package com.touchKin.touchkinapp.custom;

import java.util.Calendar;
import java.util.List;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.touchKin.touchkinapp.services.LocationSendingService;

/**
 * Created by admin on 5/22/2015.
 */
public class ServiceHelper {
	public static synchronized void stopBackgroundServiceIfRunning(
			Context context) {
		boolean IsAlreadyRunning = ServiceHelper
				.checkIfAppTrackerServiceIsRunning(context);

		// Log.d("Is AppTrackerService is running: %s",
		// String.valueOf(IsAlreadyRunning));

		if (IsAlreadyRunning) {
			Intent intent = new Intent(context, LocationSendingService.class);
			context.stopService(intent);
		}

	}

	public static void startBackgroundServiceIfNotAlreadyRunning(
			Context applicationContext) {

		boolean IsAlreadyRunning = ServiceHelper
				.checkIfAppTrackerServiceIsRunning(applicationContext);

		Log.d("Service run: %s", String.valueOf(IsAlreadyRunning));

		if (!IsAlreadyRunning) {

			
		}
	}

	public static boolean checkIfAppTrackerServiceIsRunning(Context context) {

		return checkIfServiceIsRunning(context,
				"com.touchKin.touchkinapp.services.LocationSendingService");
	}

	private static boolean checkIfServiceIsRunning(Context context,
			String serviceName) {

		ComponentName componentName = new ComponentName(
				"com.touchKin.touchkinapp", serviceName);

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<ActivityManager.RunningServiceInfo> procList = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		if (procList != null) {

			for (ActivityManager.RunningServiceInfo appProcInfo : procList) {
				if (appProcInfo != null
						&& componentName.equals(appProcInfo.service)) {
					Log.d("%s is already running", serviceName);
					return true;
				}
			}
		}
		Log.d("%s is not running", serviceName);
		return false;
	}
}
