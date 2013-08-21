package me.vpu.wifiautorebooter;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class WiFiAutoRebooterStarter extends Activity {
	static final String LOG_TAG = "WiFiAutoRebooter";
	static final int NOTIFICATION_ID = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "WiFiAutoRebooterStarter.onCreate()");
		
		addNotificationIcon();
		startReceiver();
		finish();
	}
	
	private void addNotificationIcon() {
		Notification n = new Notification();
		n.icon = R.drawable.ic_launcher;
		n.tickerText = "WiFiAutoRebooter running...";
		n.contentView = new RemoteViews("me.vpu.wifiautorebooter", R.layout.notificationlayout);
		n.flags = Notification.FLAG_NO_CLEAR;
		
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(NOTIFICATION_ID, n);
	}
	
	private void startReceiver() {
		Intent alarmIntent = new Intent(getApplicationContext(), WiFiAutoRebooterReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis(), pendingIntent);
	}
}
