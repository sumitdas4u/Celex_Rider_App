package com.celex.rider.AllFragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.celex.rider.Adapters.OrderDetail_Adapter;
import com.celex.rider.Adapters.TransferImageAdapter;
import com.celex.rider.AllActivitys.Barcode_scaner_A;
import com.celex.rider.AllActivitys.GetCancelReason_A;
import com.celex.rider.AllActivitys.GetSignature_A;
import com.celex.rider.AllActivitys.MainActivity;
import com.celex.rider.AllActivitys.Verify_Otp_A;
import com.celex.rider.AllFragments.Document.Upload_Photos_F;
import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.CodeClasses.RootFragment;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.DataModels.My_Orders_Model;
import com.celex.rider.DataModels.OrderModel;

import com.celex.rider.R;
import com.celex.rider.Users_Chat.Chat_Activity;
import com.celex.rider.Users_Chat.Preview_F;
import com.celex.rider.interfaces.Callback;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ServerValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.celex.rider.CodeClasses.Functions.roam_update_meta_data_with_location;
import static com.celex.rider.CodeClasses.Variables.name;


public class OrderDetail_F extends RootFragment implements View.OnClickListener {

    TextView tv_username;
    TextView tv_pickup_name;
    TextView tv_dropoff_info_name;
    TextView tv_dropoff_info_number;

    TextView tv_dropoff_info_email;
    TextView tv_dropoff_info_address;
    TextView tv_weight;
    TextView tv_qty;
    TextView tv_total;
    TextView order_id_txt, tv_pickup_number, tv_pickup_email;
    ReadMoreTextView tv_Item_transfer;
    String orderid, id, shipment_id, otp_verified, user_id, user_full_name, user_image, otp, otp_required, status, selected_pos;
    String delivery_address_lat, delivery_address_long, rider_id;
    Button btn_Rider_status, btn_Open_signature, btn_cancel, btn_hold, btn_return, btn_Open_otp,btn_Open_photo_upload;
    ImageView iv_profile;
    RelativeLayout iv_menu_open, report_btn;
    LinearLayout note_div, upper_div, tv_dropoff_info_mapit,transfer_div;
    RecyclerView rc_order_product;
    RecyclerView rc_transfer_image;
    LinearLayout phone_btn, chat_btn, phone_rlt;
    final String st_order_id = "order_id";
    String empty_time = "0000-00-00 00:00:00";
    String Order_Delivered_Successfully = "Order Delivered Successfully";
    String Order_return_to_station_Successfully = "Order Return Successfully";
    String Delivered = "Delivered";
    String On_The_Way_To_Dropoff = "On The Way To Dropoff";
    String Order_Picked = "Order Picked";
    String On_The_Way_To_Pickup = "On The Way To Pickup";
    String user_role = "user_role";
    String Order = "Order", symbol;
    private String reason = "";
    View view;
    Callback callback;
   // private DatabaseReference databaseReference;
    private PopupWindow mDropdown = null;
    LayoutInflater mInflater;

    private int FRAGMENT_CODE = 900;
   private My_Orders_Model my_orders_model_ = null;
    public OrderDetail_F(Callback callback) {
        this.callback = callback;
    }

