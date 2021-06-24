package com.celex.rider.AllActivitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.R;
import com.celex.rider.interfaces.CallBack_internet;

public class Wifi_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        Functions.RegisterConnectivity(this, new CallBack_internet() {
            @Override
            public void Get_Response(String requestType, String response) {
                if(response.equalsIgnoreCase("connected")) {
                   finish();
                    overridePendingTransition(R.anim.in_from_top,R.anim.out_from_bottom);
                }
            }
        });

    }

}