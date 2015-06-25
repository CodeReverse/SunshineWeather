/**
 * @author qinding
 * 2015-6-24下午2:06:33
 */
package com.qind.weather.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.baidu.location.m;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.qind.weather.db.CityDao;
import com.qind.weather.db.ProvinceDao;
import com.qind.weather.model.City;
import com.qind.weather.model.Province;

public class ProvinceUtility {

	private static final String PROVINCE_URL = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getRegionProvince";
	private static final String CITY_URL = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getSupportCityString?theRegionCode=";
	private static HttpUtils mHttpUtils;
	private volatile static ProvinceUtility mProvinceUtility;

	private ProvinceUtility() {
	}

	/*
	 * 单例模式
	 */
	public static ProvinceUtility getInstance() {
		if (mProvinceUtility == null) {
			synchronized (ProvinceUtility.class) {
				if (mProvinceUtility == null) {
					mProvinceUtility = new ProvinceUtility();
				}
			}
		}
		return mProvinceUtility;
	}

	public void getProvince(ProvinceDao proDao,CityDao cityDao) {
		mHttpUtils = new HttpUtils();
		mHttpUtils.send(HttpMethod.GET, PROVINCE_URL, new GetProvinceCallBack(proDao,cityDao));
	}

	class GetProvinceCallBack extends RequestCallBack<String> {
		private ProvinceDao mProvinceDao;
		private CityDao mCityDao;

		public GetProvinceCallBack(ProvinceDao proDao,CityDao cityDao) {
			mProvinceDao = proDao;
			mCityDao = cityDao;
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			LogUtils.d(arg0.result);
			analysisProXml(new ByteArrayInputStream(arg0.result.getBytes()), mProvinceDao,mCityDao);
		}

	}

	private void analysisProXml(InputStream xml, ProvinceDao proDao,CityDao cityDao) {
		List<Province> proList = new ArrayList<Province>();
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(xml, "UTF-8");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("string".equals(parser.getName())) {
						String provinceStr = parser.nextText();
						Province province = new Province();
						int n = provinceStr.indexOf(',');
						province.setProvinceName(provinceStr.substring(0, n));
						province.setProvinceCode(Long.valueOf(provinceStr.substring(n + 1)));
						proList.add(province);
					}
					break;
				case XmlPullParser.END_TAG:
					break;

				case XmlPullParser.END_DOCUMENT:
					break;
				}
				eventType = parser.next();
			}
			xml.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			proDao.saveOrUpdateAll(proList);
			for(int i = 0;i<proList.size();i++){
				getCity(proList.get(i), cityDao);
			}
		}

	}

	public void getCity(Province province, CityDao cityDao) {
		mHttpUtils = new HttpUtils();
		String url = CITY_URL + String.valueOf(province.getProvinceCode());
		mHttpUtils.send(HttpMethod.GET, url, new GetCityCallBack(cityDao, province));
	}

	class GetCityCallBack extends RequestCallBack<String> {
		private CityDao mDao;
		private Province mPronvince;

		public GetCityCallBack(CityDao cityDao, Province province) {
			mDao = cityDao;
			mPronvince = province;
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			analysisCityXml(new ByteArrayInputStream(arg0.result.getBytes()), mDao, mPronvince);
		}

	}

	private void analysisCityXml(InputStream xml, CityDao cityDao, Province province) {
		List<City> cityList = new ArrayList<City>();
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(xml, "UTF-8");
			int types = parser.getEventType();
			while (types != XmlPullParser.END_DOCUMENT) {
				switch (types) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("string".equals(parser.getName())) {
						String cityStr = parser.nextText();
						int n = cityStr.indexOf(',');
						City city = new City();
						city.setProvinceCode(province.getProvinceCode());
						city.setProvinceName(province.getProvinceName());
						city.setCityCode(Long.valueOf(cityStr.substring(n + 1)));
						city.setCityName(cityStr.substring(0, n));
						cityList.add(city);
					}
					break;

				case XmlPullParser.END_TAG:
					break;
				}
				types = parser.next();

			}
			xml.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			cityDao.saveOrUpdateAll(cityList);
		}
	}
}
