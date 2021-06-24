package com.celex.rider.AllFragments;

import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.CodeClasses.RootFragment;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.R;
import com.celex.rider.interfaces.Callback;

import org.json.JSONException;
import org.json.JSONObject;

public class Verify_Phone_No extends RootFragment implements View.OnClickListener {

    private PinView et_code;
    TextView txt_sub_title, verification_title_tv;
    private View view;
    String phone_no, fromWhere;
    Callback callback;

    public Verify_Phone_No(Callback callback) {
        this.callback = callback;
    }

    public Verify_Phone_No() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_verify_phone_no, container, false);
        Bundle bundle = getArguments();
        phone_no = bundle.getString("phone_number");
        fromWhere = bundle.getString("fromWhere");
        METHOD_init_views();
        return view;
    }

    private void METHOD_init_views() {
        txt_sub_title = view.findViewById(R.id.tv_sub_title);
        et_code = view.findViewById(R.id.et_code);
        verification_title_tv = view.findViewById(R.id.verification_title_tv);

        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.btn_verify_code).setOnClickListener(this);
        SetupScreenData();
    }

    private void SetupScreenData() {
        if (fromWhere.equals("phone_number")) {
            txt_sub_title.setText(view.getContext().getString(R.string.check_your_sms_message_we_ve) + " " + phone_no);
            verification_title_tv.setText(getResources().getString(R.string.verify_phone_number));
        } else {
            txt_sub_title.setText(view.getContext().getString(R.string.check_your_email_message_we_have) + " " + phone_no);
            verification_title_tv.setText(getResources().getString(R.string.verify_email_address));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                Functions.hideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;
            case R.id.btn_verify_code:
                if (TextUtils.isEmpty(et_code.getText().toString())) {
                    Toast.makeText(getContext(), "" + getResources().getString(R.string.enter_verification_code), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fromWhere.equals("phone_number")) {
                    CallApi_verifyPhoneCode(et_code.getText().toString());
                } else {
                    CallApi_verifyEmailCode(et_code.getText().toString());
                }
                break;
            default:
                break;
        }
    }


    private void CallApi_verifyEmailCode(String code) {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", Variables.userDetails_pref.getString(Variables.id, ""));
            sendobj.put("new_email", phone_no);
            sendobj.put("code", code);


            Log.d("Rider_log123", "resp at code : " + sendobj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.show_loader(getContext(), false, false);
        ApiRequest.Call_Api(getContext(), Api_urls.VERIFY_CHANGE_EMAIL_CODE, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            Variables.userDetails_pref.edit().putString(Variables.email, phone_no).apply();
                            if (callback != null) {
                                callback.Responce("success");
                                getActivity().onBackPressed();
                            }
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


    private void CallApi_verifyPhoneCode(String code) {
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("verify", "1");
            sendobj.put("phone", phone_no);
            sendobj.put("code", code);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.show_loader(getContext(), false, false);
        ApiRequest.Call_Api(getContext(), Api_urls.VERIFY_PHONE_NO, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            Variables.userDetails_pref.edit().putString(Variables.phone, phone_no).apply();
                            if (callback != null) {
                                callback.Responce("success");
                                getActivity().onBackPressed();
                            }
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