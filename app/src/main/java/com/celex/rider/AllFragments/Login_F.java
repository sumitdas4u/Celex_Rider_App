package com.celex.rider.AllFragments;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.AllActivitys.MainActivity;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.R;
import com.celex.rider.CodeClasses.Variables;
import com.google.firebase.FirebaseApp;


import org.json.JSONException;
import org.json.JSONObject;

public class Login_F extends Fragment implements View.OnClickListener {

    private EditText et_email, et_pass;
    private ImageView hide_password_btn, show_password_btn;
    private FrameLayout fl;
    private String devicetoken;
    private Boolean check = true;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(getActivity());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, null);


        METHOD_init_views();

        return view;
    }


    private void METHOD_init_views() {
        fl = view.findViewById(R.id.login_fl_id);

        et_email = view.findViewById(R.id.et_email);
        et_pass = view.findViewById(R.id.et_password);

        hide_password_btn = view.findViewById(R.id.iv_hide);
        show_password_btn = view.findViewById(R.id.show_btn);
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        show_password_btn.setOnClickListener(this);
        hide_password_btn.setOnClickListener(this);

        Button btn_login = view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                getFragmentManager().popBackStackImmediate();
                break;


            case R.id.iv_hide:
                et_pass.setTransformationMethod(null);
                et_pass.setSelection(et_pass.length());
                show_password_btn.setVisibility(View.VISIBLE);
                hide_password_btn.setVisibility(View.GONE);
                break;
            case R.id.show_btn:
                et_pass.setTransformationMethod(new PasswordTransformationMethod());
                hide_password_btn.setVisibility(View.VISIBLE);
                show_password_btn.setVisibility(View.GONE);
                et_pass.setSelection(et_pass.length());
                break;
            case R.id.btn_login:
                if (METHOD_inputValidation())
                    CallApi_Login();

                break;

            default:
                break;
        }
    }

    private void CallApi_Login() {

        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("uid", et_email.getText().toString());
            sendobj.put("password", et_pass.getText().toString());
            sendobj.put("device_token", devicetoken);
            sendobj.put("role", "rider");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.show_loader(getActivity(), false, false);
        ApiRequest.Call_Api(getContext(), Api_urls.LOGIN, sendobj, resp -> {

            Log.e("Error", "response" + resp);

            Functions.cancel_loader();


            try {
                JSONObject respobj = new JSONObject(resp);
                Log.d(Variables.TAG, respobj.toString());
                if (respobj.getString("status").equals("0")) {
                    et_pass.setError(getString(R.string.wrong_login));
                    et_email.setError(getString(R.string.wrong_login));
                }
                if (respobj.getString("status").equals("1")) {

                    JSONObject userobj = respobj.getJSONObject("data");

          /*          JSONObject Country_info = respobj.getJSONObject("msg").optJSONObject("Country");

                    String country = Country_info.optString("country");
                    String country_code = Country_info.optString("country_code");

                    if (country != null && !country.equals("")) {
                        Variables.userDetails_pref.edit().putString(Variables.city, country).apply();
                        Variables.userDetails_pref.edit().putString(Variables.country_code, country_code).apply();
                    }
*/

                    METHOD_saveUserDetails(userobj);

                } else {
                    String result = respobj.getString("msg");
                    Functions.dialouge(getActivity(), getString(R.string.login_details), result);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        });

    }


    private Boolean METHOD_inputValidation() {
        Boolean value = false;
        if (et_email.length()>4) {
            if (et_pass.length() > 3)
                value = true;
            else
                et_pass.setError(getString(R.string.pass_is_weak));
        } else {
            et_email.setError(getString(R.string.please_enter_correct_email));
        }

        return value;
    }


    private void METHOD_saveUserDetails(JSONObject userobj) throws JSONException {

       ;
        String id = "" + userobj.optString("id");
        String name = "" + userobj.optString("name");
        String uid = "" + userobj.optString("uid");
        String email = "" + userobj.optString("email");

        String phone = "" + userobj.optString("mobile");
        String login_token = "" + userobj.optString("token");


        //   String member_since = "" + userobj.optString("created");
       // String memeber_date = Functions.convert_datetime(member_since, "member_since");

        SharedPreferences.Editor editor = Variables.userDetails_pref.edit();
        editor.putString(Variables.id, id);
        editor.putString(Variables.name, name);
       // editor.putString(Variables.lname, lname);
        editor.putString(Variables.uid, uid);
        editor.putString(Variables.email, email);
        try {
            String image = "" +  userobj.getJSONObject("driver_details").optString("image");
            editor.putString(Variables.image, image);
        }catch (Exception e){
            e.printStackTrace();
        }

        editor.putString(Variables.token, devicetoken);
        editor.putString(Variables.login_token, login_token);
        editor.putString(Variables.phone, phone);

        editor.putBoolean(Variables.is_login, true);
        editor.putString(Variables.setlocale, "en");
        editor.commit();
        startActivity(new Intent(getActivity(), MainActivity.class));

        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        getActivity().finish();
    }


}
