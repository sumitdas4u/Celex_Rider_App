package com.celex.rider.AllFragments;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.CodeClasses.RootFragment;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.R;
import com.celex.rider.interfaces.Callback;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

public class Change_phone_no extends RootFragment implements View.OnClickListener {


    View view;
    EditText et_country_code, et_phone_number;
    String old_number;
    CountryCodePicker ccp;


    public Change_phone_no() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_change_phone_no, container, false);
        InitControl();
        ActionControl();
        return view;
    }

    private void ActionControl() {
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.change_number_container).setOnClickListener(this);
        view.findViewById(R.id.register_no_continue).setOnClickListener(this);
    }

    private void InitControl() {
        et_country_code = view.findViewById(R.id.et_country_code);
        et_phone_number = view.findViewById(R.id.et_phoneno);
        ccp = view.findViewById(R.id.ccp);

        //  ccp.registerPhoneNumberTextView(et_phone_number);

        SetupScreenData();
    }

    private void SetupScreenData() {
        String country_code = Variables.userDetails_pref.getString(Variables.country_code, "");
        old_number = Variables.userDetails_pref.getString(Variables.phone, "");
        et_country_code.setText(country_code);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                Functions.hideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;
            case R.id.register_no_continue: {
                if (TextUtils.isEmpty(et_phone_number.getText().toString())) {
                    Functions.dialouge(getActivity(), getString(R.string.alert), getString(R.string.please_enter_number));
                    return;
                }

                String phoneNo = et_phone_number.getText().toString();
                if (phoneNo.charAt(0) == '0') {
                    phoneNo = phoneNo.substring(1);
                }
                phoneNo = ccp.getSelectedCountryCodeWithPlus() + phoneNo;

                if (phoneNo.equals(old_number)) {
                    Functions.dialouge(getActivity(), getResources().getString(R.string.alert), getString(R.string.old_number_error));
                } else {
                    CallApi_verifyforgotPasswordCode(phoneNo);
                }

            }
            break;
            default:
                break;
        }
    }

    private void ShowRegisterAuth(String phoneNo) {
        Verify_Phone_No verify_phone_no = new Verify_Phone_No(new Callback() {
            @Override
            public void Responce(String resp) {
                if (resp != null && resp.equals("success")) {
                    getActivity().onBackPressed();
                }
            }
        });
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("phone_number", phoneNo);
        bundle.putString("fromWhere", "phone_number");
        verify_phone_no.setArguments(bundle);
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.change_number_container, verify_phone_no).addToBackStack(null).commit();

    }

    private void CallApi_verifyforgotPasswordCode(String phoneNo) {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("phone", phoneNo);
            sendobj.put("user_id", Variables.userDetails_pref.getString(Variables.id, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.show_loader(getContext(), false, false);
        ApiRequest.Call_Api(getContext(), Api_urls.CHANGE_PHONE_NO, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            ShowRegisterAuth(phoneNo);
                        } else {
                            String result = respobj.getString("msg");
                            Functions.dialouge(getActivity(), getString(R.string.alert), result);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }
}