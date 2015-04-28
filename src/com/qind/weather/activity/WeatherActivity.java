package com.qind.weather.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.qind.weather.R;
import com.qind.weather.constant.Constant;
import com.qind.weather.model.JsonInfo;
import com.qind.weather.model.WeatherData;
import com.qind.weather.model.WeatherResults;
import com.qind.weather.utils.HttpUtil;
import com.qind.weather.utils.HttpUtil.HttpCallbackListener;
import com.qind.weather.utils.Utility;

public class WeatherActivity extends BaseActivity {
	private Context context = WeatherActivity.this;
	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView DescriptionText, windText, tempText, currentDateText;
	private Button switchCity;
	private Button RefreshWeather;

	private PullToRefreshScrollView pullToRefreshScrollView;
	private SlidingMenu slidingMenu;
	private LinkedList<String> mListItems;
	private ListView filterList;

	private LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	private LocationMode mode = LocationMode.Hight_Accuracy;
	private String coor = "gcj02";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		mLocationClient = new LocationClient(getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		initLocation();
		init();
		setListener();
		BaseActivity.addActivity(this);
		if (SunshineApplication.isFirstLoad && Utility.isConnected(context)) {
			mLocationClient.start();
		} else if (Utility.isConnected(context) == false) {
			Utility.openNetworkSetting(this);
		}
	}

	private void initLocation() {
		// TODO Auto-generated method stub
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
			getWeatherInfo(location.getLatitude() + "", location.getLongitude()
					+ "");
		}

	}

	private void init() {
		// TODO Auto-generated method stub
		weatherInfoLayout = (LinearLayout) findViewById(R.id.ll_weatherInfo);
		cityNameText = (TextView) findViewById(R.id.tv_cityName);
		publishText = (TextView) findViewById(R.id.tv_publishText);
		DescriptionText = (TextView) findViewById(R.id.tv_description);
		tempText = (TextView) findViewById(R.id.tv_temp);
		windText = (TextView) findViewById(R.id.tv_wind);
		currentDateText = (TextView) findViewById(R.id.tv_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		pullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_to_refresh_listview);
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setMenu(R.layout.slidingmenu);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		filterList = (ListView) findViewById(R.id.lv_filterlist);
	}

	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
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

	private void queryWeatherCode(String countyCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	private void queryWeatherInfo(String weatherCode) {
		String address = "http://m.weather.com.cn/data/" + weatherCode
				+ ".html";
		System.out.println("address: " + address);
		queryFromServer(address, "weatherCode");
	}

	private void queryFromServer(final String address, final String type) {
		// TODO Auto-generated method stub
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
					});
				}
			}

			@Override
			public void onFailed(Exception e) {
				// TODO Auto-generated method stub
				publishText.setText("同步失败");
			}
		});
	}

	private void setListener() {
		// TODO Auto-generated method stub
		switchCity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent(WeatherActivity.this,
				// ChooseAreaActivity.class);
				// intent.putExtra("from_weather_activity", true);
				// startActivity(intent);
				// finish();
				mLocationClient.start();
			}
		});
		pullToRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						// TODO Auto-generated method stub
						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						// Do work to refresh the list here.
						new GetDataTask().execute();
					}

				});
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

	private void getWeatherInfo(String a, String b) {
		HttpUtils httpUtils = new HttpUtils();
		String url = Constant.WEATHER_URL + b + "," + a + "&output=json&ak="
				+ Constant.BAIDU_AK + "&mcode=" + Constant.BAIDU_MCODE;
		System.out.println(url);
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(context, arg1, Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				JsonInfo jsonInfo = gson.fromJson(arg0.result, JsonInfo.class);
				WeatherResults weatherResults = jsonInfo.getResults().get(0);
				ArrayList<WeatherData> weatherDatas = (ArrayList<WeatherData>) weatherResults
						.getWeather_data();
				WeatherData weatherData = weatherDatas.get(0);
				System.out.println(weatherData.getDate()
						+ weatherData.getTemperature() + weatherData.getWind());
				SharedPreferences.Editor editor = PreferenceManager
						.getDefaultSharedPreferences(WeatherActivity.this)
						.edit();
				editor.putString("city_name", weatherResults.getCurrentCity());
				editor.putString("temp", weatherData.getTemperature());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",
						Locale.CHINA);
				editor.putString("current_date", sdf.format(new Date()));
				editor.putString("weather_desp", weatherData.getWeather());
				editor.putString("wind", weatherData.getWind());
				editor.commit();
				showWeather();
			}
		});
	}
}
