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
class AppStatus {

    private boolean buoyInWater = false;

    private String displayStatus = "";

    boolean isBuoyInWater() {
        return buoyInWater;
    }

    void setBuoyInWater(boolean buoyInWater) {
        this.buoyInWater = buoyInWater;
    }

    String getDisplayStatus() {
        return displayStatus;
    }

    void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }
}
