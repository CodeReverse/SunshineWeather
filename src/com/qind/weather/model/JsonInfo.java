package com.qind.weather.model;

import java.io.Serializable;
import java.util.List;

public class JsonInfo implements Serializable {
	private int error;
	private String status;
	private String date;
	private List<WeatherResults> results;

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<WeatherResults> getResults() {
		return results;
	}

	public void setResults(List<WeatherResults> results) {
		this.results = results;
	}

}
