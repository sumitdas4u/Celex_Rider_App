package com.celex.rider.AllFragments.HomeFragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.celex.rider.AllActivitys.Barcode_scaner_A;
import com.celex.rider.AllActivitys.GetSignature_A;
import com.celex.rider.AllFragments.OrderDetail_F;
import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.AllActivitys.MainActivity;
import com.celex.rider.Adapters.Orders_Adapter;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.DataModels.My_Orders_Model;
import com.celex.rider.R;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.interfaces.API_CallBack;
import com.celex.rider.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static com.celex.rider.CodeClasses.Functions.roam_update_meta_data_with_location;

public class Home_F extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    View view;
    ArrayList<My_Orders_Model> data_list;
    RecyclerView recyclerView;
    Orders_Adapter adapter;
    My_Orders_Model my_orders_model_1;
    public int Selected_here;
    String id,consignment_id;
    private SwipeRefreshLayout swipeRefreshLayout;
    TextView no_data_txt,tv_total_order;
    SharedPreferences s_pref;
    ProgressBar progressbar;
    NewBroadCast mReceiver;
    Handler handler;
    int page_count = 1;
    ProgressBar load_more_progress;
    LinearLayoutManager linearLayoutManager;
    boolean ispost_finsh , fromBroadcast = false;

    private class NewBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            page_count = 1;
			fromBroadcast = true;
            CallApi_showRiderOrders();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, null, false);
        s_pref = getContext().getSharedPreferences(Variables.userDetails_pref_name, Context.MODE_PRIVATE);
        no_data_txt = view.findViewById(R.id.no_data_txt);
        tv_total_order = view.findViewById(R.id.tv_total_order);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        progressbar = view.findViewById(R.id.progressbar);
        recyclerView = view.findViewById(R.id.rc_my_orders);
        load_more_progress = view.findViewById(R.id.load_more_progress);
        swipeRefreshLayout.setOnRefreshListener(this);
        data_list = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new Orders_Adapter(getContext(), data_list, (postion, Model, view1) -> {

            my_orders_model_1 = (My_Orders_Model) Model;
            id = my_orders_model_1.id;
            consignment_id = my_orders_model_1.consignment_id;

            switch (view1.getId()) {

                case R.id.btn_yes:
                  //  Toast.makeText(getContext(), Variables.userDetails_pref.getString(Variables.trip_roam_current_id, "null"), Toast.LENGTH_SHORT).show();
                    if( Variables.userDetails_pref.getString(Variables.trip_roam_current_id, null)!=null){
                        startScanCode(consignment_id,postion,id);
                    }else{
                        Toast.makeText(getContext(), "Please start the trip. before any activity", Toast.LENGTH_SHORT).show();
                    }

                    //todo:: after scan code implementation remove this

                    break;

                case R.id.btn_no:
                    if( Variables.userDetails_pref.getString(Variables.trip_roam_current_id, null)!=null){
                        CallApi_riderOrderResponse(id, "2", postion);
                    }else{
                        Toast.makeText(getContext(), "Please start the trip. before any activity", Toast.LENGTH_SHORT).show();
                    }

                    break;

                default:

                    OrderDetail_F history_a = new OrderDetail_F(resp -> {

                    });
                    FragmentManager fragmentManager_order = getActivity().getSupportFragmentManager();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("dataModel", my_orders_model_1);
                    bundle.putString("selected_pos", String.valueOf(MainActivity.selected_posiotion));
                    history_a.setArguments(bundle);
                    FragmentTransaction fragmentTransaction_history = fragmentManager_order.beginTransaction();
                    fragmentTransaction_history.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_left);
                    fragmentTransaction_history.replace(R.id.fragment_container_layout, history_a,"order_detail_f").addToBackStack("order_detail_f").commit();
                    break;

            }
        });
        recyclerView.setAdapter(adapter);

        mReceiver = new NewBroadCast();
        getActivity().registerReceiver(mReceiver, new IntentFilter("Active"));
        page_count = 1;
        CallApi_showRiderOrders();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                scrollOutitems = linearLayoutManager.findLastVisibleItemPosition();

                if (userScrolled && (scrollOutitems == data_list.size() - 1)) {
                    userScrolled = false;

                    if (load_more_progress.getVisibility() != View.VISIBLE && !ispost_finsh) {
                        load_more_progress.setVisibility(View.VISIBLE);
                        page_count = page_count + 1;
                        CallApi_showRiderOrders();
                    }
                }


            }
        });


        return view;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (view != null && isVisibleToUser) {
            handler = new Handler();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    page_count = 1;
                    CallApi_showRiderOrders();
                }
            }, 500);
        }
    }
    private void CallApi_RoamDestinationUpdate(String order_id) {


        JSONObject sendobj = new JSONObject();



        try {
            sendobj.put("roam_trip_id", Variables.userDetails_pref.getString(Variables.trip_roam_current_id, ""));
            sendobj.put("order_id", "" + order_id);
            sendobj.put("roam_user_id", "" + Variables.userDetails_pref.getString(Variables.geospark_user,""));

        } catch (JSONException e) {
            e.printStackTrace();
        }




        ApiRequest.Call_Api(getContext(), Api_urls.URL_ROAM_TRIP_DESTINATION_ADD, sendobj, resp -> {
            Log.d("trip", resp);

            if (resp != null) {
                try {
                    JSONObject respobj = new JSONObject(resp);
                    /*if (respobj.getString("status").equals("1")) {

                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }
    private void CallApi_showRiderOrders() {

    if(page_count == 1 && fromBroadcast == true ){
            data_list.clear();
            fromBroadcast = false;
        }
        JSONObject sendobj = new JSONObject();

        if (data_list.isEmpty() && !swipeRefreshLayout.isRefreshing()) {
            progressbar.setVisibility(View.VISIBLE);
        } else {
            progressbar.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();


        try {
            sendobj.put("user_id", Variables.userDetails_pref.getString(Variables.id, ""));
            sendobj.put("starting_point", "" + page_count);
            sendobj.put("type", "" + "pending");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        no_data_txt.setVisibility(View.GONE);

        ApiRequest.Call_Api(getContext(), Api_urls.SHOW_RIDER_ORDERS, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                progressbar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);
                        if (respobj.getString("status").equals("1")) {
                            JSONArray jsonArray_PendingOrders = respobj.getJSONObject("data").getJSONArray("orders");

                            tv_total_order.setText( respobj.getJSONObject("data").getJSONObject("pagination").getString("total")+ " Pending Orders");
                            Functions.Order_Parse_Data(jsonArray_PendingOrders, "Pending Orders", new API_CallBack() {
                                @Override
                                public void ArrayData(ArrayList arrayList) {

                                    if (!arrayList.isEmpty() && page_count == 1) {
                                        data_list.clear();
                                        data_list.addAll(arrayList);
                                    } else {
                                        data_list.addAll(arrayList);
                                    }
                                    if (data_list.isEmpty()) {
                                        no_data_txt.setVisibility(View.VISIBLE);
                                    } else {
                                        no_data_txt.setVisibility(View.GONE);
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        load_more_progress.setVisibility(View.GONE);
                    }

                }
            }
        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            default:
                break;
        }
    }
    private void startScanCode(String shipment_id, int position, String id) {
        Intent intent4 = new Intent(getActivity(), Barcode_scaner_A.class);
        Bundle dataBundle4 = new Bundle();
        dataBundle4.putString("shipment_id", shipment_id);
        dataBundle4.putInt("position", position);
        dataBundle4.putString("id", id);
       // CallApi_riderOrderResponse(id, "1", position);
        intent4.putExtras(dataBundle4);
        startActivityForResult(intent4, 567);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 567) {
            if (resultCode == RESULT_OK) {
                String id=data.getStringExtra("id");

                int position=data.getIntExtra("position",0);

                CallApi_riderOrderResponse(id, "1", position);
            }
        }
    }
    private void CallApi_riderOrderResponse(String Order_id, String rider_response, int position) {

        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("order_id", Order_id + "");

            sendobj.put("rider_response", rider_response + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Functions.show_loader(getActivity(), false, false);
        ApiRequest.Call_Api(getContext(), Api_urls.URL_RIDER_ORDER_RESPONSE, sendobj, resp -> {
            Functions.cancel_loader();
            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);
                    if (respobj.getString("status").equals("1")) {

                        data_list.remove(position);
                        adapter.notifyDataSetChanged();
                        page_count = 1;
                       // CallApi_showRiderOrders();

                        JSONObject roamSendobj = new JSONObject();
                        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                        String format = s.format(new Date());
                        try {

/*            String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());

            String newString = time.replaceAll("\\/", "-");*/

                            roamSendobj.put("shipment id",  data_list.get(position).consignment_id);
                            if(rider_response.equals("1")){
                                roamSendobj.put("status", "picked up");
                            }else if(rider_response.equals("2")){
                                roamSendobj.put("status", "cancel picked up");
                            }

                            roamSendobj.put("time", format);
                            //     roamSendobj.put("response", RiderOrder.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        roam_update_meta_data_with_location(roamSendobj);


                        if (rider_response.equals("1")) {
                            data_list.clear();
                            page_count = 1;
                            CallApi_showRiderOrders();
                            CallApi_RoamDestinationUpdate(Order_id);
                      /*      OrderDetail_F history_a = new OrderDetail_F();
                            FragmentManager fragmentManager_order = getActivity().getSupportFragmentManager();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("dataModel", my_orders_model_1);
                            bundle.putString("selected_pos", "1");
                            history_a.setArguments(bundle);
                            FragmentTransaction fragmentTransaction_history = fragmentManager_order.beginTransaction();
                            fragmentTransaction_history.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_left);
                            fragmentTransaction_history.replace(R.id.fragment_container_layout, history_a,"order_detail_f").addToBackStack("order_detail_f").commit();
*/
                        } else if (rider_response.equals("2")) {
                            data_list.clear();
                            page_count = 1;
                            CallApi_showRiderOrders();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }

        });

    }



    @Override
    public void onRefresh() {
        page_count = 1;
        CallApi_showRiderOrders();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

}
