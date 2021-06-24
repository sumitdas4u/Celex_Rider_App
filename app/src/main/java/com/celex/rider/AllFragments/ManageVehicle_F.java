package com.celex.rider.AllFragments;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.celex.rider.AllActivitys.MainActivity;
import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.CodeClasses.RootFragment;
import com.celex.rider.R;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.Variables;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ManageVehicle_F extends RootFragment implements View.OnClickListener {

    TextView et_Manufacturer, et_Model, et_Year, et_LicensePlate, et_Color;
    TextView vehical_type_et;
    String Vehicle_id;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_manage_vehicle, container, false);

        initView();

        CallApi_showVehicle(getActivity());
        return view;
    }


    void initView() {

        vehical_type_et = view.findViewById(R.id.vehical_type_et);

        et_Manufacturer = view.findViewById(R.id.et_Manufacturer);
        et_Model = view.findViewById(R.id.et_Model);
        et_Year = view.findViewById(R.id.et_Year);
        et_LicensePlate = view.findViewById(R.id.et_LicensePlate);
        et_Color = view.findViewById(R.id.et_Color);

        view.findViewById(R.id.iv_menu).setOnClickListener(this);
    //    view.findViewById(R.id.btn_save_changes).setOnClickListener(this);
        view.findViewById(R.id.vehicle_type_rlt).setOnClickListener(this);

        et_Model.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        et_LicensePlate.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        et_Manufacturer.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence cs, int start, int end, Spanned spanned, int dStart, int dEnd) {
                // TODO Auto-generated method stub
                if (cs.equals("")) { // for backspace
                    return cs;
                }
                if (cs.toString().matches("[a-zA-Z ]+")) {
                    return cs;
                }
              return cs;
            }
        }
        });


    }

    public boolean checkYear(String valid_until){
        Date today = Calendar.getInstance().getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date strDate = null;
        try {
            strDate = sdf.parse(valid_until);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (today.after(strDate)) {
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_menu:
                Functions.hideSoftKeyboard(getActivity());
                MainActivity.Open_drawer();
                break;

           /* case R.id.btn_save_changes:

                if (checkValidate(getActivity())) {
                    if(checkYear(et_Year.getText().toString())) {
                        CallApi_update_vehicle(getActivity());
                    }else{
                       Functions.dialouge(getActivity(),getResources().getString(R.string.alert),getString(R.string.year_above));
                    }
                }

                break;*/

            case R.id.vehicle_type_rlt:

                Open_vheicle_fragment();
                break;


            default:
                break;

        }

    }

    public void Open_vheicle_fragment() {
        SelectVehicle_F selectVehicle_f = new SelectVehicle_F(bundle -> {
            if (bundle != null) {
                String vehicle_type = bundle.getString("Vehicle_name");
                String cap = vehicle_type.substring(0, 1).toUpperCase() + vehicle_type.substring(1);
                vehical_type_et.setText(cap);
                Vehicle_id = bundle.getString("Vehicle_id");
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString("vehicle_id", Vehicle_id);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        selectVehicle_f.setArguments(bundle);
        fragmentTransaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        fragmentTransaction.replace(R.id.manageVehicle, selectVehicle_f).addToBackStack(null).commit();
    }

    //this will update the vechials data of the rider
  /*  private void CallApi_update_vehicle(Context context) {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", Variables.userDetails_pref.getString(Variables.id, ""));
            sendobj.put("vehicle_type_id", (Vehicle_id));
            sendobj.put("make", (et_Manufacturer.getText().toString()) + "");
            sendobj.put("model", (et_Model.getText().toString()) + "");
            sendobj.put("year", (et_Year.getText().toString()) + "");
            sendobj.put("license_plate", (et_LicensePlate.getText().toString()) + "");
            sendobj.put("color", (et_Color.getText().toString()) + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Functions.show_loader(context, false, false);
        ApiRequest.Call_Api(context, Api_urls.URL_ADD_VEHICLE, sendobj, resp -> {

            Functions.cancel_loader();

            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);

                    if (respobj.getString("code").equals("200")) {
                        getActivity().onBackPressed();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }

        });

    }
*/
    //this will get the vechials data of the rider
    private void CallApi_showVehicle(Context context) {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", Variables.userDetails_pref.getString(Variables.id, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Functions.show_loader(context, false, false);
        ApiRequest.Call_Api(context, Api_urls.URL_SHOW_VEHICLE, sendobj, resp -> {

            Functions.cancel_loader();
            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);


                    if (respobj.getString("status").equals("1")) {
                        JSONObject Vehicle_characteristics = respobj.getJSONObject("data");

                       // JSONObject VehicleType = respobj.getJSONObject("msg").getJSONObject("VehicleType");

                        Vehicle_id = Vehicle_characteristics.getString("id");

                        String vehicle_type = Vehicle_characteristics.optString("name");
                        String cap = vehicle_type.substring(0, 1).toUpperCase() + vehicle_type.substring(1);
                        vehical_type_et.setText(cap);

                        et_Manufacturer.setText(Vehicle_characteristics.getString("make"));
                        et_Model.setText(Vehicle_characteristics.getString("model"));
                        et_Year.setText(Vehicle_characteristics.getString("year"));
                        et_LicensePlate.setText(Vehicle_characteristics.getString("license_plate"));
                        et_Color.setText(Vehicle_characteristics.getString("color"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();


                }

            }

        });

    }

    public boolean checkValidate(Context context) {
        String vehical_type = vehical_type_et.getText().toString();
        String st_Manufacturer = et_Manufacturer.getText().toString();
        String st_Model = et_Model.getText().toString();
        String st_Year = et_Year.getText().toString();
        String st_LicensePlate = et_LicensePlate.getText().toString();
        String st_Color = et_Color.getText().toString();

        if (TextUtils.isEmpty(vehical_type)) {
            Functions.dialouge(context, getResources().getString(R.string.alert), getResources().getString(R.string.vehicle_type_empty));
            return false;
        } else if (TextUtils.isEmpty(st_Manufacturer)) {
            Functions.dialouge(context, getResources().getString(R.string.alert), getResources().getString(R.string.Manufacture_empty));
            return false;
        } else if (TextUtils.isEmpty(st_Model)) {
            Functions.dialouge(context, getResources().getString(R.string.alert), getResources().getString(R.string.model_empty));
            return false;
        } else if (TextUtils.isEmpty(st_Year)) {
            Functions.dialouge(context, getResources().getString(R.string.alert), getResources().getString(R.string.year_empty));
            return false;
        } else if (TextUtils.isEmpty(st_LicensePlate)) {
            Functions.dialouge(context, getResources().getString(R.string.alert), getResources().getString(R.string.license_plate_empty));
            return false;
        } else if (TextUtils.isEmpty(st_Color)) {
            Functions.dialouge(context, getResources().getString(R.string.alert), getResources().getString(R.string.color_empty));
            return false;
        }


        return true;
    }


}