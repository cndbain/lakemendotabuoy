package com.candacebain.lakemendotabuoy;

import java.util.Date;
import java.util.EnumMap;

public class BuoyData {

    /**
     * The data identifiers from the metobjs server
     */
    public enum BuoyDataType{
        AIR_TEMP("mendota.buoy.air_temp"),
        REL_HUM("mendota.buoy.rel_hum"),
        WIND_SPEED("mendota.buoy.wind_speed"),
        WIND_DIRECTION("mendota.buoy.wind_direction"),
        WIND_GUST("mendota.buoy.gust"),
        TIMESTAMP("timestamp"),
        /*  The number 1 represents the surface temperature (0m), 2 is 0.5m, 3 is 1.0m, 4 is 1.5m, 5 is 2m, followed by 1m increments up to number 23 which is at a depth of 20m below the surface */
        WATER_TEMP_0("mendota.buoy.water_temp_1"),
        WATER_TEMP_1("mendota.buoy.water_temp_3"),
        WATER_TEMP_5("mendota.buoy.water_temp_8"),
        WATER_TEMP_10("mendota.buoy.water_temp_13"),
        WATER_TEMP_15("mendota.buoy.water_temp_18"),
        WATER_TEMP_20("mendota.buoy.water_temp_23"),
        DEWPOINT_CALC("mendota.buoy.dewpoint"),
        DO_SAT("mendota.buoy.doptosat"),
        DO_PPM("mendota.buoy.doptoppm"),
        CHLOROPHYLL("mendota.buoy.chlorophyll"),
        PHYCOCYANIN("mendota.buoy.phycocyanin"),
        UNKNOWN("");

        private String metobsString;

        BuoyDataType(String metobsString){
            this.metobsString = metobsString;
        }

        @Override public String toString() {
            return this.metobsString;
        }

        public static BuoyDataType fromString(String metobsString){
            String compareMetobsString = "";
            if (metobsString != null){
                compareMetobsString = metobsString.trim().toLowerCase();
            }

            for (BuoyDataType buoyDataType : BuoyDataType.values()){
                if (buoyDataType.toString().trim().toLowerCase().equals(compareMetobsString)){
                    return buoyDataType;
                }
            }
            return BuoyDataType.UNKNOWN;
        }
    }

    public class Results {
        public Double [][] data = null;
        private String [] symbols = null;
        private Date [] timestamps = null;
    }

    private int code;
    private int num_results;
    private String status;
    Results results = null;
    private AppStatus appStatus = null;

    private EnumMap<BuoyDataType, Integer> columnIndices = new EnumMap<>(BuoyDataType.class);

    /**
     * Our no-argument constructor, will only be called by GSON
     */
    public BuoyData(){

    }

    /**
     * If our column indices haven't been initialized yet, do so now
     */
    private void initColumnIndices() {
        if (columnIndices.size() == 0) {
            for (int i = 0; i < results.symbols.length; i++) {
                BuoyDataType buoyDataType = BuoyDataType.fromString(results.symbols[i]);
                if (buoyDataType != BuoyDataType.UNKNOWN) {
                    columnIndices.put(buoyDataType, i);
                }
            }
        }
    }

    /**
     * Sets the application status
     *
     * @param appStatus the application status
     */
    void setStatus(AppStatus appStatus) {
        this.appStatus = appStatus;
    }

    /**
     * @return The application status, or none if not available
     */
    AppStatus getAppStatus(){
        return this.appStatus;
    }

    /**
     * @return The most recent timestamp
     */
    Date getMostRecentTimestamp(){
        if (results.timestamps.length > 0){
            return results.timestamps[results.timestamps.length-1];
        }
        return null;
    }

    /**
     * @param type the data type we're checking
     * @return true if we have data for this type
     */
    boolean hasData(BuoyDataType type){
        initColumnIndices();
        return (columnIndices.containsKey(type));
    }

    /**
     * @param type the data type we're fetching
     * @return The most recent value for this data type, or -1 if we have no values
     */
    Double getMostRecentValue(BuoyDataType type) {
        if (hasData(type)) {
            int columnIndex = columnIndices.get(type);
            if (results.data.length > 0) {
                if (results.data[results.data.length - 1].length > columnIndex) {
                    Double value = results.data[results.data.length - 1][columnIndex];
                    if (value != null) {
                        return value;
                    }
                }
            }
        }
        return Double.NaN;
    }
}
