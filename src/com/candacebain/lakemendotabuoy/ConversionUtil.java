package com.candacebain.lakemendotabuoy;

public class ConversionUtil {
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static double celsiusToFahrenheit (double value) {
	    return (9.0/5.0)*value+32;
	}

	/*
	    Convert meters/second to miles/hour.

	    from http://www.4wx.com/wxcalc/formulas/windConversion.php
	    
	    Args:
	        val in MPS
	    Returns:
	        MPS, or null if val == null
	*/
	public static double metersPerSecondToMilesPerHour (double value) {
	    return value*2.23694;
	}

	/*
	    Convert meters/second to knots.

	    from http://www.4wx.com/wxcalc/formulas/windConversion.php

	    Args:
	        val in MPS.
	    Returns:
	        knots, or null if value == null
	*/
	public static double metersPerSecondToKnots (double value) {
	    return value*1.9438445; 
	}
}
