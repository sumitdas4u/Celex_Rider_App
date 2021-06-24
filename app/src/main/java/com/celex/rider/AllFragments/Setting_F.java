package com.celex.rider.AllFragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.celex.rider.AllActivitys.MainActivity;
import com.celex.rider.R;
import com.celex.rider.CodeClasses.DarkModePrefManager;
import com.celex.rider.CodeClasses.RootFragment;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.interfaces.Callback;

import java.util.Locale;

public class Setting_F extends RootFragment implements View.OnClickListener {
    RelativeLayout rl_about;
    TextView language_layout, language_txt, contact_us_email;
    ImageView iv_menu;
    View view;
    ToggleButton theme_btn;
    Dialog dialog;
    boolean isChecked = false;
    String language;

    public Setting_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        language = Variables.userDetails_pref.getString(Variables.setlocale, "en");

        iv_menu = view.findViewById(R.id.iv_menu);
        rl_about = view.findViewById(R.id.rl_contact_us);
     //  language_layout = view.findViewById(R.id.language_layout);
    //   language_txt = view.findViewById(R.id.language_txt);

        view.findViewById(R.id.privacy_layout).setOnClickListener(this::onClick);
        view.findViewById(R.id.terms_layout).setOnClickListener(this::onClick);
        view.findViewById(R.id.rlt_change_password).setOnClickListener(this::onClick);
        rl_about.setOnClickListener(this::onClick);
        iv_menu.setOnClickListener(this::onClick);
//        language_layout.setOnClickListener(this::onClick);

        theme_btn = view.findViewById(R.id.theme_btn);
        contact_us_email = view.findViewById(R.id.contact_us_email);
        contact_us_email.setText(Variables.CONTACT_US_EMAIL);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            theme_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_switch_green));
            isChecked = true;
        } else {
            theme_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_switch_off));
            isChecked = false;
        }
        theme_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecked) {
                    isChecked = false;
                    DarkModePrefManager darkModePrefManager = new DarkModePrefManager(getActivity());
                    darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
                    getActivity().recreate();

                } else {
                    isChecked = true;
                    DarkModePrefManager darkModePrefManager = new DarkModePrefManager(getActivity());
                    darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
                    getActivity().recreate();

                }
            }
        });
/*
        if (language.equals("en")) {
            language_txt.setText(getResources().getString(R.string.english));
        } else {
            language_txt.setText(getResources().getString(R.string.arabic));
        }*/
        return view;
    }


    public void openLink(String url, String title) {
        Web_View webview_f = new Web_View();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", title);
        webview_f.setArguments(bundle);
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_container_layout, webview_f).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rl_contact_us:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + Variables.CONTACT_US_EMAIL));
                startActivity(Intent.createChooser(emailIntent, "Send feedback"));
                break;

            case R.id.rlt_change_password:
                Change_Password_F change_password_f = new Change_Password_F();
                FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                fragmentTransaction.replace(R.id.fragment_container_layout, change_password_f).addToBackStack(null).commit();


                break;
            case R.id.privacy_layout:
                openLink(Variables.PRIVACY_POLICY, getResources().getString(R.string.privacy_policy));

                break;
            case R.id.terms_layout:
                openLink(Variables.TERMS_AND_CONDITION, getResources().getString(R.string.terms_condition));
                break;

            case R.id.iv_menu:
                MainActivity.Open_drawer();
                break;
            /*case R.id.language_layout:
                Select_Language_F selectLanguage_f = new Select_Language_F(new Callback() {
                    @Override
                    public void Responce(String resp) {
                        if (resp != null) {
                            if (!resp.equals(language)) {
                                setLocale(resp);
                            }
                        }
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putString("language_selected", language);
                selectLanguage_f.setArguments(bundle);
                FragmentManager fragmentManager_history = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction_history = fragmentManager_history.beginTransaction();
                fragmentTransaction_history.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                fragmentTransaction_history.replace(R.id.fragment_container_layout, selectLanguage_f).addToBackStack(null).commit();
                break;*/

            default:
                break;
        }
    }


    private void setLocale(String lang) {
        Variables.userDetails_pref.edit().putString(Variables.setlocale, lang).commit();
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = new Configuration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);
        updateActivity();
    }

    public void updateActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onBackPressed() {
        getActivity().overridePendingTransition(R.anim.out_to_right, R.anim.out_to_left);
        return super.onBackPressed();
    }
}
