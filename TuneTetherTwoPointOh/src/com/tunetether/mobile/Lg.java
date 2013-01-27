package com.tunetether.mobile;

import android.util.Log;

/**
 * A Logging utility class
 * @author aaronsnoswell
 */
public class Lg {

	private static String compose(Object... obs) {
		String msg = "";
		for(Object o : obs) {
			if(o == null) {
				msg += "null ";
			} else {
				msg += o.toString() + " ";
			}
		}
		if(obs.length != 0) msg = msg.substring(0, msg.length()-1);
		return msg;
	}

	public static void d(Object... obs) {
		Log.d(TAG, compose(obs));
	}

	public static void i(Object... obs) {
		Log.i(TAG, compose(obs));
	}

	public static void v(Object... obs) {
		Log.v(TAG, compose(obs));
	}

	public static void e(Object... obs) {
		Log.e(TAG, compose(obs));
	}

	public static void w(Object... obs) {
		Log.w(TAG, compose(obs));
	}

	private final static String TAG = "TuneTether";

}