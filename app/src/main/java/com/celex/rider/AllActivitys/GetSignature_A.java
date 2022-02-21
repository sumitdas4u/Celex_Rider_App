package com.celex.rider.AllActivitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.R;
import com.celex.rider.Utils.DrawingViewUtils;
import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.DarkModePrefManager;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.interfaces.CallBack_internet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GetSignature_A extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.main_drawing_view)
    DrawingViewUtils mDrawingViewUtils;

    private int mCurrentBackgroundColor;
    private int mCurrentColor;
    private int mCurrentStroke;
    TextView tv_date , signed_text;
    String order_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_getsignature);


        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            order_id = extras.getString("order_id");
        }


        ButterKnife.bind(this);
        initDrawingView();




        tv_date = findViewById(R.id.tv_last_date);
        signed_text = findViewById(R.id.signed_text);

        findViewById(R.id.iv_back).setOnClickListener(this::onClick);
        findViewById(R.id.tv_reset).setOnClickListener(this::onClick);
        findViewById(R.id.tv_submit).setOnClickListener(this::onClick);

        Functions.RegisterConnectivity(this, new CallBack_internet() {
            @Override
            public void Get_Response(String requestType, String response) {
                if(response.equalsIgnoreCase("disconnected")) {
                    startActivity(new Intent(GetSignature_A.this,Wifi_Activity.class));
                    overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top);
                }
            }
        });
        check_location();
}


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.tv_reset:
                mDrawingViewUtils.clearCanvas();
                break;

            case R.id.tv_submit:


                if(!signed_text.getText().toString().isEmpty()) {


                        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        //Find the currently focused view, so we can grab the correct window token from it.
                        View view = this.getCurrentFocus();
                        //If no view currently has focus, create a new one, just so we can grab a window token from it
                        if (view == null) {
                            view = new View(this);
                        }
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    CallApi_addSignature(GetSignature_A.this, Functions.Convert_Bitmap_to_base64(mDrawingViewUtils.getBitmap()), order_id);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 100);


                }else{
                    Functions.dialouge(this,getResources().getString(R.string.alert),getResources().getString(R.string.please_enter_name));
                }
                break;

            default: return;
        }
    }

    private void initDrawingView()
    {
        mCurrentBackgroundColor = ContextCompat.getColor(this, android.R.color.white);
        mCurrentColor = ContextCompat.getColor(this, android.R.color.black);
        mCurrentStroke = 10;

        mDrawingViewUtils.setBackgroundColor(mCurrentBackgroundColor);
        mDrawingViewUtils.setPaintColor(mCurrentColor);
        mDrawingViewUtils.setPaintStrokeWidth(mCurrentStroke);

    }



    private void CallApi_addSignature(Context context,String base64,String order_id) throws IOException {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("order_id", order_id);
            sendobj.put("signature_person_name", signed_text.getText().toString());
            JSONObject file_data = new JSONObject();
            file_data.put("file_data",base64);
            sendobj.put("signature",base64);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("freshly_logged","params at sendoNj"+sendobj.toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Functions.wrtieFileOnInternalStorage(context,"signature_api_params",sendobj.toString(),"","");
        }

        Functions.show_loader(this,false,false);
        ApiRequest.Call_Api(GetSignature_A.this, Api_urls.URL_ADD_SIGNATURE, sendobj, resp ->  {


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
                        finish();
                    }

                }


        });
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
        startActivity(new Intent(GetSignature_A.this, Enable_location_A.class));
        overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Functions.unRegisterConnectivity(GetSignature_A.this);
    }

}
