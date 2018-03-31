package com.ucarry.developer.android.Utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

import java.util.HashMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ucarry.developer.android.Model.BankDetail;
import com.ucarry.developer.android.activity.LoginActivity;
import okhttp3.Headers;
import com.yourapp.developer.karrierbay.R;


/**
 * Created by nirmal.ku on 11/7/2016.
 */
public class SessionManager {
    public SharedPreferences sharedPreferences;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Sharedpref file name
    private static final String PREF_NAME = "KarrierBay";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    // User Address
    public static final String KEY_ADDRESS = "address";
    // User latitude
    public static final String KEY_LATITUDE = "latitude";
    // User longitude
    public static final String KEY_LONGITUDE = "longitude";

    // User Phone
    public static final String KEY_PHONE = "phone";

    public static final String KEY_IMAGE = "image";

    public static final String KEY_AADHAR = "aadhar_link";

    public static final String KEY_VERIFIED = "verified";

    public static final String FCM_REG_ID = "reg_id";

    public static final String BANK_DETAIL_ACC_NO = "bank_detail_account_no";

    public static final String BANK_DETAIL_IFSC = "bank_detail_ifsc";

    public static final String BANK_DETAIL_BANK_NAME = "bank_detail_bank_name";

    public static final String KEY_UID = "uid";


    public static final String ACCESS_TOKEN = "Access-Token";
    public static final String CLIENT = "Client";
    // Constructor
    public SessionManager(Context context){
        this._context = context;
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String email, String name, Headers headers, String phone ,String uid){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing name in pref
        editor.putString(KEY_NAME, name);
        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
        // commit changes
        editor.putString(ACCESS_TOKEN, headers.get("access-token"));

        editor.putString(CLIENT,headers.get("client"));

        editor.putString(KEY_PHONE, phone);

        editor.putString(KEY_UID , uid);



        editor.commit();
    }

    public void put(String key , String val) {

        editor.putString(key,val);
        editor.commit();
    }

    public String getvalStr(String key) {

        String val = sharedPreferences.getString(key,null);
        return val;

    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean checkLogin(){

        String uid = sharedPreferences.getString(SessionManager.KEY_UID,null);
        if(null == uid || uid.isEmpty()) {
            return false;
        }
        return true;
    }

    public HashMap<String,String> getUserDetails()
    {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, sharedPreferences.getString(KEY_NAME, null));
        // user email id
        user.put(KEY_EMAIL, sharedPreferences.getString(KEY_EMAIL, null));
        //user address
        user.put(KEY_ADDRESS, sharedPreferences.getString(KEY_ADDRESS, null));
        // return user
        user.put(KEY_PHONE, sharedPreferences.getString(KEY_PHONE, null));

        user.put(KEY_IMAGE, sharedPreferences.getString(KEY_IMAGE, null));

        user.put(KEY_AADHAR, sharedPreferences.getString(KEY_AADHAR, null));

        user.put(KEY_VERIFIED , sharedPreferences.getString(KEY_VERIFIED,null));

        user.put(KEY_UID , sharedPreferences.getString(KEY_UID,null));

        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }


    public void address(String address, String lat, String lon)
    {
        editor.putString(KEY_ADDRESS,address);
        editor.putString(KEY_LATITUDE,lat);
        editor.putString(KEY_LONGITUDE,lon);
        editor.commit();
    }

    public boolean checkForPlayService(Context ctx) {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(ctx);
        if (code == ConnectionResult.SUCCESS) {
            return true;
        }
        return false;
    }

    }
