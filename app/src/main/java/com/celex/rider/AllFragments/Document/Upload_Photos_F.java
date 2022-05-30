package com.celex.rider.AllFragments.Document;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.celex.rider.Adapters.DeliveryBoyAdapter;
import com.celex.rider.Adapters.DocumentAdapter;
import com.celex.rider.BuildConfig;
import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.CodeClasses.RootFragment;
import com.celex.rider.DataModels.DocumentModel;
import com.celex.rider.DataModels.DriverModel;
import com.celex.rider.R;
import com.celex.rider.interfaces.Callback;
import com.github.drjacky.imagepicker.ImagePicker;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;

public class Upload_Photos_F extends RootFragment implements View.OnClickListener {

    View view;
    RecyclerView recyclerView,recyclerViewDeliveryBoy;
    ArrayList<DocumentModel> photoArrayList = new ArrayList<>();
    ArrayList<DriverModel> delivery_boy_list = new ArrayList<>();
    DocumentAdapter documentHomeAdapter;
    DeliveryBoyAdapter deliveryBoyAdapter;
    Button btn_submit_doc;
    private static final int RESULT_LOAD_IMG = 4335;
    Bitmap bitmap = null;
    String extension = null, imageName, doctype, Order_id, upload_type;
    long size;
    int id;
    Callback callback;
    RelativeLayout rl_upload_doc;
    EditText ed_comments;
    Uri selectedImage;
    TextView no_data_text;
    ProgressBar progressbar;
    ActivityResultLauncher<Intent> photoPickActivityResultLauncher;
    public Upload_Photos_F(String orderid, String upload_type, Callback callback) {
        this.Order_id = orderid;
        this.callback = callback;
        this.upload_type = upload_type;
    }
    public Upload_Photos_F(String orderid, Callback callback) {
        this.Order_id = orderid;
        this.callback = callback;

    }
    public Upload_Photos_F() {
        // doesn't do anything special
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_upload_photos,container, false);

        initViews();


