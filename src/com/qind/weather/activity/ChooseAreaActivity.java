package com.qind.weather.activity;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.qind.weather.R;
import com.qind.weather.db.SunshineDbOperation;
import com.qind.weather.model.City;
import com.qind.weather.model.County;
import com.qind.weather.model.Province;
import com.qind.weather.utils.HttpUtil;
import com.qind.weather.utils.Utility;
import com.qind.weather.utils.HttpUtil.HttpCallbackListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	private Context context = ChooseAreaActivity.this;
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;

	private ProgressDialog dialog;
	private TextView titleTextView;
	private ListView areaListView;

	private ArrayAdapter<String> adapter;
	private SunshineDbOperation dbOperation;
	private List<String> dataList = new ArrayList<String>();
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;

	private Province selectedProvince;
	private City selectedCity;
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_area);
		init();
		setListener();
	}

	private void init() {
		// TODO Auto-generated method stub
		titleTextView = (TextView) findViewById(R.id.tv_title);
		areaListView = (ListView) findViewById(R.id.lv_areaList);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		areaListView.setAdapter(adapter);
		dbOperation = SunshineDbOperation.getInstance(this);
	}

	private void setListener() {
		// TODO Auto-generated method stub
		areaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(arg2);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(arg2);
					queryCounties();
				}
			}

		});
		queryProvinces();

	}

	private void queryProvinces() {
		provinceList = dbOperation.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			areaListView.setSelection(0);
			titleTextView.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	private void queryCounties() {
		// TODO Auto-generated method stub
		countyList = dbOperation.loCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			areaListView.setSelection(0);
			titleTextView.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	private void queryCities() {
		// TODO Auto-generated method stub
		cityList = dbOperation.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			areaListView.setSelection(0);
			titleTextView.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	private void queryFromServer(final String code, final String type) {
		// TODO Auto-generated method stub
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
//			address = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx/getSupportCity";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				System.out.println(response);
				if ("province".equals(type)) {
					result = Utility.handleProvinceResponse(dbOperation,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(dbOperation,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(dbOperation,
							response, selectedCity.getId());
				}
				if (result) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							colseProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}

					});
				}
			}

			@Override
			public void onFailed(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						colseProgressDialog();
						Toast.makeText(context, "加载失败", Toast.LENGTH_LONG)
								.show();
					}
				});
			}
		});
	}

	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if (dialog == null) {
			dialog = new ProgressDialog(context);
			dialog.setMessage("正在加载");
			dialog.setCanceledOnTouchOutside(false);
		}
		dialog.show();
	}

	private void colseProgressDialog() {
		// TODO Auto-generated method stub
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}
}
