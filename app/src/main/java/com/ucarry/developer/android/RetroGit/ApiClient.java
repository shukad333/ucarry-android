package com.ucarry.developer.android.RetroGit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import com.ucarry.developer.android.Utilities.SessionManager;
import com.ucarry.developer.android.activity.LoginActivity;
import com.ucarry.developer.android.activity.SignUpActivity;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by narasinga_m on 8/26/2016.
 */
public class ApiClient {

   // public static final String BASE_URL = "http://35.162.42.110:3000/";
   // public static final String BASE_URL = "http://52.27.131.145:3000/";
   // public static final String BASE_URL = "http://35.161.197.248:3000/";
    public static final String BASE_URL = "http://34.209.145.25:3000/";//"http://34.209.145.25:3000/";//"http://52.42.79.56:3000";//http://52.42.79.56:3000/";//"http://10.0.2.2:3000/";//
    private static final String TAG = ApiClient.class.getName();

    private static Retrofit retrofit = null;
    public static Retrofit getClient() {

//        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
//        }
        return retrofit;
    }
   public static Retrofit getClientWithHeader(Context c) {
       final SessionManager sessionManager = new SessionManager(c);

       if (sessionManager.checkLogin()) {


           String uid = sessionManager.getUserDetails().get(SessionManager.KEY_UID);

           if(null==uid || uid.isEmpty()) {

               Log.d(TAG,"No login Info .. Hence going to sign up page");
               Intent intent = new Intent(c, LoginActivity.class);
               c.startActivity(intent);
           }

           else {
               OkHttpClient okClient = new OkHttpClient.Builder()
                       .addInterceptor(
                               new Interceptor() {
                                   @Override
                                   public Response intercept(Interceptor.Chain chain) throws IOException {

                                       Request request = chain.request().newBuilder().addHeader("Uid", sessionManager.getUserDetails().get(SessionManager.KEY_UID)).build();
                                       Log.d(TAG, sessionManager.getUserDetails().get(SessionManager.KEY_UID));
                                       return chain.proceed(request);

                                   }
                               })
                       .build();

               retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).client(okClient).build();

           }
       } else {


           Log.d(TAG,"No login Info .. Hence going to sign up page");
           Intent intent = new Intent(c, SignUpActivity.class);
           c.startActivity(intent);
       }
       return retrofit;
   }
}
