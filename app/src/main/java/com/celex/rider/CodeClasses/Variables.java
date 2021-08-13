package com.celex.rider.CodeClasses;

import android.content.SharedPreferences;
import android.os.Environment;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Variables  {

    public static SharedPreferences userDetails_pref;
    public static String userDetails_pref_name = "UserDetails_Pref_Name";
    public static String RIDER_ONLINE_STATUS ="rOnlineStatus";


    public static String current_order_id= "current_order_id";
    public static String current_order_status= "current_order_status";
    public static String current_order_update_at= "current_order_update_at";

    public static String symbol = "symbol";
    public static String currency_code = "code";
    public static String currency_code_usd = "code";

    public static String my_current_lat = "my_current_lat_freshly";
    public static String my_current_lng = "my_current_lng_freshly";
    public static String id = "Id";
    public static String name = "First_name__freshly";
    public static String geospark_user = "geo_spark_user";

    public static String uid = "user_id";
    public static String email = "email";
    public static String image = "Image_freshly";
    public static String token = "Token_freshly";
    public static String login_token = "Token_Login";
    public static String phone = "phone_freshly";
    public static String city = "city_freshly";
    public static String country_code = "country_code";
    public static String memeber_date = "memeber_date";
    public static String chat_reciever_id = "chat_reciever_id";
    public static String is_login = "Is_Login_freshly";
    public static String folder_freshly_rider = Environment.getExternalStorageDirectory()+"/freshly_Rider/";

    public static final String PRIVACY_POLICY ="https://www.privacypolicygenerator.info/live.php?token=RCOyD366gNjCKnLJJJIQhYOoD7lG4zDB";
    public static final String TERMS_AND_CONDITION ="https://www.privacypolicygenerator.info/live.php?token=RCOyD366gNjCKnLJJJIQhYOoD7lG4zDB";

    public static final String CONTACT_US_EMAIL ="support@gmail.com";

    public static String setlocale = "Setlocale";
    public static String user_pic;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);
     public static final SimpleDateFormat DATE_FORMAT1 = new SimpleDateFormat("dd-MM-yyyy HH:mmZZ", Locale.ENGLISH);

    public  static String df1_pattern="yyyy-MM-dd HH:mm:ss";

    public static final String TAG = "Rider_log";
    public static String Opened_Chat_id="null";
    public static SharedPreferences download_sharedPreferences;
    public static String download_pref = "download_pref";
    public static final int PERMISSION_CAMERA_CODE =786;
    public static final int PERMISSION_WRITE_DATA =788;
    public static final int PERMISSION_READ_DATA =789;
    public static final int PERMISSION_RECORDING_AUDIO =790;


}
