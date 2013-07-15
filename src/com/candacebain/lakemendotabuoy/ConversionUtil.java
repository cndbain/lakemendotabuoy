package com.candacebain.lakemendotabuoy;

public class ConversionUtil {
	/**
	 * Convert celsius to fahrenheit
	 * 
	 * @param value celsius temperature value
	 * @return  fahrenheit temperature value
	 */
	public static double celsiusToFahrenheit (double value) {
	    return (9.0/5.0)*value+32;
	}

	/**
	 *  Convert meters/second to miles/hour.
	 *  
	 *  from http://www.4wx.com/wxcalc/formulas/windConversion.php
	 *  
	 * @param value in meters per second
	 * @return value in miles per hour
	 */
	public static double metersPerSecondToMilesPerHour (double value) {
	    return value*2.23694;
	}
	
	/**
	 * Convert meters/second to knots.
	 * 
	 * from http://www.4wx.com/wxcalc/formulas/windConversion.php
	 * 
	 * @param value in meters per second
	 * @return in knots
	 */
	public static double metersPerSecondToKnots (double value) {
	    return value*1.9438445; 
	}
}
