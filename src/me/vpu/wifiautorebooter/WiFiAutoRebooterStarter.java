package me.vpu.wifiautorebooter;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class WiFiAutoRebooterStarter extends Activity {
	private static final String LOG_TAG = WiFiAutoRebooterStarter.class.getName();
	private static final int NOTIFICATION_ID = 0;
	private static final String ROUTER_IP = "192.168.0.1";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "WiFiAutoRebooterStarter.onCreate()");
		
		addNotificationIcon();
		WiFiAutoRebooterReceiver.scheduleNextWiFiCheck(getApplicationContext(), ROUTER_IP, 0);
		finish();
	}
	
	private void addNotificationIcon() {
		Notification n = new Notification();
		n.icon = R.drawable.ic_launcher;
		n.tickerText = getResources().getString(R.string.notification_text);
		n.contentView = new RemoteViews("me.vpu.wifiautorebooter", R.layout.notificationlayout);
		n.flags = Notification.FLAG_NO_CLEAR;
		
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(NOTIFICATION_ID, n);
	}
}
