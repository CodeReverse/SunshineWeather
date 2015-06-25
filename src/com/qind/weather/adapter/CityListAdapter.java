/**
 * @author qinding
 * 2015-6-25下午4:00:18
 */
package com.qind.weather.adapter;

import java.util.List;

import com.qind.weather.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CityListAdapter extends BaseAdapter {
	private LayoutInflater mLayoutInflater;
	private List<String> list;
	private Context context;

	public CityListAdapter(Context context, List<String> list) {
		this.list = list;
		this.context = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		 return list == null ? 0 : list.size();
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
		ViewHolder holder = null;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.gridviewitem_city, null);
			holder.cityName = (TextView) convertView.findViewById(R.id.tv_cityname);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.cityName.setText(list.get(position));
		return convertView;
	}
	
	private static class ViewHolder{
		public TextView cityName;
	}

}
