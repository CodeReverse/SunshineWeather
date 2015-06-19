package com.qind.weather.activity;

import com.qind.weather.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class WelcomeActivity extends Activity {
	private ImageView splashImg;
	private Handler handler = new Handler();
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome_layout);
		
		//设置启动页动画效果
		splashImg = (ImageView) findViewById(R.id.splashImg);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha_scale_translate);
		animation.setFillAfter(true);
		splashImg.setAnimation(animation);
		
		intent = new Intent(this, WeatherActivity.class);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				startActivity(intent);
				finish();
			}
		}, 3000);
	}
}
