package com.celex.rider.CodeClasses;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.celex.rider.interfaces.Callback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiRequest {
    public static RequestQueue requestQueue;

    public static void Call_Api (final Context context, String url, JSONObject jsonObject,
                                 final Callback callback){

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                response -> {
                        Log.d(Variables.TAG, response.toString());
                        if(callback!=null) {
                            callback.Responce(response.toString());


                    }
                }, error -> {

                if(callback!=null)
                 callback .Responce(error.toString());

        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization","Bearer " +Variables.userDetails_pref.getString(Variables.login_token,"0"));


                return headers;
            }
        };

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);

    }



    public static void Call_Api_GetRequest (final Activity context, final String url, final Callback callback){


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if(callback!=null) {
                            callback.Responce(response.toString());
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {


                if(callback!=null)
                    callback .Responce(error.toString());

            }
        }) ;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.getCache().clear();
        requestQueue.add(jsonObjReq);

    }
}
