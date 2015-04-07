package com.candacebain.lakemendotabuoy;

import com.google.gson.Gson;

import junit.framework.TestCase;

import org.junit.Test;

public class AppStatusTest extends TestCase {

    // Test writing and reading an AppStatus class to a JSON string
    @Test
    public void testSerializeToJson(){

        AppStatus appStatus = new AppStatus();

        Gson gson = new Gson();

        String appStatusString = gson.toJson(appStatus);
        AppStatus retrievedAppStatus = gson.fromJson(appStatusString, AppStatus.class);

        assertEquals(retrievedAppStatus.getDisplayStatus(), appStatus.getDisplayStatus());
        assertEquals(retrievedAppStatus.isBuoyInWater(), appStatus.isBuoyInWater());

        appStatus.setBuoyInWater(true);
        appStatus.setDisplayStatus("Some string here");

        appStatusString = gson.toJson(appStatus);
        retrievedAppStatus = gson.fromJson(appStatusString, AppStatus.class);

        assertEquals(retrievedAppStatus.getDisplayStatus(), appStatus.getDisplayStatus());
        assertEquals(retrievedAppStatus.isBuoyInWater(), appStatus.isBuoyInWater());

    }
}