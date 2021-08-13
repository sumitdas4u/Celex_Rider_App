package com.celex.rider.service;

import android.content.Context;
import android.util.Log;

import com.roam.sdk.models.RoamError;
import com.roam.sdk.models.RoamLocation;
import com.roam.sdk.service.RoamReceiver;


public class LocationReceiver extends RoamReceiver {

    @Override
    public void onLocationUpdated(Context context, RoamLocation geoSparkLocation) {
        super.onLocationUpdated(context, geoSparkLocation);
        Log.e("Location", "Lat " + geoSparkLocation.getLocation().getLatitude() + " Lng " + geoSparkLocation.getLocation().getLongitude());



    }

    @Override
    public void onError(Context context, RoamError geoSparkError) {
        Log.e("Location", geoSparkError.getMessage());
    }
}