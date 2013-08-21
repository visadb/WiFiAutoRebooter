package me.vpu.wifiautorebooter;

import java.net.HttpURLConnection;
import java.net.URL;
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
	private static final int checkIntervalInSeconds = 5 * 60;
	
	@Override
	public void onReceive(Context mCtx, Intent intent) {
		Log.d(LOG_TAG, "WiFiAutoRebooterReceiver.onReceive()");

		new CheckInternetConnectivityTask(mCtx).execute();

		scheduleNextWiFiCheck(mCtx, checkIntervalInSeconds * 1000);
	}
	
	private void onInternetConnectivityCheckComplete(Context mCtx, boolean internetReachable) {
		Log.d(LOG_TAG, "Internet connected: "+internetReachable);
		if (!internetReachable) {
			WifiManager ws = (WifiManager)mCtx.getSystemService(Context.WIFI_SERVICE);
			boolean disableSucceeded = ws.setWifiEnabled(false);
			boolean enableSucceeded = ws.setWifiEnabled(true);
			Log.d(LOG_TAG, "Toggling wifi: "+disableSucceeded+"/"+enableSucceeded);
		}
	}
	
	public static void scheduleNextWiFiCheck(Context mCtx, long delayInMillis) {
		Intent alarmIntent = new Intent(mCtx, WiFiAutoRebooterReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(mCtx, 0, alarmIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager)mCtx.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis() + delayInMillis, pendingIntent);
	}
	
	
	private class CheckInternetConnectivityTask extends AsyncTask<Void, Void, Boolean> {
		private Context mCtx;
		
		public CheckInternetConnectivityTask(Context mCtx) {
			super();
			this.mCtx = mCtx;
		}
		
		@Override
		protected Boolean doInBackground(Void... args) {
			HttpURLConnection urlConnect = null;
			try {
				URL url = new URL("http://www.google.com/");
				urlConnect = (HttpURLConnection)url.openConnection();
				urlConnect.setRequestMethod("HEAD");
				urlConnect.setRequestProperty("Accept-Encoding", "");
				urlConnect.getContent();
			} catch (Exception e) {
				return false;
			} finally {
				if (urlConnect != null)
					urlConnect.disconnect();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			onInternetConnectivityCheckComplete(mCtx, result);
		}
	
	}
}