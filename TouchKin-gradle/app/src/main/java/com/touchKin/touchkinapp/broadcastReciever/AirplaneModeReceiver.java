package com.touchKin.touchkinapp.broadcastReciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

public class AirplaneModeReceiver extends BroadcastReceiver {
	Context context;
	private boolean isAirplaneModeOn() {
		Boolean val = Settings.System.getInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) != 0;
		Log.d("airplane", "msg" + val);
		return Settings.System.getInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) != 0;

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

	}
}
