package com.benbroadaway.foscamcontroller;

import java.util.Locale;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
	Context context;
	
	public SectionsPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		Fragment f = null;
		switch (position) {
		case 0:
			f =  new PTZPointsFragment();
			break;
		case 1:
			f = new MotionDetectionFragment();
			break;
		case 2:
			f = new SnapshotFragment();
			break;
		}
		
		return f;
	}

	@Override
	public int getCount() {
		// Show 3 total pages.
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return ((FragmentActivity)context).getResources().getString(R.string.title_PTZ_Points).toUpperCase(l);
		case 1:
			return ((FragmentActivity)context).getResources().getString(R.string.title_Motion_Detection).toUpperCase(l);
		case 2:
			return ((FragmentActivity)context).getResources().getString(R.string.title_Snapshot).toUpperCase(l);
		}
		return null;
	}
}
