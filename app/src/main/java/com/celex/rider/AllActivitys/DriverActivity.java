package com.celex.rider.AllActivitys;

import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;


import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.DataModels.DriverModel;
import com.celex.rider.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.database.annotations.Nullable;



import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;


public class DriverActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient; // Object used to receive location updates

    private LocationRequest locationRequest; // Object that defines important parameters regarding location request.
    GeoFire geoFire;
    DatabaseReference ref1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // 5 second delay between each request
        locationRequest.setFastestInterval(1000); // 5 seconds fastest time in between each request
        locationRequest.setSmallestDisplacement(0); // 10 meters minimum displacement for new location request
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // enables GPS high accuracy location requests
        sendUpdatedLocationMessage();


       // DatabaseReference ref = FirebaseDatabase.getInstance().getReference("path/to/geofire");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers_Location");
         ref1 = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
        geoFire = new GeoFire(ref);
    }

    /*
        This method gets user's current location and publishes message to channel.
     */
    private void sendUpdatedLocationMessage() {
        try {
            mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    Location location = locationResult.getLastLocation();
                    geoFire.setLocation(Variables.userDetails_pref.getString(Variables.id, ""), new GeoLocation(location.getLatitude(), location.getLongitude()));



                    DriverModel model = new  DriverModel(Integer.parseInt(Variables.userDetails_pref.getString(Variables.id, "")),
                            Variables.userDetails_pref.getString(Variables.name, ""),
                            Variables.userDetails_pref.getString(Variables.phone, ""),
                            "",
                            Double.toString(location.getLatitude()),
                            Double.toString(location.getLongitude()),
                            0,
                            Variables.userDetails_pref.getString(Variables.image, "")
                            ) ;

                    ref1.child(  Variables.userDetails_pref.getString(Variables.uid, "")).setValue(model, (databaseError, databaseReference) -> {
                        if (databaseError != null) {
                            Toast.makeText(DriverActivity.this, "Data could not be saved " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                      //     Toast.makeText(DriverActivity.this, "Data saved successfully.", Toast.LENGTH_SHORT).show();
                        }
                    });



                    //   Toast.makeText(DriverActivity.this, "Latitude :" + location.getLatitude() + " Longitude :" + location.getLongitude() , Toast.LENGTH_SHORT).show();


                    /*Map<String, Object> custom = new HashMap<>();
                    final int random = new Random().nextInt(61) + 20; // [0, 60] + 20 => [20, 80]

                    custom.put("order_id", "order_id"+random);
                    custom.put("order_status", "order_status"+random);
                    pubnub.publish()
                            .message(message)
                            .meta(custom)
                            .channel(Variables.PUBNUB_CHANNEL_NAME)
                            .async(new PNCallback<PNPublishResult>() {
                                @Override
                                public void onResponse(PNPublishResult result, PNStatus status) {
                                    // handle publish result, status always present, result if successful
                                    // status.isError() to see if error happened
                                    if (!status.isError()) {
                                        System.out.println("pub timetoken: " + result.getTimetoken());
                                    }
                                    System.out.println("pub status code: "+ Variables.PUBNUB_CHANNEL_NAME+ status);
                                }
                            });*/
                }
            }, Looper.myLooper());

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /*
        Helper method that takes in latitude and longitude as parameter and returns a LinkedHashMap representing this data.
        This LinkedHashMap will be the message published by driver.
     */
    private LinkedHashMap<String, String> getNewLocationMessage(double lat, double lng) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

        map.put("lat", String.valueOf(lat));
        map.put("lng", String.valueOf(lng));
        return map;
    }
}