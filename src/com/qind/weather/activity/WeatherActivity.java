package com.qind.weather.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.qind.weather.R;
import com.qind.weather.adapter.CityListAdapter;
import com.qind.weather.constant.Constant;
import com.qind.weather.db.SunshineDbOperation;
import com.qind.weather.model.JsonInfo;
import com.qind.weather.model.WeatherData;
import com.qind.weather.model.WeatherResults;
import com.qind.weather.utils.Utility;
import com.qind.weather.widget.ClearEditText;
import com.qind.weather.widget.ClearEditText.ClearCallBack;

public class WeatherActivity extends BaseActivity implements OnRefreshListener<ScrollView> {
	private TextView cityNameText;
	private TextView publishText;
	private TextView DescriptionText;
	private TextView windText;
	private TextView tempText;
	private TextView currentDateText;
	private ClearEditText searchcityEt;
	private Button switchCity;
	private LinearLayout weatherInfoLayout;

	private PullToRefreshScrollView pullToRefreshScrollView;
	private SlidingMenu slidingMenu;
	private GridView listGridView;
	private ArrayList<String> mListItems;
	private ArrayList<String> tempItems;
	private CityListAdapter adapter;

	private LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	private LocationMode mode = LocationMode.Hight_Accuracy;
	private String coor = "gcj02";
	private SQLiteDatabase mDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
		init();

		// 初始化百度定位
		mLocationClient = new LocationClient(getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		initLocation();

		if (SunshineApplication.isFirstLoad && Utility.isConnected(this)) {
			mLocationClient.start();
		} else if (Utility.isConnected(this) == false) {
			Utility.openNetworkSetting(this);
		}

		mDatabase = new SunshineDbOperation(this).openDatabase();
		Cursor cursor = mDatabase.query("Province", new String[] { "provinceName" }, null, null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				mListItems.add(cursor.getString(0));
				LogUtils.e(cursor.getString(0));
			}
			tempItems = (ArrayList<String>) mListItems.clone();
			LogUtils.e(tempItems.toString());
		}
	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(mode);// 设置定位模式
		option.setCoorType(coor);// 返回的定位结果是百度经纬度，默认值gcj02
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append(location.getDirection());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				// 运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
			}
			System.out.println(sb.toString());
			getWeatherInfo(location.getLatitude() + "", location.getLongitude() + "");
		}

	}

	@SuppressWarnings("unchecked")
	private void init() {
		cityNameText = (TextView) findViewById(R.id.tv_cityName);
		publishText = (TextView) findViewById(R.id.tv_publishText);
		DescriptionText = (TextView) findViewById(R.id.tv_description);
		tempText = (TextView) findViewById(R.id.tv_temp);
		windText = (TextView) findViewById(R.id.tv_wind);
		currentDateText = (TextView) findViewById(R.id.tv_date);

		switchCity = (Button) findViewById(R.id.switch_city);
		switchCity.setOnClickListener(this);
		pullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_to_refresh_listview);
		pullToRefreshScrollView.setOnRefreshListener(this);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.ll_weatherInfo);

		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setMenu(R.layout.slidingmenu);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		searchcityEt = (ClearEditText) slidingMenu.findViewById(R.id.et_searchcity);
		searchcityEt.addTextChangedListener(textWatcher);
		listGridView = (GridView) slidingMenu.findViewById(R.id.gridview_list);
		mListItems = new ArrayList<String>();
		adapter = new CityListAdapter(this, mListItems);
		listGridView.setAdapter(adapter);
	}

	private void showWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.CHINA);
		cityNameText.setText(prefs.getString("city_name", ""));
		tempText.setText(prefs.getString("temp", ""));
		DescriptionText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("更新于" + sdf.format(new Date()));
		currentDateText.setText(prefs.getString("current_date", ""));
		windText.setText(prefs.getString("wind", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.switch_city:
			// mLocationClient.start();
			slidingMenu.showMenu();
			break;

		default:
			break;
		}
	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (TextUtils.isEmpty(s)) {
				mListItems.clear();
				for(int i = 0 ;i<tempItems.size() ;i++){
					mListItems.add(tempItems.get(i));
				}
				adapter.notifyDataSetChanged();
			}
		
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			String key = searchcityEt.getText().toString();
			if (!TextUtils.isEmpty(key)) {
				Cursor cursor = mDatabase.query("City", new String[] { "cityName,provinceName" }, "cityName =? or provinceName = ?",
						new String[] { key, key }, null, null, null);
				if (cursor != null && cursor.getCount() > 0) {
					mListItems.clear();
					while (cursor.moveToNext()) {
						mListItems.add(cursor.getString(0));
						LogUtils.e(cursor.getString(0));
					}
				}
				adapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
		String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
				| DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

		// Update the LastUpdatedLabel
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

		// Do work to refresh the list here.
		new GetDataTask().execute();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			mLocationClient.start();
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// mListItems.addFirst("Added after refresh...");
			// ada.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			publishText.setText("同步中……");
			pullToRefreshScrollView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

	/**
	 * 发送请求获取天气
	 * 
	 * @param a
	 * @param b
	 */
	private void getWeatherInfo(String Latitude, String Longitude) {
		HttpUtils httpUtils = new HttpUtils();
		String url = Constant.WEATHER_URL + Longitude + "," + Latitude + "&output=json&ak=" + Constant.BAIDU_AK + "&mcode="
				+ Constant.BAIDU_MCODE;
		httpUtils.send(HttpMethod.GET, url, new HttpCallBack());
	}

	private void getWeatherInfo(String countyName) {
		HttpUtils httpUtils = new HttpUtils();
		String url = Constant.WEATHER_URL + countyName + "&output=json&ak=" + Constant.BAIDU_AK + "&mcode=" + Constant.BAIDU_MCODE;
		LogUtils.d(url);
		httpUtils.send(HttpMethod.GET, url, new HttpCallBack());
	}

	class HttpCallBack extends RequestCallBack<String> {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			Toast.makeText(WeatherActivity.this, arg1, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			LogUtils.d(arg0.result);
			handleJson(arg0.result);
			showWeather();
		}

	}

	/**
	 * 处理返回的JSON
	 * 
	 * @param result
	 */
	private void handleJson(String result) {
		Gson gson = new Gson();
		JsonInfo jsonInfo = gson.fromJson(result, JsonInfo.class);
		WeatherResults weatherResults = jsonInfo.getResults().get(0);
		ArrayList<WeatherData> weatherDatas = (ArrayList<WeatherData>) weatherResults.getWeather_data();
		WeatherData weatherData = weatherDatas.get(0);
		System.out.println(weatherData.getDate() + weatherData.getTemperature() + weatherData.getWind());
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
		editor.putString("city_name", weatherResults.getCurrentCity());
		editor.putString("temp", weatherData.getTemperature());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		editor.putString("current_date", sdf.format(new Date()));
		editor.putString("weather_desp", weatherData.getWeather());
		editor.putString("wind", weatherData.getWind());
		editor.commit();
	}

}
