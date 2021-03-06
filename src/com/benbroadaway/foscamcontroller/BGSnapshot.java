package com.benbroadaway.foscamcontroller;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;

public class BGSnapshot extends AsyncTask<String, Void, String> {
	
	String cam_URL, cam_CGI, user_Pass;
	private Context app_context;
	private Bitmap snapshot;
	private boolean saveToGallery;
	
	public BGSnapshot(Context context) {

		this.app_context = context;
		
		SharedPreferences sharedPrefs = app_context.getSharedPreferences(app_context.getPackageName() + "_preferences", 0);
		
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
		
		this.user_Pass = "usr=" + sharedPrefs.getString("prefUsername", "NULL") + "&pwd=" + pass;
		this.cam_URL = sharedPrefs.getString("prefCamURL", "NULL");
		this.cam_CGI = ((FragmentActivity) app_context).getResources().getString(R.string.camCGI);
		this.saveToGallery = sharedPrefs.getBoolean("prefSaveToGallery", false);
	}
	
	
	@Override
	protected String doInBackground(String... params) {
		String result = "";
		String urlString = cam_URL + cam_CGI + "?cmd=snapPicture2&" + user_Pass;
		
		SharedPreferences prefs = app_context.getSharedPreferences(app_context.getPackageName() + "_preferences", 0);
		boolean doHttps = prefs.getBoolean("prefCamHTTPS", false);
		
		NetworkUtils net = new NetworkUtils(urlString, doHttps);
		
		snapshot = net.getBitmap();
		
		if (snapshot == null) {
			result = "fail";
		} else {
			result = saveSnapshot();
		}
	
		return result;
	}
	
	private String saveSnapshot() {
		String result = "fail";
		try {
			String loc = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
			File file = new File(loc, "snapshot.png");
			
			FileOutputStream out = new FileOutputStream(file);
			snapshot.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
			result = "success";
		} catch (Exception ex) {
			result = "fail";
			Log.e("FoscamController", "Couldn't save snapshot");
		}
		
		return result;
	}
	
	@Override
	protected void onPostExecute(String result) {
		if (result.equals("success") && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			
			try {
				ImageView imgSnapshot = (ImageView) ((FragmentActivity) app_context).findViewById(R.id.imgSnapshot);
				imgSnapshot.setImageBitmap(snapshot);

				if (saveToGallery) {
					
					String date = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Calendar.getInstance().getTime());
					
					MediaStore.Images.Media.insertImage(app_context.getContentResolver(), snapshot, "cam_snapshot_" + date , "snapshot from foscam camera");
				}

				
				Log.i("FoscamController", "Took a snapshot");

			} catch (Exception ex) {
				Log.e("FoscamController", "Couldn't update snapshot");
			}
		}
	}
}
