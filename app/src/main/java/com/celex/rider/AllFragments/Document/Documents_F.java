package com.celex.rider.AllFragments.Document;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.celex.rider.Adapters.DocumentHomeAdapter;
import com.celex.rider.AllActivitys.MainActivity;
import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.RootFragment;
import com.celex.rider.DataModels.DocumentHomeModel;
import com.celex.rider.R;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.Users_Chat.Preview_F;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Documents_F extends RootFragment implements View.OnClickListener {

    RecyclerView recyclerView;
    DocumentHomeAdapter documentHomeAdapter;
    ArrayList<DocumentHomeModel> document_list = new ArrayList<>();
    ImageView iv_upload;
    Upload_Doc_One_F upload_doc_f;
    SwipeRefreshLayout swipreferesh_layout;
    View view;
    TextView no_data_text;
    ProgressBar progressbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_documents, container, false);

        initViews();
        CallApi_Documents();
        return view;
    }

    void initViews() {

        iv_upload = view.findViewById(R.id.iv_upload);
        recyclerView = view.findViewById(R.id.rc_documents);
        progressbar = view.findViewById(R.id.progressbar);
        no_data_text = view.findViewById(R.id.no_data_txt);

        swipreferesh_layout = view.findViewById(R.id.swipreferesh_layout);

        swipreferesh_layout.setOnRefreshListener(() -> {

            CallApi_Documents();

        });

        view.findViewById(R.id.iv_menu).setOnClickListener(this);
        iv_upload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_upload:

                upload_doc_f = new Upload_Doc_One_F(resp -> {
                    if (resp != null && resp.equals("done")) {
                        swipreferesh_layout.setRefreshing(true);
                        CallApi_Documents();
                    }

                });
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                fragmentTransaction.replace(R.id.rl_document, upload_doc_f, "tag");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                break;

            case R.id.iv_menu:

                MainActivity.Open_drawer();

                break;

            default:
                return;
        }

    }

    private final String[] okFileExtensions = new String[] {
            "jpg",
            "png",
            "gif",
            "jpeg"
    };
    public boolean accept(String file) {
        for (String extension: okFileExtensions) {
            if (file.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    ////fetch all the documents url from database
    private void CallApi_Documents() {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", Variables.userDetails_pref.getString(Variables.id, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (document_list.isEmpty() && !swipreferesh_layout.isRefreshing()) {
            progressbar.setVisibility(View.VISIBLE);
        }
        ApiRequest.Call_Api(getActivity(), Api_urls.URL_SHOW_USER_DOCUMENTS, sendobj, resp -> {

            progressbar.setVisibility(View.GONE);
            swipreferesh_layout.setRefreshing(false);
            ArrayList<DocumentHomeModel> temp_list = new ArrayList<>();
            try {
                JSONObject respobj = new JSONObject(resp);

                if (respobj.getString("status").equals("1")) {

                    no_data_text.setVisibility(View.GONE);
                    JSONArray respobjJSONArray = respobj.getJSONObject("data").getJSONArray("documents");

                    for (int i = 0; i < respobjJSONArray.length(); i++) {
                        JSONObject UserDocument = respobjJSONArray.getJSONObject(i);
                     //   JSONObject UserDocument = respobjJSONArray.getJSONObject(i).getJSONObject("UserDocument");
                        DocumentHomeModel documentHomeModel = new DocumentHomeModel();
                        documentHomeModel.file_name = UserDocument.optString("attachment");
                        documentHomeModel.type = UserDocument.optString("type");
                        documentHomeModel.status = UserDocument.optString("status");
                        documentHomeModel.extension = UserDocument.optString("file_type");
                        temp_list.add(documentHomeModel);
                    }

                    document_list.clear();
                    document_list.addAll(temp_list);
                    if (document_list == null || document_list.isEmpty() || document_list.equals("")) {
                        no_data_text.setVisibility(View.VISIBLE);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setHasFixedSize(true);

                    documentHomeAdapter = new DocumentHomeAdapter(getActivity(), document_list, (postion, Model, view) -> {
                        switch (view.getId()) {
                            case R.id.main_div:

                                if(!accept(document_list.get(postion).extension)){
                                    try {

                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(document_list.get(postion).file_name));
                                        startActivity(browserIntent);

                                    } catch (Exception e) {
                                        e.printStackTrace();

                                    }
                                }else{
                                    Preview_F see_image_f = new Preview_F();
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    Bundle args = new Bundle();
                                    args.putSerializable("document_url", document_list.get(postion).file_name);
                                    args.putSerializable("isfromchat", false);
                                    see_image_f.setArguments(args);
                                    transaction.addToBackStack(null);
                                    transaction.replace(R.id.rl_document, see_image_f).commit();
                                }
                                break;
                            default:
                                return;
                        }


                    });
                    documentHomeAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(documentHomeAdapter);
                } else {
                    no_data_text.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        });
    }
}