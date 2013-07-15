package com.candacebain.lakemendotabuoy;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	
	public static final String KEY_PREF_WIND_SPEED_UNITS = "pref_windSpeedUnits";
	public static final String KEY_PREF_DISTANCE_UNITS = "pref_distanceUnits";
	public static final String KEY_PREF_TEMPERATURE_UNITS = "pref_temperatureUnits";
	public static final String KEY_PREF_UPDATE_INTERVAL = "pref_updateInterval";
	
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}