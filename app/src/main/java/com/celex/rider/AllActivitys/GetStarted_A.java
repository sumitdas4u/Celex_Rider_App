package com.celex.rider.AllActivitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.celex.rider.AllFragments.Login_F;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.R;
import com.celex.rider.CodeClasses.DarkModePrefManager;
import com.celex.rider.interfaces.CallBack_internet;


public class GetStarted_A extends AppCompatActivity implements View.OnClickListener {

    TextView tv_get_started;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_getstarted);

        METHOD_init_views();

        Functions.RegisterConnectivity(this, new CallBack_internet() {
            @Override
            public void Get_Response(String requestType, String response) {
                if(response.equalsIgnoreCase("disconnected")) {
                    startActivity(new Intent(GetStarted_A.this,Wifi_Activity.class));
                    overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top);
                }
            }
        });
    }



    private void METHOD_init_views(){
        tv_get_started = findViewById(R.id.tv_get_started);
        tv_get_started.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_get_started:
                Login_F f = new Login_F();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.main_container, f).addToBackStack(null).commit();

                break;

            default:
                break;

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Functions.unRegisterConnectivity(GetStarted_A.this);
    }

}
