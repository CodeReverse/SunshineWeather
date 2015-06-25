/**
 * @author qinding
 * 2015-6-23下午2:56:50
 */
package com.qind.weather.db;

import java.util.List;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

public abstract class BaseDao<T> {
	protected static final String DB_NAME = "sunshine";
	protected static final int DB_VERSION = 1;
	
	protected DbUtils mDbUtils;
	
	public BaseDao(Context context) {
		mDbUtils = DbUtils.create(context, DB_NAME);
	}
	
	public abstract T findById(int id);
	
	public DbUtils getDbUtils() {
		return mDbUtils;
	}
	
	public void close() {
		mDbUtils.close();
	}
	
	public void save(T t) {
		try {
			mDbUtils.save(t);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	public void saveOrUpdate(T t) {
		try {
			mDbUtils.saveOrUpdate(t);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	public void saveOrUpdateAll(List<T> list) {
		try {
			mDbUtils.saveOrUpdateAll(list);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
}
