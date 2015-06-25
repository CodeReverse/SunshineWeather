package com.qind.weather.model;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "Province")
public class Province {
	@Id
	@NoAutoIncrement
	private Long provinceCode;
	private String provinceName;
	
	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public Long getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(Long provinceCode) {
		this.provinceCode = provinceCode;
	}

	@Override
	public String toString() {
		return "Province [provinceName=" + provinceName + ", provinceCode=" + provinceCode + "]";
	}
}
