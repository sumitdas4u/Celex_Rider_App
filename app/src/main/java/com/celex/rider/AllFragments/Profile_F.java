package com.celex.rider.AllFragments;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.celex.rider.AllActivitys.MainActivity;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.DataModels.SelectCountryModel;
import com.celex.rider.R;
import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.interfaces.oncallback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.ybs.countrypicker.Country;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class Profile_F extends Fragment implements View.OnClickListener {


    private static final int RESULT_LOAD_IMG = 456;
    private TextView tv_country_profile, tv_memeber_date, change_no_txt, change_email_txt, change_country, text_offline;
    private EditText et_phone_no_profile, et_email_profile, et_first_name_profile;
    ImageView iv_user_profile;
    String country_id, f_name, l_name, online_status, user_id;
    private int GALLERY = 1;
    RelativeLayout image_rlt;
    oncallback fragmentCallback;
    ToggleButton online_status_btn;
    View view;
    Bundle bundle;
    ArrayList<SelectCountryModel> ids;
    boolean isChecked = false;
    List<Country> listCountry = new ArrayList<>();

    public Profile_F(oncallback oncallback) {
        this.fragmentCallback = oncallback;
    }

    public Profile_F() {
        //empty constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_profile, container, false);


        METHOD_init_views();
        bundle = new Bundle();
        //l_name = (Variables.userDetails_pref.getString(Variables.lname, ""));
        f_name = (Variables.userDetails_pref.getString(Variables.name, ""));
        user_id = Variables.userDetails_pref.getString(Variables.id, "");
        String city = (Variables.userDetails_pref.getString(Variables.city, ""));
        String memeber_date = (Variables.userDetails_pref.getString(Variables.memeber_date, ""));
        online_status = Variables.userDetails_pref.getString(Variables.RIDER_ONLINE_STATUS, "0");

        tv_memeber_date.setText(memeber_date);
        String image_url = Variables.userDetails_pref.getString(Variables.image, "");

        if (online_status.equalsIgnoreCase("1")) {
            online_status_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_switch_green));
            text_offline.setText(getResources().getString(R.string.online));
            isChecked = true;
        } else {
            online_status_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_switch_off));
            text_offline.setText(getResources().getString(R.string.offline_txt));
            isChecked = false;
        }

        if (image_url != null && !image_url.equals("")) {
            Picasso.get().load(Api_urls.IMAGE_BASE_URL + image_url)
                    .placeholder(R.drawable.ic_user_icon)
                    .into(iv_user_profile);
        }

        et_first_name_profile.setText(f_name );
        if (city == null || (city.equals("") || city.equals("null"))) {
            tv_country_profile.setText("Select Country");
        } else {
            tv_country_profile.setText(city);
        }

        et_phone_no_profile.setInputType(InputType.TYPE_NULL);
        et_email_profile.setInputType(InputType.TYPE_NULL);
        et_first_name_profile.setInputType(InputType.TYPE_NULL);
        tv_country_profile.setClickable(false);

        updateData();

        return view;
    }

    public void updateData() {
        String phone = (Variables.userDetails_pref.getString(Variables.phone, ""));
        String email = (Variables.userDetails_pref.getString(Variables.uid, ""));
        et_email_profile.setText(email);
        et_phone_no_profile.setText(phone);
    }

    private void METHOD_init_views() {
        et_first_name_profile = view.findViewById(R.id.et_first_name_profile);
        tv_country_profile = view.findViewById(R.id.tv_country_profile);
        et_email_profile = view.findViewById(R.id.et_email_profile);
        tv_memeber_date = view.findViewById(R.id.tv_memeber_date);
        et_phone_no_profile = view.findViewById(R.id.et_phone_no_profile);
        iv_user_profile = view.findViewById(R.id.iv_user_profile);
        online_status_btn = view.findViewById(R.id.online_status_btn);
        change_email_txt = view.findViewById(R.id.change_email_txt);
        // text_offline = view.findViewById(R.id.text_offline);
        online_status_btn.setOnClickListener(this);
        change_email_txt.setOnClickListener(this);
        view.findViewById(R.id.iv_menu).setOnClickListener(this);
        view.findViewById(R.id.change_country).setOnClickListener(this);

        change_no_txt = view.findViewById(R.id.change_no_txt);
        image_rlt = view.findViewById(R.id.image_rlt);
        text_offline = view.findViewById(R.id.text_offline);

        image_rlt.setOnClickListener(this);
        change_no_txt.setOnClickListener(this);
        tv_country_profile.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                MainActivity.Open_drawer();
                break;


            case R.id.image_rlt:

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY);

                break;
            case R.id.online_status_btn:
                if (isChecked) {
                    getOnlineStatus("0");
                    online_status_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_switch_off));
                    text_offline.setText(getResources().getString(R.string.online));
                } else {
                    getOnlineStatus("1");
                    online_status_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_switch_green));
                    text_offline.setText(getResources().getString(R.string.online));
                }
                break;


         /*   case R.id.btn_save_changes:

                CallApi_editProfile(getActivity());
                break;*/

            case R.id.change_no_txt:

                Change_phone_no change_phone_no = new Change_phone_no();
                FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
                beginTransaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                beginTransaction.replace(R.id.fragment_container_layout, change_phone_no).addToBackStack(null).commit();

                break;

            case R.id.change_email_txt:

                Email_change_f email_change_f = new Email_change_f();
                FragmentManager fragmentManager_email = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction_email = fragmentManager_email.beginTransaction();
                fragmentTransaction_email.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                fragmentTransaction_email.replace(R.id.fragment_container_layout, email_change_f).addToBackStack(null).commit();

                break;


            case R.id.change_country:

                CallApi_showCountries();
                break;

            default:
                break;
        }
    }

    public void Opencountry() {
        final CountryPicker picker = CountryPicker.newInstance("Select Country");
        if(picker != null){
            picker.dismiss();
        }
        picker.setCountriesList(listCountry);
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                tv_country_profile.setText(name);
                for (int i = 0; i < listCountry.size(); i++) {
                    if (listCountry.get(i).getCode().equals(code)) {
                        String list = listCountry.get(i).getCode();
                        for (int j = 0; j < ids.size(); j++) {
                            if (ids.get(i).iso.equals(list)) {
                                country_id = ids.get(i).country_id;
                            }
                        }
                        break;
                    }
                }

                picker.dismiss();
                CallApi_editProfile(getActivity());
            }
        });

        picker.show(getFragmentManager(), "Select Country");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    CropImage.activity(selectedImage)
                            .setAspectRatio(1, 1)
                            .start(getActivity());
                }
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    iv_user_profile.setImageURI(resultUri);
                    InputStream imageStream = null;
                    try {
                        imageStream = getActivity().getContentResolver().openInputStream(resultUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), null, true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    String image_base64 = Bitmap_to_base64(getActivity(), rotatedBitmap);
                    Functions.show_loader(getActivity(), false, false);
                    CallApi_addUserImage(getActivity(), image_base64);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }
    }

    public static String Bitmap_to_base64(Activity activity, Bitmap imagebitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagebitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteArray = baos.toByteArray();
        String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return base64;
    }

    private void CallApi_addUserImage(Context context, String base64) {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", user_id);
            JSONObject file_data = new JSONObject();
            file_data.put("file_data", base64);
            sendobj.put("image", file_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.show_loader(context, false, false);
        ApiRequest.Call_Api(context, Api_urls.URL_ADD_USER_IMAGE, sendobj, resp -> {

            Functions.cancel_loader();


            try {
                JSONObject respobj = new JSONObject(resp);

                if (respobj.getString("code").equals("200")) {

                    JSONObject userobj = respobj.getJSONObject("msg").getJSONObject("User");
                    String imageurl = userobj.optString("image");
                    Variables.userDetails_pref.edit().putString(Variables.image, imageurl).apply();
                    if (fragmentCallback != null) {
                        bundle = new Bundle();
                        bundle.putString("image", imageurl);
                        fragmentCallback.onResponce(bundle);
                    }
                    Functions.cancel_loader();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Functions.cancel_loader();

            }
        });

    }


    private void CallApi_editProfile(Context context) {


        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", Variables.userDetails_pref.getString(Variables.id, ""));
            sendobj.put("first_name", f_name + "");
            sendobj.put("last_name", l_name + "");
            sendobj.put("country_id", (country_id + ""));
            sendobj.put("phone", (et_phone_no_profile.getText().toString()) + "");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Functions.show_loader(context, false, false);
        ApiRequest.Call_Api(context, Api_urls.URL_EDIT_PROFILE, sendobj, resp -> {
           Functions.cancel_loader();
            JSONObject jsonResponse = null;

            try {
                jsonResponse = new JSONObject(resp);
                int code_id = Integer.parseInt(jsonResponse.optString("code"));
                if (code_id == 200) {
                    JSONObject json = new JSONObject(jsonResponse.toString());
                    JSONObject msgObj = json.getJSONObject("msg");
                    JSONObject json1 = new JSONObject(msgObj.toString());
                    JSONObject user_obj = json1.getJSONObject("User");
                    JSONObject country_obj = json1.getJSONObject("Country");
                    String user_id = user_obj.optString("id");
                    String first_name = user_obj.optString("first_name");
                    String last_name = user_obj.optString("last_name");
                    String email = user_obj.optString("email");
                    String imageurl = user_obj.optString("image");
                    String phone = user_obj.optString("phone");

                    String country = country_obj.optString("country");
                    String country_code = country_obj.getString("country_code");
                    Variables.userDetails_pref.edit().putString(Variables.city, country).apply();
                    Variables.userDetails_pref.edit().putString(Variables.id, user_id).apply();
                    Variables.userDetails_pref.edit().putString(Variables.name, first_name).apply();
               //     Variables.userDetails_pref.edit().putString(Variables.lname, last_name).apply();
                    Variables.userDetails_pref.edit().putString(Variables.email, email).apply();
                    Variables.userDetails_pref.edit().putString(Variables.image, imageurl).apply();
                    Variables.userDetails_pref.edit().putString(Variables.phone, phone).apply();
                    Variables.userDetails_pref.edit().putString(Variables.image, imageurl).apply();
                    Variables.userDetails_pref.edit().putString(Variables.country_code, country_code).apply();

                    bundle.putString("name", first_name + " " + last_name);
                    bundle.putString("fromWhere", "profiledit");
                    bundle.putString("image", imageurl);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

    }

    public void pass_data_back() {
        if (fragmentCallback != null) {
            fragmentCallback.onResponce(bundle);
            getActivity().onBackPressed();
        }
    }

    //this will get Online Status of the rider
    public void getOnlineStatus(String status) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", user_id);
            jsonObject.put("online", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(getActivity(), Api_urls.UPDATE_RIDER_STATUS, jsonObject, resp -> {

            try {
                JSONObject jsonObject1 = new JSONObject(resp);

                int code = Integer.parseInt(jsonObject1.optString("status"));

                if (code == 1) {


                    JSONObject UserInfo = jsonObject1.optJSONObject("data");

                    String online = UserInfo.optString("online");

                    SharedPreferences.Editor editor = Variables.userDetails_pref.edit();

                    if (online.equals("1")) {
                        text_offline.setText(getResources().getString(R.string.online));
                        online_status_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_switch_green));
                        editor.putString(Variables.RIDER_ONLINE_STATUS, "1");
                        editor.commit();
                        isChecked = true;

                    } else {
                        text_offline.setText(getResources().getString(R.string.offline_txt));
                        online_status_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_switch_off));
                        editor.putString(Variables.RIDER_ONLINE_STATUS, "0");
                        editor.commit();
                        isChecked = false;
                    }
                    bundle = new Bundle();
                    bundle.putString("fromWhere", "status");
                    fragmentCallback.onResponce(bundle);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        });


    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    private void CallApi_showCountries() {

        JSONObject sendobj = new JSONObject();

        ApiRequest.Call_Api(getContext(), Api_urls.URL_SHOW_COUNTRIES, sendobj, resp -> {

            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);

                    if (respobj.getString("code").equals("200")) {

                        listCountry.clear();
                        ids = new ArrayList<>();
                        JSONArray arrayOfAllCountries = respobj.getJSONArray("msg");


                        for (int i = 0; i < arrayOfAllCountries.length(); i++) {

                            JSONObject Order = arrayOfAllCountries.getJSONObject(i).getJSONObject("Country");
                            SelectCountryModel my_orders_model_class = new SelectCountryModel();

                            my_orders_model_class.currency_code = Order.getString("currency_code");
                            my_orders_model_class.country_id = Order.getString("id");
                            my_orders_model_class.country_name = Order.getString("name");
                            my_orders_model_class.country_code = Order.getString("country_code");
                            my_orders_model_class.iso = Order.getString("iso");
                            ids.add(my_orders_model_class);
                            Country c = new Country();
                            c.setDialCode("+" + Order.optString("country_code"));
                            c.setCode(Order.optString("iso"));
                            c.loadFlagByCode(getActivity());
                            listCountry.add(c);
                        }
                        Opencountry();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }


            }
        });

    }
}
