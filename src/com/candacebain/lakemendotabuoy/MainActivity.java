package com.candacebain.lakemendotabuoy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String DEBUG_TAG = "LakeMendotaBuoy";
	
	// Do an enum map with these values?
	private static final String AIR_TEMP = "AIR_TEMP";
	private static final String REL_HUM = "REL_HUM";
	private static final String DEWPOINT_CALC = "DEWPOINT_CALC";
	private static final String WIND_SPEED = "WIND_SPEED_2.0";
	private static final String WIND_DIRECTION = "WIND_DIRECTION_2.0";
	private static final String WIND_GUST = "WIND_GUST";
	
	private static final String WATER_TEMP_0 = "WATER_TEMP_0";
	private static final String WATER_TEMP_1 = "WATER_TEMP_1";
	private static final String WATER_TEMP_5 = "WATER_TEMP_5";
	private static final String WATER_TEMP_10 = "WATER_TEMP_10";
	private static final String WATER_TEMP_15 = "WATER_TEMP_15";
	private static final String WATER_TEMP_20 = "WATER_TEMP_20";

	// !!! Can also use XML here
	private static final String dataQueryURL = "http://metobs.ssec.wisc.edu/app/mendota/buoy/data/json?symbols=t:rh:td:spd:dir:gust:wt_0.0:wt_1.0:wt_5.0:wt_10.0:wt_15.0:wt_20.0";

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updateData();
		setContentView(R.layout.activity_main);

		findViews();
	}
	
	private void findViews(){
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void updateData() {
		// !!! I'll need to figure out how to show error messages
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
	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			// params comes from the execute() call: params[0] is the url.
			try {
				return downloadUrl(urls[0]);
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid.";
			}
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			displayData(result);
		}
	}

	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a InputStream, which it returns as
	// a string.
	private String downloadUrl(String myurl) throws IOException {
		InputStream is = null;
		// Only display the first 5000 characters of the retrieved
		// web page content.
		int len = 5000;

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
            
            List entries = new ArrayList();

            parser.require(XmlPullParser.START_TAG, ns, "feed");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                // Starts by looking for the entry tag
                if (name.equals("entry")) {
                    entries.add(readEntry(parser));
                } else {
                    skip(parser);
                }
            }  
            return entries;
			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} finally {
			if (is != null) {
				is.close();
			}
		}
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
