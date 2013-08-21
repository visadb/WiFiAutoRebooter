package me.vpu.wifiautorebooter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

public class WiFiAutoRebooterReceiver extends BroadcastReceiver {
	private static final String LOG_TAG = WiFiAutoRebooterReceiver.class.getName();
	private static final int checkIntervalInSeconds = 30;
	private static String ROUTER_IP_KEY = "routerIP";
	private static String DEFAULT_ROUTER_IP = "192.168.0.1";
	
	@Override
	public void onReceive(Context mCtx, Intent intent) {
		Log.d(LOG_TAG, "WiFiAutoRebooterReceiver.onReceive()");

		String routerIP = intent.getExtras().getString(ROUTER_IP_KEY);
		routerIP = routerIP==null ? DEFAULT_ROUTER_IP : routerIP;
		new CheckRouterConnectivityTask(mCtx, routerIP).execute();

		scheduleNextWiFiCheck(mCtx, routerIP, checkIntervalInSeconds * 1000);
	}
	
	private void onRouterConnectivityCheckComplete(Context mCtx, String routerIP, boolean routerReachable) {
		Log.d(LOG_TAG, "Router "+routerIP+" reachable: "+routerReachable);
		if (!routerReachable) {
			WifiManager ws = (WifiManager)mCtx.getSystemService(Context.WIFI_SERVICE);
			boolean reassociationSucceeded = ws.reassociate();
			Log.d(LOG_TAG, "Reassociation succeeded: "+reassociationSucceeded);
		}
	}
	
	public static void scheduleNextWiFiCheck(Context mCtx, String routerIP, long delayInMillis) {
		Intent alarmIntent = new Intent(mCtx, WiFiAutoRebooterReceiver.class);
		alarmIntent.putExtra(ROUTER_IP_KEY, routerIP);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(mCtx, 0, alarmIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager)mCtx.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis() + delayInMillis, pendingIntent);
	}
	
	
	private class CheckRouterConnectivityTask extends AsyncTask<Void, Void, Boolean> {
		Context mCtx;
		String routerIP;
		
		public CheckRouterConnectivityTask(Context mCtx, String routerIP) {
			super();
			this.mCtx = mCtx;
			this.routerIP = routerIP;
		}
		
		@Override
		protected Boolean doInBackground(Void... args) {
			try {
				InetAddress router = InetAddress.getByName(routerIP);
				boolean isReachable = router.isReachable(1000);
				return isReachable;
			} catch (Throwable t) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				t.printStackTrace(pw);
				Log.e(LOG_TAG, sw.toString());
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			onRouterConnectivityCheckComplete(mCtx, routerIP, result);
		}
	
	}
}