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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class BGGetPTZPoints extends AsyncTask<String, Void, String> {
	Context app_context;
	private String cam_URL, cam_CGI, user_Pass;
	//private String PTZ_PREFS;

	public BGGetPTZPoints(Context context) {
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
			pass = SimpleCrypto.decrypt(mac, pass);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.user_Pass = "usr=" + sharedPrefs.getString("prefUsername", "NULL") + "&pwd=" + pass;
		this.cam_URL = sharedPrefs.getString("prefCamURL", "NULL");
	}

	@Override
	protected String doInBackground(String... params) {
		SharedPreferences prefs = app_context.getSharedPreferences(app_context.getPackageName() + "_preferences", 0);
		boolean doHttps = prefs.getBoolean("prefCamHTTPS", false);

		String urlString = cam_URL + cam_CGI + "?cmd=getPTZPresetPointList" + "&" + user_Pass;
		StringBuilder result = new StringBuilder();

		try {
			NetworkUtils net = new NetworkUtils(urlString, doHttps);

			Document doc = net.getXMLDocument();

			if (doc == null) {
				return "fail";
			}

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("result");

			Node n = nList.item(0);

			Element e = (Element) n;

			if (!e.getTextContent().equals("0")) {
				Toast toast = Toast.makeText(app_context, "Failed to obtain PTZ points.", Toast.LENGTH_SHORT);
				toast.show();
				return "fail";
			}

			// get count of ptz points
			nList = doc.getElementsByTagName("cnt");
			e = (Element) nList.item(0);

			int ptzCount = Integer.parseInt(e.getTextContent());

			for (int i=0; i<ptzCount; i++) {
				if (i!=0 && i!=ptzCount)
					result.append(";");

				nList = doc.getElementsByTagName("point" + i);
				e = (Element) nList.item(0);

				result.append(e.getTextContent());

			}


		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

		return result.toString();
	}

	protected void onPostExecute(String results) {

		String[] ptzPoints;

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

		ptzPoints = results.split(";");

		SharedPreferences sharedPrefs = app_context.getSharedPreferences(app_context.getPackageName() + "_preferences", 0);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString("ptzPoints", results);

		editor.commit();


		FragmentActivity mainActivity = (FragmentActivity) app_context;
		RadioGroup rg = (RadioGroup) mainActivity.findViewById(R.id.ptzGroup);

		if (rg == null)
			return;

		rg.removeAllViews();

		for (int i=0; i<ptzPoints.length; i++ ) {
			//Add RadioButtons
			RadioButton rb = new RadioButton(app_context);
			rb.setText(ptzPoints[i]);
			rg.addView(rb);
		}


	}

}
