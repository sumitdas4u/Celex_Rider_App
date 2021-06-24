package com.celex.rider.AllFragments;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

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

import org.json.JSONException;
import org.json.JSONObject;

public class Email_change_f extends RootFragment implements View.OnClickListener {


    View view;
    EditText et_email;
    String old_email;

    public Email_change_f() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_email_change_f, container, false);
        ActionControl();
        return view;
    }

    private void ActionControl() {
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.change_number_container).setOnClickListener(this);
        view.findViewById(R.id.register_no_continue).setOnClickListener(this);
        et_email = view.findViewById(R.id.et_email);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                Functions.hideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;
            case R.id.register_no_continue: {

                String email = et_email.getText().toString().toLowerCase();
                old_email = Variables.userDetails_pref.getString(Variables.email, "");
                if (!Functions.isValidEmail(email)) {
                    Functions.dialouge(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.please_enter_correct_email));
                    return;
                }  else if (email.equals(old_email)) {
                    Functions.dialouge(getActivity(), getResources().getString(R.string.alert), getString(R.string.old_email_error));
                    return;
                } else {
                    CallApi_verifyforgotPasswordCode(email);
                }

            }
            break;

            default:
                break;
        }
    }

    private void ShowRegisterAuth(String email) {
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
        bundle.putString("phone_number", email);
        bundle.putString("fromWhere", "email");
        verify_phone_no.setArguments(bundle);
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.change_number_container, verify_phone_no).addToBackStack(null).commit();

    }

    private void CallApi_verifyforgotPasswordCode(String email) {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", Variables.userDetails_pref.getString(Variables.id, ""));
            sendobj.put("email", email);
      } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.show_loader(getContext(), false, false);
        ApiRequest.Call_Api(getContext(), Api_urls.CHANGE_EMAIL_ADDRESS, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            ShowRegisterAuth(email);
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