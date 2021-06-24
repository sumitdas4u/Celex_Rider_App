package com.celex.rider.DataModels;

import androidx.recyclerview.widget.RecyclerView;

public class MeterReadingModel   {

    public String date_reading;
    public String start_km;
    public String end_km;
    public String start_img;
    public String end_img;
    public String total;




    public MeterReadingModel(String date_reading, String start_km, String end_km, String start_img, String end_img, String total) {
        this.date_reading = date_reading;
        this.start_km = start_km;
        this.end_km = end_km;
        this.start_img = start_img;
        this.end_img = end_img;
        this.total = total;
    }

    public MeterReadingModel() {

    }






}
