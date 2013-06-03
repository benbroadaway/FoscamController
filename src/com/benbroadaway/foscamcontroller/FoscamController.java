package com.benbroadaway.foscamcontroller;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

public class FoscamController extends FragmentActivity implements
ActionBar.TabListener {

	private static final int RESULT_SETTINGS = 1;

	public String ptzPoint;

	public static final  String cam_URL = "https://broadaway.isa-geek.net:55443";
	public static final  String cam_CGI = "/cgi-bin/CGIProxy.fcgi";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_foscam_controller);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(),
				this);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.foscam_controller, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_settings:
			Intent i = new Intent(this, UserSettingsActivity.class);
			//this.startActivity(i);
			startActivityForResult(i, RESULT_SETTINGS);
			break;

		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SETTINGS:
			// do something after settings activity is closed
			break;
		}
	}


	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public void onToggleClicked(View view) {
		// Is the toggle on?
		Switch sw = (Switch) view;
		boolean on = sw.isChecked();

		if (on) {
			// Enable vibrate
		} else {
			// Disable vibrate
		}
	}

	public void RefreshPTZPoints(View view) {
		BGGetPTZPoints ptzSetup = new BGGetPTZPoints(this);
		ptzSetup.execute();
	}

	public void goToPTZPoint(View view) {
		//detect the view that was "clicked"
		switch(view.getId())
		{
		case R.id.btnPtzSubmit:
			RadioGroup rg = (RadioGroup) findViewById(R.id.ptzGroup);
			ptzPoint = ((RadioButton) findViewById(rg.getCheckedRadioButtonId())).getText().toString();

			new BGGoToPTZPoint(ptzPoint, this).execute();
			break;

		}
	}	

	public void takeSnapshot(View view) {
		BGSnapshot snapper = new BGSnapshot(this);
		snapper.execute();
	}
	
	public void moveUp(View view) {
		BGMovePTZ mover = new BGMovePTZ(this, BGMovePTZ.PTZDirection.UP);
		mover.execute();
	}
	
	public void moveDown(View view) {
		BGMovePTZ mover = new BGMovePTZ(this, BGMovePTZ.PTZDirection.DOWN);
		mover.execute();
	}
	
	public void moveLeft(View view) {
		BGMovePTZ mover = new BGMovePTZ(this, BGMovePTZ.PTZDirection.LEFT);
		mover.execute();
	}
	
	public void moveRight(View view) {
		BGMovePTZ mover = new BGMovePTZ(this, BGMovePTZ.PTZDirection.RIGHT);
		mover.execute();
	}
}
