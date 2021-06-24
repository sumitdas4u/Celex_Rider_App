package com.celex.rider.Location_Services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.celex.rider.AllActivitys.MainActivity;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.DataModels.DriverModel;
import com.celex.rider.R;
import com.celex.rider.interfaces.LocationService_callback;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


public class GetLocation_Service extends Service
         {

    private final IBinder binder = new LocalBinder();
    private Location mLastLocation;
      //       FirebaseFirestore db;
             private LocationRequest locationRequest; // Object that defines important parameters regarding location request.
             private FusedLocationProviderClient mFusedLocationClient; // Object used to receive location updates

    // Location updates intervals in sec
    private final int UPDATE_INTERVAL = 5000;
    private final int FATEST_INTERVAL = 2000;
    private final int DISPLACEMENT = 10;
    SharedPreferences pending_job_pref;
    GeoFire geoFire;
    DatabaseReference ref1;
    double latitude;
    double longitude;
    LatLng latLng;
    Context context;
    String user_id;
             String CHANNEL_ONE_ID = "celex.location.Service";
             String CHANNEL_ONE_NAME = "celex location service";
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public GetLocation_Service getService() {
            // Return this instance of LocalService so clients can call public methods
            return GetLocation_Service.this;
        }
    }

    final class Mythreadclass implements Runnable {

        @Override
        public void run() {
            locationRequest.setInterval(UPDATE_INTERVAL);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setSmallestDisplacement(DISPLACEMENT);

        }
    }

    @Override
    public void onCreate(){
        Log.d(Variables.TAG,"running");
        buildGoogleApiClient();

     //    db = FirebaseFirestore.getInstance();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        context = getApplicationContext();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers_Location");
        ref1 = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
        geoFire = new GeoFire(ref);
        pending_job_pref = getSharedPreferences(Variables.userDetails_pref_name, MODE_PRIVATE);

        Variables.userDetails_pref = getSharedPreferences(Variables.userDetails_pref_name, MODE_PRIVATE);


        user_id = pending_job_pref.getString(Variables.id, "");
        createLocationRequest();
    }

    private LocationService_callback serviceCallbacks;

    public void setCallbacks(LocationService_callback callbacks) {
        serviceCallbacks = callbacks;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Thread thread = new Thread(new GetLocation_Service.Mythreadclass());
        thread.start();

        if(Build.VERSION.SDK_INT >25){
           startRunningInForeground();
        }

        return Service.START_STICKY;
    }


    protected synchronized void buildGoogleApiClient() {
        Log.d("pky","in  build google client");

    }

    protected void createLocationRequest(){
        Log.d("pky","in createlocationRequest");
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL);
            locationRequest.setFastestInterval(FATEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
        startLocationUpdates();
    }


    LocationCallback locationCallback;

    public void   startLocationUpdates() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    mLastLocation = locationResult.getLastLocation();
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();

                    latLng =  new LatLng(latitude,longitude);
                    if(serviceCallbacks!=null)
                        serviceCallbacks.onDataReceived(new LatLng(latitude,longitude));

                    Log.d(Variables.TAG,"longitude"+longitude+"-- latitude"+longitude);


                    upload_to_sharedPreference(new LatLng(latitude,longitude));
                    geoFire.setLocation(Variables.userDetails_pref.getString(Variables.id, ""), new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));



                    DriverModel model = new  DriverModel(Integer.parseInt(Variables.userDetails_pref.getString(Variables.id, "")),
                            Variables.userDetails_pref.getString(Variables.uid, ""),
                            Variables.userDetails_pref.getString(Variables.name, ""),
                            Variables.userDetails_pref.getString(Variables.phone, ""),
                            "",
                            Double.toString(mLastLocation.getLatitude()),
                            Double.toString(mLastLocation.getLongitude()),
                            0,
                             ServerValue.TIMESTAMP,
                            Variables.userDetails_pref.getString(Variables.image, "")
                    ) ;

                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(getBaseContext());


                    model.setCurrent_order_id(sharedPreferences.getString(Variables.current_order_id, ""));
                    model.setCurrent_order_status(sharedPreferences.getString(Variables.current_order_status, ""));
                    model.setCurrent_order_update_at(sharedPreferences.getString(Variables.current_order_update_at, ""));

                       //Toast.makeText(getBaseContext(), sharedPreferences.getString(Variables.current_order_id, ""), Toast.LENGTH_SHORT).show();
                    ref1.child(  Variables.userDetails_pref.getString(Variables.id, "")).setValue(model, (databaseError, databaseReference) -> {
                        if (databaseError != null) {
                       //     Toast.makeText(DriverActivity.this, "Data could not be saved " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            //     Toast.makeText(DriverActivity.this, "Data saved successfully.", Toast.LENGTH_SHORT).show();
                        }
                    });

                /*    Map<String, Object> data = new HashMap<>();
                    data.put("latitude",  Double.toString(mLastLocation.getLatitude()));
                    data.put("longitude", Double.toString(mLastLocation.getLongitude()));
                    data.put("uid",   Variables.userDetails_pref.getString(Variables.uid, ""));
                    data.put("name",  Variables.userDetails_pref.getString(Variables.name, ""));
                    data.put("phone",   Variables.userDetails_pref.getString(Variables.phone, ""));
                    data.put("image", Variables.userDetails_pref.getString(Variables.image, ""));
                    data.put("current_order_id", sharedPreferences.getString(Variables.current_order_id, ""));
                    data.put("current_order_status", sharedPreferences.getString(Variables.current_order_status, ""));
                    data.put("created_at",  ServerValue.TIMESTAMP);
*/


                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                 //   cal.setTimeInMillis(Timestamp.now().getSeconds()*1000);
                    String date = DateFormat.format("MM-yyyy", cal).toString();

                 //   db.collection("user").document(Variables.userDetails_pref.getString(Variables.id, "")).collection(date).add(model);

                }
            }, Looper.myLooper());

        } catch (Exception e) {
            e.printStackTrace();
        }

        locationCallback= new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {

                    }
                }
            }
        };

        mFusedLocationClient.requestLocationUpdates(locationRequest,locationCallback
                , Looper.myLooper());

    }

    protected void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }



    public void upload_to_sharedPreference(LatLng latLng){

        Log.d(Variables.TAG,latLng.toString());

        double lat =  (latLng.latitude);
        double lon =  (latLng.longitude);

        SharedPreferences.Editor editor = pending_job_pref.edit();
        editor.putString(Variables.my_current_lat,Double.toString(lat) );
        editor.putString(Variables.my_current_lng,Double.toString(lon) );
        editor.commit();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    public  void CallApi_Of_add_LOcation(double lat , double lng) {

       /* JSONObject jsonObject1 = new JSONObject();


        try {
            jsonObject1.put("lat",lat+"");
            jsonObject1.put("long",lng+"");
            if(user_id !=null) {
                jsonObject1.put("user_id", user_id);
            }
        } catch (JSONException e) {
            Log.d(Variables.TAG,e.toString());
        }


        ApiRequest.Call_Api(this, Api_urls.URL_ADD_USER_LAT_LONG, jsonObject1,null);*/
    }

    private void startRunningInForeground() {

        if (Build.VERSION.SDK_INT >= 26) {

            if (Build.VERSION.SDK_INT > 26) {

                NotificationChannel notificationChannel = null;
                notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                        CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_MIN);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setShowBadge(true);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (manager != null) {
                    manager.createNotificationChannel(notificationChannel);
                }

                 Notification notification = new Notification.Builder(getApplicationContext(),CHANNEL_ONE_ID)
                        .setChannelId(CHANNEL_ONE_ID)
                        .setSmallIcon(R.drawable.ic_location)
                         .setContentText("Running in background & Location  Updating.....")
                         .build();

                Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                notification.contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

                 startForeground(101, notification);
            }
            else {
                startForeground(101, updateNotification());
            }
        }

        else {

            Notification notification = new NotificationCompat.Builder(this,CHANNEL_ONE_ID)
                    .setSmallIcon(R.drawable.ic_location)

                    .setContentText("Running in background & Location  Updating.....")
                    .setOngoing(true).build();

            startForeground(101, notification);
        }
    }


    private Notification updateNotification() {

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        return new NotificationCompat.Builder(this,CHANNEL_ONE_ID)
                .setSmallIcon(R.drawable.ic_pause_icon)
                .setContentIntent(pendingIntent)

                .setOngoing(true).build();
    }
}