        ActivityResultLauncher<Intent> launcher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                    if (result.getResultCode() == RESULT_OK) {
                        assert result.getData() != null;
                        Uri resultUri = result.getData().getData();


                        InputStream imageStream = null;
                        try {
                            assert resultUri != null;
                            imageStream = getActivity().getContentResolver().openInputStream(resultUri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        bitmap = BitmapFactory.decodeStream(imageStream);

                        extension = Objects.requireNonNull(resultUri.getPath()).replaceAll("^.*\\.", "");
                        File f = new File(resultUri.getPath());
                        imageName = f.getName();
                        DocumentModel documentMode = new DocumentModel();
                        documentMode.image=bitmap;
                        documentMode.documnet_name = imageName;

                        photoArrayList.add(documentMode);

                        recyclerView = view.findViewById(R.id.rc_upload_images);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setHasFixedSize(true);
                        documentHomeAdapter = new DocumentAdapter(getContext(), photoArrayList, (postion, Model, view) -> {

                            DocumentModel documentModel = (DocumentModel) Model;
                            if (view.getId() == R.id.delete_btn) {
                                photoArrayList.remove(postion);
                                documentHomeAdapter.notifyDataSetChanged();
                                // arrayList.clear();
                                extension = "";
                                bitmap = null;
                                btn_submit_doc.setClickable(false);
                                btn_submit_doc.setFocusable(false);
                            }

                        });
                        recyclerView.setAdapter(documentHomeAdapter);
                        btn_submit_doc.setClickable(true);
                        btn_submit_doc.setFocusable(true);

                    } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                        // Use ImagePicker.Companion.getError(result.getData()) to show an error
                    }
                });

        photoPickActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Log.e("pick", selectedImage.toString());

                        CropImage.activity(selectedImage)
                                .setAspectRatio(1, 1)
                                .start(getActivity());
                    }
                });

        return view;
    }


    void initViews() {
        progressbar = view.findViewById(R.id.progressbar);
        no_data_text = view.findViewById(R.id.no_data_txt);
        btn_submit_doc = view.findViewById(R.id.btn_submit_transfer);
        ed_comments = view.findViewById(R.id.et_comments);
        btn_submit_doc.setOnClickListener(this);
        rl_upload_doc = view.findViewById(R.id.rl_upload_photos);
        rl_upload_doc.setOnClickListener(this);
        view.findViewById(R.id.iv_back).setOnClickListener(this::onClick);

    }


    ActivityResultLauncher<Uri> openCameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    Log.e("TAG", "Camera result: "+result);
                    if(result){
                        CropImage.activity(selectedImage)
                                .setAspectRatio(1, 1)
                                .start(getActivity());

//                        InputStream imageStream = null;
//                        try {
//                            assert selectedImage != null;
//                            imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                        bitmap = BitmapFactory.decodeStream(imageStream);
//                        extension = Objects.requireNonNull(selectedImage.getPath()).replaceAll("^.*\\.", "");
//                        File f = new File(selectedImage.getPath());
//                        imageName = f.getName();
//                        DocumentModel documentMode = new DocumentModel();
//                        documentMode.image=bitmap;
//                        documentMode.documnet_name = imageName;
//
//                        photoArrayList.add(documentMode);
//
//                        recyclerView = view.findViewById(R.id.rc_upload_images);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                        recyclerView.setHasFixedSize(true);
//                        documentHomeAdapter = new DocumentAdapter(getContext(), photoArrayList, (postion, Model, view) -> {
//
//                            DocumentModel documentModel = (DocumentModel) Model;
////                            if (view.getId() == R.id.delete_btn) {
////                                photoArrayList.remove(postion);
////                                documentHomeAdapter.notifyDataSetChanged();
////                                // arrayList.clear();
////                                extension = "";
////                                bitmap = null;
////                                btn_submit_doc.setClickable(false);
////                                btn_submit_doc.setFocusable(false);
////                            }
//
//                        });
//                        recyclerView.setAdapter(documentHomeAdapter);
//                        btn_submit_doc.setClickable(true);
//                        btn_submit_doc.setFocusable(true);
//

                    }
                }
            }
    );


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_back:
                getFragmentManager().popBackStack();
                break;

            case R.id.rl_upload_photos:
                Toast.makeText(getActivity(), "click Successfully",
                        Toast.LENGTH_LONG).show();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    try {
                        File file = File.createTempFile(
                                "JPEG_" + System.currentTimeMillis(),
                                ".jpg",
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        );

                        Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file);

                        selectedImage = uri;

                        Log.e("TAG", "Uri: " + uri.getPath());
                        Log.e("TAG", "File: " + file.getPath());

                        openCameraLauncher.launch(uri);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");

                    startActivityForResult(intent, RESULT_LOAD_IMG);
                }



