package com.celex.rider.CodeClasses;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.celex.rider.DataModels.My_Orders_Model;
import com.celex.rider.R;
import com.celex.rider.interfaces.API_CallBack;
import com.celex.rider.interfaces.CallBack_internet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.DOWNLOAD_SERVICE;

public class Functions {


    public static void Order_Parse_Data(JSONArray jsonArray, String senerio, API_CallBack api_callBack) {


        ArrayList<My_Orders_Model> temp_list = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject order_object = jsonArray.getJSONObject(i);


              //  JSONObject Order = order_object.getJSONObject("orders");
                JSONObject OrderDetails = order_object.getJSONObject("order_details");
            //    JSONObject drop = OrderDetails.optJSONObject("drop");
             //   JSONObject pick_up = OrderDetails.optJSONObject("pick_up");

              My_Orders_Model my_orders_model_ = new My_Orders_Model();

                my_orders_model_.id = order_object.getString("id");
                my_orders_model_.consignment_id = order_object.getString("consignment_no");
                my_orders_model_.order_create_date = order_object.getString("created");

                my_orders_model_.delivery_datetime = order_object.getString("delivery_datetime");

             //   JSONObject store_obj = Order.getJSONObject("Store").getJSONObject("StoreLocation");
              //  JSONObject user_obj = Order.getJSONObject("User");

                my_orders_model_.order_person_name = order_object.getString("drop_company_name");
                my_orders_model_.qty = OrderDetails.getString("qty");
                my_orders_model_.weight = OrderDetails.getString("weight");

              //  String store_city = pick_up.getString("address1");
        /*        String store_street = pick_up.getString("address1")
                        +pick_up.getString("address2")
                        +pick_up.getString("address3");*/
             //   my_orders_model_.sender_name = drop.getString("name");



               // JSONObject DeliveryAddress_obj = Order.getJSONObject("DeliveryAddress");
                String delivery_address = order_object.getString("drop_address");

                my_orders_model_.delievery_address = delivery_address;

               // my_orders_model_.on_the_way_to_pickup = order_object.getString("on_the_way_to_pickup");
                my_orders_model_.pickup_datetime = order_object.getString("pickup_datetime");
                my_orders_model_.on_the_way_to_dropoff = order_object.getString("on_the_way_to_dropoff");
                my_orders_model_.delivered = order_object.getString("delivery_datetime");
                my_orders_model_.undelivery_datetime = order_object.getString("un_delivery_datetime");
                my_orders_model_.rider_reponse = order_object.getString("rider_response");
                my_orders_model_.is_transferred = order_object.optString("is_transferred","0");
                my_orders_model_.transfer_reason = order_object.optString("transfer_reason","");

                my_orders_model_.current_view = senerio;

          //      my_orders_model_.store_street = store_street;
             //   my_orders_model_.store_city = pick_up.optString("address2", "");
               // my_orders_model_.store_state = pick_up.optString("address3", "");

                temp_list.add(my_orders_model_);

            } catch (Exception e) {
                e.printStackTrace();

                //api_callBack.ArrayData(temp_list);
            }


        }

        api_callBack.ArrayData(temp_list);

    }

    //This method will show loader when getting data from server
    private static Dialog dialog;

    public static void show_loader(Context context, boolean outside_touch, boolean cancleable) {

        if (dialog != null) {
            dialog.dismiss();
        }

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_loader_dialog);
        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.d_white_round_bkg));

        CamomileSpinner loader = dialog.findViewById(R.id.loader);
        loader.start();

        if (!outside_touch)
            dialog.setCanceledOnTouchOutside(false);

        if (!cancleable)
            dialog.setCancelable(false);

        dialog.show();

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    //This method will cancel the running loader
    public static void cancel_loader() {
        if (dialog != null) {
            dialog.cancel();
            dialog.dismiss();
        }
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static String Check_Image_Status(Context context, long Image_DownloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Query ImageDownloadQuery = new DownloadManager.Query();

        //set the query filter to our previously Enqueued download
        ImageDownloadQuery.setFilterById(Image_DownloadId);

        //Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(ImageDownloadQuery);
        if (cursor.moveToFirst()) {
            return DownloadStatus(cursor);
        }
        return "";
    }


    private static String DownloadStatus(Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);

        switch (status) {
            case DownloadManager.STATUS_FAILED:
                return "STATUS_FAILED";

            case DownloadManager.STATUS_PAUSED:
                return "";

            case DownloadManager.STATUS_PENDING:
                return "";

            case DownloadManager.STATUS_RUNNING:
                return "";

            case DownloadManager.STATUS_SUCCESSFUL:
                return "STATUS_SUCCESSFUL";
            default:
                return "none";
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void wrtieFileOnInternalStorage(Context mcoContext, String sFileName, String sBody, String time, String apllication_name) throws IOException {


        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "ParcelRider");

        if (!mediaStorageDir.exists()) {
            if (mediaStorageDir.mkdirs()) {
                create_file(mediaStorageDir, mcoContext, sFileName, sBody, time, apllication_name);
            }

        } else {
            create_file(mediaStorageDir, mcoContext, sFileName, sBody, time, apllication_name);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void create_file(File path, Context mcoContext, String sFileName, String sBody, String time, String apllication_name) throws IOException {


        File file = new File(path, sFileName + ".txt");

        if (file.exists()) {
            String data = getContentFile("" + file, sBody, time, apllication_name);

        } else {

            FileOutputStream stream = new FileOutputStream(file);

            try {
                stream.write((sBody + "   " + time + "   " + apllication_name).getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                stream.close();
            }
        }
    }

    static FileOutputStream stream = null;
    static BufferedReader br = null;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getContentFile(String path, String sbody, String time, String apllication_name) throws IOException {
        try {
            br = new BufferedReader(new FileReader(path));


            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

            String everything = sb.toString();
            br.close();

            stream = new FileOutputStream(path);
            stream.write((sbody + "    " + time + "    " + apllication_name).getBytes());
            return everything;

        } catch (Exception e) {
            return "error";
        } finally {
            br.close();
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        return Bitmap.createScaledBitmap(realImage, width,
                height,true);
    }


    public static String Convert_Bitmap_to_base64(Bitmap bitmap) {


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        bitmap.recycle();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encoded;
    }


    public static Double Roundoff_decimal(Double value) {
        return Double.valueOf(new DecimalFormat("##.##").format(value));
    }


    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(String mysql_date_time) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:m:s");
        Date date = null;
        try {
            date = formatter.parse(mysql_date_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        long time = date.getTime();

        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now =System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String convert_datetime(String date, String isfrom) {
        DateFormat df = new SimpleDateFormat(Variables.df1_pattern);

        java.util.Date d = null;
        try {
            d = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (isfrom.equals("convert_dateonly")) {
            df = new SimpleDateFormat("dd-MM-yyyy");
        } else if (isfrom.equals("history")) {
            df = new SimpleDateFormat("dd MMM hh:mm a");
        } else if (isfrom.equals("datetime")) {
            df = new SimpleDateFormat("dd MMM hh a");
        } else if (isfrom.equals("member_since")) {
            df = new SimpleDateFormat("MMM yyyy");
        } else {
            df = new SimpleDateFormat("MMM-dd hh:mm a");
        }
        return df.format(d);
    }


    static Integer today_day = 0;

    // change the date into (today ,yesterday and date)
    public static String ChangeDate(String date) {

        Calendar cal = Calendar.getInstance();
        today_day = cal.get(Calendar.DAY_OF_MONTH);
        Log.d("Rider_log", "today date : " + today_day);
        //current date in millisecond
        long currenttime = System.currentTimeMillis();

        //database date in millisecond
        long databasedate = 0;
        Date d = null;
        try {
            DateFormat df = new SimpleDateFormat(Variables.df1_pattern);
            d = df.parse(date);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);
            date = simpleDateFormat.format(d);
            databasedate = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = currenttime - databasedate;
        if (difference < 86400000) {
            int chatday = Integer.parseInt(date.substring(0, 2));
            Log.d("Rider_log", "chatday date : " + chatday);
            if (today_day == chatday)
                return "Today";
            else if ((today_day - chatday) == 1)
                return "Yesterday";
        } else if (difference < 172800000) {
            int chatday = Integer.parseInt(date.substring(0, 2));
            if ((today_day - chatday) == 1)
                return "Yesterday";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd");

        if (d != null)
            return sdf.format(d);
        else
            return "";
    }

    public static String Show_Message_Time(String date) {
        Date d = null;
        try {
            DateFormat df = new SimpleDateFormat(Variables.df1_pattern);
            d = df.parse(date);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            date = sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            return date;
        } else {
            return "null";
        }
    }


    public static Matrix Get_orentation(String path) {
        Matrix matrix = new Matrix();
        ExifInterface exif = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            try {
                exif = new ExifInterface(path);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;

                    default:
                        return matrix;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return matrix;

    }

    public static void dialouge(Context context, String title, String message) {

        if (dialog != null) {
            dialog.dismiss();
        }

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_alert_dialouge);
        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.d_round_corner_white_bkg));

        TextView header_txt = dialog.findViewById(R.id.header_txt);
        TextView message_txt = dialog.findViewById(R.id.alert_msg_txt);
        Button ok_btn = dialog.findViewById(R.id.ok_btn);
        header_txt.setText(title);
        message_txt.setText(message);

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        dialog.show();

    }

    //Converts device pixels to regular pixels to draw
    public static int convertDpToPx(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }


    static BroadcastReceiver broadcastReceiver;
    static IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    static IntentFilter intentFilter1 = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);

    public static void unRegisterConnectivity(Context mContext) {
        try {
            if (broadcastReceiver != null)
                mContext.unregisterReceiver(broadcastReceiver);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    public static void RegisterConnectivity(Context context, final CallBack_internet callback) {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isConnectedToInternet(context)) {
                    callback.Get_Response("alert", "connected");
                } else {
                    callback.Get_Response("alert", "disconnected");
                }
            }
        };

        context.registerReceiver(broadcastReceiver, intentFilter);
    }


    public static Boolean isConnectedToInternet(Context context) {
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e("NetworkChange", e.getMessage());
            return false;
        }
    }

    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        if(arr!=null) {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] != null && arr[i].length() > 0) {
                        sb.append(Character.toUpperCase(arr[i].charAt(0)))
                                .append(arr[i].substring(1)).append(" ");
                }
            }
        }
        return sb.toString().trim();
    }


}
