package com.touchKin.touchkinapp.broadcastReciever;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.touchKin.touchkinapp.services.GcmIntentService;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ComponentName comp = new ComponentName(context.getPackageName(),
				GcmIntentService.class.getName());
		startWakefulService(context, (intent.setComponent(comp)));
		Log.d("Broadcast", "Log");
		setResultCode(Activity.RESULT_OK);

	}

}
