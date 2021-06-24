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

import com.celex.rider.Adapters.DocumentMeterAdapter;
import com.celex.rider.AllActivitys.MainActivity;
import com.celex.rider.AllFragments.Document.Upload_Doc_Second_F;
import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.RootFragment;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.DataModels.MeterReadingModel;
import com.celex.rider.R;
import com.celex.rider.Users_Chat.Preview_F;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Meter_Reading_F extends RootFragment implements View.OnClickListener {

    RecyclerView recyclerView;
    DocumentMeterAdapter documentHomeAdapter;
    ArrayList<MeterReadingModel> document_list = new ArrayList<>();
   // ImageView iv_upload;
   Button btn_start,btn_end;
    SwipeRefreshLayout swipreferesh_layout;
    View view;
    TextView no_data_text;
    ProgressBar progressbar;
    public Meter_Reading_F() {
        // doesn't do anything special
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_meter_reading, container, false);

        initViews();
        CallApi_MeterReadings();

        assert getFragmentManager() != null;
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.e("DEBUG", "onResume of HomeFragment");

                Handler handler = new Handler();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CallApi_MeterReadings();
                    }
                }, 500);

            }
        });
        return view;
    }

    void initViews() {

       // iv_upload = view.findViewById(R.id.iv_upload);
        recyclerView = view.findViewById(R.id.meter_documents);
        progressbar = view.findViewById(R.id.progressbar);
        no_data_text = view.findViewById(R.id.no_data_txt);

        swipreferesh_layout = view.findViewById(R.id.swipreferesh_layout);

        swipreferesh_layout.setOnRefreshListener(() -> {

            CallApi_MeterReadings();

        });

        view.findViewById(R.id.iv_menu).setOnClickListener(this);
        view.findViewById(R.id.iv_menu).setOnClickListener(this);
       view.findViewById(R.id.btn_end).setOnClickListener(this);
       view.findViewById(R.id.btn_start).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_end:
                Upload_Doc_Second_F f = new Upload_Doc_Second_F("2", resp -> {
                });
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.rl_document, f).addToBackStack(null).commit();
                break;
            case R.id.btn_start:

                f = new Upload_Doc_Second_F("1", resp -> {

                });
                ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.rl_document, f).addToBackStack(null).commit();

                break;
            case R.id.iv_menu:

                MainActivity.Open_drawer();

                break;

            default:
                return;
        }

    }

    ////fetch all the documents url from database
    private void CallApi_MeterReadings() {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", Variables.userDetails_pref.getString(Variables.id, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (document_list.isEmpty() && !swipreferesh_layout.isRefreshing()) {
            progressbar.setVisibility(View.VISIBLE);
        }
        ApiRequest.Call_Api(getActivity(), Api_urls.URL_GET_METER_READING, sendobj, resp -> {

            progressbar.setVisibility(View.GONE);
            swipreferesh_layout.setRefreshing(false);
            ArrayList<MeterReadingModel> temp_list = new ArrayList<>();
            try {
                JSONObject respobj = new JSONObject(resp);

                if (respobj.getString("status").equals("1")) {

                    no_data_text.setVisibility(View.GONE);
                    JSONArray respobjJSONArray = respobj.getJSONObject("data").getJSONArray("documents");

                    for (int i = 0; i < respobjJSONArray.length(); i++) {
                        JSONObject UserDocument = respobjJSONArray.getJSONObject(i);
                     //   JSONObject UserDocument = respobjJSONArray.getJSONObject(i).getJSONObject("UserDocument");
                        MeterReadingModel documentHomeModel = new MeterReadingModel();
                        documentHomeModel.date_reading = UserDocument.optString("reading_date");
                        documentHomeModel.start_km = UserDocument.optString("start_km");
                        documentHomeModel.end_km = UserDocument.optString("end_km");
                        documentHomeModel.start_img = UserDocument.optString("start_image");
                        documentHomeModel.end_img = UserDocument.optString("end_image");
                        documentHomeModel.total = UserDocument.optString("total_km");
                        temp_list.add(documentHomeModel);
                    }

                    document_list.clear();
                    document_list.addAll(temp_list);
                    if (document_list == null || document_list.isEmpty() || document_list.equals("")) {
                        no_data_text.setVisibility(View.VISIBLE);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setHasFixedSize(true);

                    documentHomeAdapter = new DocumentMeterAdapter(getActivity(), document_list, (postion, Model, view) -> {
                        switch (view.getId()) {
                            case R.id.img_start:
                                Preview_F see_image_f = new Preview_F();
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                Bundle args = new Bundle();
                                args.putSerializable("document_url",   document_list.get(postion).start_img);

                                see_image_f.setArguments(args);
                                transaction.addToBackStack(null);
                                transaction.replace(R.id.rl_document, see_image_f).commit();
                                break;

                            case R.id.img_end:
                                see_image_f = new Preview_F();
                                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                args = new Bundle();
                                args.putSerializable("document_url",  document_list.get(postion).end_img);

                                see_image_f.setArguments(args);
                                transaction.addToBackStack(null);
                                transaction.replace(R.id.rl_document, see_image_f).commit();
                                break;
                            default:
                                return;
                        }


                    });
                    documentHomeAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(documentHomeAdapter);
                } else {
                    no_data_text.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        });
    }
}