package com.benbroadaway.foscamcontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
 
public class UserSettingsActivity extends PreferenceActivity {
	
	OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener() {
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			if (key.equals("prefPassword") && !prefs.getString(key, "NULL").contains("encENC")) {
				String clearPass = prefs.getString(key, "NULL");
				
				
				WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				String mac = wm.getConnectionInfo().getMacAddress();
				mac = mac.replace(":", "");
				mac = mac + mac.substring(mac.length() - 4, mac.length());
				
				
				Editor editor = prefs.edit();
				String encryptedPass = clearPass;
				try {
					encryptedPass = "encENC" + SimpleCrypto.encrypt(mac, clearPass);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				editor.putString(key, encryptedPass);
				editor.commit();
			}
		}
	}; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //addPreferencesFromResource(R.xml.settings);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        
        
        String packageName = this.getPackageName();        
        SharedPreferences sharedPrefs = this.getSharedPreferences(packageName + "_preferences", 0);
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener);
    }
    
    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }
}
