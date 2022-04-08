package com.celex.rider.AllActivitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.booking.rtlviewpager.RtlViewPager;
import com.celex.rider.Adapters.Pager_Adapter;
import com.celex.rider.AllFragments.Document.Documents_F;
import com.celex.rider.AllFragments.History_F;
import com.celex.rider.AllFragments.HomeFragments.Home_F;
import com.celex.rider.AllFragments.ManageVehicle_F;
import com.celex.rider.AllFragments.Meter_Reading_F;
import com.celex.rider.AllFragments.Profile_F;
import com.celex.rider.AllFragments.Return_F;
import com.celex.rider.AllFragments.Setting_F;
import com.celex.rider.AllFragments.Trip_Summary_F;
import com.celex.rider.BuildConfig;
import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.DarkModePrefManager;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.CodeClasses.Variables;

import com.celex.rider.R;
import com.celex.rider.Users_Chat.Chat_Activity;
import com.celex.rider.interfaces.Callback;
import com.celex.rider.interfaces.oncallback;
import com.celex.rider.service.ForegroundService;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import com.google.firebase.messaging.FirebaseMessaging;
import com.roam.sdk.Roam;
import com.roam.sdk.RoamPublish;
import com.roam.sdk.RoamTrackingMode;
import com.roam.sdk.callback.RoamActiveTripsCallback;
import com.roam.sdk.callback.RoamCallback;
import com.roam.sdk.callback.RoamCreateTripCallback;
import com.roam.sdk.callback.RoamLocationCallback;
import com.roam.sdk.callback.RoamLogoutCallback;
import com.roam.sdk.callback.RoamTripCallback;
import com.roam.sdk.callback.RoamTripStatusCallback;
import com.roam.sdk.callback.RoamTripSummaryCallback;
import com.roam.sdk.models.ActiveTrips;
import com.roam.sdk.models.RoamError;
import com.roam.sdk.models.RoamTrip;
import com.roam.sdk.models.RoamTripStatus;
import com.roam.sdk.models.RoamUser;
import com.roam.sdk.models.createtrip.RoamCreateTrip;
import com.roam.sdk.models.tripsummary.RoamTripSummary;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.celex.rider.CodeClasses.Functions.roam_update_meta_data_with_location;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static MainActivity mainMenuActivity;
    public static DrawerLayout drawer;
    long mBackPressed = 0;
    Pager_Adapter pager_adapter;
    private TextView tv_username_drawer, txt_app_version, tv_online_txt;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ImageView image_login_status, admin_chat;
    public static int selected_posiotion = 0;
    private String language;
    CircleImageView iv_profile_drawer;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private ProgressBar progressBar;
    private Button btn_create_trip,btn_start_pause_trip,btn_stop_trip,btn_test_trip;
    private String store_user_id, order_id, firstname, last_name, id, st_store_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Variables.userDetails_pref = getSharedPreferences(Variables.userDetails_pref_name, MODE_PRIVATE);

        Variables.userDetails_pref = getSharedPreferences(Variables.userDetails_pref_name, MODE_PRIVATE);
        language = Variables.userDetails_pref.getString(Variables.setlocale, "en");

        Locale myLocale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();

        Configuration conf = new Configuration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);



        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitView();
        Initialize_click_listener();
        showOnlineStatus();
        //Get_Public_IP();

        Intent intent_chat = getIntent();

        if (intent_chat != null) {
            String pushnotification = intent_chat.getStringExtra("pushnotification");

            if (pushnotification != null && pushnotification.equals("yes")) {

                String receiver_name = intent_chat.getStringExtra("Receiver_name");
                String type = intent_chat.getStringExtra("type");
                String receiverid = intent_chat.getStringExtra("Receiverid");
                String senderid = intent_chat.getStringExtra("senderid");
                String receiver_pic = intent_chat.getStringExtra("Receiver_pic");
                String receiver_order_id = intent_chat.getStringExtra("Receiver_order_id");
                String receiver_store_id = intent_chat.getStringExtra("Receiver_store_id");

                if (type != null) {
                    if (type.contains("rider")) {
                        open_chat_fragment(receiver_name, type, receiverid, senderid, receiver_pic, receiver_order_id, receiver_store_id);
                    }
                }
            } else {
                ///For Background Notification to open chat
                String title = intent_chat.getStringExtra("title");
                String message = intent_chat.getStringExtra("body");
                String sender = intent_chat.getStringExtra("sender");
                String recevier = intent_chat.getStringExtra("receiver");
                String action_type = intent_chat.getStringExtra("type");
                String order = intent_chat.getStringExtra("order");

                if (action_type != null && action_type.contains("chat")) {
                    try {
                        if (order != null && !order.equals("")) {
                            JSONObject order_Obj = new JSONObject(order);
                            store_user_id = order_Obj.optString("store_user_id");
                            order_id = order_Obj.optString("id");
                        }

                        JSONObject sender_Obj = new JSONObject(sender);
                        firstname = sender_Obj.optString("first_name");
                        last_name = sender_Obj.optString("last_name");
                        id = sender_Obj.getString("id");
                        st_store_img = sender_Obj.optString("image");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    open_chat_fragment(firstname + " " + last_name, action_type, id, Variables.userDetails_pref.getString(Variables.id, ""), st_store_img, order_id, store_user_id);
                }
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            check_permissions();
        }

        tv_username_drawer.setText((Variables.userDetails_pref.getString(Variables.name, "")));
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = ((PackageInfo) pInfo).versionName;
            txt_app_version.setText("V" + " " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        String image_url = Variables.userDetails_pref.getString(Variables.image, "app/dtrack");

        if (image_url != null && !image_url.equals("")) {

            update_profile_image(image_url);
        }

        pager_adapter = new Pager_Adapter(getResources(), getSupportFragmentManager());
        viewPager.setAdapter(pager_adapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        viewPager = new RtlViewPager(MainActivity.this);
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_layout);
                if (!(fragment instanceof Home_F))
                    admin_chat.setVisibility(View.GONE);
            } else {
                admin_chat.setVisibility(View.GONE);

            }
        });

        Functions.RegisterConnectivity(this, (requestType, response) -> {
            if (response.equalsIgnoreCase("disconnected")) {
                startActivity(new Intent(MainActivity.this, Wifi_Activity.class));
                overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
            }
        });


        Roam.getUser( Variables.userDetails_pref.getString(Variables.geospark_user, ""), new RoamCallback() {
            @Override
            public void onSuccess(RoamUser RoamUser) {
                Log.d("TAG","uid geo"+ RoamUser.getUserId());
                SharedPreferences.Editor editor = Variables.userDetails_pref.edit();
                editor.putString(Variables.geospark_user,   RoamUser.getUserId());
                editor.apply();
            }
            @Override
            public void onFailure(RoamError error) {
                Roam.createUser(Variables.userDetails_pref.getString(Variables.name, ""),null, new RoamCallback() {
                    @Override
                    public void onSuccess(RoamUser RoamUser) {
                        SharedPreferences.Editor editor = Variables.userDetails_pref.edit();
                        editor.putString(Variables.geospark_user,   RoamUser.getUserId());
                        editor.apply();
                    }
                    @Override
                    public void onFailure(RoamError RoamError) {
                        RoamError.getCode();
                        RoamError.getMessage();
                    }
                });
                // do something when get user failure
                // access Roam error code with error.getCode()
                // access Roam error message with error.getMessage()
            }
        });
        Log.d("TAG","uid"+ Variables.userDetails_pref.getString(Variables.geospark_user, ""));

        Roam.toggleListener(true, true, new RoamCallback() {
            @Override
            public void onSuccess(RoamUser roamUser) {
                Log.d("geo toogle success","uid"+ Variables.userDetails_pref.getString(Variables.geospark_user, ""));


            }

            @Override
            public void onFailure(RoamError roamError) {

                Log.e("geo", "toggleListenerError: "+  "called");
            }
        });

        METHOD_customizeTabs();
        enable_location();








    }

    //Open chat frgment
    public void open_chat_fragment(String receiver_name, String type, String receiverid, String senderid, String receiver_pic, String receiver_order_id, String receiver_store_id) {
        Bundle args = new Bundle();

        args.putString("senderid", Variables.userDetails_pref.getString(Variables.id, ""));
        args.putString("st_store_user_id", receiverid);
        args.putString("store_name", receiver_name);
        args.putString("store_img", receiver_pic);
        args.putString("order_id", receiver_order_id);
        args.putString("store_id", receiver_store_id);
        args.putString("fragment", type);

        Chat_Activity chat_activity = new Chat_Activity();
        FragmentManager fm1 = getSupportFragmentManager();
        chat_activity.setArguments(args);
        FragmentTransaction ft1 = fm1.beginTransaction();
        ft1.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft1.replace(R.id.main_container, chat_activity).addToBackStack(null).commit();

    }


    //to get public ip address of the network
    public void Get_Public_IP() {
        ApiRequest.Call_Api_GetRequest(this, "https://api.ipify.org/?format=json", resp -> {
            try {
                JSONObject responce = new JSONObject(resp);
                String ip = responce.optString("ip");
               // Add_Firebase_tokon(ip);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }



    //Add Firebase Tokon to server





    public void InitView() {
        iv_profile_drawer = findViewById(R.id.iv_profile);
        admin_chat = findViewById(R.id.admin_chat_btn);
        txt_app_version = findViewById(R.id.txt_app_version);
        drawer = findViewById(R.id.drawer);
        image_login_status = findViewById(R.id.image_login_status);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        tv_username_drawer = findViewById(R.id.tv_username_drawer);
        tv_online_txt = findViewById(R.id.tv_online_txt);
        progressBar = findViewById(R.id.progressbar);
        btn_create_trip = findViewById(R.id.btnCreateTrip);
        btn_start_pause_trip = findViewById(R.id.btnTrip);
        btn_stop_trip = findViewById(R.id.btnTripStop);
        btn_test_trip = findViewById(R.id.btn_test);
    }

    public void Initialize_click_listener() {
        findViewById(R.id.admin_chat_btn).setOnClickListener(this);
        findViewById(R.id.logout_btn).setOnClickListener(this);
        findViewById(R.id.profile_btn).setOnClickListener(this);
        findViewById(R.id.iv_menu).setOnClickListener(this);
        findViewById(R.id.setting_btn).setOnClickListener(this);
        findViewById(R.id.meter_btn).setOnClickListener(this);
        findViewById(R.id.manage_vehicle_btn).setOnClickListener(this);
        findViewById(R.id.Documents_btn).setOnClickListener(this);
        findViewById(R.id.history_btn).setOnClickListener(this);
        findViewById(R.id.return_btn).setOnClickListener(this);
        findViewById(R.id.home_btn).setOnClickListener(this);
        findViewById(R.id.btnCreateTrip).setOnClickListener(this);
        findViewById(R.id.btnTrip).setOnClickListener(this);
        findViewById(R.id.btnTripStop).setOnClickListener(this);
        findViewById(R.id.btn_test).setOnClickListener(this);
        findViewById(R.id.trip_btn).setOnClickListener(this);


    }

    @SuppressLint("SetTextI18n")
    private void METHOD_customizeTabs() {

        View v1 = LayoutInflater.from(this).inflate(R.layout.item_order_tabs_layout, null);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.black));

        TextView tv1 = v1.findViewById(R.id.tv_tab_title);
        tv1.setText(R.string.new_order);
        tv1.setTextColor(getResources().getColor(R.color.white));
        tv1.setBackground(getResources().getDrawable(R.drawable.d_round_corner_colored));
        tabLayout.getTabAt(0).setCustomView(v1);

        View v2 = LayoutInflater.from(this).inflate(R.layout.item_order_tabs_layout, null);
        TextView tv2 = v2.findViewById(R.id.tv_tab_title);
        tv2.setText(R.string.processing);
        tv2.setTextColor(getResources().getColor(R.color.black));
        tv2.setBackground(getResources().getDrawable(R.drawable.d_round_button_white_tab));
        tabLayout.getTabAt(1).setCustomView(v2);

        View v3 = LayoutInflater.from(this).inflate(R.layout.item_order_tabs_layout, null);
        TextView tv3 = v3.findViewById(R.id.tv_tab_title);
        tv3.setText("On The Way");
        tv3.setTextColor(getResources().getColor(R.color.black));
        tv3.setBackground(getResources().getDrawable(R.drawable.d_round_button_white_tab));
        tabLayout.getTabAt(2).setCustomView(v3);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                selected_posiotion = tab.getPosition();

                if (v != null) {
                    TextView tv = v.findViewById(R.id.tv_tab_title);
                    tv.setTextColor(getResources().getColor(R.color.white));
                    tv.setBackground(getResources().getDrawable(R.drawable.d_round_corner_colored));
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View v = tab.getCustomView();

                if (v != null) {
                    TextView tv = v.findViewById(R.id.tv_tab_title);
                    tv.setTextColor(getResources().getColor(R.color.black));
                    tv.setBackground(getResources().getDrawable(R.drawable.d_round_button_white_tab));
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // on tab reselect this method will be ca
            }
        });
    }

    public static void Open_drawer() {
        drawer.openDrawer(GravityCompat.START);
    }

    FragmentManager fm;

    @Override
    public void onClick(View view) {

        fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentById(R.id.fragment_container_layout);


        switch (view.getId()) {

            case R.id.iv_menu:
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.btnCreateTrip:
                btn_create_trip.setEnabled(false);
                createTrip();
                break;
            case R.id.btnTripStop:
                stopTrip();
                break;
            case R.id.btn_test:
                JSONObject sendobj = new JSONObject();

                try {


                    sendobj.put("st_order_id", id);

                    sendobj.put("reason", "reason");
                    sendobj.put("status", "status");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                roam_update_meta_data_with_location(sendobj);
                break;

            case R.id.btnTrip:
                startActivity(new Intent(this, TripActivity.class).putExtra("OFFLINE", false));
                break;

            case R.id.home_btn:
                if (fragment instanceof Home_F) {
                    drawer.closeDrawers();
                } else {
                    drawer.closeDrawers();
                    remove_old_fragments();
                }
                break;


            case R.id.profile_btn:

                if (fragment instanceof Profile_F) {
                    drawer.closeDrawers();
                } else {
                    drawer.closeDrawers();


                    Profile_F profile_fragment = new Profile_F(new oncallback() {
                        @Override
                        public void onResponce(Bundle bundle) {
                            if (bundle != null) {
                                String image = bundle.getString("image");
                                String fromwhere = bundle.getString("fromWhere");
                                if (fromwhere != null && fromwhere.equals("profiledit")) {
                                    String name = bundle.getString("name");
                                    update_profile_image(image);
                                    tv_username_drawer.setText(name);
                                } else if (fromwhere != null && fromwhere.equals("status")) {
                                    showOnlineStatus();
                                } else {
                                    update_profile_image(image);
                                }
                            }
                        }
                    });
                    fm = getSupportFragmentManager();
                    remove_old_fragments();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_left);
                    ft.replace(R.id.fragment_container_layout, profile_fragment, "profile_f").addToBackStack(null).commit();

                }
                break;
            case R.id.meter_btn:
                if (fragment instanceof Meter_Reading_F) {
                    drawer.closeDrawers();
                } else {
                    drawer.closeDrawers();
                    Meter_Reading_F meter_fragment = new Meter_Reading_F();
                    fm = getSupportFragmentManager();
                    remove_old_fragments();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_left);
                    fragmentTransaction.replace(R.id.fragment_container_layout, meter_fragment, "meter_fragment").addToBackStack(null).commit();
                }
                break;
            case R.id.trip_btn:
                if (fragment instanceof Trip_Summary_F) {
                    drawer.closeDrawers();
                } else {
                    drawer.closeDrawers();
                    Trip_Summary_F trip_fragment = new Trip_Summary_F();
                    fm = getSupportFragmentManager();
                    remove_old_fragments();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_left);
                    fragmentTransaction.replace(R.id.fragment_container_layout, trip_fragment, "trip_fragment").addToBackStack(null).commit();
                }
                break;
            case R.id.setting_btn:
                if (fragment instanceof Setting_F) {
                    drawer.closeDrawers();
                } else {
                    drawer.closeDrawers();
                    Setting_F setting_fragment = new Setting_F();
                    fm = getSupportFragmentManager();
                    remove_old_fragments();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_left);
                    fragmentTransaction.replace(R.id.fragment_container_layout, setting_fragment, "setting_f").addToBackStack(null).commit();
                }
                break;

            case R.id.logout_btn:
                showPopup();
                break;

            case R.id.Documents_btn:
                if (fragment instanceof Documents_F) {
                    drawer.closeDrawers();
                } else {
                    drawer.closeDrawers();

                    Documents_F documents_f = new Documents_F();
                    fm = getSupportFragmentManager();
                    remove_old_fragments();
                    FragmentTransaction fragmentTransaction_documents = fm.beginTransaction();
                    fragmentTransaction_documents.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_left);
                    fragmentTransaction_documents.replace(R.id.fragment_container_layout, documents_f, "documents_f").addToBackStack(null).commit();

                }
                break;

            case R.id.manage_vehicle_btn:

            //    startActivity(new Intent(MainActivity.this, DriverActivity.class));


                if (fragment instanceof ManageVehicle_F) {
                    drawer.closeDrawers();
                } else {
                    drawer.closeDrawers();

                    ManageVehicle_F manageVehicle_a = new ManageVehicle_F();
                    fm = getSupportFragmentManager();
                    remove_old_fragments();
                    FragmentTransaction fragmentTransaction_vehicle = fm.beginTransaction();
                    fragmentTransaction_vehicle.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_left);
                    fragmentTransaction_vehicle.replace(R.id.fragment_container_layout, manageVehicle_a, "manageVehicle_f").addToBackStack(null).commit();
                    drawer.closeDrawers();
                }


                break;

            case R.id.history_btn:

                if (fragment instanceof History_F) {
                    drawer.closeDrawers();
                } else {
                    drawer.closeDrawers();

                    History_F history_a = new History_F();
                    fm = getSupportFragmentManager();
                    remove_old_fragments();
                    FragmentTransaction fragmentTransaction_history = fm.beginTransaction();
                    fragmentTransaction_history.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_left);
                    fragmentTransaction_history.replace(R.id.fragment_container_layout, history_a, "fragment_history").addToBackStack(null).commit();

                }
                break;
            case R.id.return_btn:

                if (fragment instanceof Return_F) {
                    drawer.closeDrawers();
                } else {
                    drawer.closeDrawers();

                    Return_F return_f = new Return_F();
                    fm = getSupportFragmentManager();
                    remove_old_fragments();
                    FragmentTransaction fragmentTransaction_history = fm.beginTransaction();
                    fragmentTransaction_history.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_left);
                    fragmentTransaction_history.replace(R.id.fragment_container_layout, return_f, "fragment_return").addToBackStack(null).commit();

                }
                break;

            case R.id.admin_chat_btn:
                drawer.closeDrawers();
                open_chat_fragment("Admin", "admin", "0", Variables.userDetails_pref.getString(Variables.id, ""), "", "0", "0");

                break;

            default:
                return;
        }
    }
    private void show() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void hide() {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

     String getActiveTrip() {


        hide();
     //   Log.d("Geo","trips_id"+ trips_id[0]);
        return" trips_id[0]";
    }

    private void checkActiveTrip() {

        show();

        Roam.activeTrips(false, new RoamActiveTripsCallback() {
            @Override
            public void onSuccess(RoamTrip RoamTrip) {

                List<ActiveTrips> activeTrips = RoamTrip.getActiveTrips();

                if (activeTrips.size() != 0) {
                    // activeTrips.get(0).getSyncStatus();
                    SharedPreferences.Editor editor = Variables.userDetails_pref.edit();
                    editor.putString(Variables.trip_roam_current_id, activeTrips.get(0).getTripId());
                    editor.apply();

                     if (activeTrips.get(0).isStarted()) {
                        if (activeTrips.get(0).isPaused()) {
                            btn_start_pause_trip.setTextColor(getResources().getColor(R.color.white));
                            btn_start_pause_trip.setBackgroundColor(getResources().getColor(R.color.accent));
                            btn_start_pause_trip.setText("Trip Paused Not tracking");
                        } else {
                            btn_start_pause_trip.setText("Trips Tracking Started");
                            btn_start_pause_trip.setTextColor(getResources().getColor(R.color.white));
                            btn_start_pause_trip.setBackgroundColor(getResources().getColor(R.color.green));
                        }
                    } else {
                         btn_start_pause_trip.setText("Trips not Started");
                         btn_start_pause_trip.setTextColor(getResources().getColor(R.color.white));
                         btn_start_pause_trip.setBackgroundColor(getResources().getColor(R.color.accent));
                    }
                    btn_start_pause_trip.setVisibility(View.VISIBLE);
                    btn_create_trip.setVisibility(View.GONE);

                    btn_stop_trip.setVisibility(View.VISIBLE);
                         }
                else{
                    btn_create_trip.setEnabled(true);
                    btn_create_trip.setVisibility(View.VISIBLE);
                    btn_start_pause_trip.setVisibility(View.GONE);
                    btn_stop_trip.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(RoamError error) {

                hide();
            }
        });
        hide();




    }
    private void stopTrip( ) {

        show();

        Roam.activeTrips(false, new RoamActiveTripsCallback() {
            @Override
            public void onSuccess(RoamTrip RoamTrip) {

                List<ActiveTrips> activeTrips = RoamTrip.getActiveTrips();

                if (activeTrips.size() != 0) {
                    // activeTrips.get(0).getSyncStatus();
                    String trip_id = activeTrips.get(0).getTripId();
                  //  Toast.makeText(getBaseContext(), trip_id, Toast.LENGTH_SHORT).show();

                    Roam.startTrip(trip_id, "Trip was not started", new RoamTripCallback() {
                        @Override
                        public void onSuccess(String message) {
                            stopTripAndSummary(trip_id);
                          //  Toast.makeText(getBaseContext(),"fail"+trip_id, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(RoamError RoamError) {
                           //Toast.makeText(getBaseContext(),"fail"+trip_id, Toast.LENGTH_SHORT).show();
                            stopTripAndSummary(trip_id);
                        }
                    });


                }else{
                    btn_create_trip.setVisibility(View.VISIBLE);
                    btn_start_pause_trip.setVisibility(View.GONE);
                    btn_stop_trip.setVisibility(View.GONE);
                    hide();
                }


            }

            @Override
            public void onFailure(RoamError error) {

                hide();
            }
        });


    }
private void stopTripAndSummary(String trip_id){

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
            ApiRequest.Call_Api(getApplicationContext(), Api_urls.URL_STOP_TRIP, params, resp ->     {

                if (resp != null) {
                    try {

                        JSONObject respobj = new JSONObject(resp);

                        if(respobj.getString("status").equals("1")){
                            Roam.stopTrip(trip_id, new RoamTripCallback() {
                                @Override
                                public void onSuccess(String message) {
                                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                                    checkActiveTrip();
                                    hide();
                                }
                                @Override
                                public void onFailure(RoamError RoamError) {
                                   // Toast.makeText(getBaseContext(), RoamError.getMessage(), Toast.LENGTH_SHORT).show();
                                    checkActiveTrip();
                                    hide();
                                }
                            });

//delete the current id trip
                            SharedPreferences.Editor editor = Variables.userDetails_pref.edit();
                            editor.putString(Variables.trip_roam_current_id, null);
                            editor.apply();
                        }else{
                        //    Toast.makeText(getBaseContext(), "issue with stop trip", Toast.LENGTH_SHORT).show();
                            hide();
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();

                        hide();
                    }

                }
            });

        }

        @Override
        public void onFailure(RoamError roamError) {
            Toast.makeText(getBaseContext(), "issue with stop trip", Toast.LENGTH_SHORT).show();
            hide();
            Log.d("geo","error"+ roamError.getMessage());
        }

    });
}

    private void createTrip() {

        show();


        Roam.createTrip(null, null, false, null,new RoamCreateTripCallback() {
                @Override
                public void onSuccess(RoamCreateTrip RoamTrip) {

                            Roam.startTrip(RoamTrip.getId(), "Roam-TRIP-DESCRIPTION", new RoamTripCallback() {
                                     @Override
                                     public void onSuccess(String message) {
                                         Toast.makeText(getBaseContext(),"trip started"+ message, Toast.LENGTH_SHORT).show();
                                         checkActiveTrip();
                                         createTripApiCall(RoamTrip.getId());


                                     }

                                     @Override
                                     public void onFailure(RoamError RoamError) {
                                         Toast.makeText(getBaseContext(),RoamError.getMessage(), Toast.LENGTH_SHORT).show();
                                         checkActiveTrip();
                                         hide();


                                     }
                            });

                  }
                  @Override
                 public void onFailure(RoamError RoamError) {
                      checkActiveTrip();
                      hide();
                      btn_create_trip.setEnabled(true);
                 }
        });


    }

    private void createTripApiCall(String trip_id){

        JSONObject params = new JSONObject();
        try {
            params.put("roam_trip_id",trip_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiRequest.Call_Api(this, Api_urls.URL_CREATE_TRIP, params, resp ->     {

            if (resp != null) {
                try {

                    JSONObject respobj = new JSONObject(resp);
                 //   Toast.makeText(getBaseContext(),"trip status"+ respobj.getJSONObject("message").getJSONArray("data").get(0), Toast.LENGTH_SHORT).show();
                    if (respobj.getString("status").equals("1")) {
                        SharedPreferences.Editor editor = Variables.userDetails_pref.edit();
                        editor.putString(Variables.trip_roam_current_id, trip_id);
                        editor.apply();
                        hide();

                    }else{
                        stopTripAndSummary(trip_id);
                        hide();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    stopTripAndSummary(trip_id);


                }

            }
        });

    }
    @Override
    public void onBackPressed() {
        int count = this.getSupportFragmentManager().getBackStackEntryCount();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        } else {
            if (count == 0) {
                if (mBackPressed + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                    return;
                } else {
                    Toast.makeText(getBaseContext(), "Tap Again To Exit", Toast.LENGTH_SHORT).show();
                    mBackPressed = System.currentTimeMillis();
                }
            } else {
                super.onBackPressed();
            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean check_permissions() {


        String[] PERMISSIONS = {
                Manifest.permission.CALL_PHONE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, 202);
        } else {
             enable_location();

            return true;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            enable_location();
        }

        return false;
    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;

    }

    public void showOnlineStatus() {

        String online_status = Variables.userDetails_pref.getString(Variables.RIDER_ONLINE_STATUS, "0");
        if (online_status.equals("1")) {
            tv_online_txt.setText(getResources().getString(R.string.online));
            tv_online_txt.setTextColor(getResources().getColor(R.color.green));
            image_login_status.setImageDrawable(getResources().getDrawable(R.drawable.circle_image));
        } else {
            tv_online_txt.setText(getResources().getString(R.string.offline_txt));
            tv_online_txt.setTextColor(getResources().getColor(R.color.text_color_gray));
            image_login_status.setImageDrawable(getResources().getDrawable(R.drawable.circle_image_offline));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        enable_location();

    }


    // first step helper function
    private void showPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        call_api_for_logout();
                    }
                }).setNegativeButton(R.string.cancel, null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    private void call_api_for_logout() {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", Variables.userDetails_pref.getString(Variables.uid, ""));
            sendobj.put("token", Variables.userDetails_pref.getString(Variables.login_token, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(MainActivity.this, Api_urls.LOGOUT, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Roam.stopTracking();
                Roam.logout(new RoamLogoutCallback() {
                    @Override
                    public void onSuccess(String message) {

                    }

                    @Override
                    public void onFailure(RoamError roamError) {
                        // Access Error code and message here
                        // roamError.code;
                        // roamError.message;
                    }
                });
                stopService(new Intent(MainActivity.this, ForegroundService.class));
               // stopService(new Intent(MainActivity.this, GetLocation_Service.class));
                Variables.userDetails_pref.edit().putBoolean(Variables.is_login, false).apply();
                SharedPreferences.Editor editor1 = Variables.userDetails_pref.edit();
                editor1.clear().commit();
                editor1.apply();
                startActivity(new Intent(MainActivity.this, GetStarted_A.class));
                finish();
            }
        });


    }

    private void remove_old_fragments() {

        int count = this.getSupportFragmentManager().getBackStackEntryCount();
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 1; i <= count; i++) {
            fm.popBackStack();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);

        }
    }

    private void update_profile_image(String image_url) {
        if (image_url != null && !image_url.equals(""))
            Picasso.get().load(Api_urls.IMAGE_BASE_URL + image_url)
                    .placeholder(R.drawable.ic_user_icon)
                    .into(iv_profile_drawer);
    }

    // if user does not permitt the app to get the location then we will go to the enable location screen to enable the location permission
    private void enable_location() {
        if (!Roam.checkLocationServices()) {
            startActivity(new Intent(MainActivity.this, Enable_location_A.class));
            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
        } else if (!Roam.checkLocationPermission()) {
            startActivity(new Intent(MainActivity.this, Enable_location_A.class));
            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Roam.checkBackgroundLocationPermission()) {
            startActivity(new Intent(MainActivity.this, Enable_location_A.class));
            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
        }else{
            // active tracking

            Roam.disableBatteryOptimization();

          // Roam.allowMockLocation(true);
            RoamTrackingMode roamTrackingMode = new RoamTrackingMode.Builder(20, 60)
                    .setDesiredAccuracy(RoamTrackingMode.DesiredAccuracy.HIGH)
                    .build();
            Roam.startTracking(roamTrackingMode);

            Roam.setForegroundNotification(true
                    , "Celex Rider"
                    , "Celex Rider is running in background"
                    , R.drawable.ic_location
                    , "com.celex.rider.AllActivitys.MainActivity");

            RoamPublish RoamPublish = new RoamPublish.Builder()
                    .build();
            Roam.publishAndSave(RoamPublish);
            Roam.enableAccuracyEngine(35);
            trackingStatus();
           // Roam.getCurrentLocation(120);

            Roam.getCurrentLocation(RoamTrackingMode.DesiredAccuracy.HIGH , 20, new RoamLocationCallback(){
                @Override
                public void location(Location location) {
                    // Access location data here
                    Functions.Add_Device_Data(location.getLatitude(),location.getLongitude(),getApplicationContext());
                }
                @Override
                public void onFailure(RoamError roamError) {
                    // Access Error code and message here
                    // roamError.getCode();
                    // roamError.getMessage();
                }
            });

            checkActiveTrip();
        }


    }

    private void trackingStatus() {
        if (Roam.isLocationTracking()) {
            Log.e("roam", "subc userid " +Variables.userDetails_pref.getString(Variables.geospark_user, ""));

            Roam.subscribe(Roam.Subscribe.LOCATION, Variables.userDetails_pref.getString(Variables.geospark_user, ""));
            Roam.updateLocationWhenStationary(2000);
            startService(new Intent(this, ForegroundService.class));
            Log.d("Geo", "Start stoped");
        } else {
            stopService(new Intent(this, ForegroundService.class));
            Log.d("Geo", "service stoped");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Functions.unRegisterConnectivity(MainActivity.this);
    }







}