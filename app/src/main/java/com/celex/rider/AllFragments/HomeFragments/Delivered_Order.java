package com.celex.rider.AllFragments.HomeFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.celex.rider.Adapters.Orders_Adapter;
import com.celex.rider.AllActivitys.Barcode_scaner_A;
import com.celex.rider.AllActivitys.MainActivity;
import com.celex.rider.AllFragments.OrderDetail_F;
import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.DataModels.My_Orders_Model;
import com.celex.rider.R;
import com.celex.rider.interfaces.API_CallBack;
import com.celex.rider.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class Delivered_Order extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    View view;
    ArrayList<My_Orders_Model> data_list;
    RecyclerView recyclerView;
    Orders_Adapter adapter;
    My_Orders_Model my_orders_model_1;
    public int Selected_here;
    String id;
    private SwipeRefreshLayout swipeRefreshLayout;
    TextView no_data_txt;
    SharedPreferences s_pref;
    ProgressBar progressbar;
    Handler handler;
    int page_count = 1;
    ProgressBar load_more_progress;
    LinearLayoutManager linearLayoutManager;
    boolean ispost_finsh;

    public Delivered_Order() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_delivered_order, container, false);
        s_pref = getContext().getSharedPreferences(Variables.userDetails_pref_name, Context.MODE_PRIVATE);
        no_data_txt = view.findViewById(R.id.no_data_txt);
        data_list = new ArrayList<>();
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        progressbar = view.findViewById(R.id.progressbar);
        recyclerView = view.findViewById(R.id.rc_my_orders);
        load_more_progress = view.findViewById(R.id.load_more_progress);
        swipeRefreshLayout.setOnRefreshListener(this);

        Set_Adapter();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        page_count = 1;


        assert getFragmentManager() != null;
        getFragmentManager().addOnBackStackChangedListener(() -> {
            Log.e("DEBUG", "onResume of HomeFragment");

            Handler handler = new Handler();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CallApi_showRiderOrders();
                }
            }, 500);

        });
        CallApi_showRiderOrders();
        return view;
    }

    private void CallApi_showRiderOrders() {

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
            sendobj.put("type", "" + "on_the_way");
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
                            JSONArray jsonArray_PendingOrders =  respobj.getJSONObject("data").getJSONArray("orders");
                            Functions.Order_Parse_Data(jsonArray_PendingOrders, "Completed Orders", new API_CallBack() {
                                @Override
                                public void ArrayData(ArrayList arrayList) {
                                    if (page_count == 1) {
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
    public void onResume() {
      //  CallApi_showRiderOrders();
        super.onResume();
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

    public void Set_Adapter() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new Orders_Adapter(getContext(), data_list, (postion, Model, view1) -> {

            my_orders_model_1 = (My_Orders_Model) Model;
            id = my_orders_model_1.id;

            OrderDetail_F history_a = new OrderDetail_F();
            FragmentManager fragmentManager_order = getActivity().getSupportFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putSerializable("dataModel", my_orders_model_1);
            bundle.putString("selected_pos", String.valueOf(MainActivity.selected_posiotion));
            history_a.setArguments(bundle);
            FragmentTransaction fragmentTransaction_history = fragmentManager_order.beginTransaction();
            fragmentTransaction_history.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_left);
            fragmentTransaction_history.replace(R.id.fragment_container_layout, history_a, "order_detail_f").addToBackStack("order_detail_f").commit();
        });
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        page_count = 1;
        CallApi_showRiderOrders();
    }
}