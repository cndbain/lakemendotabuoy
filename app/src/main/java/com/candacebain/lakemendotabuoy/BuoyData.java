package com.candacebain.lakemendotabuoy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by candace on 4/6/15.
 */
public class BuoyData {

    // TODO - put an enum here for the buoy symbols.

    // Get this from http://metobs.ssec.wisc.edu/app/mendota/buoy/data/json?symbols=t:rh:td:spd:dir:gust:wt_0.0:wt_1.0:wt_5.0:wt_10.0:wt_15.0:wt_20.0:do_ppm:do_sat:chlor:pc

    // Should be like this:

    // {"symbols": ["AIR_TEMP", "REL_HUM", "DEWPOINT_CALC", "WIND_SPEED_2.0", "WIND_DIRECTION_2.0", "WIND_GUST", "WATER_TEMP_0.0", "WATER_TEMP_1.0", "WATER_TEMP_5.0", "WATER_TEMP_10.0", "WATER_TEMP_15.0", "WATER_TEMP_20.0", "DO_PPM", "DO_SAT", "CHLOROPHYLL_0.4", "PHYCOCYANIN_0.4"], "stamps": ["2014-10-12 17:13:00", "2014-10-12 17:13:05", "2014-10-12 17:13:10", "2014-10-12 17:13:15", "2014-10-12 17:13:20", "2014-10-12 17:13:25", "2014-10-12 17:13:30", "2014-10-12 17:13:35", "2014-10-12 17:13:40", "2014-10-12 17:13:45", "2014-10-12 17:13:50", "2014-10-12 17:13:55"], "data": [[-999.0, -999.0, NaN, -999.0, -999.0, -999.0, -999.0, 13.91, 14.15, 14.67, 14.16, 0.0, 9.84, 95.2, -544.9, 5213.0], [-999.0, -999.0, NaN, -999.0, -999.0, -999.0, -999.0, 13.91, 14.15, 14.67, 14.16, 0.0, 9.84, 95.2, -544.9, 5213.0], [-999.0, -999.0, NaN, -999.0, -999.0, -999.0, -999.0, 13.91, 14.15, 14.67, 14.16, 0.0, 9.84, 95.2, -544.9, 5213.0], [-999.0, -999.0, NaN, -999.0, -999.0, -999.0, -999.0, 13.91, 14.15, 14.67, 14.16, 0.0, 9.84, 95.2, -544.9, 5213.0], [-999.0, -999.0, NaN, -999.0, -999.0, -999.0, -999.0, 13.91, 14.15, 14.67, 14.16, 0.0, 9.84, 95.2, -544.9, 5213.0], [-999.0, -999.0, NaN, -999.0, -999.0, -999.0, -999.0, 13.91, 14.15, 14.67, 14.16, 0.0, 9.84, 95.2, -544.9, 5213.0], [-999.0, -999.0, NaN, -999.0, -999.0, -999.0, -999.0, 13.91, 14.15, 14.67, 14.16, 0.0, 9.84, 95.2, -544.9, 5213.0], [-999.0, -999.0, NaN, -999.0, -999.0, -999.0, -999.0, 13.91, 14.15, 14.67, 14.16, 0.0, 9.84, 95.2, -544.9, 5213.0], [-999.0, -999.0, NaN, -999.0, -999.0, -999.0, -999.0, 13.91, 14.15, 14.67, 14.16, 0.0, 9.84, 95.2, -544.9, 5213.0], [-999.0, -999.0, NaN, -999.0, -999.0, -999.0, -999.0, 13.91, 14.15, 14.67, 14.16, 0.0, 9.84, 95.2, -544.9, 5213.0], [-999.0, -999.0, NaN, -999.0, -999.0, -999.0, -999.0, 13.91, 14.15, 14.67, 14.16, 0.0, 9.84, 95.2, -544.9, 5213.0], [-999.0, -999.0, NaN, -999.0, -999.0, -999.0, -999.0, 13.91, 14.15, 14.67, 14.16, 0.0, 9.84, 95.2, -544.9, 5213.0]]}

    // Use GSON to parse this out.

    // I need this for the GSON date parser.

    private static final SimpleDateFormat inputDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.US);

    // TODO - and remove the old src directory, I think

    private String [] symbols;
    private Date [] stamps;
    private float [][] data;

    // Gson needs this
    public void BuoyData(){
    }
}
