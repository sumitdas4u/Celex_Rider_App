package com.celex.rider.DataModels;

import java.util.Map;

public class DriverModel {


    public Map<String, String> last_updated = null;
    public int id = 0;
    public String name = null;
    public String uid = null;
    public String mobile = null;
    public String whats_app= null;
    public String location_lat= null;
    public String location_long= null;
    public String photo= "";
    public int is_online= 0;

    public String current_order_id= null;
    public String current_order_status= null;



    public String current_order_update_at= null;




    public DriverModel(int id, String uid, String name, String mobile, String whats_app, String location_lat, String location_long, int is_online, Map<String, String> last_update, String photo) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.whats_app = whats_app;
        this.location_lat = location_lat;
        this.location_long = location_long;
        this.is_online = is_online;
        this.photo = photo;
        this.uid = uid;
        this.last_updated = last_update;

    }
    public DriverModel(int id, String name, String mobile, String whats_app, String location_lat, String location_long, int is_online,String photo) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.whats_app = whats_app;
        this.location_lat = location_lat;
        this.location_long = location_long;
        this.is_online = is_online;
        this.photo = photo;
    }

    public DriverModel() {

    }
    public String getCurrent_order_update_at() {
        return current_order_update_at;
    }

    public void setCurrent_order_update_at(String current_order_update_at) {
        this.current_order_update_at = current_order_update_at;
    }

    public String getCurrent_order_id() {
        return current_order_id;
    }

    public void setCurrent_order_id(String current_order_id) {
        this.current_order_id = current_order_id;
    }

    public String getCurrent_order_status() {
        return current_order_status;
    }

    public void setCurrent_order_status(String current_order_status) {
        this.current_order_status = current_order_status;
    }
}
