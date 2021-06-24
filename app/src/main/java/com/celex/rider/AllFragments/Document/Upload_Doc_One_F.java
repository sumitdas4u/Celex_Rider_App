package com.celex.rider.AllFragments.Document;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.celex.rider.CodeClasses.RootFragment;
import com.celex.rider.DataModels.DocumentHomeModel;
import com.celex.rider.R;
import com.celex.rider.interfaces.Callback;

import java.util.ArrayList;


public class Upload_Doc_One_F extends RootFragment implements View.OnClickListener {

    View view;
    RelativeLayout rl_driving_licence, rl_vehicle_insurence, rl_veicleRegistration, rl_NationId;
    Callback callback;
    String response = null;
    ArrayList<DocumentHomeModel> document_list = new ArrayList<>();

    public Upload_Doc_One_F(Callback called) {
        this.callback = called;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_upload_document_initial, container, false);

        initView();

        return view;

    }


    void initView() {

        rl_driving_licence = view.findViewById(R.id.rl_driving_licence);
        rl_vehicle_insurence = view.findViewById(R.id.rl_vehicle_insurence);
        rl_veicleRegistration = view.findViewById(R.id.rl_veicleRegistration);
        rl_NationId = view.findViewById(R.id.rl_NationId);
        rl_driving_licence.setOnClickListener(this);
        rl_vehicle_insurence.setOnClickListener(this);
        rl_veicleRegistration.setOnClickListener(this);
        rl_NationId.setOnClickListener(this);

        view.findViewById(R.id.iv_back).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.rl_driving_licence:

                Upload_Doc_Second_F f = new Upload_Doc_Second_F("rl_driving_licence", resp -> {

                    if (resp != null) {
                        response = resp;
                    }
                });
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.rl_document, f).addToBackStack(null).commit();
                break;


            case R.id.rl_vehicle_insurence:

                Upload_Doc_Second_F f2 = new Upload_Doc_Second_F("rl_vehicle_insurence", resp -> {

                    if (resp != null) {
                        response = resp;
                    }

                });
                FragmentTransaction ft2 = getFragmentManager().beginTransaction();
                ft2.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft2.replace(R.id.rl_document, f2).addToBackStack(null).commit();
                break;


            case R.id.rl_veicleRegistration:

                Upload_Doc_Second_F f3 = new Upload_Doc_Second_F("rl_veicleRegistration", resp -> {

                    if (resp != null) {
                        response = resp;

                    }
                });
                FragmentTransaction ft3 = getFragmentManager().beginTransaction();
                ft3.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft3.replace(R.id.rl_document, f3).addToBackStack(null).commit();
                break;


            case R.id.rl_NationId:

                Upload_Doc_Second_F f4 = new Upload_Doc_Second_F("rl_NationId", resp -> {

                    if (resp != null) {
                        response = resp;

                    }
                });
                FragmentTransaction ft4 = getFragmentManager().beginTransaction();
                ft4.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft4.replace(R.id.rl_document, f4).addToBackStack(null).commit();
                break;

            case R.id.iv_back:
                getActivity().onBackPressed();
                break;

            default:
                return;

        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (callback != null) {
            callback.Responce(response);
            response = null;
        }
    }
}
