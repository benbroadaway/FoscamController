package com.benbroadaway.foscamcontroller;

//import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class LiveMoveFragment extends Fragment {
	//private Context mainActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_live_move, container, false);
		
		//mainActivity = rootView.getContext();
		
		
		
		return rootView;
	}
}
