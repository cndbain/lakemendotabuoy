package com.candacebain.lakemendotabuoy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String DEBUG_TAG = "LakeMendotaBuoy";

	private static final DecimalFormat decimalFormat = new DecimalFormat(
			"000.0");

	private static final String AIR_TEMP = "AIR_TEMP";
	private static final String REL_HUM = "REL_HUM";
	private static final String DEWPOINT_CALC = "DEWPOINT_CALC";
	private static final String WIND_SPEED = "WIND_SPEED_2.0";
	private static final String WIND_DIRECTION = "WIND_DIRECTION_2.0";
	private static final String WIND_GUST = "WIND_GUST";

	private static final String WATER_TEMP_0 = "WATER_TEMP_0.0";
	private static final String WATER_TEMP_1 = "WATER_TEMP_1.0";
	private static final String WATER_TEMP_5 = "WATER_TEMP_5.0";
	private static final String WATER_TEMP_10 = "WATER_TEMP_10.0";
	private static final String WATER_TEMP_15 = "WATER_TEMP_15.0";
	private static final String WATER_TEMP_20 = "WATER_TEMP_20.0";

	private static final String dataQueryURL = "http://metobs.ssec.wisc.edu/app/mendota/buoy/data/xml?symbols=t:rh:td:spd:dir:gust:wt_0.0:wt_1.0:wt_5.0:wt_10.0:wt_15.0:wt_20.0";

	private String[] windDirections = null;
	
	private TextView windDirection;
	private TextView windSpeed;
	private TextView windGust;
	private TextView airTemperature;
	private TextView dewPoint;
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
	
	// !!! Figure out logging
	// !!! Figure out how to show the preferences
	// !!! Figure out how to automatically update
	// !!! Figure out how to show when last update happened
	// !!! Figure out how to add a button to force an update
	// !!! Figure out how to show an error when anything goes wrong

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		setContentView(R.layout.activity_main);
		initializeViews();
		updateData();
	}

	private void initializeViews() {
		windDirection = (TextView) findViewById(R.id.wind_direction);
		windSpeed = (TextView) findViewById(R.id.wind_speed);
		windGust = (TextView) findViewById(R.id.wind_gust);
		airTemperature = (TextView) findViewById(R.id.air_temperature);
		dewPoint = (TextView) findViewById(R.id.dew_point);
		humidity = (TextView) findViewById(R.id.humidity);

		surfaceTemperature = (TextView) findViewById(R.id.surface_temperature);
		oneMeterTemperature = (TextView) findViewById(R.id.one_meter_temperature);
		fiveMeterTemperature = (TextView) findViewById(R.id.five_meter_temperature);
		tenMeterTemperature = (TextView) findViewById(R.id.ten_meter_temperature);
		fifteenMeterTemperature = (TextView) findViewById(R.id.fifteen_meter_temperature);
		twentyMeterTemperature = (TextView) findViewById(R.id.twenty_meter_temperature);

		oneMeterCaption = (TextView) findViewById(R.id.one_meter_temperature_caption);
		fiveMeterCaption = (TextView) findViewById(R.id.five_meter_temperature_caption);
		tenMeterCaption = (TextView) findViewById(R.id.ten_meter_temperature_caption);
		fifteenMeterCaption = (TextView) findViewById(R.id.fifteen_meter_temperature_caption);
		twentyMeterCaption = (TextView) findViewById(R.id.twenty_meter_temperature_caption);
		
		// !!! Probably have a set caption method here

		String temperatureUnits = PreferenceManager
				.getDefaultSharedPreferences(this).getString(
						SettingsActivity.KEY_PREF_TEMPERATURE_UNITS, "");
		if (temperatureUnits
				.equals(getString(R.string.pref_distanceUnits_feet))) {
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
		} else if (temperatureUnits
				.equals(getString(R.string.pref_distanceUnits_meters))) {
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
		
		if (windDirections == null){
			windDirections = getResources().getStringArray(R.array.windDirections);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	// !!! Finish dealing with the array
	String formatWindDirection(double windDirectionInDegrees) {
		if (windDirectionInDegrees <= 0 || windDirectionInDegrees > 360) {
			return getString(R.string.unknown_value);
		}

		// deal with special north case
		if ((windDirectionInDegrees >= 348.75 && windDirectionInDegrees <= 360)
				|| windDirectionInDegrees > 0 && windDirectionInDegrees < 11.25)
			return getString(R.string.wind_direction_N);

		double i = 11.25; // 1/2 degree increment between the directions
		for (int j = 0; j < windDirections.length; j++) {
			if (windDirectionInDegrees >= i
					&& windDirectionInDegrees < (i + 22.5)) {
				return windDirections[j];
			}
			i += 22.5;
		}

		// shouldn't ever get here
		return getString(R.string.unknown_value);
	}
	
	private String formatTemperature(double value){
		// What units should we use to display temperature?
		String temperatureUnits = PreferenceManager
				.getDefaultSharedPreferences(this).getString(
						SettingsActivity.KEY_PREF_TEMPERATURE_UNITS, "");
		boolean isTemperatureFahrenheit = temperatureUnits.equals(getString(R.string.pref_temperatureUnits_fahrenheit));
		
		String temperatureUnitSuffix = getString(R.string.celsius_suffix); 
		if (isTemperatureFahrenheit){
			temperatureUnitSuffix = getString(R.string.fahrenheit_suffix);
		}
		
		double useValue = value;
		if (isTemperatureFahrenheit){
			useValue =  ConversionUtil.celsiusToFahrenheit(value);
		}
		
		return(decimalFormat.format(useValue) + temperatureUnitSuffix).replaceAll("\\G0", " ");
	}
	
	private String formatWindSpeed(double value){
		// What units should we use to display wind speed?
		String windSpeedUnits = PreferenceManager
				.getDefaultSharedPreferences(this).getString(
						SettingsActivity.KEY_PREF_WIND_SPEED_UNITS, "");
		boolean isWindSpeedMph = windSpeedUnits.equals(getString(R.string.pref_windSpeedUnits_milesPerHour));
		boolean isWindSpeedKnots = windSpeedUnits.equals(getString(R.string.pref_windSpeedUnits_knots));
		
		// Set up the suffixes for the units
		String windSpeedUnitSuffix = " " + getString(R.string.meters_per_second_suffix);
		if (isWindSpeedMph){
			windSpeedUnitSuffix = " " + getString(R.string.miles_per_hour_suffix);
		}
		else if (isWindSpeedKnots){
			windSpeedUnitSuffix = " " + getString(R.string.knots_suffix);
		}
		
		double useValue = value;
		if (isWindSpeedMph){
			useValue =  ConversionUtil.metersPerSecondToMilesPerHour(value);
		}
		else if (isWindSpeedKnots){
			useValue =  ConversionUtil.metersPerSecondToKnots(value);
		}
		
		return(decimalFormat.format(useValue) + windSpeedUnitSuffix).replaceAll("\\G0", " ");
	}
	
	private String formatHumidity(double value){
		String humidityUnitSuffix = getString(R.string.percent_suffix);
		return (decimalFormat.format(value) + humidityUnitSuffix).replaceAll("\\G0", " ");
	}
	

	private void displayData(Map<String, Double> result) {
		// !!! Add spaces before the wind units, and maybe spaces to make things line up if they're < 10?
		

		// Clear old values
		String unknownValue = getString(R.string.unknown_value);
		windDirection.setText(unknownValue);
		windSpeed.setText(unknownValue);
		windGust.setText(unknownValue);
		airTemperature.setText(unknownValue);
		dewPoint.setText(unknownValue);
		humidity.setText(unknownValue);
		surfaceTemperature.setText(unknownValue);
		oneMeterTemperature.setText(unknownValue);
		fiveMeterTemperature.setText(unknownValue);
		tenMeterTemperature.setText(unknownValue);
		fifteenMeterTemperature.setText(unknownValue);
		twentyMeterTemperature.setText(unknownValue);

		if (result.containsKey(WIND_DIRECTION)) {
			windDirection.setText(formatWindDirection(result.get(WIND_DIRECTION)));
		}
		if (result.containsKey(WIND_SPEED)) {
			windSpeed.setText(formatWindSpeed(result.get(WIND_SPEED)));
		}
		if (result.containsKey(WIND_GUST)) {
			windGust.setText(formatWindSpeed(result.get(WIND_GUST)));
		}
		if (result.containsKey(AIR_TEMP)) {
			airTemperature.setText(formatTemperature(result.get(AIR_TEMP)));
		}
		if (result.containsKey(DEWPOINT_CALC)) {
			dewPoint.setText(formatTemperature(result.get(DEWPOINT_CALC)));
		}
		if (result.containsKey(REL_HUM)) {
			humidity.setText(formatHumidity(result.get(REL_HUM)));
		}
		if (result.containsKey(WATER_TEMP_0)) {
			surfaceTemperature.setText(formatTemperature(result.get(WATER_TEMP_0)));
		}
		if (result.containsKey(WATER_TEMP_1)) {
			oneMeterTemperature.setText(formatTemperature(result.get(WATER_TEMP_1)));
		}
		if (result.containsKey(WATER_TEMP_5)) {
			fiveMeterTemperature.setText(formatTemperature(result.get(WATER_TEMP_5)));
		}
		if (result.containsKey(WATER_TEMP_10)) {
			tenMeterTemperature.setText(formatTemperature(result.get(WATER_TEMP_10)));
		}
		if (result.containsKey(WATER_TEMP_15)) {
			fifteenMeterTemperature.setText(formatTemperature(result.get(WATER_TEMP_15)));
		}
		if (result.containsKey(WATER_TEMP_20)) {
			twentyMeterTemperature.setText(formatTemperature(result.get(WATER_TEMP_20)));
		}
	}

	private void updateData() {
		// !!! I'll need to figure out how to show error messages
		// !!! And remember to get the error messages from the resources.
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new DownloadWebpageTask().execute(dataQueryURL);
		} else {
			// !!! display error
		}
	}

	// Uses AsyncTask to create a task away from the main UI thread. This task
	// takes a
	// URL string and uses it to create an HttpUrlConnection. Once the
	// connection
	// has been established, the AsyncTask downloads the contents of the webpage
	// as
	// an InputStream. Finally, the InputStream is converted into a string,
	// which is
	// displayed in the UI by the AsyncTask's onPostExecute method.
	private class DownloadWebpageTask extends
			AsyncTask<String, Void, Map<String, Double>> {
		@Override
		protected Map<String, Double> doInBackground(String... urls) {

			// params comes from the execute() call: params[0] is the url.
			try {
				return downloadUrl(urls[0]);
			} catch (IOException e) {
				// !!! Have to figure out what to do with exceptions/error
				// handling
				// return "Unable to retrieve web page. URL may be invalid.";
				return new HashMap<String, Double>();
			}
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(Map<String, Double> result) {
			displayData(result);
		}
	}

	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a InputStream, which it returns as
	// a string.
	private Map<String, Double> downloadUrl(String myurl) throws IOException {
		InputStream is = null;
		Map<String, Double> entries = new HashMap<String, Double>();
		try {
			URL url = new URL(myurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			Log.d(DEBUG_TAG, "The response is: " + response);
			is = conn.getInputStream();

			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(is, null);
			parser.nextTag();

			String dataType = null;

			parser.require(XmlPullParser.START_TAG, null, "metobs");
			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				if (parser.getEventType() == XmlPullParser.START_TAG) {
					String name = parser.getName();
					if (name.equals("data")) {
						dataType = parser.getAttributeValue(null, "symbol");
					}
				} else if (parser.getEventType() == XmlPullParser.TEXT
						&& dataType != null) {
					// Get the value and store it
					String text = parser.getText();
					String[] values = text.split(",");
					entries.put(dataType,
							Double.parseDouble(values[values.length - 1]));
				} else if (parser.getEventType() == XmlPullParser.END_TAG) {
					dataType = null;
				}
			}
			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return entries;
	}

	// Reads an InputStream and converts it to a String.
	public String readIt(InputStream stream, int len) throws IOException,
			UnsupportedEncodingException {
		Reader reader = null;
		reader = new InputStreamReader(stream, "UTF-8");
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}

}
