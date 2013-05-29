package com.benbroadaway.foscamcontroller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class PTZPointsFragment extends Fragment {
	private static final String PTZ_PREFS = "PTZPreferences";
	private FoscamController mainActivity;
	private String[] ptzPoints;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View rootView = inflater
				.inflate(R.layout.fragment_ptz_points,
						container, false);
		
			
		//String[] ptzPoints = res.getStringArray(R.array.ptz_points_array);
		mainActivity = (FoscamController) rootView.getContext();		
		
		ptzPoints = mainActivity.ptzPoints;
		
		if (ptzPoints == null) {
			SharedPreferences settings = mainActivity.getSharedPreferences(PTZ_PREFS, 0);
			String str = settings.getString("ptzPoints", "");
		       
			if (!str.equals("")) {
				ptzPoints = str.split(";");
				mainActivity.ptzPoints = ptzPoints;
			} else {
				return rootView;
			}
		} else {
			StringBuilder str = new StringBuilder();
			
			for (int i=0; i<ptzPoints.length; i++) {
				if (i!=0 && i!=ptzPoints.length) 
					str.append(";");
				
				str.append(ptzPoints[i]);
			}
			
			SharedPreferences settings = mainActivity.getSharedPreferences(PTZ_PREFS, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("ptzPoints", str.toString());
			
			editor.commit();
		}
		
		
        RadioGroup rg = (RadioGroup) rootView.findViewById(R.id.ptzGroup);

        for (int i=0; i<ptzPoints.length; i++ ) {
        	//Add RadioButtons
	        RadioButton rb = new RadioButton(rootView.getContext());
	        rb.setText(ptzPoints[i]);
	        rg.addView(rb);
        }
		
		return rootView;
	}
	
	@Override
	public void onPause() {
		super.onPause();
			
		if (ptzPoints == null) {
			return;
		}
		
		StringBuilder str = new StringBuilder();
		
		for (int i=0; i<ptzPoints.length; i++) {
			if (i!=0 && i!= ptzPoints.length)
				str.append(";");
			
			str.append(ptzPoints[i]);
		}
		
		
		// We need an Editor object to make preference changes.
	      // All objects are from android.context.Context
	      SharedPreferences settings = mainActivity.getSharedPreferences(PTZ_PREFS, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString("ptzPoints", str.toString());

	      // Commit the edits!
	      editor.commit();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (mainActivity == null)
			return;
		
		SharedPreferences settings = mainActivity.getSharedPreferences(PTZ_PREFS, 0);
	       String str = settings.getString("ptzPoints", "");
	       
	       if (!str.equals("")) {
	    	   ptzPoints = str.split(";");
	       }
	}
	

}
