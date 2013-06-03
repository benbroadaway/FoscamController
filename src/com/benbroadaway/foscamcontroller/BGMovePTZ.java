package com.benbroadaway.foscamcontroller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class BGMovePTZ extends AsyncTask<String, Void, String>  {
	
	private Context app_context;
	private String cam_URL, cam_CGI, user_Pass;
	private PTZDirection ptz_Direction;
	
	public enum PTZDirection {
		UP(0), DOWN(1), LEFT(2), RIGHT(3);
		private int value;
		
		private PTZDirection(int value) {
			this.value = value;
		}
		
		public String toString() {
			switch (value) {
			case 0:
				return "Up";
			case 1:
				return "Down";
			case 2:
				return "Left";
			case 3:
				return "Right";
			default:
				return "";
			}
		}
	}
	
	
	
	public BGMovePTZ(Context context, PTZDirection ptzDirection) {
		this.app_context = context;
		this.cam_CGI = ((FragmentActivity) app_context).getResources().getString(R.string.camCGI);
		
		SharedPreferences sharedPrefs = app_context.getSharedPreferences(app_context.getPackageName() + "_preferences", 0);
		
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		String mac = wm.getConnectionInfo().getMacAddress();
		mac = mac.replace(":", "");
		mac = mac + mac.substring(mac.length() - 4, mac.length());

		String pass = sharedPrefs.getString("prefPassword", "NULL");
		pass = pass.replaceFirst("encENC", "");
		
		try {
			pass = SimpleCrypto.decrypt(mac,  pass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.user_Pass = "usr=" + sharedPrefs.getString("prefUsername", "NULL") + "&pwd=" + pass;
		this.cam_URL = sharedPrefs.getString("prefCamURL", "NULL");
		
		this.ptz_Direction = ptzDirection;
	}

	@Override
	protected String doInBackground(String... params) {
		SharedPreferences prefs = app_context.getSharedPreferences(app_context.getPackageName() + "_preferences", 0);
		boolean doHttps = prefs.getBoolean("prefCamHTTPS", false);
		
		
		String startUrlString = "";
		String endUrlString   = cam_URL + cam_CGI + "?cmd=ptzStopRun" + "&" + user_Pass;

		String result = "fail";
		
		switch (ptz_Direction) {
		case UP:
			startUrlString = cam_URL + cam_CGI + "?cmd=ptzMoveUp" + "&" + user_Pass;
			break;
		case DOWN:
			startUrlString = cam_URL + cam_CGI + "?cmd=ptzMoveDown" + "&" + user_Pass;
			break;
		case LEFT:
			startUrlString = cam_URL + cam_CGI + "?cmd=ptzMoveLeft" + "&" + user_Pass;
			break;
		case RIGHT:
			startUrlString = cam_URL + cam_CGI + "?cmd=ptzMoveRight" + "&" + user_Pass;
			break;
		}
		
		try {
			NetworkUtils startMove = new NetworkUtils(startUrlString, doHttps);
			
			startMove.getXMLDocument();	
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		try {

			NetworkUtils endMove = new NetworkUtils(endUrlString, doHttps);
			endMove.getXMLDocument();
			
			result = "success";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return result.toString();
	}
	
	protected void onPostExecute(String results) {
		// Do something to the interface
		if (results == null) {
			Toast toast = Toast.makeText(app_context, "Terrible Failure! Bad password?", Toast.LENGTH_SHORT);
			toast.show();
			return;
		} else if (results.equals("fail")) {
			Toast toast = Toast.makeText(app_context, "Failure!", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}
		
		BGSnapshot snapper = new BGSnapshot(app_context);
		snapper.execute();
		
	}

}
