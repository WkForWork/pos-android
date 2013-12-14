package com.android.component;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.android.common.MyApp;
import com.android.service.MyService;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

/**
 * 自动同步组件
 * 
 * @author superleo
 * 
 */
// 定义成一个可以注入的组件
@EBean
public class SchedularComponent {

	// 注入 Context 变量
	@RootContext
	Context context;

	// 注入 Activity 变量
	@RootContext
	Activity activity;

	@App
	MyApp myApp;

	protected void onStart(Activity cls) {
		AlarmManager aManager = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
		// 指定启动ChangeService组件
		Intent intent = new Intent(cls, MyService.class);
		// 创建PendingIntent对象
		final PendingIntent pi = PendingIntent.getService(cls, 0, intent, 0);
		// 设置每30分钟执行pi代表的组件一次
		aManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, myApp.getSetting_time(), pi);
	}
}
