/*
 *   Copyright 2013 Candace Bain
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.candacebain.lakemendotabuoy;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements
OnSharedPreferenceChangeListener{

	public static final String KEY_PREF_WIND_SPEED_UNITS = "pref_windSpeedUnits";
	public static final String KEY_PREF_DISTANCE_UNITS = "pref_distanceUnits";
	public static final String KEY_PREF_TEMPERATURE_UNITS = "pref_temperatureUnits";
	public static final String KEY_PREF_UPDATE_INTERVAL = "pref_updateInterval";
	public static final String KEY_PREF_DISPLAY_ADDITIONAL_DATA = "pref_displayAdditionalData";

	private ListPreference prefUpdateInterval;
	private ListPreference prefWindSpeedUnits;
	private ListPreference prefTemperatureUnits;
	private ListPreference prefDistanceUnits;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate()
	 */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		/* Find our controls */
		prefUpdateInterval = (ListPreference) getPreferenceScreen()
				.findPreference(KEY_PREF_UPDATE_INTERVAL);
		prefWindSpeedUnits = (ListPreference) getPreferenceScreen()
				.findPreference(KEY_PREF_WIND_SPEED_UNITS);
		prefTemperatureUnits = (ListPreference) getPreferenceScreen()
				.findPreference(KEY_PREF_TEMPERATURE_UNITS);
		prefDistanceUnits = (ListPreference) getPreferenceScreen()
				.findPreference(KEY_PREF_DISTANCE_UNITS);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
    protected void onResume() {
        super.onResume();
        
        // Setup the initial values
        prefUpdateInterval.setSummary(prefUpdateInterval.getEntry());
        prefWindSpeedUnits.setSummary(prefWindSpeedUnits.getEntry());
        prefTemperatureUnits.setSummary(prefTemperatureUnits.getEntry());
        prefDistanceUnits.setSummary(prefDistanceUnits.getEntry());
        
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

	/**
	 * A preference changed, update our summary text values
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		switch (key) {
			case KEY_PREF_UPDATE_INTERVAL:
				prefUpdateInterval.setSummary(prefUpdateInterval.getEntry());
				break;
			case KEY_PREF_WIND_SPEED_UNITS:
				prefWindSpeedUnits.setSummary(prefWindSpeedUnits.getEntry());
				break;
			case KEY_PREF_TEMPERATURE_UNITS:
				prefTemperatureUnits.setSummary(prefTemperatureUnits.getEntry());
				break;
			case KEY_PREF_DISTANCE_UNITS:
				prefDistanceUnits.setSummary(prefDistanceUnits.getEntry());
				break;
		}
	}
}