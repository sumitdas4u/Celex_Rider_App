package com.celex.rider.DataModels;

import java.io.Serializable;

public class OrderModel implements Serializable {


    public String getOrder_price() {
        return order_price;
    }

    public void setOrder_price(String order_price) {
        this.order_price = order_price;
    }


    public void setOrder_p_id(String order_p_id) {
        this.order_p_id = order_p_id;
    }

    public String order_p_id;

    public String order_price;
    public String getOrder_product_image() {
        return order_product_image;
    }

    public void setOrder_product_image(String order_product_image) {
        this.order_product_image = order_product_image;
    }

    public String order_product_image;


    public String getOrder_quantity() {
        return order_quantity;
    }

    public void setOrder_quantity(String order_quantity) {
        this.order_quantity = order_quantity;
    }

    public String getOrder_product_title() {
        return order_product_title;
    }

    public void setOrder_product_title(String order_product_title) {
        this.order_product_title = order_product_title;
    }

    public String order_quantity;
    public String order_product_title;

    public String getOrder_product_size() {
        return order_product_size;
    }

    public void setOrder_product_size(String order_product_size) {
        this.order_product_size = order_product_size;
    }

    public String order_product_size;


    public String getReg_No() {
        return reg_no;
    }

    public void setReg_No(String reg_no) {
        this.reg_no = reg_no;
    }

    public String reg_no;

    public String getChassis_No() {
        return chassis_no;
    }

    public void setChassis_No(String chassis_no) {
        this.chassis_no = chassis_no;
    }

    public String chassis_no;

    public String getEngine_No() {
        return engine_no;
    }

    public void setEngine_No(String engine_no) {
        this.engine_no = engine_no;
    }

    public String engine_no;

}
