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
	@SuppressWarnings("deprecation")
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
    @SuppressWarnings("deprecation")
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
    @SuppressWarnings("deprecation")
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
		
		if (key.equals(KEY_PREF_UPDATE_INTERVAL)){
			prefUpdateInterval.setSummary(prefUpdateInterval.getEntry());
		}
		else if (key.equals(KEY_PREF_WIND_SPEED_UNITS)){
			prefWindSpeedUnits.setSummary(prefWindSpeedUnits.getEntry());
		}
		else if (key.equals(KEY_PREF_TEMPERATURE_UNITS)){
			prefTemperatureUnits.setSummary(prefTemperatureUnits.getEntry());
		}
		else if (key.equals(KEY_PREF_DISTANCE_UNITS)){
			prefDistanceUnits.setSummary(prefDistanceUnits.getEntry());
		}
	}
}