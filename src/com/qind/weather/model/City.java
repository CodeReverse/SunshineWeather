package com.qind.weather.model;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "City")
public class City {
	@Id
	@NoAutoIncrement
	private long cityCode;
	private long provinceCode;
	private String cityName;
	private String provinceName;

	public long getCityCode() {
		return cityCode;
	}

	public void setCityCode(long cityCode) {
		this.cityCode = cityCode;
	}

	public long getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(long provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

}
