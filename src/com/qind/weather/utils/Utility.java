package com.qind.weather.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.qind.weather.db.SunshineDbOperation;
import com.qind.weather.model.City;
import com.qind.weather.model.County;
import com.qind.weather.model.Province;

public class Utility {

	// 工具类无需实例化
	private Utility() {
		throw new AssertionError();
	}

	// 判断网络是否连接
	public static boolean isConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null != connectivityManager) {
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			if (null != info && info.isConnected()) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	// 判断wifi网络是否可用
	public static boolean isWifiDataEnable(Context context) {
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			boolean isWifiDataEnable = false;
			isWifiDataEnable = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
			return isWifiDataEnable;

		} catch (Exception e) {
			return false;
		}
	}

	// 打开网络设置
	public static void openNetworkSetting(Activity activity) {
		if (android.os.Build.VERSION.SDK_INT > 10) {
			activity.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
		} else {
			activity.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		}
	}

}