    public OrderDetail_F() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_detailed, container, false);

        Bundle extras = getArguments();
        Object a;

       // databaseReference = FirebaseDatabase.getInstance().getReference();
        initViews();
        SetAllCLickListner();
        symbol = Variables.userDetails_pref.getString(Variables.symbol, "₹");
        if (extras != null) {
            a = extras.getSerializable("dataModel");
            selected_pos = extras.getString("selected_pos");
            if (selected_pos != null && selected_pos.equals("0")) {
                btn_Rider_status.setVisibility(View.GONE);



            }
            my_orders_model_ = (My_Orders_Model) a;
            id = my_orders_model_.id;
            shipment_id = my_orders_model_.consignment_id;

            CallApi_show_detail();
        }


        if (my_orders_model_ != null) {

            id = my_orders_model_.id;
            shipment_id = my_orders_model_.consignment_id;
            //] Toast.makeText(getActivity(), my_orders_model_.on_the_way_to_dropoff, Toast.LENGTH_SHORT).show();

            if (!my_orders_model_.rider_reponse.equals("2") && my_orders_model_.undelivery_datetime.equals(empty_time)) {
                if (!(my_orders_model_.delivered.equals(empty_time)) && !(my_orders_model_.is_delivered_doc.equals("1"))) {
                    //btn_Rider_status.setText(getString(R.string.order_delivered));
                    //btn_Rider_status.setVisibility(View.VISIBLE);
                    btn_Open_photo_upload.setVisibility(View.VISIBLE);
                } else if (!(my_orders_model_.on_the_way_to_dropoff.equals(empty_time)) && (my_orders_model_.delivered.equals(empty_time))) {
                    btn_Rider_status.setText(Delivered);
                    btn_cancel.setVisibility(View.VISIBLE);
                    btn_Rider_status.setVisibility(View.VISIBLE);
                } else if (!(my_orders_model_.pickup_datetime.equals(empty_time)) && (my_orders_model_.delivered.equals(empty_time))) {
                    btn_Rider_status.setVisibility(View.VISIBLE);
                    btn_Rider_status.setText(getString(R.string.on_the_way));
                }  else if (!(my_orders_model_.undelivery_datetime.equals(empty_time))) {
                    btn_Rider_status.setText(getString(R.string.order_return_to_station));
                }

                else {
                  btn_Rider_status.setText(getString(R.string.order_picked));
                    btn_Rider_status.setVisibility(View.GONE);
                }

            } else {
                btn_Rider_status.setVisibility(View.VISIBLE);
                btn_Rider_status.setText(getString(R.string.order_return_to_station));
            }


        } else {
            btn_Rider_status.setText("Sorry Data receving error");
        }

        return view;
    }


    // initalize the view that is used in this screen
    void initViews() {

        order_id_txt = view.findViewById(R.id.order_id_txt);

        phone_btn = view.findViewById(R.id.phone_btn);

        // tv_pickup_number = view.findViewById(R.id.tv_pickup_number);

        tv_username = view.findViewById(R.id.tv_username);

        // tv_pickup_email = view.findViewById(R.id.tv_pickup_email);

        //  tv_pickup_name = view.findViewById(R.id.tv_pickup_name);

        tv_weight = view.findViewById(R.id.tv_other_weight);
        tv_qty = view.findViewById(R.id.tv_other_qty);

        tv_dropoff_info_name = view.findViewById(R.id.tv_dropoff_info_name);

        tv_dropoff_info_number = view.findViewById(R.id.tv_dropoff_info_number);

        tv_dropoff_info_email = view.findViewById(R.id.tv_dropoff_info_email);

        tv_dropoff_info_mapit = view.findViewById(R.id.tv_dropoff_info_mapit);

        tv_dropoff_info_address = view.findViewById(R.id.tv_dropoff_info_address);

        //tv_pickup_mapit = view.findViewById(R.id.tv_pickup_mapit);

        tv_total = view.findViewById(R.id.tv_total);

        tv_Item_transfer = view.findViewById(R.id.tv_transfer);



        iv_profile = view.findViewById(R.id.iv_profile);

        note_div = view.findViewById(R.id.note_div);
        transfer_div = view.findViewById(R.id.transfer_div);

        rc_order_product = view.findViewById(R.id.recylerview);
        rc_transfer_image = view.findViewById(R.id.recylerview_transfer_images);

        upper_div = view.findViewById(R.id.upper_div);

        btn_Rider_status = view.findViewById(R.id.btn_Rider_status);

        btn_Open_signature = view.findViewById(R.id.btn_Open_signature);
        btn_Open_otp = view.findViewById(R.id.btn_Open_otp);
        btn_Open_photo_upload = view.findViewById(R.id.btn_Open_photo_upload);
        btn_cancel = view.findViewById(R.id.btn_undelivered);
        btn_hold = view.findViewById(R.id.btn_hold);
        btn_return = view.findViewById(R.id.btn_return);

      //  btn_Open_signature = view.findViewById(R.id.btn_Open_signature);
        report_btn = view.findViewById(R.id.iv_menu_open);

    }

    void SetAllCLickListner() {
        //view.findViewById(R.id.chat_btn).setOnClickListener(this);
        view.findViewById(R.id.iv_menu).setOnClickListener(this);
        btn_Rider_status.setOnClickListener(this);
        btn_Open_signature.setOnClickListener(this);
        btn_Open_otp.setOnClickListener(this);
        btn_Open_photo_upload.setOnClickListener(this);
        btn_hold.setOnClickListener(this);
        btn_return.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        //  tv_pickup_mapit.setOnClickListener(this);
        tv_dropoff_info_mapit.setOnClickListener(this);
        view.findViewById(R.id.phone_btn).setOnClickListener(this);
        view.findViewById(R.id.iv_menu_open).setOnClickListener(this);
    }


    //this method will fetch the detail of the order
    private void CallApi_show_detail() {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put(st_order_id, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.show_loader(getActivity(), false, false);
        ApiRequest.Call_Api(getActivity(), Api_urls.SHOW_RIDER_ORDER_DETAILS, sendobj, resp -> {

            Functions.cancel_loader();
            if (resp != null) {
                try {
                    JSONObject respobj = new JSONObject(resp);
                    if (respobj.getString("status").equals("1")) {
                        parse_data(respobj);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    //this method will parse the data coming from api
    private void parse_data(JSONObject respobj) {
        try {
            JSONArray OrderProduct = null;
            //User Object
            JSONObject data = respobj.getJSONObject("data");
            JSONObject order_details = respobj.getJSONObject("data").getJSONObject("order_details");

            rider_id = data.getJSONObject("rider_user").optString("id");

            String user_f_name = data.optString("drop_contact_person");
            String name = data.optString("drop_company_name");

            String user_email = data.optString("drop_email");
            String user_phone = data.optString("drop_mobile");
            String delivery_address_street = data.optString("drop_address");
            user_image = data.optString("image");
            user_id = data.optString("id");


            user_full_name = user_f_name;

            tv_username.setText(name);
            tv_dropoff_info_name.setText(user_full_name);
            tv_dropoff_info_address.setText(delivery_address_street);
            if (user_email != null && !user_email.equals("")) {
                tv_dropoff_info_email.setText(user_email);
            } else {
                tv_dropoff_info_email.setText(R.string.no_email);
            }

            if (user_phone != null && !user_phone.equals("")) {
                tv_dropoff_info_number.setText(user_phone);
            } else {
                tv_dropoff_info_number.setText(R.string.no_number);
                phone_btn.setVisibility(View.GONE);
            }

            orderid = data.optString("consignment_no");
            order_id_txt.setText(getResources().getString(R.string.order) + orderid);




            String qty = order_details.optString("qty");
            String weight = order_details.optString("weight");
            String transfer_reason = data.optString("transfer_reason","");
            String is_transferred = data.optString("is_transferred","0");
            otp_required = data.optString("otp_required","0");
            otp = data.optString("otp","0");
            otp_verified = data.optString("otp_verified","0");




            if(is_transferred.equals("1")){
                JSONArray transferred_images= data.getJSONArray("transfer_images");
                ArrayList<String> listdata = new ArrayList<String>();

                for (int i=0;i<transferred_images.length();i++){
                    listdata.add(transferred_images.getString(i));
                }
                writeTransferRecycler( listdata);
            }else{
                transfer_div.setVisibility(View.GONE);
            }





                tv_Item_transfer.setText(transfer_reason);




            tv_qty.setText(qty);
            tv_weight.setText(weight );

            ArrayList<OrderModel> orderList = new ArrayList<>();
            OrderProduct = order_details.getJSONArray("plates_details");


            for (int i = 0; i < OrderProduct.length(); i++) {
                OrderModel orderModel = new OrderModel();
                JSONObject dataobj = OrderProduct.getJSONObject(i);
                orderModel.setOrder_p_id(dataobj.optString("id"));
                orderModel.setReg_No(dataobj.optString("reg_no"));

                orderList.add(orderModel);
                writeRecycler(orderList);
            }

            delivery_address_lat = data.optString("drop_lat");
            delivery_address_long = data.optString("drop_long");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void writeRecycler(ArrayList<OrderModel> featureModelClassList) {
        rc_order_product.setLayoutManager(new LinearLayoutManager(getActivity()));
        rc_order_product.setHasFixedSize(true);
        OrderDetail_Adapter adapter = new OrderDetail_Adapter(getActivity(), featureModelClassList);
        rc_order_product.setAdapter(adapter);
        rc_order_product.setNestedScrollingEnabled(false);
    }
    private void writeTransferRecycler(ArrayList<String> featureModelClassList) {
        rc_transfer_image.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));

        rc_transfer_image.setHasFixedSize(true);



        TransferImageAdapter adapter1 = new TransferImageAdapter(getActivity(), featureModelClassList, (postion, Model, view) -> {
            switch (view.getId()) {
                case R.id.image:

                        //Toast.makeText(getActivity(), featureModelClassList.get(postion), Toast.LENGTH_SHORT).show();
                    Preview_F see_image_f = new Preview_F();

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Bundle args = new Bundle();
                    args.putSerializable("image_url", featureModelClassList.get(postion));
                    args.putSerializable("isfromchat", false);
                    see_image_f.setArguments(args);

                    transaction.addToBackStack(null);
                    transaction.replace(R.id.li_acticvityDetail, see_image_f).commit();

                    break;
                default:
                    return;
            }


        });
        rc_transfer_image.setAdapter(adapter1);
        rc_transfer_image.setNestedScrollingEnabled(false);
    }

    private void CallApi_returnOrderStatus() {
        JSONObject sendobj = new JSONObject();

        try {

/*            String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());

            String newString = time.replaceAll("\\/", "-");*/

            sendobj.put(st_order_id, id);

            sendobj.put("reason", "");
            sendobj.put("status", status);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        roam_update_meta_data_with_location(sendobj);




        Functions.show_loader(getActivity(), false, false);
        ApiRequest.Call_Api(getActivity(), Api_urls.URL_RETURN_TO_STATION_ORDER, sendobj, resp -> {

            Functions.cancel_loader();

            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);

                    if (respobj.getString("status").equals("1")) {
                        btn_Rider_status.setText(Order_return_to_station_Successfully);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(Variables.TAG, e.toString());
                }
            }

        });
    }

    SharedPreferences sharedPreferences;
    //call api for update order status
    private void CallApi_updateRiderOrderStatus(String status) {

        //Objects.requireNonNull(getActivity()).startService(new Intent(getActivity(), GetLocation_Service.class));
        JSONObject sendobj = new JSONObject();

        try {

/*            String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());

            String newString = time.replaceAll("\\/", "-");*/

            sendobj.put(st_order_id, id);
            sendobj.put("reason", reason);
            sendobj.put("status", status);

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Variables.current_order_id, "" + shipment_id);
            editor.putString(Variables.current_order_status, "" +status);
      //      editor.putString(Variables.current_order_update_at, "" + ServerValue.TIMESTAMP);
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }




        Functions.show_loader(getActivity(), false, false);
        ApiRequest.Call_Api(getActivity(), Api_urls.URL_UPDATE_RIDER_ORDER_STATUS, sendobj, resp -> {
            Log.d(Variables.TAG, "Bearer " +Variables.userDetails_pref.getString(Variables.login_token,""));
            Log.d(Variables.TAG, "json " +sendobj.toString());
            Functions.cancel_loader();

            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);

                    if (respobj.getString("status").equals("1")) {

                        // JSONObject user = respobj.getJSONObject("msg");

                        JSONObject RiderOrder = respobj.getJSONObject("data");





                        My_Orders_Model my_orders_model_ = new My_Orders_Model();

               //         my_orders_model_.on_the_way_to_pickup = RiderOrder.getString("on_the_way_to_pickup");
                        my_orders_model_.pickup_datetime = RiderOrder.getString("pickup_datetime");
                        my_orders_model_.on_the_way_to_dropoff = RiderOrder.getString("on_the_way_to_dropoff");
                        my_orders_model_.delivered = RiderOrder.getString("delivery_datetime");
                        my_orders_model_.rider_reponse = RiderOrder.getString("rider_response");
                        my_orders_model_.undelivery_datetime = RiderOrder.getString("un_delivery_datetime");

                        my_orders_model_.is_delivered_doc = RiderOrder.optString("is_delivered_doc","0");
                        //   Toast.makeText(getActivity(), my_orders_model_.user_reponse, Toast.LENGTH_SHORT).show();
                        if (!my_orders_model_.rider_reponse.equals("2") && my_orders_model_.undelivery_datetime.equals(empty_time)) {

                            if (!(my_orders_model_.delivered.equals(empty_time))) {
                                btn_Rider_status.setText(getString(R.string.order_delivered));
                                btn_Rider_status.setVisibility(View.VISIBLE);
                                btn_Open_signature.setVisibility(View.GONE);

                            } else if (!(my_orders_model_.on_the_way_to_dropoff.equals(empty_time)) && (my_orders_model_.delivered.equals(empty_time))) {
                                btn_Rider_status.setText(Delivered);
                                btn_cancel.setVisibility(View.VISIBLE);

                            } else if (!(my_orders_model_.pickup_datetime.equals(empty_time))) {
                                if (isAdded())// This {@link androidx.fragment.app.Fragment} class method is responsible to check if the your view is attached to the Activity or not
                                {
                                    btn_Rider_status.setText(getResources().getString(R.string.order_return_to_station));
                                }


                            }   else {
                                btn_Rider_status.setText(getString(R.string.order_picked));
                            }
                        } else {
                            btn_Rider_status.setVisibility(View.VISIBLE);
                            if (isAdded())// This {@link androidx.fragment.app.Fragment} class method is responsible to check if the your view is attached to the Activity or not
                            {
                                btn_Rider_status.setText(getResources().getString(R.string.order_return_to_station));
                            }


                        }

                        JSONObject roamSendobj = new JSONObject();
                        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                        String format = s.format(new Date());
                        try {

/*            String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());

            String newString = time.replaceAll("\\/", "-");*/

                            roamSendobj.put("shipment id", shipment_id);
                            roamSendobj.put("reason", reason);
                            roamSendobj.put("status", status);
                            roamSendobj.put("time", format);
                            roamSendobj.put("address", my_orders_model_.delievery_address);
                            roamSendobj.put("receiver name", my_orders_model_.receiver_name);


                            //     roamSendobj.put("response", RiderOrder.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        roam_update_meta_data_with_location(roamSendobj);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(Variables.TAG, e.toString());
                }
            }

        });
    }
    ActivityResultLauncher<Intent> launchCodeForOTPActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        btn_Open_otp.setVisibility(View.GONE);
                        btn_Open_photo_upload.setVisibility(View.VISIBLE);
                    }
                }
            });

    ActivityResultLauncher<Intent> launchDeliveredBarCodeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        btn_cancel.setVisibility(View.GONE);
                        //btn_hold.setVisibility(View.GONE);
                        btn_Rider_status.setVisibility(View.GONE);

                        if(otp_required.equals("1") && !otp_verified.equals("1")){
                            btn_Open_otp.setVisibility(View.VISIBLE);
                        }else{
                            btn_Open_photo_upload.setVisibility(View.VISIBLE);
                        }

                    }
                }
            });

    ActivityResultLauncher<Intent> launchReturnToStationBarCodeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        status = "return_to_station";
                        CallApi_returnOrderStatus();
                    }
                }
            });

    ActivityResultLauncher<Intent> launchOpenSignatureDeliveryActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String user_Id = data.getStringExtra("result");
                        if (user_Id.equals("ok")) {
                            // btn_Open_signature.setVisibility(View.GONE);

                            status = "delivered";
                            CallApi_updateRiderOrderStatus(status);

                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> launchHoldActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        reason = data.getStringExtra("reason");
                        assert reason != null;
                        if (!reason.equals("")) {
                            status = "hold";
                            CallApi_updateRiderOrderStatus(status);
                            assert getFragmentManager() != null;
                            getFragmentManager().popBackStack();


                        }
                    }
                }
            });
    ActivityResultLauncher<Intent> launchReturnActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        reason = data.getStringExtra("reason");
                        assert reason != null;
                        if (!reason.equals("")) {
                            status = "return";
                            CallApi_updateRiderOrderStatus(status);
                            assert getFragmentManager() != null;
                            getFragmentManager().popBackStack();
                            Toast.makeText(getActivity(), Order_return_to_station_Successfully, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

           /* case R.id.chat_btn:

                chatFragment(Variables.userDetails_pref.getString(Variables.id, ""), user_full_name, user_image, orderid, "null");

                break;*/

            case R.id.iv_menu:
          /*      Upload_Photos_F f = new Upload_Photos_F(id,"delivery", resp -> {
                });
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                f.setTargetFragment(this, FRAGMENT_CODE);


                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.li_acticvityDetail, f).addToBackStack(null).commit();*/


                Intent intent41 = new Intent(getActivity(), GetSignature_A.class);
                Bundle dataBundle41 = new Bundle();
                dataBundle41.putString(st_order_id, id);
                intent41.putExtras(dataBundle41);

                launchOpenSignatureDeliveryActivity.launch(intent41);

                MainActivity.Open_drawer();
                break;

            case R.id.btn_Open_otp:
                Intent intent_otp = new Intent(getActivity(), Verify_Otp_A.class);
                Bundle dataBundle_otp = new Bundle();
                dataBundle_otp.putString(st_order_id, id);
                dataBundle_otp.putString("otp", otp);
                intent_otp.putExtras(dataBundle_otp);

                launchCodeForOTPActivity.launch(intent_otp);
                break;

            case R.id.btn_Open_photo_upload:

                Upload_Photos_F f1 = new Upload_Photos_F(id,"delivery", resp -> {
                });
                FragmentTransaction ft1 = getFragmentManager().beginTransaction();
                f1.setTargetFragment(this, FRAGMENT_CODE);


                ft1.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft1.replace(R.id.li_acticvityDetail, f1).addToBackStack(null).commit();
                break;


            case R.id.btn_Rider_status:
                if( Variables.userDetails_pref.getString(Variables.trip_roam_current_id, null)==null){
                    Toast.makeText(getContext(), "Please start the trip. before any activity", Toast.LENGTH_SHORT).show();
                    break;
                }

                String btn_status = btn_Rider_status.getText().toString();
                if (btn_Rider_status.getText().equals(Order_return_to_station_Successfully)) {
                    Toast.makeText(getActivity(), Order_return_to_station_Successfully, Toast.LENGTH_SHORT).show();
                    assert getFragmentManager() != null;
                    getFragmentManager().popBackStack();
                }
                if (!(btn_Rider_status.getText().toString()).equals(Order_Delivered_Successfully)) {

                    if (btn_status.equals(On_The_Way_To_Pickup)) {
                        status = "on_the_way_to_pickup";
                        CallApi_updateRiderOrderStatus(status);
                    } else if (btn_status.equals(Order_Picked)) {
                        status = "pickup_datetime";
                        CallApi_updateRiderOrderStatus(status);
                    } else if (btn_status.equals(On_The_Way_To_Dropoff)) {
                        status = "on_the_way_to_dropoff";
                        CallApi_updateRiderOrderStatus(status);

                    } else if (btn_status.equals(Delivered)) {
                        Intent intent4 = new Intent(getActivity(), Barcode_scaner_A.class);
                        Bundle dataBundle4 = new Bundle();
                        dataBundle4.putString("shipment_id", shipment_id);
                        dataBundle4.putString("id", id);
                        intent4.putExtras(dataBundle4);

                        launchDeliveredBarCodeActivity.launch(intent4);
                        status = "delivered";


                    } else if (btn_status.equals(getString(R.string.order_return_to_station))) {
                        Intent intent4 = new Intent(getActivity(), Barcode_scaner_A.class);
                        Bundle dataBundle4 = new Bundle();
                        dataBundle4.putString("shipment_id", shipment_id);
                        dataBundle4.putString("id", id);
                        intent4.putExtras(dataBundle4);
                        launchReturnToStationBarCodeActivity.launch(intent4);

                        status = "return_to_station";

                    }

                } else {
                    Toast.makeText(getActivity(), Order_Delivered_Successfully, Toast.LENGTH_SHORT).show();
                    assert getFragmentManager() != null;
                    getFragmentManager().popBackStack();
                }


                break;

            case R.id.btn_Open_signature:

                Intent intent4 = new Intent(getActivity(), GetSignature_A.class);
                Bundle dataBundle4 = new Bundle();
                dataBundle4.putString(st_order_id, id);
                intent4.putExtras(dataBundle4);

                launchOpenSignatureDeliveryActivity.launch(intent4);

                break;
            case R.id.btn_hold:
                status = "hold";
                Intent intent = new Intent(getActivity(), GetCancelReason_A.class);
                Bundle dataBundle2 = new Bundle();
                dataBundle2.putString(st_order_id, id);
                intent.putExtras(dataBundle2);

                launchHoldActivity.launch(intent);

                break;
            case R.id.btn_undelivered:
                btn_hold.setVisibility(View.VISIBLE);
                btn_return.setVisibility(View.VISIBLE);
                btn_Rider_status.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.GONE);

                /*status = "return";
                Toast.makeText(getActivity(), status, Toast.LENGTH_SHORT).show();
                CallApi_updateRiderOrderStatus(status);
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
*/
                break;
            case R.id.btn_return:

                Intent intent2 = new Intent(getActivity(), GetCancelReason_A.class);
                Bundle dataBundle3 = new Bundle();
                dataBundle3.putString(st_order_id, id);
                intent2.putExtras(dataBundle3);
                launchReturnActivity.launch(intent2);

                break;
            /*case R.id.tv_pickup_mapit:

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("www.google.com")
                        .appendPath("maps")
                        .appendPath("dir")
                        .appendPath("")
                        .appendQueryParameter("api", "1")
                        .appendQueryParameter("destination", store_lat + "," + store_long);
                String url = builder.build().toString();
                Log.d("Directions", url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;*/

            case R.id.tv_dropoff_info_mapit:
/*
                Uri.Builder builder1 = new Uri.Builder();
                builder1.scheme("https")
                        .authority("www.google.com")
                        .appendPath("maps")
                        .appendPath("dir")
                        .appendPath("")
                        .appendQueryParameter("api", "1")
                        .appendQueryParameter("destination", delivery_address_lat + "," + delivery_address_long);
                String url1 = builder1.build().toString();
                Log.d("Directions", url1);
                Intent i1 = new Intent(Intent.ACTION_VIEW);
                i1.setData(Uri.parse(url1));
                startActivity(i1);
                */

                if (!delivery_address_lat.equals("") && !delivery_address_long.equals("")) {

                    // Toast.makeText(getActivity(), "lat", Toast.LENGTH_SHORT).show();
                    double latitude = Double.parseDouble(delivery_address_lat);
                    double longitude = Double.parseDouble(delivery_address_long);
                    String label = user_full_name;
                    String uriBegin = "geo:" + latitude + "," + longitude;
                    String query = latitude + "," + longitude + "(" + label + ")";
                    String encodedQuery = Uri.encode(query);
                    String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                    Uri uri = Uri.parse(uriString);
                    Intent intent_map = new Intent(android.content.Intent.ACTION_VIEW, uri);
                    startActivity(intent_map);

                } else {
                    // Toast.makeText(getActivity(), "address", Toast.LENGTH_SHORT).show();
                    String geoUri = "http://maps.google.com/maps?q=loc:"
                            + tv_username.getText().toString()
                            + " "
                            + tv_dropoff_info_address.getText().toString();
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    startActivity(intent1);
                    break;
                }


            case R.id.phone_btn:

                Long number = Long.parseLong(tv_dropoff_info_number.getText().toString());

                String country_code = Variables.userDetails_pref.getString(Variables.country_code, "");
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + country_code + number));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }

                startActivity(callIntent);
                break;

            case R.id.iv_menu_open:
                initiatePopupWindow(report_btn);
                break;

            default:
                return;

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==FRAGMENT_CODE){
            int isupload = data.getIntExtra("upload", 0);
            if (isupload==1 && (my_orders_model_.delivered.equals(empty_time))) {
                btn_Open_photo_upload.setVisibility(View.GONE);
                btn_Open_signature.setVisibility(View.VISIBLE);
            }
        }




    }

    //open the chat fragment and on item click and pass your id and the other person id in which
    //you want to chat with them and this parameter is that is we move from match list or inbox list
    public void chatFragment(String receiverid, String name, String picture, String order_id, String store_id) {
        Chat_Activity chat_activity = new Chat_Activity();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);

        Bundle args = new Bundle();
        args.putString("store_id", user_id);
        args.putString("st_store_user_id", user_id);
        args.putString("store_name", name);
        args.putString("store_img", picture);
        args.putString("fragment", "rider_user_chat");
        args.putString("order_id", order_id);
        chat_activity.setArguments(args);

        transaction.addToBackStack(null);
        transaction.replace(R.id.li_acticvityDetail, chat_activity).commit();
    }


    private PopupWindow initiatePopupWindow(View anchor_view) {

        try {

            mInflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = mInflater.inflate(R.layout.popup_layout, null);

            //If you want to add any listeners to your textviews, these are two //textviews.
            final TextView tv_current_order = (TextView) layout.findViewById(R.id.tv_current_order);

            tv_current_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chatFragment(Variables.userDetails_pref.getString(Variables.id, ""), "Admin", "user_image", "", "0");
                    mDropdown.dismiss();
                }
            });


            layout.measure(View.MeasureSpec.UNSPECIFIED,
                    View.MeasureSpec.UNSPECIFIED);
            mDropdown = new PopupWindow(layout, FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, true);
            //mDropdown.showAsDropDown(anchor_view, 4, 4);
            String lang = Variables.userDetails_pref.getString(Variables.setlocale, "");
            if (lang.equals("en")) {
                mDropdown.showAtLocation(anchor_view, Gravity.TOP | Gravity.END, -80, 170);
            } else {
                mDropdown.showAtLocation(anchor_view, Gravity.TOP | Gravity.START, 40, 170);
            }
            mDropdown.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDropdown.setElevation(5);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDropdown;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (callback != null) {
            callback.Responce(status);
            status = null;
        }
    }

}
