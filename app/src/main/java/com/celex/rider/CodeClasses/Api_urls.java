package com.celex.rider.CodeClasses;

public class Api_urls {


    private static final String DOMAIN = "https://app.celexhsrp.in/api/";

    public static final String IMAGE_BASE_URL = "";

    public static final String LOGIN = DOMAIN + "login";
    public static final String VALID_TOKEN = DOMAIN + "user/valid";

    public  static final String SHOW_RIDER_ORDERS = DOMAIN +"driver/orders";

    public static final String URL_ADD_USER_IMAGE = DOMAIN +"addUserImage";

    public  static final  String URL_UPDATE_RIDER_ORDER_STATUS = DOMAIN +"driver/update-order-status";
    public  static final  String URL_RETURN_TO_STATION_ORDER = DOMAIN +"driver/order/return";
    public  static final  String URL_GET_CANCEL_REASON = DOMAIN +"order/reasons";

    public  static final  String SHOW_RIDER_ORDER_DETAILS = DOMAIN +"driver/order-details";

    public  static final  String URL_RIDER_ORDER_RESPONSE = DOMAIN +"driver/order/status";

    public static final  String URL_ADD_SIGNATURE = DOMAIN +"driver/order/addsignature";
    public static final  String URL_SEND_DELIVERY_SMS = DOMAIN +"driver/order/send-delivery-sms";
    public static final  String URL_VERIFY_OTP = DOMAIN +"driver/order/verify-otp";

    public static final  String URL_EDIT_PROFILE = DOMAIN +"editProfile";

    public static final String URL_SHOW_COUNTRIES = DOMAIN +"showCountries";

    public static final String URL_ADD_USER_LAT_LONG = DOMAIN +"addUserLatLong";

    public static final String URL_ADD_DOCUMENT = DOMAIN +"driver/addmeter";
        public static final String URL_TRANSFER_DELIVERY = DOMAIN +"driver/transfer";
        public static final String URL_UPLOAD_IMAGES= DOMAIN +"driver/order/delivered-doc";
        public static final String URL_GET_SIMILAR_DELIVERY_BOY = DOMAIN +"driver/similarboy";

    public static final String URL_GET_METER_READING = DOMAIN +"driver/meter";

   // public static final String URL_ADD_VEHICLE = DOMAIN +"driver/vehicle";

    public static final String URL_SHOW_VEHICLE = DOMAIN +"driver/vehicle";

    public static final String URL_SHOW_VEHICLE_TYPES = DOMAIN +"showVehicleTypes";

    public static final String URL_SHOW_RIDER_ORDER_HISTORY = DOMAIN +"showRiderOrderHistory";

    public static final String URL_SHOW_USER_DOCUMENTS = DOMAIN +"driver/documents";

    public static final String UPDATE_RIDER_STATUS = DOMAIN +"user/online";


    public static final String URL_SHOW_CURRENCY = DOMAIN+"showCurrency";

    public static final String SEND_MESSAGE_NOTIFICATION = DOMAIN+"sendMessageNotification";


    public static final String ADD_DEVICE_DATA = DOMAIN+"user/add-device-data";


    public static final String LOGOUT = DOMAIN+"user/logout";

    public static final String CHANGE_EMAIL_ADDRESS = DOMAIN+"changeEmailAddress";

    public static final String VERIFY_CHANGE_EMAIL_CODE = DOMAIN+"verifyChangeEmailCode";


    public static final String CHANGE_PHONE_NO = DOMAIN+"changePhoneNo";

    public static final String VERIFY_PHONE_NO = DOMAIN+"verifyPhoneNo";


    public static final String CHANGE_PASSWORD = DOMAIN+"user/change-password";

//trips v3
public static final String URL_CREATE_TRIP = DOMAIN +"driver/createtrip";
public static final String URL_STOP_TRIP = DOMAIN +"driver/stoptrip";
}