//                ImagePicker.Companion.with(getActivity())
//                        .crop()	    			//Crop image(Optional), Check Customization for more option
//                       // .cropOval()	    		//Allow dimmed layer to have a circle inside
//                        .cropFreeStyle()	    //Let the user to resize crop bounds
//                        .cameraOnly()          //We have to define what image provider we want to use
//                        .maxResultSize(1080, 1080,true)	//Final image resolution will be less than 1080 x 1080(Optional)
//                        .createIntent();

             /*   File root = getContext().getCacheDir(); // consider using getExternalFilesDir(Environment.DIRECTORY_PICTURES); you need to check the file_paths.xml
                File capturedPhoto = new File(root, "some_photo.jpeg");
                *//*if(!photoFile.exists()) {
                    photoFile.mkdirs();
                }*//*
                Uri photoURI = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", capturedPhoto);

           *//*         String name = new Date()+ "yyyy-MM-dd-hh-mm-ss";
                    File destination = new File(Environment
                            .getExternalStorageDirectory(), name + ".jpg");

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(destination));
                    startActivityForResult(intent, RESULT_LOAD_IMG);*//*
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //File photo = new File(Environment.getExternalStorageDirectory(),  photoURI);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);

             *//*       selectedImage =  FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".fileprovider", photo);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            selectedImage);*//*

                photoPickActivityResultLauncher.launch(intent);*/

                break;

            case R.id.btn_submit_transfer:
                Functions.show_loader(getActivity(), false, false);
                if (  photoArrayList.size()>0) {
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CallApi_addUserDoc(getContext(), Order_id);
                        }
                    }, 100);

                } else {
                    Functions.dialouge(getActivity(), getResources().getString(R.string.alert),"please select at least single image ");
                }
                break;

            default:
                return;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK ) {
            if (requestCode == RESULT_LOAD_IMG) {

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                bitmap = imageBitmap;

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), imageBitmap, "Pic", null);

                selectedImage = Uri.parse(path);


                extension = Objects.requireNonNull(selectedImage.getPath()).replaceAll("^.*\\.", "");
                //File f = new File(selectedImage.getPath());
                imageName = "Pic";
                DocumentModel documentMode = new DocumentModel();
                documentMode.image=bitmap;
                documentMode.documnet_name = imageName;

                photoArrayList.add(documentMode);

                recyclerView = view.findViewById(R.id.rc_upload_images);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);
                documentHomeAdapter = new DocumentAdapter(getContext(), photoArrayList, (postion, Model, view) -> {

                    DocumentModel documentModel = (DocumentModel) Model;
                    switch (view.getId()) {

                        case R.id.delete_btn:
                            photoArrayList.remove(postion);
                            documentHomeAdapter.notifyDataSetChanged();
                            // arrayList.clear();
                            extension = "";
                            bitmap = null;
                            btn_submit_doc.setClickable(false);
                            btn_submit_doc.setFocusable(false);

                            break;
                        default:
                            return;
                    }

                });
                recyclerView.setAdapter(documentHomeAdapter);
                btn_submit_doc.setClickable(true);
                btn_submit_doc.setFocusable(true);
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);


                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    InputStream imageStream = null;
                    try {
                        imageStream = getActivity().getContentResolver().openInputStream(resultUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    bitmap = BitmapFactory.decodeStream(imageStream);

                    extension = Objects.requireNonNull(resultUri.getPath()).replaceAll("^.*\\.", "");
                    File f = new File(resultUri.getPath());
                    imageName = f.getName();
                    DocumentModel documentMode = new DocumentModel();
                    documentMode.image=bitmap;
                    documentMode.documnet_name = imageName;

                    photoArrayList.add(documentMode);

                    recyclerView = view.findViewById(R.id.rc_upload_images);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setHasFixedSize(true);
                    documentHomeAdapter = new DocumentAdapter(getContext(), photoArrayList, (postion, Model, view) -> {

                        DocumentModel documentModel = (DocumentModel) Model;
                        switch (view.getId()) {

                            case R.id.delete_btn:
                                photoArrayList.remove(postion);
                                documentHomeAdapter.notifyDataSetChanged();
                               // arrayList.clear();
                                extension = "";
                                bitmap = null;
                                btn_submit_doc.setClickable(false);
                                btn_submit_doc.setFocusable(false);

                                break;
                            default:
                                return;
                        }

                    });
                    recyclerView.setAdapter(documentHomeAdapter);
                    btn_submit_doc.setClickable(true);
                    btn_submit_doc.setFocusable(true);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }
    }

    private void CallApi_addUserDoc(Context context, String id) {




        JSONObject sendobj = new JSONObject();

        try {

            sendobj.put("comments", ed_comments.getText());
            sendobj.put("order_id", id);

            JSONArray file_data = new JSONArray();
            for (int i = 0; i < photoArrayList.size(); i++) {
                file_data.put(Functions.Convert_Bitmap_to_base64(Functions.scaleDown(photoArrayList.get(i).image,800)));
            }
            sendobj.put("attachment", file_data);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Api_urls.URL_UPLOAD_IMAGES, sendobj, resp -> {

            try {
                JSONObject respobj = new JSONObject(resp);

                if (respobj.getString("status").equals("1")) {

                    btn_submit_doc.setClickable(false);
                    btn_submit_doc.setFocusable(false);
                    rl_upload_doc.setClickable(false);
                    rl_upload_doc.setFocusable(false);
                    btn_submit_doc.setClickable(false);
                    btn_submit_doc.setFocusable(false);
                    Functions.cancel_loader();
                    Toast.makeText(getActivity(), "Added Successfully",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(context, Upload_Photos_F.class);
                    intent.putExtra("upload",1);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                    pass_data_back();

                }

            } catch (JSONException e) {
                Functions.cancel_loader();
                e.printStackTrace();
                Toast.makeText(getActivity(), "some thing went wrong",
                        Toast.LENGTH_LONG).show();
            }

        });

    }

    public void pass_data_back() {
        if (callback != null) {
            callback.Responce("done");
        }
        if ( getActivity().getSupportFragmentManager() != null && ! getActivity().getSupportFragmentManager().isStateSaved()) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
       // getActivity().getSupportFragmentManager().popBackStack();
    }

}
