package com.benbroadaway.foscamcontroller;

import java.io.File;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.support.v4.app.Fragment;

public class SnapshotFragment extends Fragment {
	
	Bitmap snapshot;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View rootView = inflater.inflate(R.layout.fragment_snapshot, container, false);
		
		try {
			String loc = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
			File file = new File(loc, "snapshot.png");
			
			snapshot = BitmapFactory.decodeFile(file.getAbsolutePath());
			
			ImageView imgSnapshot = (ImageView) rootView.findViewById(R.id.imgSnapshot);
			imgSnapshot.setImageBitmap(snapshot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rootView;
	}
}
