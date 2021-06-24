package com.celex.rider.AllFragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.celex.rider.Adapters.VehicleAdapter;
import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.DataModels.SelectVehicleModel;
import com.celex.rider.R;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.interfaces.oncallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SelectVehicle_F extends Fragment implements View.OnClickListener {

    View v;
    RecyclerView rv_select_vehical;
    EditText et_serch;
    String vehicle_id = null;
    VehicleAdapter vehicleAdapter;
    oncallback oncallback;
    List<SelectVehicleModel> selectCountryModelList = new ArrayList<>();

    public SelectVehicle_F(oncallback oncallback) {
        this.oncallback = oncallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_select_vehicle, null);


        Bundle bundle = getArguments();
        if (bundle != null) {
            vehicle_id = bundle.getString("vehicle_id");
        }
        et_serch = v.findViewById(R.id.et_serch);

        rv_select_vehical = v.findViewById(R.id.rv_select_vehical);

        CallApi_showVehicleTypes();


        v.findViewById(R.id.iv_back).setOnClickListener(this);

        et_serch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //TODO implementation
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                //TODO implementation
            }

            @Override
            public void afterTextChanged(Editable editable) {
                vehicleAdapter.getFilter().filter(editable.toString());
            }
        });

        return v;
    }


    @Override
    public void onClick(View view) {


        switch (view.getId()) {

            case R.id.iv_back:
                Functions.hideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;

            default:
                break;


        }
    }

    private void CallApi_showVehicleTypes() {

        selectCountryModelList.clear();

        JSONObject sendobj = new JSONObject();

        Functions.show_loader(getActivity(), false, false);
        ApiRequest.Call_Api(getContext(), Api_urls.URL_SHOW_VEHICLE_TYPES, sendobj, resp -> {

            Functions.cancel_loader();

            try {
                JSONObject respobj = new JSONObject(resp);

                if (respobj.getString("code").equals("200")) {

                    JSONArray arrayOfAllCountries = respobj.getJSONArray("msg");

                    for (int i = 0; i < arrayOfAllCountries.length(); i++) {

                        JSONObject Order = arrayOfAllCountries.getJSONObject(i).getJSONObject("VehicleType");

                        SelectVehicleModel my_orders_model_class = new SelectVehicleModel();

                        my_orders_model_class.Vehicle_id = Order.getString("id");
                        my_orders_model_class.Vehicle_name = Order.getString("name");

                        selectCountryModelList.add(my_orders_model_class);

                    }

                    rv_select_vehical.setLayoutManager(new LinearLayoutManager(getContext()));
                    rv_select_vehical.setHasFixedSize(true);
                    vehicleAdapter = new VehicleAdapter(getContext(), vehicle_id, selectCountryModelList, (postion, Model, view) -> {

                        SelectVehicleModel selectCountryModel = (SelectVehicleModel) Model;

                        Bundle bundle = new Bundle();
                        bundle.putString("Vehicle_name", selectCountryModel.Vehicle_name + "");
                        bundle.putString("Vehicle_id", selectCountryModel.Vehicle_id + "");

                        oncallback.onResponce(bundle);
                        Functions.hideSoftKeyboard(getActivity());
                        getFragmentManager().popBackStack();

                    });
                    rv_select_vehical.setAdapter(vehicleAdapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
        });

    }
}
