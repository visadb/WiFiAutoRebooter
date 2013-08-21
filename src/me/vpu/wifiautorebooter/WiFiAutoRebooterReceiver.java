package me.vpu.wifiautorebooter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WiFiAutoRebooterReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Log.d(WiFiAutoRebooterStarter.LOG_TAG, "WiFiAutoRebooterReceiver.onReceive()");
		try {
			InetAddress router = InetAddress.getByName("127.0.0.1");
			boolean isReachable = router.isReachable(300);
			Log.d(WiFiAutoRebooterStarter.LOG_TAG, "Router reachable: "+isReachable);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
