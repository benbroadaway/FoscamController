package com.benbroadaway.foscamcontroller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.widget.Toast;

public class BGGoToPTZPoint extends AsyncTask<String, Void, String> {
	
	String ptz_Point;
	Context app_context;
	
	public BGGoToPTZPoint(String ptzPoint, Context context) {
		this.app_context = context;
		this.ptz_Point = ptzPoint;
	}
	
	@Override
	protected String doInBackground(String... params) {
		String result;
		
		SharedPreferences sharedPrefs = ((FragmentActivity) app_context).getSharedPreferences(app_context.getPackageName() + "_preferences", 0);
		
        WifiManager wm = (WifiManager) app_context.getSystemService(Context.WIFI_SERVICE);
		String mac = wm.getConnectionInfo().getMacAddress();
		mac = mac.replace(":", "");
		mac = mac + mac.substring(mac.length() - 4, mac.length());
		
		String pass = sharedPrefs.getString("prefPassword", "NULL");
		pass = pass.replaceFirst("encENC", "");
		
        try {
			pass = SimpleCrypto.decrypt(mac, pass);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String userPass = "usr=" + sharedPrefs.getString("prefUsername", "NULL") + "&pwd=" + pass;
		String camURL = sharedPrefs.getString("prefCamURL", "NULL");
		String camCGI = ((FragmentActivity) app_context).getResources().getString(R.string.camCGI);
		
		String urlString = camURL + camCGI + "?cmd=ptzGotoPresetPoint&name=" + ptz_Point + "&" +userPass;
		
		NetworkUtils net = new NetworkUtils(urlString);
		
		Document doc = net.getXMLDocument();
		
		if (doc == null) {
			result = "fail";
			return result;
		}
		
		doc.getDocumentElement().normalize();
		
		NodeList nList = doc.getElementsByTagName("result");
		
		Node n = nList.item(0);
		
		Element e = (Element) n;
		
		if (e.getTextContent().equals("0")) {
			result = "success";
		} else {
			Toast toast = Toast.makeText(app_context, "Command failed!", Toast.LENGTH_SHORT);
			toast.show();
			result = "fail";
		}
		
		return result;
	}      

	@Override
	protected void onPostExecute(String result) {
		// Do something to the interface
		if (result.equals("success")) {
			Toast toast = Toast.makeText(app_context, "Success!", Toast.LENGTH_SHORT);
			toast.show();			
		} else if (result.equals("fail")) {
			Toast toast = Toast.makeText(app_context, "Failure!", Toast.LENGTH_SHORT);
			toast.show();
		} else {
			Toast toast = Toast.makeText(app_context, "Something went bad wrong!", Toast.LENGTH_SHORT);
			toast.show();
		}

  	}
}
