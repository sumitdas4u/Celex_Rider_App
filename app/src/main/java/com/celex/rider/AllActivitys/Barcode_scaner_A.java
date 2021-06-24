package com.celex.rider.AllActivitys;

import androidx.appcompat.app.AppCompatActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;


public class Barcode_scaner_A extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);


        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }


    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        String TAG = "log";
       // Log.v(TAG, rawResult.getText()); // Prints scan results
     //   Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
      String value= new String(rawResult.getText()) ;
        Log.v(TAG, value); // Prints scan results
        String shipment_id = getIntent().getExtras().getString("shipment_id");
        Log.v(TAG, "ship -  "+shipment_id); // Prints the scan format (qrcode, pdf417 etc.)
        String id = getIntent().getExtras().getString("id");
        int position = getIntent().getExtras().getInt("position");
        assert shipment_id != null;
        if(value.toLowerCase().equals(shipment_id.toLowerCase())){


            Intent returnIntent = new Intent();
            returnIntent.putExtra("id",id);
            returnIntent.putExtra("position",position);
            returnIntent.putExtra("shipment_id",shipment_id);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }else
        {
            Toast.makeText(this, "Not Matched!! Try Again ",
                    Toast.LENGTH_LONG).show();
        }


        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }
}