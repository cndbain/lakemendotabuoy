package com.candacebain.lakemendotabuoy;

/**
 * Created by candace on 4/6/15.
 *
 * We pull this status from https://s3-us-west-2.amazonaws.com/lakemendotabuoy/buoy_status.json
 *
 * A boolean for whether the buoy is currently in the water
 *
 * A status message to display to the user if something is wrong
 */
public class AppStatus {

    private boolean buoyInWater = false;

    private String displayStatus = "";

    public boolean isBuoyInWater() {
        return buoyInWater;
    }

    public void setBuoyInWater(boolean buoyInWater) {
        this.buoyInWater = buoyInWater;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }
}
