package com.qind.weather.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	/*
	 * 回调接口
	 */
	public interface HttpCallbackListener {
		void onFinish(String response);

		void onFailed(Exception e);
	}

	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpURLConnection urlConnection = null;
				try {
					URL url = new URL(address);
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setConnectTimeout(5000);
					urlConnection.setReadTimeout(5000);
					InputStream in = urlConnection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					if (listener != null) {
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if (listener != null) {
						listener.onFailed(e);
					}
				} finally {
					if (urlConnection != null) {
						urlConnection.disconnect();
					}
				}
			}
		}).start();
	}
}
