package me.vpu.wifiautorebooter;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.util.Log;

public class Util {
	public static void logStackTrace(String logTag, Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		Log.e(logTag, sw.toString());
	}
}
