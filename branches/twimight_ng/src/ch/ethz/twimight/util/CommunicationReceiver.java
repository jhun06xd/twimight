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
package ch.ethz.twimight.util;

import java.util.Date;

import ch.ethz.twimight.net.tds.TDSAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Listends for changes in connectivity and starts the TDSThread if a new connection
 * is detected.
 * @author thossmann
 *
 */
public class CommunicationReceiver extends BroadcastReceiver {
	
	private static final String TAG = "CommunicationReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		
		// connectivity changed!
		NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		Log.i(TAG, "Connectivity changed: " + currentNetworkInfo.toString() + " " + (new Date()).toString());
		
		// are we connected?
		try{
			// TDS communication
			if(currentNetworkInfo.isConnected() && TDSAlarm.isTdsEnabled(context)){
				// remove currently scheduled updates and schedule an immediate one
				new TDSAlarm();
			}
			
			// Twitter update
			// TODO
		} catch (Exception e) {
			Log.e(TAG, "Error on connectivity change");
		}
			
		
	}
	
	
}