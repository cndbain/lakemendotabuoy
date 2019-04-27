package com.candacebain.lakemendotabuoy;

import java.util.Date;
import java.util.EnumMap;

public class BuoyData {

    /**
     * The data identifiers from the metobjs server
     */
    public enum BuoyDataType{
        AIR_TEMP("AIR_TEMP"),
        REL_HUM("REL_HUM"),
        WIND_SPEED("WIND_SPEED_2.0"),
        WIND_DIRECTION("WIND_DIRECTION_2.0"),
        WIND_GUST("WIND_GUST"),
        TIMESTAMP("timestamp"),
        WATER_TEMP_0("WATER_TEMP_0.0"),
        WATER_TEMP_1("WATER_TEMP_1.0"),
        WATER_TEMP_5("WATER_TEMP_5.0"),
        WATER_TEMP_10("WATER_TEMP_10.0"),
        WATER_TEMP_15("WATER_TEMP_15.0"),
        WATER_TEMP_20("WATER_TEMP_20.0"),
        DEWPOINT_CALC("DEWPOINT_CALC"),
        DO_SAT("DO_SAT"),
        DO_PPM("DO_PPM"),
        CHLOROPHYLL("CHLOROPHYLL_0.4"),
        PHYCOCYANIN("PHYCOCYANIN_0.4"),
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

    private String [] symbols = null;
    private Date [] stamps = null;
    private Double [][] data = null;
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
            for (int i = 0; i < symbols.length; i++) {
                BuoyDataType buoyDataType = BuoyDataType.fromString(symbols[i]);
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
        if (stamps.length > 0){
            return stamps[stamps.length-1];
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
            if (data.length > 0) {
                if (data[data.length - 1].length > columnIndex) {
                    return data[data.length - 1][columnIndex];
                }
            }
        }
        return Double.NaN;
    }
}
