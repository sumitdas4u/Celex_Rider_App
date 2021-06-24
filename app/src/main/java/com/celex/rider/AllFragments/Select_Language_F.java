package com.celex.rider.AllFragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celex.rider.CodeClasses.RootFragment;
import com.celex.rider.R;
import com.celex.rider.interfaces.Callback;

public class Select_Language_F extends RootFragment implements View.OnClickListener {

    View view;
    Callback callback;
    TextView english_tv, arabic_tv;
    ImageView iv_tick, iv_tick_arabic;

    public Select_Language_F() {
        // Required empty public constructor
    }

    public Select_Language_F(Callback callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_language, container, false);

        Bundle bundle = getArguments();


        iv_tick = view.findViewById(R.id.iv_tick);
        iv_tick_arabic = view.findViewById(R.id.iv_tick_arabic);
        english_tv = view.findViewById(R.id.english_tv);
        arabic_tv = view.findViewById(R.id.arabic_tv);
        english_tv.setOnClickListener(this);
        arabic_tv.setOnClickListener(this);

        view.findViewById(R.id.iv_back).setOnClickListener(this);


        if (bundle != null) {
            if (bundle.getString("language_selected").equals("en")) {
                iv_tick.setVisibility(View.VISIBLE);
                iv_tick_arabic.setVisibility(View.GONE);
            } else {
                iv_tick.setVisibility(View.GONE);
                iv_tick_arabic.setVisibility(View.VISIBLE);
            }
        }
        return view;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back:
                getActivity().onBackPressed();
                break;

            case R.id.english_tv:
                pass_data_back("en");
                break;
            case R.id.arabic_tv:
                pass_data_back("ar");
                break;

            default:
                break;
        }
    }

    private void pass_data_back(String ar) {
        if (callback != null) {
            callback.Responce(ar);
            getActivity().onBackPressed();
        }
    }

}