package com.qind.weather.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qind.weather.R;

public class WeatherInfoFragment extends Fragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.weather_info, container);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
}
