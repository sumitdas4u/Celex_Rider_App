package com.celex.rider.AllFragments.Document;

import android.Manifest;
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
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;
import static com.celex.rider.CodeClasses.Functions.roam_update_meta_data_with_location;

public class Transfer_Delivery_F extends RootFragment implements View.OnClickListener {

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
    private DriverModel my_driver_model;
    private int selected_driver_id=-1;

    public Transfer_Delivery_F(String orderid,String upload_type, Callback callback) {
        this.Order_id = orderid;
        this.callback = callback;
        this.upload_type = upload_type;
    }
    public Transfer_Delivery_F(String orderid, Callback callback) {
        this.Order_id = orderid;
        this.callback = callback;

    }
    public Transfer_Delivery_F() {
        // doesn't do anything special
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_select_delivery_boy, container, false);

        initViews();


        CallApi_DeliveryBoyList();
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                try {
                    File file = File.createTempFile(
                            "JPEG_"+System.currentTimeMillis(),
                            ".jpg",
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    );

                    if (file.exists()) {

                        // the file is created
                        // as the function returned true
                        System.out.println("Temp File created: "
                                + file.getAbsolutePath());
                    }

                    else {

                        // display the file cannot be created
                        // as the function returned false
                        System.out.println("Temp File cannot be created: "
                                + file.getAbsolutePath());
                    }

                    Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID+".fileprovider", file);

                    selectedImage = uri;







                    Log.e("TAG", "Uri: " + uri.getPath());
                    Log.e("TAG", "File: " + file.getPath());

                    openCameraLauncher.launch(uri);


                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
                else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");

            // selectedImage =  FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".fileprovider", photo);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
//                                selectedImage);
            startActivityForResult(intent, RESULT_LOAD_IMG);
        }
                break;

            case R.id.btn_submit_transfer:
                Functions.show_loader(getActivity(), false, false);
                if ( selected_driver_id != -1 && !ed_comments.getText().toString().matches("")) {
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CallApi_addUserDoc(getContext(), Order_id);
                        }
                    }, 100);

                } else {
                    Functions.dialouge(getActivity(), getResources().getString(R.string.alert),"please fill the comments or select driver");
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

                CropImage.activity(selectedImage)
                        .setAspectRatio(1, 1)
                        .start(getActivity());
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
    private void CallApi_DeliveryBoyList() {

        JSONObject sendobj = new JSONObject();



        if (delivery_boy_list.isEmpty() ) {
            progressbar.setVisibility(View.VISIBLE);
        }
        ApiRequest.Call_Api(getActivity(), Api_urls.URL_GET_SIMILAR_DELIVERY_BOY, sendobj, resp -> {

            progressbar.setVisibility(View.GONE);

            ArrayList<DriverModel> temp_list = new ArrayList<>();
            try {
                JSONObject respobj = new JSONObject(resp);

                if (respobj.getString("status").equals("1")) {

                    no_data_text.setVisibility(View.GONE);
                    JSONArray respobjJSONArray = respobj.getJSONArray("data");

                    for (int i = 0; i < respobjJSONArray.length(); i++) {
                        JSONObject DeliveryBoy = respobjJSONArray.getJSONObject(i);
                        //   JSONObject UserDocument = respobjJSONArray.getJSONObject(i).getJSONObject("UserDocument");
                        DriverModel documentHomeModel = new DriverModel();
                        documentHomeModel.id = DeliveryBoy.optInt("id");
                        documentHomeModel.name = DeliveryBoy.optString("name");
                        documentHomeModel.mobile = DeliveryBoy.optString("mobile");
                        documentHomeModel.whats_app = DeliveryBoy.optString("whats_app");
                        documentHomeModel.location_lat = DeliveryBoy.getJSONObject("driver_details").optString("location_lat");
                        documentHomeModel.location_long = DeliveryBoy.getJSONObject("driver_details").optString("location_long");
                        documentHomeModel.is_online = DeliveryBoy.getJSONObject("driver_details").getInt("is_online");
                        documentHomeModel.photo = DeliveryBoy.getJSONObject("driver_details").optString("image");


                        temp_list.add(documentHomeModel);
                    }

                    delivery_boy_list.clear();
                    delivery_boy_list.addAll(temp_list);
                    if (delivery_boy_list == null || delivery_boy_list.isEmpty() || delivery_boy_list.equals("")) {
                        no_data_text.setVisibility(View.VISIBLE);
                    }
                    recyclerViewDeliveryBoy = view.findViewById(R.id.rc_delivery_boy);
                    recyclerViewDeliveryBoy.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerViewDeliveryBoy.setHasFixedSize(true);

                    deliveryBoyAdapter = new DeliveryBoyAdapter(getActivity(), delivery_boy_list, (postion, Model, view) -> {
                        switch (view.getId()) {


                            case R.id.rl_holder:
                                my_driver_model = (DriverModel) Model;
                                selected_driver_id = my_driver_model.id;

                                break;

                            case R.id.tv_mobile:
                                my_driver_model = (DriverModel) Model;
                                Intent callIntent = new Intent(Intent.ACTION_CALL);


                                callIntent.setData(Uri.parse("tel:" +  my_driver_model.mobile));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
                                    // TODO: Consider calling
                                    //    Activity#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for Activity#requestPermissions for more details.
                                    return;
                                }

                                startActivity(callIntent);
                                break;

                            default:
                                return;
                        }
                    });

                    deliveryBoyAdapter.notifyDataSetChanged();
                    recyclerViewDeliveryBoy.setAdapter(deliveryBoyAdapter);


                } else {
                    no_data_text.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        });
    }

    private void CallApi_addUserDoc(Context context, String id) {




        JSONObject sendobj = new JSONObject();

        try {

            sendobj.put("comments", ed_comments.getText());
            sendobj.put("order_id", id);
            sendobj.put("driver_id", selected_driver_id);
            JSONArray file_data = new JSONArray();
            for (int i = 0; i < photoArrayList.size(); i++) {
                file_data.put(Functions.Convert_Bitmap_to_base64(Functions.scaleDown(photoArrayList.get(i).image,800)));
            }
            sendobj.put("attachment", file_data);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Api_urls.URL_TRANSFER_DELIVERY, sendobj, resp -> {

            try {
                JSONObject respobj = new JSONObject(resp);

                if (respobj.getString("status").equals("1")) {

                    JSONObject roamSendobj = new JSONObject();
                    SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    String format = s.format(new Date());
                    try {



                        roamSendobj.put("status", "Transferred Order");
                        roamSendobj.put("order_id", id);
                        roamSendobj.put("Transferred to Driver id:", selected_driver_id);
                        roamSendobj.put("comments", ed_comments.getText());
                        roamSendobj.put("time", format);
                        //     roamSendobj.put("response", RiderOrder.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    roam_update_meta_data_with_location(roamSendobj);

                    btn_submit_doc.setClickable(false);
                    btn_submit_doc.setFocusable(false);
                    rl_upload_doc.setClickable(false);
                    rl_upload_doc.setFocusable(false);
                    btn_submit_doc.setClickable(false);
                    btn_submit_doc.setFocusable(false);
                    Functions.cancel_loader();
                    Toast.makeText(getActivity(), "Added Successfully",
                            Toast.LENGTH_LONG).show();
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
        getFragmentManager().popBackStack();
    }

}
