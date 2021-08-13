package com.celex.rider.AllActivitys;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

 
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.R;
import com.celex.rider.Location_Services.GpsUtils;
import com.roam.sdk.Roam;

/**
 * A simple {@link Fragment} subclass.
 */
public class Enable_location_A extends AppCompatActivity  {


    TextView enable_location_btn;


    public Enable_location_A() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_location);

        enable_location_btn = findViewById(R.id.enable_location_btn);
        enable_location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationPermission();
            }
        });

    }


    private void getLocationPermission() {

        if (!Roam.checkLocationServices()) {
            Roam.requestLocationServices(this);
        } else if (!Roam.checkLocationPermission()) {
            Roam.requestLocationPermission(this);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Roam.checkBackgroundLocationPermission()) {
            Roam.requestBackgroundLocationPermission(this);
        } else {
            finish();
        }


    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

}

