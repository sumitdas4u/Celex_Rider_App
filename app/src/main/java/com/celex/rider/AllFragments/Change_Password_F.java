package com.celex.rider.AllFragments;

import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.CodeClasses.RootFragment;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.R;
import com.celex.rider.interfaces.Callback;

import org.json.JSONException;
import org.json.JSONObject;

public class Change_Password_F extends RootFragment implements View.OnClickListener {

    private EditText et_old_password, et_new_password, et_confirm_password;
    private View view;
    private RelativeLayout rlt_hide_password, rlt_hide_new_pass, rlt_hide_confirm_pass;
    private ImageView iv_hide_password, iv_hide_new_pass, iv_hide_confirm_pass;

    private Boolean check = true;

    public Change_Password_F() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_change_password, container, false);
        METHOD_init_views();
        return view;
    }

    private void METHOD_init_views() {

        et_old_password = view.findViewById(R.id.et_password);
        et_new_password = view.findViewById(R.id.et_new_password);
        et_confirm_password = view.findViewById(R.id.et_confirm_password);

        rlt_hide_password = view.findViewById(R.id.hide_layout);
        rlt_hide_new_pass = view.findViewById(R.id.hide_new_layout);
        rlt_hide_confirm_pass = view.findViewById(R.id.hide_confirm_layout);

        iv_hide_password = view.findViewById(R.id.iv_hide_password);
        iv_hide_new_pass = view.findViewById(R.id.iv_hide_new_pass);
        iv_hide_confirm_pass = view.findViewById(R.id.iv_hide_confirm_pass);

        rlt_hide_password.setOnClickListener(this);
        rlt_hide_new_pass.setOnClickListener(this);
        rlt_hide_confirm_pass.setOnClickListener(this);

        view.findViewById(R.id.btn_reset_pass).setOnClickListener(this);
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.clickless).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                Functions.hideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;
            case R.id.btn_reset_pass:
                if (TextUtils.isEmpty(et_old_password.getText().toString())) {
                    et_old_password.setFocusable(true);
                    Functions.dialouge(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.old_cant_empty));
                    return;
                }

                else if (TextUtils.isEmpty(et_new_password.getText().toString())) {
                    et_new_password.setFocusable(true);
                    Functions.dialouge(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.new_cant_empty));
                    return;
                }
                else if (et_new_password.getText().toString().length() < 6) {
                    Functions.dialouge(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.invalid_password));
                    et_new_password.setFocusable(true);
                    return;
                }
                else if (TextUtils.isEmpty(et_confirm_password.getText().toString())) {
                    Functions.dialouge(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.confirm_cant_empty));
                    et_confirm_password.setFocusable(true);
                    return;
                }
                else if (!(et_new_password.getText().toString().equalsIgnoreCase(et_confirm_password.getText().toString()))) {
                    et_new_password.setFocusable(true);
                    Functions.dialouge(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.password_must_match));
                    return;
                }
                else if ((et_new_password.getText().toString().equalsIgnoreCase(et_old_password.getText().toString()))) {
                    et_old_password.setFocusable(true);
                    Functions.dialouge(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.old_new_pass));
                    return;
                } else {
                    CallApi_changeForgotPassword();
                }
                break;
            case R.id.hide_layout:
                if (!check) {
                    iv_hide_password.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_hide));
                    check = true;
                    et_old_password.setTransformationMethod(new PasswordTransformationMethod());
                    et_old_password.setSelection(et_old_password.length());
                } else {

                    iv_hide_password.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_show));
                    check = false;
                    et_old_password.setTransformationMethod(null);
                    et_old_password.setSelection(et_old_password.length());
                }
                break;

            case R.id.hide_confirm_layout:
                if (!check) {
                    iv_hide_confirm_pass.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_hide));
                    check = true;
                    et_confirm_password.setTransformationMethod(new PasswordTransformationMethod());
                    et_confirm_password.setSelection(et_confirm_password.length());
                } else {
                    iv_hide_confirm_pass.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_show));
                    check = false;
                    et_confirm_password.setTransformationMethod(null);
                    et_confirm_password.setSelection(et_confirm_password.length());
                }

                break;
            case R.id.hide_new_layout:
                if (!check) {
                    iv_hide_new_pass.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_hide));
                    check = true;
                    et_new_password.setTransformationMethod(new PasswordTransformationMethod());
                    et_new_password.setSelection(et_new_password.length());
                } else {
                    iv_hide_new_pass.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_show));
                    check = false;
                    et_new_password.setTransformationMethod(null);
                    et_new_password.setSelection(et_new_password.length());
                }
                break;

            default:
                break;
        }
    }


    private void CallApi_changeForgotPassword() {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", Variables.userDetails_pref.getString(Variables.id, ""));
            sendobj.put("old_password", et_old_password.getText().toString());
            sendobj.put("new_password", et_new_password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.show_loader(getContext(), false, false);
        ApiRequest.Call_Api(getContext(), Api_urls.CHANGE_PASSWORD, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("status").equals("1")) {
                            getActivity().onBackPressed();
                        } else {
                            Functions.dialouge(getActivity(), getResources().getString(R.string.alert), "" + respobj.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}