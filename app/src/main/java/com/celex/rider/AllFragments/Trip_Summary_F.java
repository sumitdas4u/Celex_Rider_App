package com.celex.rider.AllFragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.celex.rider.Adapters.TripAdapter;
import com.celex.rider.AllActivitys.MainActivity;
import com.celex.rider.AllFragments.Document.Upload_Doc_Second_F;
import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.RootFragment;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.DataModels.TripModel;
import com.celex.rider.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Trip_Summary_F extends RootFragment implements View.OnClickListener {

    RecyclerView recyclerView;
    TripAdapter tripAdapter;
    ArrayList<TripModel> document_list = new ArrayList<>();
   // ImageView iv_upload;
   Button btn_start,btn_end;
    SwipeRefreshLayout swipreferesh_layout;
    View view;
    TextView no_data_text;
    ProgressBar progressbar;
    public Trip_Summary_F() {
        // doesn't do anything special
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_trip_summary, container, false);

        initViews();
        CallApi_TripsSummary();

        assert getFragmentManager() != null;
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.e("DEBUG", "onResume of HomeFragment");

                Handler handler = new Handler();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CallApi_TripsSummary();
                    }
                }, 500);

            }
        });
        return view;
    }

    void initViews() {

       // iv_upload = view.findViewById(R.id.iv_upload);
        recyclerView = view.findViewById(R.id.Rv_trip_summary);
        progressbar = view.findViewById(R.id.progressbar);
        no_data_text = view.findViewById(R.id.no_data_txt);

        swipreferesh_layout = view.findViewById(R.id.swipreferesh_layout);

        swipreferesh_layout.setOnRefreshListener(() -> {

            CallApi_TripsSummary();

        });

        view.findViewById(R.id.iv_menu).setOnClickListener(this);


       view.findViewById(R.id.btn_change_request).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_change_request:
                Toast.makeText(getContext(), "under development soon will be released", Toast.LENGTH_SHORT).show();

                break;

            case R.id.iv_menu:

                MainActivity.Open_drawer();

                break;

            default:
                return;
        }

    }

    ////fetch all the documents url from database
    private void CallApi_TripsSummary() {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", Variables.userDetails_pref.getString(Variables.id, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (document_list.isEmpty() && !swipreferesh_layout.isRefreshing()) {
            progressbar.setVisibility(View.VISIBLE);
        }
        ApiRequest.Call_Api(getActivity(), Api_urls.URL_GET_TRIP_SUMMARY, sendobj, resp -> {

            progressbar.setVisibility(View.GONE);
            swipreferesh_layout.setRefreshing(false);
            ArrayList<TripModel> temp_list = new ArrayList<>();
            try {
                JSONObject respobj = new JSONObject(resp);

                if (respobj.getString("status").equals("1")) {

                    no_data_text.setVisibility(View.GONE);
                    JSONArray respobjJSONArray = respobj.getJSONObject("data").getJSONArray("trips");

                    for (int i = 0; i < respobjJSONArray.length(); i++) {
                        JSONObject UserDocument = respobjJSONArray.getJSONObject(i);
                     //   JSONObject UserDocument = respobjJSONArray.getJSONObject(i).getJSONObject("UserDocument");
                        TripModel tripModel = new TripModel();
                        tripModel.trip_distance = UserDocument.optString("trip_distance");
                        tripModel.trip_duration = UserDocument.optString("trip_duration");
                        tripModel.trip_request_distance = UserDocument.optString("trip_request_distance");
                        tripModel.trip_fixed_distance = UserDocument.optString("trip_fixed_distance");
                        tripModel.trip_date = UserDocument.optString("trip_date");

                        temp_list.add(tripModel);
                    }

                    document_list.clear();
                    document_list.addAll(temp_list);
                    if (document_list == null || document_list.isEmpty() || document_list.equals("")) {
                        no_data_text.setVisibility(View.VISIBLE);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setHasFixedSize(true);

                    tripAdapter = new TripAdapter(getActivity(), document_list, (postion, Model, view) -> {
                        switch (view.getId()) {
                            case R.id.img_start:

                                break;
                            case R.id.img_end:

                                break;
                            default:
                                return;
                        }


                    });
                    tripAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(tripAdapter);
                } else {
                    no_data_text.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        });
    }
}