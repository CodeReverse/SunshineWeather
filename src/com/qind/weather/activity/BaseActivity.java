package com.qind.weather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class BaseActivity extends Activity implements OnClickListener{
	public static List<Activity> activities = new ArrayList<Activity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getInstance().addActivity(this);
	}

	@Override
	public void onClick(View v) {
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getInstance().finishActivity(this);
	}
}
