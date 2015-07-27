package com.touchKin.touchkinapp.custom;

import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Color;

/**
 * Created by sbaiget on 11/04/2014.
 */
public class Utils {

	public static int darkenColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.8f;
		return Color.HSVToColor(hsv);
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}
}
