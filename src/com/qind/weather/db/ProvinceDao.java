/**
 * @author qinding
 * 2015-6-24下午3:23:19
 */
package com.qind.weather.db;

import java.util.List;

import com.lidroid.xutils.exception.DbException;
import com.qind.weather.model.Province;

import android.content.Context;

public class ProvinceDao extends BaseDao<Province> {

	/**
	 * @param context
	 */
	public ProvinceDao(Context context) {
		super(context);
	}

	@Override
	public Province findById(int id) {
		try {
			return mDbUtils.findById(Province.class, id);
		} catch (DbException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Province> findAll(){
		try{
			return mDbUtils.findAll(Province.class);
		}catch(DbException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public void deleteAll(){
		try{
			mDbUtils.deleteAll(Province.class);
		}catch(DbException e){
			e.printStackTrace();
		}
	}

}
