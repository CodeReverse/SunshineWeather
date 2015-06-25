/**
 * @author qinding
 * 2015-6-24下午3:35:34
 */
package com.qind.weather.db;

import android.content.Context;

import com.qind.weather.model.City;

public class CityDao extends BaseDao<City>{

	public CityDao(Context context) {
		super(context);
	}

	@Override
	public City findById(int id) {
		return null;
	}

}
