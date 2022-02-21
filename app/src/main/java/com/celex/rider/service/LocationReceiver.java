package com.celex.rider.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.Functions;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.roam.sdk.Roam;
import com.roam.sdk.callback.RoamActiveTripsCallback;
import com.roam.sdk.callback.RoamTripCallback;
import com.roam.sdk.callback.RoamTripSummaryCallback;
import com.roam.sdk.models.ActiveTrips;
import com.roam.sdk.models.RoamError;
import com.roam.sdk.models.RoamLocation;
import com.roam.sdk.models.RoamTrip;
import com.roam.sdk.models.tripsummary.RoamTripSummary;
import com.roam.sdk.service.RoamReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class LocationReceiver extends RoamReceiver {

    @Override
    public void onLocationUpdated(Context context, RoamLocation roamLocation) {
        super.onLocationUpdated(context, roamLocation);
        Log.e("Location", "Lat " + roamLocation.getLocation().getLatitude() + " Lng " + roamLocation.getLocation().getLongitude());
        stopTrip(context);
    }

    @Override
    public void onError(Context context, RoamError roamError) {
        Log.e("onLocationUpdated", roamError.getMessage());
    }

    private void stopTrip(Context mContext) {



        Roam.activeTrips(false, new RoamActiveTripsCallback() {
            @Override
            public void onSuccess(RoamTrip RoamTrip) {

                List<ActiveTrips> activeTrips = RoamTrip.getActiveTrips();

                if (activeTrips.size() != 0) {
                    // activeTrips.get(0).getSyncStatus();
                    String trip_id = activeTrips.get(0).getTripId();
                    //    Toast.makeText(getBaseContext(), trip_id, Toast.LENGTH_SHORT).show();


                    String dtStart = activeTrips.get(0).getCreatedAt();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    try {
                        format.setTimeZone(TimeZone.getTimeZone("UTC"));

                        Date date = format.parse(dtStart);

                        System.out.println(date);


                        Calendar c1 = Calendar.getInstance(); // today
                        c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday

                        Calendar c2 = Calendar.getInstance();
                        c2.setTime(date); // your date
                        Log.e("Location", "c1 " +  c1.get(Calendar.DAY_OF_YEAR) + " c2 " + c2.get(Calendar.DAY_OF_YEAR));

                        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {



                            Roam.startTrip(trip_id, "Trip was not started", new RoamTripCallback() {
                                @Override
                                public void onSuccess(String message) {
                                    stopTripAndSummary(trip_id, mContext);
                                    //  Toast.makeText(getBaseContext(),"fail"+trip_id, Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onFailure(RoamError RoamError) {
                                    //     Toast.makeText(getBaseContext(),"fail"+trip_id, Toast.LENGTH_SHORT).show();
                                    stopTripAndSummary(trip_id, mContext);
                                }
                            });
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }




                }


            }

            @Override
            public void onFailure(RoamError error) {


            }
        });


    }
    private void stopTripAndSummary(String trip_id, Context mContext){

        Log.d("geo","trip_id"+ trip_id);
        // Log.d("geo",uid+ trip_id);




        Roam.getTripSummary(trip_id, new RoamTripSummaryCallback() {
            @Override
            public void onSuccess(RoamTripSummary roamTripSummary) {
                JSONObject params = new JSONObject();
                try {
                    params.put("roam_trip_id",trip_id);
                    params.put("roam_trip_distance",roamTripSummary.getDistance_covered());
                    params.put("roam_trip_duration",roamTripSummary.getDuration());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApiRequest.Call_Api(mContext, Api_urls.URL_STOP_TRIP, params, resp ->     {

                    if (resp != null) {
                        try {

                            JSONObject respobj = new JSONObject(resp);

                            if(respobj.getString("status").equals("1")){
                                Roam.stopTrip(trip_id, new RoamTripCallback() {
                                    @Override
                                    public void onSuccess(String message) {
                                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onFailure(RoamError RoamError) {
                                        Toast.makeText(mContext, RoamError.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();

                            FirebaseCrashlytics.getInstance().recordException(e);
                        }

                    }
                });

            }

            @Override
            public void onFailure(RoamError roamError) {
                Log.d("geo","error"+ roamError.getMessage());
            }

        });
    }

}

/*

public class LocationReceiver extends RoamReceiver {


    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
    }
    @Override
    public void onLocationUpdated(Context context, RoamLocation geoSparkLocation) {
        super.onLocationUpdated(context, geoSparkLocation);
        Log.e("Location", "Lat " + geoSparkLocation.getLocation().getLatitude() + " Lng " + geoSparkLocation.getLocation().getLongitude());
       Functions.Add_Device_Data(geoSparkLocation.getLocation().getLatitude(),geoSparkLocation.getLocation().getLongitude(),mContext);
        stopTrip();
    }

    @Override
    public void onError(Context context, RoamError geoSparkError) {
        Log.e("Location", geoSparkError.getMessage());
    }



}*/
