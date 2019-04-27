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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MainActivity extends Activity {

	/**
	 * The source of all our buoy data
	 */
	private static final String dataQueryURL = "http://metobs.ssec.wisc.edu/app/mendota/buoy/data/json?symbols=t:rh:td:spd:dir:gust:wt_0.0:wt_1.0:wt_5.0:wt_10.0:wt_15.0:wt_20.0:do_ppm:do_sat:chlor:pc";

    /**
     * Extra status information about the buoy
     */
    private static final String statusQueryURL = "https://s3-us-west-2.amazonaws.com/lakemendotabuoy/buoy_status.json";

	/**
	 * Used to know when we've returned from the settings view
	 */
	private static final int SETTINGS_RESPONSE = 1;

	/**
	 * Format double values for display
	 */

	private static final DecimalFormat decimalFormat = new DecimalFormat(
			"##0.0");

    /**
	 * Parse and format date values
	 */
	private static final SimpleDateFormat outputDateFormat = new SimpleDateFormat(
			"MMM dd, yyyy h:mm a", Locale.US);

    /**
     * Used to parse the JSON data we we get from the server
     */
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).serializeSpecialFloatingPointValues().create();

	/**
	 * Wind directions text array
	 */
	private String[] windDirections = null;

	/**
	 * Views we might want to interact with
	 */
	private TextView windDirection;
	private TextView windSpeed;
	private TextView windGust;
	private TextView airTemperature;
	private TextView humidity;

	private TextView surfaceTemperature;
	private TextView oneMeterTemperature;
	private TextView fiveMeterTemperature;
	private TextView tenMeterTemperature;
	private TextView fifteenMeterTemperature;
	private TextView twentyMeterTemperature;

	private TextView oneMeterCaption;
	private TextView fiveMeterCaption;
	private TextView tenMeterCaption;
	private TextView fifteenMeterCaption;
	private TextView twentyMeterCaption;

	private TextView dewPoint;
	private TextView dissolvedOxygen;
	private TextView dissolvedOxygenSaturation;
	private TextView chlorophyll;
	private TextView phycocyanin;

	private TextView additionalDataCaption;
	private View additionalDataLine;
	private TableLayout additionalDataTable;

    private TextView messageDataCaption;
    private View messageDataLine;
    private TextView messageText;

	private TextView updatedAt;
	private ProgressBar updateProgressBar;

	private Timer updateTimer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate()
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);

		// Get values from resources
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		if (windDirections == null) {
			windDirections = getResources().getStringArray(
					R.array.windDirections);
		}
		setContentView(R.layout.activity_main);

		// Set up our views
		initializeViews();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();

		// Stop updating our data
		stopUpdateTimer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// Start updating our data
		startUpdateTimer();
	}

	/**
	 * Set up our options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Respond to options actions
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivityForResult(new Intent(this, SettingsActivity.class),
                    SETTINGS_RESPONSE);
			return true;
		case R.id.action_about:
			displayAboutDialog();
			return true;
		case R.id.action_update:
			updateData();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * If the user hasData just returned from the settings menu, update our
	 * measurement units and restart the update timer
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SETTINGS_RESPONSE) {
			setWaterDepthCaptions();
			setAdditionalDataVisibility();
			startUpdateTimer();
		}
	}

	/**
	 * Find the views we want to interact with while the application is running
	 */
	private void initializeViews() {
		windDirection = findViewById(R.id.wind_direction);
		windSpeed = findViewById(R.id.wind_speed);
		windGust = findViewById(R.id.wind_gust);
		airTemperature = findViewById(R.id.air_temperature);
		humidity = findViewById(R.id.humidity);

		surfaceTemperature = findViewById(R.id.surface_temperature);
		oneMeterTemperature = findViewById(R.id.one_meter_temperature);
		fiveMeterTemperature = findViewById(R.id.five_meter_temperature);
		tenMeterTemperature = findViewById(R.id.ten_meter_temperature);
		fifteenMeterTemperature = findViewById(R.id.fifteen_meter_temperature);
		twentyMeterTemperature = findViewById(R.id.twenty_meter_temperature);

		oneMeterCaption = findViewById(R.id.one_meter_temperature_caption);
		fiveMeterCaption = findViewById(R.id.five_meter_temperature_caption);
		tenMeterCaption = findViewById(R.id.ten_meter_temperature_caption);
		fifteenMeterCaption = findViewById(R.id.fifteen_meter_temperature_caption);
		twentyMeterCaption = findViewById(R.id.twenty_meter_temperature_caption);

		dewPoint = findViewById(R.id.dew_point);
		dissolvedOxygen = findViewById(R.id.dissolved_oxygen);
		dissolvedOxygenSaturation = findViewById(R.id.dissolved_oxygen_saturation);
		chlorophyll = findViewById(R.id.chlorophyll);
		phycocyanin = findViewById(R.id.phycocyanin);

		additionalDataCaption = findViewById(R.id.additional_data_caption);
		additionalDataLine = findViewById(R.id.additional_data_line);
		additionalDataTable = findViewById(R.id.additional_data_table);

        messageDataCaption = findViewById(R.id.message_caption);
        messageDataLine = findViewById(R.id.message_line);
        messageText = findViewById(R.id.message_text);

		updatedAt = findViewById(R.id.updated_at);
		updateProgressBar = findViewById(R.id.update_progress_bar);

		setWaterDepthCaptions();
		setAdditionalDataVisibility();

        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setNaN("-");
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
	}

	/**
	 * Changes the water depth captions based on the user's preferred distance
	 * units
	 */
	private void setWaterDepthCaptions() {
		String distanceUnits = PreferenceManager.getDefaultSharedPreferences(
                this).getString(SettingsActivity.KEY_PREF_DISTANCE_UNITS, "");
		if (getString(R.string.pref_distanceUnits_feet).equals(distanceUnits)) {
			oneMeterCaption
					.setText(getString(R.string.one_meter_temperature_caption_in_feet));
			fiveMeterCaption
					.setText(getString(R.string.five_meter_temperature_caption_in_feet));
			tenMeterCaption
					.setText(getString(R.string.ten_meter_temperature_caption_in_feet));
			fifteenMeterCaption
					.setText(getString(R.string.fifteen_meter_temperature_caption_in_feet));
			twentyMeterCaption
					.setText(getString(R.string.twenty_meter_temperature_caption_in_feet));
		} else if (getString(R.string.pref_distanceUnits_meters).equals(distanceUnits)) {
			oneMeterCaption
					.setText(getString(R.string.one_meter_temperature_caption));
			fiveMeterCaption
					.setText(getString(R.string.five_meter_temperature_caption));
			tenMeterCaption
					.setText(getString(R.string.ten_meter_temperature_caption));
			fifteenMeterCaption
					.setText(getString(R.string.fifteen_meter_temperature_caption));
			twentyMeterCaption
					.setText(getString(R.string.twenty_meter_temperature_caption));
		}
	}

	/**
	 * Changes the visibility of the additional data based on the user's
	 * preferences
	 */
	private void setAdditionalDataVisibility() {
		boolean showAdditionalData = PreferenceManager
				.getDefaultSharedPreferences(this).getBoolean(
                        SettingsActivity.KEY_PREF_DISPLAY_ADDITIONAL_DATA,
                        false);
		if (showAdditionalData) {
			additionalDataCaption.setVisibility(android.view.View.VISIBLE);
			additionalDataLine.setVisibility(android.view.View.VISIBLE);
			additionalDataTable.setVisibility(android.view.View.VISIBLE);
		} else {
			additionalDataCaption.setVisibility(android.view.View.GONE);
			additionalDataLine.setVisibility(android.view.View.GONE);
			additionalDataTable.setVisibility(android.view.View.GONE);
		}
	}

    /**
     * Displays a status message to the users
     *
     * @param showStatusMessage Whether the status message should be displayed
     */
    private void setStatusMessageVisibility(boolean showStatusMessage){
        if (showStatusMessage) {
            messageDataCaption.setVisibility(android.view.View.VISIBLE);
            messageDataLine.setVisibility(android.view.View.VISIBLE);
            messageText.setVisibility(android.view.View.VISIBLE);
        } else {
            messageDataCaption.setVisibility(android.view.View.GONE);
            messageDataLine.setVisibility(android.view.View.GONE);
            messageText.setVisibility(android.view.View.GONE);
        }
    }

	/**
	 * Sets up a timer to update the data on the schedule preferred by the user
	 */
	private void startUpdateTimer() {

		stopUpdateTimer();
		updateTimer = new Timer();

		int updateInterval = Integer.parseInt(PreferenceManager
				.getDefaultSharedPreferences(this).getString(
						SettingsActivity.KEY_PREF_UPDATE_INTERVAL, ""));
		if (updateInterval > 0) {
			// Update data now and at the interval specified by the user
			updateTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							updateData();
						}
					});
				}
			}, 0, updateInterval);
		} else {
			updateData();
		}
	}

	/**
	 * Stop updating when we're no longer focused on this view
	 */
	private void stopUpdateTimer() {
		if (updateTimer != null) {
			updateTimer.cancel();
			updateTimer = null;
		}
	}

	/**
	 * Format the wind direction value
	 * 
	 * @param windDirectionInDegrees The wind direction value
	 * @return A formatted wind direction string
	 */
	String formatWindDirection(Double windDirectionInDegrees) {
		if (Double.isNaN(windDirectionInDegrees) || windDirectionInDegrees <= 0 || windDirectionInDegrees > 360) {
			return getString(R.string.unknown_value);
		}

		// deal with special north case
		if ((windDirectionInDegrees >= 348.75 && windDirectionInDegrees <= 360)
				|| windDirectionInDegrees > 0 && windDirectionInDegrees < 11.25)
			return getString(R.string.wind_direction_N);

		double i = 11.25; // 1/2 degree increment between the directions
		for (String windDirection : windDirections) {
			if (windDirectionInDegrees >= i
					&& windDirectionInDegrees < (i + 22.5)) {
				return windDirection;
			}
			i += 22.5;
		}

		// shouldn't ever getMostRecentValue here
		return getString(R.string.unknown_value);
	}

	/**
	 * Format the temperature value according to the user's preferences,
	 * converting units if necessary
	 * 
	 * @param value The temperature value
	 * @return A formatted temperature string
	 */
	private String formatTemperature(Double value) {
        // A quick sanity check on the temperature
        if (Double.isNaN(value) || value <= 0 || value > 100){
            return "-";
        }

		// What units should we use to display temperature?
		String temperatureUnits = PreferenceManager
				.getDefaultSharedPreferences(this).getString(
                        SettingsActivity.KEY_PREF_TEMPERATURE_UNITS, "");
		boolean isTemperatureFahrenheit = "Fahrenheit".equals(temperatureUnits);

		String temperatureUnitSuffix = getString(R.string.celsius_suffix);
		if (isTemperatureFahrenheit) {
			temperatureUnitSuffix = getString(R.string.fahrenheit_suffix);
		}

		double useValue = value;
		if (isTemperatureFahrenheit) {
			useValue = ConversionUtil.celsiusToFahrenheit(value);
		}

		return (decimalFormat.format(useValue) + temperatureUnitSuffix);
	}

	/**
	 * Format the wind speed according to the user's preferences, converting
	 * units if necessary
	 * 
	 * @param value The wind speed value
	 * @return A formatted wind speed string
	 */
	private String formatWindSpeed(Double value) {
        if (Double.isNaN(value) || value < 0){
            return "-";
        }

		// What units should we use to display wind speed?
		String windSpeedUnits = PreferenceManager.getDefaultSharedPreferences(
				this).getString(SettingsActivity.KEY_PREF_WIND_SPEED_UNITS, "");
		boolean isWindSpeedMph = "Miles Per Hour".equals(windSpeedUnits);
		boolean isWindSpeedKnots = "Knots".equals(windSpeedUnits);

		// Set up the suffixes for the units
		String windSpeedUnitSuffix = " "
				+ getString(R.string.meters_per_second_suffix);
		if (isWindSpeedMph) {
			windSpeedUnitSuffix = " "
					+ getString(R.string.miles_per_hour_suffix);
		} else if (isWindSpeedKnots) {
			windSpeedUnitSuffix = " " + getString(R.string.knots_suffix);
		}

		double useValue = value;
		if (isWindSpeedMph) {
			useValue = ConversionUtil.metersPerSecondToMilesPerHour(value);
		} else if (isWindSpeedKnots) {
			useValue = ConversionUtil.metersPerSecondToKnots(value);
		}

		return (decimalFormat.format(useValue) + windSpeedUnitSuffix);
	}

	/**
	 * Format the humidity value for display
	 * 
	 * @param value our humidity value
	 * @return The formatted humidity string
	 */
	private String formatHumidity(Double value) {
        if (Double.isNaN(value) || value < 0){
            return "-";
        }

		String humidityUnitSuffix = getString(R.string.percent_suffix);
		return (decimalFormat.format(value) + " " + humidityUnitSuffix);
	}

	/**
	 * Display the data returned from the service
	 * 
	 * @param result the data we got back from the buoy server
	 */
	private void displayData(BuoyData result) {
        AppStatus appStatus = result.getAppStatus();

        if (appStatus == null || appStatus.isBuoyInWater()) {
            if (result.hasData(BuoyData.BuoyDataType.WIND_DIRECTION)) {
                windDirection.setText(formatWindDirection(result
                        .getMostRecentValue(BuoyData.BuoyDataType.WIND_DIRECTION)));
            }
            if (result.hasData(BuoyData.BuoyDataType.WIND_SPEED)) {
                windSpeed.setText(formatWindSpeed(result.getMostRecentValue(BuoyData.BuoyDataType.WIND_SPEED)));
            }
            if (result.hasData(BuoyData.BuoyDataType.WIND_GUST)) {
                windGust.setText(formatWindSpeed(result.getMostRecentValue(BuoyData.BuoyDataType.WIND_GUST)));
            }
            if (result.hasData(BuoyData.BuoyDataType.AIR_TEMP)) {
                airTemperature.setText(formatTemperature(result.getMostRecentValue(BuoyData.BuoyDataType.AIR_TEMP)));
            }
            if (result.hasData(BuoyData.BuoyDataType.REL_HUM)) {
                humidity.setText(formatHumidity(result.getMostRecentValue(BuoyData.BuoyDataType.REL_HUM)));
            }
            if (result.hasData(BuoyData.BuoyDataType.WATER_TEMP_0)) {
                surfaceTemperature.setText(formatTemperature(result
                        .getMostRecentValue(BuoyData.BuoyDataType.WATER_TEMP_0)));
            }
            if (result.hasData(BuoyData.BuoyDataType.WATER_TEMP_1)) {
                oneMeterTemperature.setText(formatTemperature(result
                        .getMostRecentValue(BuoyData.BuoyDataType.WATER_TEMP_1)));
            }
            if (result.hasData(BuoyData.BuoyDataType.WATER_TEMP_5)) {
                fiveMeterTemperature.setText(formatTemperature(result
                        .getMostRecentValue(BuoyData.BuoyDataType.WATER_TEMP_5)));
            }
            if (result.hasData(BuoyData.BuoyDataType.WATER_TEMP_10)) {
                tenMeterTemperature.setText(formatTemperature(result
                        .getMostRecentValue(BuoyData.BuoyDataType.WATER_TEMP_10)));
            }
            if (result.hasData(BuoyData.BuoyDataType.WATER_TEMP_15)) {
                fifteenMeterTemperature.setText(formatTemperature(result
                        .getMostRecentValue(BuoyData.BuoyDataType.WATER_TEMP_15)));
            }
            if (result.hasData(BuoyData.BuoyDataType.WATER_TEMP_20)) {
                twentyMeterTemperature.setText(formatTemperature(result
                        .getMostRecentValue(BuoyData.BuoyDataType.WATER_TEMP_20)));
            }
            if (result.hasData(BuoyData.BuoyDataType.DEWPOINT_CALC)) {
                dewPoint.setText(formatTemperature(result.getMostRecentValue(BuoyData.BuoyDataType.DEWPOINT_CALC)));
            }
            if (result.hasData(BuoyData.BuoyDataType.DO_SAT)) {
                dissolvedOxygen.setText(decimalFormat.format(result.getMostRecentValue(BuoyData.BuoyDataType.DO_SAT)));
            }
            if (result.hasData(BuoyData.BuoyDataType.DO_PPM)) {
                dissolvedOxygenSaturation.setText(decimalFormat.format(result
                        .getMostRecentValue(BuoyData.BuoyDataType.DO_PPM)));
            }
            if (result.hasData(BuoyData.BuoyDataType.CHLOROPHYLL)) {
                chlorophyll.setText(decimalFormat.format(result.getMostRecentValue(BuoyData.BuoyDataType.CHLOROPHYLL)));
            }
            if (result.hasData(BuoyData.BuoyDataType.PHYCOCYANIN)) {
                phycocyanin.setText(decimalFormat.format(result.getMostRecentValue(BuoyData.BuoyDataType.PHYCOCYANIN)));
            }
        }

        Date timeStamp = result.getMostRecentTimestamp();
        if (timeStamp != null) {
            updatedAt.setText(outputDateFormat.format(timeStamp));
        }

        if (appStatus != null && appStatus.getDisplayStatus() != null && appStatus.getDisplayStatus().length() > 0){
            messageText.setText(Html.fromHtml(appStatus.getDisplayStatus()));
            messageText.setMovementMethod(LinkMovementMethod.getInstance());
            setStatusMessageVisibility(true);
        } else {
            setStatusMessageVisibility(false);
        }
	}

	/**
	 * Something went wrong, tell the user what
	 * 
	 * @param message The error to show to the user
	 */
	private void displayError(String message) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, message, duration);
		toast.show();
	}

	/**
	 * Display dialog with information about the application
	 */
	private void displayAboutDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Get the layout inflater
		LayoutInflater inflater = getLayoutInflater();

        View aboutView = inflater.inflate(R.layout.about, null);

        TextView versionNameTextView = aboutView.findViewById(R.id.version_text);
        try {
            versionNameTextView.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            // Umm, there will always be a name.
        }

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(aboutView).setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Just close the dialog
					}
				});

		Dialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * Connect to the web service, download and parse the data
	 */
	private void updateData() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			updateProgressBar.setVisibility(android.view.View.VISIBLE);
			new DownloadDataTask().execute();
		} else {
			displayError(getString(R.string.error_cannot_show_data) + "\n"
					+ getString(R.string.error_no_network_connection));
		}
	}

	/**
	 * Uses AsyncTask to download the data away from the main thread.
	 */
	private class DownloadDataTask extends
			AsyncTask<Void, Void, BuoyData> {

		Throwable throwable = null;

		@Override
		protected BuoyData doInBackground(Void... params) {
			try {
				return downloadData();
			} catch (IOException e) {
				throwable = e;
				return null;
			}
		}

		/**
		 * onPostExecute displays the results of the AsyncTask.
		 */
		@Override
		protected void onPostExecute(BuoyData result) {
			if (throwable == null) {
				displayData(result);
			} else {
				displayError(getString(R.string.error_cannot_show_data) + "\n"
						+ throwable.getMessage());
			}
			updateProgressBar.setVisibility(android.view.View.INVISIBLE);
		}
	}

	/**
	 * Connects to the metobs web service, downloads data in JSON, returns the
	 * values in a BuoyData object.
	 * 
	 * @return a BuoyData object
	 */
	private BuoyData downloadData() throws IOException {
		BuoyData result;
		InputStream input = null;
        Reader reader = null;
		try {
			URL url = new URL(dataQueryURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			input = conn.getInputStream();
            reader = new InputStreamReader(input, Charset.forName("UTF-8"));

			result = gson.fromJson(reader, BuoyData.class);
            downloadStatusInformation(result);

		} finally {
			// Cleanup
            if (reader != null){
                reader.close();
            }

			if (input != null) {
				input.close();
			}
		}
		return result;
	}

    /**
     * Downloads buoy status information and adds it to the buoy data if available
     *
     * @param buoyData the data to store the application status in
     */
    private void downloadStatusInformation(BuoyData buoyData) {
        AppStatus status;
        InputStream input = null;
        Reader reader = null;
        //noinspection EmptyCatchBlock
        try {
            URL url = new URL(statusQueryURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            input = conn.getInputStream();
			reader = new InputStreamReader(input, Charset.forName("UTF-8"));

			status = gson.fromJson(reader, AppStatus.class);

            if (status != null){
                buoyData.setStatus(status);
            }
        } catch (Throwable e) {
        } finally {
            //noinspection EmptyCatchBlock
            try {
                // Cleanup
                if (reader != null) {
                    reader.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
