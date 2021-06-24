package com.celex.rider.AllActivitys;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.DarkModePrefManager;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.DataModels.My_Orders_Model;
import com.celex.rider.R;
import com.celex.rider.Utils.DrawingViewUtils;
import com.celex.rider.interfaces.CallBack_internet;
import com.celex.rider.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GetCancelReason_A extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.main_drawing_view)
    DrawingViewUtils mDrawingViewUtils;

    private int mCurrentBackgroundColor;
    private int mCurrentColor;
    private int mCurrentStroke;
    TextView tv_date , signed_text;
    String order_id;
    private Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_cancel_reason);

        //get the spinner from the xml.



        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            order_id = extras.getString("order_id");
        }


//        ButterKnife.bind(this);
       // initDrawingView();




        tv_date = findViewById(R.id.tv_last_date);
        signed_text = findViewById(R.id.signed_text);
       dropdown = findViewById(R.id.spinner_reason);
        findViewById(R.id.iv_back).setOnClickListener(this::onClick);
        findViewById(R.id.tv_reset).setOnClickListener(this::onClick);
        findViewById(R.id.tv_submit).setOnClickListener(this::onClick);

        reasonDropdown();
        Functions.RegisterConnectivity(this, new CallBack_internet() {
            @Override
            public void Get_Response(String requestType, String response) {
                if(response.equalsIgnoreCase("disconnected")) {
                    startActivity(new Intent(GetCancelReason_A.this,Wifi_Activity.class));
                    overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top);
                }
            }
        });
        check_location();
}

    private void reasonDropdown() {


        Functions.show_loader(this, false, false);
            ApiRequest.Call_Api_GetRequest(this, Api_urls.URL_GET_CANCEL_REASON, resp -> {
                String[] items = new String[0];
                Functions.cancel_loader();
                try {
                    JSONObject respobj = new JSONObject(resp);
                    if (respobj.getString("status").equals("1")) {
                        JSONArray arr = respobj.getJSONArray("data");

                         items=    new String[arr.length()];
                        for(int i=0; i < arr.length(); i++){
                            items[i]= (String) arr.get(i);
                        }
                    }



                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, items);

                    dropdown.setAdapter(adapter);

                    String[] finalItems = items;
                    dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            adapter.notifyDataSetChanged();
                            if(position==0 || position ==finalItems.length-1){
                                signed_text.setText("");//This will be the student id.
                            }else{
                                signed_text.setText(finalItems[position]);
                            }




                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.tv_reset:
                onBackPressed();
                break;

            case R.id.tv_submit:
                if(!signed_text.getText().toString().isEmpty()) {
//                    Functions.cancel_loader();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("reason",signed_text.getText().toString());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }else{
                    Functions.dialouge(this,getResources().getString(R.string.alert),getResources().getString(R.string.please_enter_reason));
                }
                break;

            default:
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
        startActivity(new Intent(GetCancelReason_A.this, Enable_location_A.class));
        overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Functions.unRegisterConnectivity(GetCancelReason_A.this);
    }

}
