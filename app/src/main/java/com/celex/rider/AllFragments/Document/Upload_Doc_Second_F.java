package com.celex.rider.AllFragments.Document;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.celex.rider.Adapters.DocumentAdapter;
import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.CodeClasses.RootFragment;
import com.celex.rider.DataModels.DocumentModel;
import com.celex.rider.R;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.interfaces.Callback;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class Upload_Doc_Second_F extends RootFragment implements View.OnClickListener {

    View view;
    RecyclerView recyclerView;
    ArrayList<DocumentModel> arrayList = new ArrayList<>();
    DocumentAdapter documentHomeAdapter;
    Button btn_submit_doc;
    private static final int RESULT_LOAD_IMG = 4335;
    Bitmap bitmap = null;
    String extension = null, imageName, doctype, UploadType;
    long size;
    int id;
    Callback callback;
    RelativeLayout rl_upload_doc;
    EditText ed_km;
    Uri selectedImage;

    public Upload_Doc_Second_F(String uploadType, Callback callback) {
        this.UploadType = uploadType;
        this.callback = callback;
    }
    public Upload_Doc_Second_F() {
        // doesn't do anything special
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_upload_documents, container, false);


        initViews();

        return view;
    }


    void initViews() {

        btn_submit_doc = view.findViewById(R.id.btn_submit_doc);
        ed_km = view.findViewById(R.id.et_meter);
        btn_submit_doc.setOnClickListener(this);
        rl_upload_doc = view.findViewById(R.id.rl_upload_doc);
        rl_upload_doc.setOnClickListener(this);
        view.findViewById(R.id.iv_back).setOnClickListener(this::onClick);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_back:
                getFragmentManager().popBackStack();
                break;

            case R.id.rl_upload_doc:

                if (!arrayList.isEmpty() && arrayList.size() > 0) {
                    Functions.dialouge(getActivity(),getResources().getString(R.string.alert),getString(R.string.already_img_selected));
                } else {

           /*         String name = new Date()+ "yyyy-MM-dd-hh-mm-ss";
                    File destination = new File(Environment
                            .getExternalStorageDirectory(), name + ".jpg");

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(destination));
                    startActivityForResult(intent, RESULT_LOAD_IMG);*/
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");

                    selectedImage =  FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".fileprovider", photo);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            selectedImage);
                    startActivityForResult(intent, RESULT_LOAD_IMG);
/*                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);*/
                }
                break;

            case R.id.btn_submit_doc:
                Functions.show_loader(getActivity(), false, false);
                if (bitmap != null && extension != null && arrayList != null && !ed_km.getText().toString().matches("")) {
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CallApi_addUserDoc(getContext(), Functions.Convert_Bitmap_to_base64(Functions.scaleDown(bitmap,800)), doctype, extension, UploadType);
                        }
                    }, 100);

                } else {
                    Functions.dialouge(getActivity(), getResources().getString(R.string.alert), getString(R.string.please_select_image));
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
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }

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

                    documentMode.documnet_name = imageName;

                    arrayList.add(documentMode);

                    recyclerView = view.findViewById(R.id.rc_upload_documents);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setHasFixedSize(true);
                    documentHomeAdapter = new DocumentAdapter(getContext(), arrayList, (postion, Model, view) -> {

                        DocumentModel documentModel = (DocumentModel) Model;
                        switch (view.getId()) {

                            case R.id.delete_btn:
                                arrayList.remove(documentModel);
                                documentHomeAdapter.notifyDataSetChanged();
                                arrayList.clear();
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


    private void CallApi_addUserDoc(Context context, String base64, String doc_type, String extension, String id) {

        JSONObject sendobj = new JSONObject();

        try {

            sendobj.put("km", ed_km.getText());
            sendobj.put("type", id);
            JSONObject file_data = new JSONObject();
     //       file_data.put("file_data", base64 + "");
            sendobj.put("attachment", base64);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Api_urls.URL_ADD_DOCUMENT, sendobj, resp -> {

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
    }

}
