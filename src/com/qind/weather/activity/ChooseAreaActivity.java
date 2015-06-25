//package com.qind.weather.activity;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.qind.weather.R;
//import com.qind.weather.db.SunshineDbOperation;
//import com.qind.weather.model.City;
//import com.qind.weather.model.County;
//import com.qind.weather.model.Province;
//import com.qind.weather.utils.HttpUtil;
//import com.qind.weather.utils.HttpUtil.HttpCallbackListener;
//import com.qind.weather.utils.Utility;
//
//public class ChooseAreaActivity extends BaseActivity implements OnItemClickListener {
//	private Context context = ChooseAreaActivity.this;
//	public static final int LEVEL_PROVINCE = 0;
//	public static final int LEVEL_CITY = 1;
//	public static final int LEVEL_COUNTY = 2;
//
//	private ProgressDialog dialog;
//	private TextView titleTextView;
//	private ListView areaListView;
//
//	private ArrayAdapter<String> adapter;
//	private SunshineDbOperation dbOperation;
//	private List<String> dataList = new ArrayList<String>();
//	private List<Province> provinceList;
//	private List<City> cityList;
//	private List<County> countyList;
//
//	private Province selectedProvince;
//	private City selectedCity;
//	private int currentLevel;
//	private boolean isFromWeatherActivity;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//		if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
//			Intent i = new Intent(this, WeatherActivity.class);
//			startActivity(i);
//			finish();
//			return;
//		}
//		setContentView(R.layout.choose_area);
//		init();
//	}
//
//	private void init() {
//		titleTextView = (TextView) findViewById(R.id.tv_title);
//		areaListView = (ListView) findViewById(R.id.lv_areaList);
//		
//		areaListView.setOnItemClickListener(this);
//		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
//		areaListView.setAdapter(adapter);
//		dbOperation = SunshineDbOperation.getInstance(this);
//	}
//
//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		if (currentLevel == LEVEL_PROVINCE) {
//			selectedProvince = provinceList.get(position);
//			queryCities();
//		} else if (currentLevel == LEVEL_CITY) {
//			selectedCity = cityList.get(position);
//			queryCounties();
//		} else if (currentLevel == LEVEL_COUNTY) {
//			String countyCode = countyList.get(position).getCountyCode();
//			Intent i = new Intent(context, WeatherActivity.class);
//			i.putExtra("county_code", countyCode);
//			startActivity(i);
//			finish();
//		}
//		queryProvinces();
//	}
//
//	private void queryProvinces() {
//		provinceList = dbOperation.loadProvinces();
//		if (provinceList.size() > 0) {
//			dataList.clear();
//			for (Province province : provinceList) {
//				dataList.add(province.getProvinceName());
//			}
//			adapter.notifyDataSetChanged();
//			areaListView.setSelection(0);
//			titleTextView.setText("中国");
//			currentLevel = LEVEL_PROVINCE;
//		} else {
//			queryFromServer(null, "province");
//		}
//	}
//
//	private void queryCounties() {
//		countyList = dbOperation.loCounties(selectedCity.getId());
//		if (countyList.size() > 0) {
//			dataList.clear();
//			for (County county : countyList) {
//				dataList.add(county.getCountyName());
//			}
//			adapter.notifyDataSetChanged();
//			areaListView.setSelection(0);
//			titleTextView.setText(selectedCity.getCityName());
//			currentLevel = LEVEL_COUNTY;
//		} else {
//			queryFromServer(selectedCity.getCityCode(), "county");
//		}
//	}
//
//	private void queryCities() {
//		cityList = dbOperation.loadCities(selectedProvince.getId());
//		if (cityList.size() > 0) {
//			dataList.clear();
//			for (City city : cityList) {
//				dataList.add(city.getCityName());
//			}
//			adapter.notifyDataSetChanged();
//			areaListView.setSelection(0);
//			titleTextView.setText(selectedProvince.getProvinceName());
//			currentLevel = LEVEL_CITY;
//		} else {
//			queryFromServer(selectedProvince.getProvinceCode(), "city");
//		}
//	}
//
//	private void queryFromServer(final String code, final String type) {
//		String address;
//		if (!TextUtils.isEmpty(code)) {
//			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
//			// address =
//			// "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx/getSupportCity";
//		} else {
//			address = "http://www.weather.com.cn/data/list3/city.xml";
//		}
//		showProgressDialog();
//		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
//
//			@Override
//			public void onFinish(String response) {
//				boolean result = false;
//				System.out.println(response);
//				if ("province".equals(type)) {
//					result = Utility.handleProvinceResponse(dbOperation, response);
//				} else if ("city".equals(type)) {
//					result = Utility.handleCitiesResponse(dbOperation, response, selectedProvince.getId());
//				} else if ("county".equals(type)) {
//					result = Utility.handleCountiesResponse(dbOperation, response, selectedCity.getId());
//				}
//				if (result) {
//					runOnUiThread(new Runnable() {
//
//						@Override
//						public void run() {
//							colseProgressDialog();
//							if ("province".equals(type)) {
//								queryProvinces();
//							} else if ("city".equals(type)) {
//								queryCities();
//							} else if ("county".equals(type)) {
//								queryCounties();
//							}
//						}
//
//					});
//				}
//			}
//
//			@Override
//			public void onFailed(Exception e) {
//				runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						colseProgressDialog();
//						Toast.makeText(context, "加载失败", Toast.LENGTH_LONG).show();
//					}
//				});
//			}
//		});
//	}
//
//	private void showProgressDialog() {
//		if (dialog == null) {
//			dialog = new ProgressDialog(context);
//			dialog.setMessage("正在加载");
//			dialog.setCanceledOnTouchOutside(false);
//		}
//		dialog.show();
//	}
//
//	private void colseProgressDialog() {
//		if (dialog != null) {
//			dialog.dismiss();
//		}
//	}
//
//	@Override
//	public void onBackPressed() {
//		if (currentLevel == LEVEL_COUNTY) {
//			queryCities();
//		} else if (currentLevel == LEVEL_CITY) {
//			queryProvinces();
//		} else {
//			if (isFromWeatherActivity) {
//				Intent intent = new Intent(this, WeatherActivity.class);
//				startActivity(intent);
//			}
//			finish();
//		}
//	}
//
//}
