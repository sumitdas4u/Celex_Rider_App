package com.celex.rider.AllActivitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.R;
import com.celex.rider.CodeClasses.DarkModePrefManager;
import com.celex.rider.CodeClasses.Variables;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.celex.rider.interfaces.CallBack_internet;
import com.celex.rider.interfaces.Callback;

import org.json.JSONException;
import org.json.JSONObject;

public class Splashscreen_A extends AppCompatActivity {


    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splashscreen);
        Fresco.initialize(this);

        Variables.userDetails_pref = getSharedPreferences(Variables.userDetails_pref_name, MODE_PRIVATE);
        Variables.download_sharedPreferences = getSharedPreferences(Variables.download_pref, MODE_PRIVATE);
   
    }

    @Override
    protected void onStart() {
        super.onStart();
        Functions.RegisterConnectivity(this, new CallBack_internet() {
            @Override
            public void Get_Response(String requestType, String response) {
                if (response.equalsIgnoreCase("disconnected")) {
                    startActivity(new Intent(Splashscreen_A.this, Wifi_Activity.class));
                    overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                } else {
                    Go_to_next_screen();
                }
            }
        });
    }



    private void Go_to_next_screen() {
        new Handler().postDelayed(() -> {

            boolean check = Variables.userDetails_pref.getBoolean(Variables.is_login, false);

            if (check) {



                JSONObject params = new JSONObject();
                try {

                    params.put("uid",Variables.userDetails_pref.getString(Variables.uid, ""));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApiRequest.Call_Api(this, Api_urls.VALID_TOKEN, params, resp -> {
                  /*  Toast.makeText(this,  resp,
                            Toast.LENGTH_LONG).show();*/
                    if (resp != null) {
                        try {

                            JSONObject respobj = new JSONObject(resp);
                            if (respobj.getString("status").equals("1")) {
                                Intent start_intent = new Intent(Splashscreen_A.this, MainActivity.class);
                                if (getIntent() != null) {
                                    if (getIntent().getExtras() != null) {
                                        start_intent.putExtras(getIntent().getExtras());
                                        setIntent(null);
                                    }
                                }
                                startActivity(start_intent);
                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                finish();
                            }else{
                                startActivity(new Intent(Splashscreen_A.this, GetStarted_A.class));
                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            startActivity(new Intent(Splashscreen_A.this, GetStarted_A.class));
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                            finish();

                        }

                    }
                });
            } else {
                startActivity(new Intent(Splashscreen_A.this, GetStarted_A.class));
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                finish();
            }

        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Functions.unRegisterConnectivity(Splashscreen_A.this);
    }
}
