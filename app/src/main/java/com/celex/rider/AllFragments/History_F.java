package com.celex.rider.AllFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.celex.rider.Adapters.HistoryAdapter;
import com.celex.rider.AllActivitys.MainActivity;
import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.CodeClasses.RootFragment;
import com.celex.rider.DataModels.My_Orders_Model;
import com.celex.rider.R;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class History_F extends RootFragment implements View.OnClickListener {

    RecyclerView rc_history;
    ArrayList<My_Orders_Model> myOrdersModelArrayList;
    HistoryAdapter historyAdapter;
    TextView amount_txt, total_order, tv_last_date, currency_country;
    String LatesetDelivery, st_currency_country;
    TextView no_data_txt;
    View view;
    int page_count = 1;
    ProgressBar load_more_progress;
    LinearLayoutManager linearLayoutManager;
    boolean ispost_finsh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_history, container, false);

        initView();
        st_currency_country = Variables.userDetails_pref.getString(Variables.currency_code_usd, "");
//        currency_country.setText(st_currency_country);
        myOrdersModelArrayList = new ArrayList<>();
        Set_Adapter();
        rc_history.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
/*                Toast.makeText(getContext(), "sc",
                        Toast.LENGTH_SHORT).show();*/
                scrollOutitems = linearLayoutManager.findLastVisibleItemPosition();

                if (userScrolled && (scrollOutitems == myOrdersModelArrayList.size() - 1)) {
                    userScrolled = false;
                    if (load_more_progress.getVisibility() != View.VISIBLE && !ispost_finsh) {
                        load_more_progress.setVisibility(View.VISIBLE);
                        page_count = page_count + 1;
                        CallApi_OrderHistory();

                    }
                }
            }
        });
        page_count = 1;
        CallApi_OrderHistory();
        return view;

    }

    public void Set_Adapter() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        rc_history.setLayoutManager(linearLayoutManager);
        rc_history.setHasFixedSize(true);
        historyAdapter = new HistoryAdapter(getActivity(), myOrdersModelArrayList, (postion, Model, view) -> {
            My_Orders_Model my_orders_model_1 = (My_Orders_Model) Model;
            OrderDetail_F history_a = new OrderDetail_F();
            FragmentManager fragmentManager_history = getActivity().getSupportFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putSerializable("dataModel", my_orders_model_1);
    /*        Toast.makeText(getContext(), String.valueOf(postion),
                    Toast.LENGTH_LONG).show();*/
            bundle.putString("selected_pos", String.valueOf(postion));
            history_a.setArguments(bundle);
            FragmentTransaction fragmentTransaction_history = fragmentManager_history.beginTransaction();
            fragmentTransaction_history.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_left);
            fragmentTransaction_history.replace(R.id.fragment_container_layout, history_a).addToBackStack(null).commit();

        });
        rc_history.setAdapter(historyAdapter);
    }

    void initView() {
        rc_history = view.findViewById(R.id.rc_history);
      //  amount_txt = view.findViewById(R.id.amount_txt);
        total_order = view.findViewById(R.id.total_order);
        tv_last_date = view.findViewById(R.id.tv_last_date);
     //   currency_country = view.findViewById(R.id.currency_country);
        no_data_txt = view.findViewById(R.id.no_data_txt);
        load_more_progress = view.findViewById(R.id.load_more_progress);
        view.findViewById(R.id.iv_menu).setOnClickListener(this);
    }

    //this will get the all history data of rider orders
    private void CallApi_OrderHistory() {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", Variables.userDetails_pref.getString(Variables.id, ""));
            sendobj.put("starting_point", "" + page_count);
            sendobj.put("type", "" + "delivered");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (myOrdersModelArrayList.isEmpty()) {
            Functions.show_loader(getActivity(), false, false);
        }

        ApiRequest.Call_Api(getActivity(), Api_urls.SHOW_RIDER_ORDERS, sendobj, resp -> {
            Functions.cancel_loader();
            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);

                    if (respobj.getString("status").equals("1")) {

                      //  JSONArray jsonArray_PendingOrders = respobj.getJSONArray("orders");
                        JSONArray jsonArray_PendingOrders = respobj.getJSONObject("data").getJSONArray("orders");

                        ArrayList<My_Orders_Model> temp_list = new ArrayList<>();


                        for (int i = 0; i < jsonArray_PendingOrders.length(); i++) {

                            if (jsonArray_PendingOrders.length() > 0) {

                             //   JSONObject Order = jsonArray_PendingOrders.getJSONObject(i).getJSONObject("Order");
                                JSONObject order_object = jsonArray_PendingOrders.getJSONObject(i);
                                JSONObject OrderDetails = order_object.getJSONObject("order_details");

                                My_Orders_Model my_orders_model = new My_Orders_Model();

                                my_orders_model.delivery_datetime = order_object.optString("delivery_datetime");
                               // my_orders_model.on_the_way_to_pickup = order_object.getString("on_the_way_to_pickup");
                                my_orders_model.pickup_datetime = order_object.getString("pickup_datetime");
                                my_orders_model.on_the_way_to_dropoff = order_object.getString("on_the_way_to_dropoff");
                                my_orders_model.delivered = order_object.getString("delivery_datetime");
                                my_orders_model.is_delivered_doc = order_object.optString("is_delivered_doc","0");
                                my_orders_model.rider_reponse = order_object.getString("rider_response");
                                my_orders_model.undelivery_datetime = order_object.getString("un_delivery_datetime");
                                my_orders_model.consignment_id = order_object.getString("consignment_no");
                                my_orders_model.id = order_object.getString("id");
                                if(page_count == 1) {
                                    LatesetDelivery = jsonArray_PendingOrders.getJSONObject(0).getString("delivery_datetime");
                                }
                                my_orders_model.receiver_name = order_object.getString("drop_company_name");
//                                my_orders_model.receiver_name = order_object.getString("sender_location_string");
                                String delivery_address = order_object.getString("drop_address");
                                my_orders_model.sender_location_string = delivery_address;

                                // my_orders_model.receiver_name = f_name + " " + l_name;

                               // JSONObject store_obj = Order.getJSONObject("Store").getJSONObject("StoreLocation");


                          /*      String store_city = store_obj.getString("city");
                                String store_street = store_obj.getString("street");
                                if (store_street != null && !store_street.equalsIgnoreCase("")) {
                                    my_orders_model.sender_location_string = store_street;
                                } else {
                                    my_orders_model.sender_location_string = store_city;
                                }*/

                                temp_list.add(my_orders_model);
                            }
                        }

                        String completed_orders_count = respobj.getJSONObject("data").getJSONObject("pagination").getString("total");


                      //  String total_earning = jsonObject.getString("total_earning");


                        total_order.setText(completed_orders_count);
                       // amount_txt.setText(Variables.userDetails_pref.getString(Variables.symbol, "") + total_earning);

                        if (LatesetDelivery != null && !LatesetDelivery.equals("") && !LatesetDelivery.equals("0000-00-00 00:00:00")) {
                            String final_date = Functions.ChangeDate(LatesetDelivery);
                            String time = Functions.Show_Message_Time(LatesetDelivery);
                            tv_last_date.setText(final_date + " at " + time);
                        }

                        if (page_count == 1) {
                            myOrdersModelArrayList.clear();
                            myOrdersModelArrayList.addAll(temp_list);
                        } else {
                            myOrdersModelArrayList.addAll(temp_list);
                        }
                        if (myOrdersModelArrayList.isEmpty()) {
                            no_data_txt.setVisibility(View.VISIBLE);
                        } else {
                            no_data_txt.setVisibility(View.GONE);
                        }
                        historyAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    load_more_progress.setVisibility(View.GONE);
                }

            }

        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_menu:
                MainActivity.Open_drawer();
                break;

            default:
                return;
        }
    }
}