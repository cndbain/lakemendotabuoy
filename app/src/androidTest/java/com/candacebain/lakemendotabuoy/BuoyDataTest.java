package com.candacebain.lakemendotabuoy;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.gson.Gson;

import org.junit.Test;
import static junit.framework.Assert.assertTrue;

@SmallTest
public class BuoyDataTest {

    @Test
    public void testDeserialization() {
        Gson gson = new Gson();
        String serverJson = "{\"code\":200,\"message\":\"\",\"num_results\":3,\"results\":{\"data\":[[5.1,49.0,-4.9,8.1,290.0,9.4,4.6,4.6,4.6,4.6,4.6,4.6,null,null,null,null],[5.1,49.0,-4.9,7.3,300.0,9.4,4.6,4.6,4.6,4.6,4.6,4.6,null,null,null,null],[5.0,50.0,-4.6,6.9,298.0,8.8,4.6,4.6,4.6,4.6,4.6,4.6,null,null,null,null]],\"symbols\":[\"mendota.buoy.air_temp\",\"mendota.buoy.rel_hum\",\"mendota.buoy.dewpoint\",\"mendota.buoy.wind_speed\",\"mendota.buoy.wind_direction\",\"mendota.buoy.gust\",\"mendota.buoy.water_temp_1\",\"mendota.buoy.water_temp_3\",\"mendota.buoy.water_temp_8\",\"mendota.buoy.water_temp_13\",\"mendota.buoy.water_temp_18\",\"mendota.buoy.water_temp_23\",\"mendota.buoy.doptoppm\",\"mendota.buoy.doptosat\",\"mendota.buoy.chlorophyll\",\"mendota.buoy.phycocyanin\"],\"timestamps\":[\"2021-03-31T03:34:00Z\",\"2021-03-31T03:35:00Z\",\"2021-03-31T03:36:00Z\"]},\"status\":\"success\"}";
        BuoyData buoyData = gson.fromJson(serverJson, BuoyData.class);

        assertTrue(buoyData.hasData(BuoyData.BuoyDataType.AIR_TEMP));


    }

}