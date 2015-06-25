package com.qind.weather.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.qind.weather.R;

public class SunshineDbOperation {
	private Context mContext;
	private SQLiteDatabase database;

	public SunshineDbOperation(Context context) {
		mContext = context;
	}

	// 文件的路径
	private final int BUFFER_SIZE = 400000;
	public final static String URL = "/data/data/com.qind.weather/files";
	public static final String PACKAGE_NAME = "com.qind.weather";
	public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME; // 在手机里存放数据库的位置
	public final static String DB_NAME = "sunshine.db";

	public SQLiteDatabase openDatabase() {
		this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
		return database;
	}

	private SQLiteDatabase openDatabase(String dbfile) {
		try {
			File file = new File(dbfile);
			if (file.exists()) {
				file.delete();
				InputStream is = mContext.getResources().openRawResource(R.raw.sunshine); // 欲导入的数据库
				FileOutputStream fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			} else {// 判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
				InputStream is = mContext.getResources().openRawResource(R.raw.sunshine); // 欲导入的数据库
				FileOutputStream fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
			return db;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
