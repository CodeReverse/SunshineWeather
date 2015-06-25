package com.qind.weather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qind.weather.R;
import com.qind.weather.db.SunshineDbOperation;
import com.qind.weather.model.City;
import com.qind.weather.model.County;
import com.qind.weather.model.Province;

public class ChooseAreaActivity extends BaseActivity implements OnItemClickListener {
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
	private boolean isFromWeatherActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
			Intent i = new Intent(this, WeatherActivity.class);
			startActivity(i);
			finish();
			return;
		}
		setContentView(R.layout.choose_area);
		init();
	}

	private void init() {
		titleTextView = (TextView) findViewById(R.id.tv_title);
		areaListView = (ListView) findViewById(R.id.lv_areaList);

		areaListView.setOnItemClickListener(this);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		areaListView.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	}

	private void showProgressDialog() {
		if (dialog == null) {
			dialog = new ProgressDialog(context);
			dialog.setMessage("正在加载");
			dialog.setCanceledOnTouchOutside(false);
		}
		dialog.show();
	}

	private void colseProgressDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
		} else if (currentLevel == LEVEL_CITY) {
		} else {
			if (isFromWeatherActivity) {
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}

}
