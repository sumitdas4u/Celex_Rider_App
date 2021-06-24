package com.celex.rider.Users_Chat;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.request.DownloadRequest;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.celex.rider.CodeClasses.CircleProgressBarDrawable;
import com.celex.rider.R;
import com.celex.rider.CodeClasses.RootFragment;
import com.celex.rider.CodeClasses.Variables;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;


public class Preview_F extends RootFragment implements View.OnClickListener {


    private View view;
    private Context context;


    private SimpleDraweeView single_image;

    private String image_url,document_url,chat_id;


    private ProgressDialog progressDialog;

    // this is the third party library that will download the image

    private File direct;
    private File fullpath;
    private Boolean isfromchatscreen = false;
    private DownloadRequest prDownloader;
    public Preview_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_preview, container, false);
        context=getContext();



        progressDialog=new ProgressDialog(context,R.style.alert_dialog);
        progressDialog.setMessage("Please Wait");

        isfromchatscreen = getArguments().getBoolean("isfromchat");
        if (isfromchatscreen){
            image_url=getArguments().getString("image_url");
            chat_id=getArguments().getString("chat_id");
        }else {
            progressDialog.show();
            image_url=getArguments().getString("image_url");
            chat_id=getArguments().getString("chat_id");
            document_url=getArguments().getString("document_url");
            Log.d("Rider_log","document_url"+document_url);
        }

        ImageButton close_gallery = view.findViewById(R.id.close_gallery);
        close_gallery.setOnClickListener(this::onClick);



        PRDownloader.initialize(getActivity().getApplicationContext());


        //get the full path of image in database
        fullpath = new File(Variables.folder_freshly_rider +chat_id+".jpg");

        //if the image file is exits then we will hide the save btn
        ImageButton savebtn = view.findViewById(R.id.savebtn);
        if(fullpath.exists() || isfromchatscreen == false){
            savebtn.setVisibility(View.GONE);
        }


        //get the directory inwhich we want to save the image
        direct = new File(Variables.folder_freshly_rider);

        //this code will download the image
        prDownloader= PRDownloader.download(image_url, direct.getPath(), chat_id+".jpg")
                .build();


        savebtn.setOnClickListener(v -> {
                Savepicture();
        });




        single_image=view.findViewById(R.id.single_image);


        // if the image is already save then we will show the image from directory otherwise
        // we will show the image by using picasso
        if(fullpath.exists()){
            Uri uri= Uri.parse(fullpath.getAbsolutePath());
            single_image.setImageURI(uri);
            progressDialog.cancel();
        }
        else {
            if(image_url !=null && !image_url.equals("")) {
                Uri uri = Uri.parse(image_url);
                single_image.setImageURI(uri);
                progressDialog.cancel();
            }else{
                GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance(getResources())
                        .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                        .setProgressBarImage(new CircleProgressBarDrawable())
                        .build();
                single_image.setHierarchy(hierarchy);
                Uri uri = Uri.parse(document_url);
                single_image.setImageURI(uri);
                progressDialog.cancel();
            }

        }

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.close_gallery:
                if (isfromchatscreen)
                    getActivity().onBackPressed();
                else
                    getFragmentManager().popBackStackImmediate();
                break;

            default:
                return;
        }
    }


    // this funtion will save the picture but we have to give tht permision to right the storage
    private void Savepicture(){
        if(Checkstoragepermision()) {

           direct = new File(Environment.getExternalStorageDirectory() + "/DCIM/Binder/");
            progressDialog.show();
            prDownloader.start(new OnDownloadListener() {
                @Override
                public void onDownloadComplete() {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.parse(direct.getPath() + chat_id + ".jpg"));
                    context.sendBroadcast(intent);
                    progressDialog.dismiss();

                    new AlertDialog.Builder(context,R.style.alert_dialog)
                            .setTitle("Image Saved")
                                .setMessage(fullpath.getAbsolutePath())
                                .setNegativeButton("ok", null)
                                .show();

                }

                @Override
                public void onError(Error error) {
                    progressDialog.dismiss();

                }

            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(context, "Click Again", Toast.LENGTH_LONG).show();
        }
    }

    private boolean Checkstoragepermision(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;

            } else {

                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }else {

            return true;
        }
    }



}


