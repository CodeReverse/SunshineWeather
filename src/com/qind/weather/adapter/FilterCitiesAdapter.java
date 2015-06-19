package com.qind.weather.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FilterCitiesAdapter extends BaseAdapter {
	private ArrayList<String> cityList;
	private Context mContext;
	protected LayoutInflater mInflater;

	public FilterCitiesAdapter(Context context, ArrayList<String> cityList) {
		this.cityList = cityList;
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return cityList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

}
