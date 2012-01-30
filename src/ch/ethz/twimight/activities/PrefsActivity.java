/*******************************************************************************
 * Copyright (c) 2011 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Paolo Carta - Implementation
 *     Theus Hossmann - Implementation
 *     Dominik Schatzmann - Message specification
 ******************************************************************************/
package ch.ethz.twimight.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import ch.ethz.twimight.R;
import ch.ethz.twimight.location.LocationAlarm;
import ch.ethz.twimight.net.opportunistic.ScanningAlarm;
import ch.ethz.twimight.net.tds.TDSAlarm;
import ch.ethz.twimight.net.twitter.TwitterAlarm;
import ch.ethz.twimight.util.Constants;

/**
 * Shows the preferences.
 * @author thossmann
 * @author pcarta
 */
public class PrefsActivity extends PreferenceActivity{

	protected static final String TAG = "PreferenceActivity";

	private OnSharedPreferenceChangeListener prefListener;
	private SharedPreferences prefs;

	// the menu
	private static final int OPTIONS_MENU_HOME = 10;

	/**
	 * Set everything up.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		prefListener = new OnSharedPreferenceChangeListener() {

			// this is where we take action after the user changes a setting!
			public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {

				if (key.equals("prefDisasterMode")) { // toggle disaster mode
					if(preferences.getBoolean("prefDisasterMode", Constants.DISASTER_DEFAULT_ON) == true){
						new ScanningAlarm(getApplicationContext(),0,true);
						finish();
						Log.i(TAG, "start scanning");
						Log.i(TAG, PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("mac", null) );
					} else {
						ScanningAlarm.stopScanning(getApplicationContext());
						finish();
						Log.i(TAG, "stop scanning");
					}
				} else if(key.equals("prefTDSCommunication")){ // toggle TDS communication
					if(preferences.getBoolean("prefTDSCommunication",	Constants.TDS_DEFAULT_ON) == true){
						new TDSAlarm(getApplicationContext(), Constants.TDS_UPDATE_INTERVAL);
						Log.i(TAG, "start TDS communication");
					} else {
						//stopService(new Intent(getApplicationContext(), TDSService.class));
						TDSAlarm.stopTDSCommuniction(getApplicationContext());
						Log.i(TAG, "stop TDS communication");
					}
				} else if(key.equals("prefLocationUpdates")) { // toggle Location updates
					if(preferences.getBoolean("prefLocationUpdates", Constants.LOCATION_DEFAULT_ON) == true){
						new LocationAlarm(getApplicationContext(), Constants.LOCATION_UPDATE_TIME);
						Log.i(TAG, "start location service");
					} else {
						LocationAlarm.stopLocationUpdate(getApplicationContext());
						Log.i(TAG, "stop location service");
					}
				} else if (key.equals("prefRunAtBoot")) {
					if (preferences.getBoolean("prefRunAtBoot", Constants.TWEET_DEFAULT_RUN_AT_BOOT) == true ) {
						new TwitterAlarm(getBaseContext(),false);
						Log.i(TAG, "start background updater");
					} else {
						TwitterAlarm.stopTwitterAlarm(getBaseContext());
					}
				}
			}
		};

	}
	
	/**
	 * Important: register shared preference change listener here!
	 */
	@Override
	public void onResume(){
		super.onResume();
		prefs.registerOnSharedPreferenceChangeListener(prefListener);
	}
	
	/**
	 * Important: unregister shared preferece chnage listener here!
	 */
	@Override
	public void onPause(){
		super.onPause();
		prefs.unregisterOnSharedPreferenceChangeListener(prefListener);
	}
	
	/**
	 * Populate the Options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		menu.add(1, OPTIONS_MENU_HOME, 1, "Home");
		return true;
	}

	/**
	 * Handle options menu selection
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){

		Intent i;
		switch(item.getItemId()){
		
		case OPTIONS_MENU_HOME:
			// show the timeline
			i = new Intent(this, ShowTweetListActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			break;
		default:
			return false;
		}
		return true;
	}

}