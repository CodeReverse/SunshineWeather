package com.qind.weather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class MainActivity extends Activity {
	private ImageView weatherImg;
	private TextView weatherInfo;
	private String url = "http://sixweather.3gpk.net/SixWeather.aspx?city=上海";
	private HttpUtils httpUtils;
	LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		httpUtils = new HttpUtils();
		if (isGpsOpen()) {
			getLocation();
		}
		// getWeatherInfo();
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		weatherImg = (ImageView) findViewById(R.id.iv_weatherimg);
		weatherInfo = (TextView) findViewById(R.id.weatherinfo);
	}

	private void getWeatherInfo() {
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				System.out.println("failure");
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				System.out.println("success");
				System.out.println(arg0.result.toString());
			}

		});
	}

	private boolean isGpsOpen() {
		if (locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			Toast.makeText(MainActivity.this, "GPS模块运行正常", Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		Toast.makeText(MainActivity.this, "请开启GPS！", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
		startActivity(intent);
		return false;

	}

	private void getLocation() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗

		String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
		Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
		updateToNewLocation(location);
		// 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
		locationManager.requestLocationUpdates(provider, 100 * 1000, 500,
				new LocationListener() {

					@Override
					public void onStatusChanged(String arg0, int arg1,
							Bundle arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProviderEnabled(String arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProviderDisabled(String arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLocationChanged(Location arg0) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void updateToNewLocation(Location location) {

		if (location != null) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			Toast.makeText(MainActivity.this,
					"维度：" + latitude + "\n经度" + longitude, Toast.LENGTH_LONG)
					.show();
			getCityInfo(latitude, longitude);
		} else {
			Toast.makeText(MainActivity.this, "无法获取位置信息", Toast.LENGTH_LONG)
					.show();
		}

	}

	private void getCityInfo(double latitude, double longitude) {
		String url = "http://maps.google.com/maps/api/geocode/json?latlng="
				+ latitude + "," + longitude + "&language=zh-CN&sensor=true";
		System.out.println(url);
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				// TODO Auto-generated method stub
				super.onLoading(total, current, isUploading);
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				System.out.println("Failure");
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				System.out.println("Success");
				System.out.println(arg0.toString());
			}
		});
	}
}
