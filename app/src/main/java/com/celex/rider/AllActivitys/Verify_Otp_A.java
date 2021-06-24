package com.celex.rider.AllActivitys;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.DarkModePrefManager;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.R;
import com.celex.rider.Utils.DrawingViewUtils;
import com.celex.rider.interfaces.CallBack_internet;
import com.chaos.view.PinView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Verify_Otp_A extends AppCompatActivity implements View.OnClickListener{




    PinView pin_view;
    private String order_id;
    private String otp;
    private Verify_Otp_A context;
    private TextView resent_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_otp_verification);


        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            order_id = extras.getString("order_id");
            otp = extras.getString("otp");
        }





        pin_view = findViewById(R.id.pin_view);
        resent_text= findViewById(R.id.otp_resend_text);

        findViewById(R.id.iv_back).setOnClickListener(this::onClick);
        findViewById(R.id.otp_resend_text).setOnClickListener(this::onClick);

        context = this;

        pin_view.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(start==3 && count ==1){
                    if(!pin_view.getText().toString().isEmpty() & pin_view.getText().toString().equals(otp)) {

                        try {
                            CallApi_otp_verify(context);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }else{
                        Functions.dialouge(context,getResources().getString(R.string.alert), "Otp Not Matched");
                    }
                }

             //   Log.d("TAG", "onTextChanged() called with: s = [" + s + "], start = [" + start + "], before = [" + before + "], count = [" + count + "]");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }


        });

        Functions.RegisterConnectivity(this, new CallBack_internet() {
            @Override
            public void Get_Response(String requestType, String response) {
                if(response.equalsIgnoreCase("disconnected")) {
                    startActivity(new Intent(Verify_Otp_A.this,Wifi_Activity.class));
                    overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top);
                }
            }
        });
        check_location();
}


    private void CallApi_otp_verify(Context context) throws IOException {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("order_id", order_id);
            sendobj.put("otp", otp);



        } catch (JSONException e) {
            e.printStackTrace();
        }



        Functions.show_loader(this,false,false);
        ApiRequest.Call_Api(Verify_Otp_A.this, Api_urls.URL_VERIFY_OTP, sendobj, resp ->  {


            if (resp!=null){

                try {
                    JSONObject respobj = new JSONObject(resp);

                    if (respobj.getString("status").equals("1")){

                        Functions.cancel_loader();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result","ok");
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(Variables.TAG," error  addSignature api" +e);
                    Functions.cancel_loader();
                   // finish();
                }

            }


        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.otp_resend_text:
                send_delivery_sms();
                break;



            default: return;
        }
    }

    private void send_delivery_sms() {
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("order_id", order_id);
            sendobj.put("otp", otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(resent_text.getText()== getResources().getString(R.string.resent_otp_text)) {

            ApiRequest.Call_Api(this, Api_urls.URL_SEND_DELIVERY_SMS, sendobj, resp ->  {

                if (resp!=null){

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("status").equals("1")){

                            new CountDownTimer(60000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    resent_text.setText( getResources().getString(R.string.resent_otp_text)+" in: " + millisUntilFinished / 1000);
                                    //here you can have your logic to set text to edittext
                                }

                                public void onFinish() {
                                    resent_text.setText( getResources().getString(R.string.resent_otp_text));
                                }

                            }.start();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(Variables.TAG," error  otp resend" +e);

                    }

                }


            });


        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private class GpsStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            check_location();
        }
    }

    private GpsStatusReceiver receiver = new GpsStatusReceiver();

    @Override
    protected void onResume() {
        super.onResume();
        if (receiver == null) {
            registerReceiver(receiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    private void check_location() {
        LocationManager lm = (LocationManager) this.getSystemService(Service.LOCATION_SERVICE);
        boolean isEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isEnabled) {
            enable_location();
        }
    }

    private void enable_location() {
        startActivity(new Intent(Verify_Otp_A.this, Enable_location_A.class));
        overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Functions.unRegisterConnectivity(Verify_Otp_A.this);
    }

}
