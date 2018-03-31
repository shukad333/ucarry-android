package com.ucarry.developer.android.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ucarry.developer.android.Model.Constants;
import com.ucarry.developer.android.Model.Otp;
import com.ucarry.developer.android.Model.User;
import com.ucarry.developer.android.Model.UserUpdateRequest;
import com.ucarry.developer.android.Utilities.Utility;
import com.yourapp.developer.karrierbay.R;

import com.ucarry.developer.android.Model.LoginRequest;
import com.ucarry.developer.android.Model.LoginResponse;
import com.ucarry.developer.android.RetroGit.ApiClient;
import com.ucarry.developer.android.RetroGit.ApiInterface;
import com.ucarry.developer.android.Utilities.BaseActivity;
import com.ucarry.developer.android.Utilities.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONObject;

import java.util.Arrays;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{




    private TextView SignUp, haveAccount, forgotPassword;
    private Button signIn;
    private EditText email, password;
    private SessionManager sessionManager;
    private static String TAG = "LOGINACTIVITY";
    private static String LOGIN_RESPONSE_TAG = "LOGIN_RESPONSE";
    private Button privacy;
    private Button tos;

    private CallbackManager callbackManager;
    private String enteredPassword;

    private static final int RC_SIGN_IN = 9001;
    private User remoteUser;
    private UserUpdateRequest remoteUserUpdateRequest = new UserUpdateRequest();
    private GoogleApiClient mGoogleApiClient;

    private static String provider = "facebook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_login);
        googleSignIn();
        faceBookSDKInitialize();
        privacy = (Button) findViewById(R.id.privacy_signup);
        tos = (Button) findViewById(R.id.terms_of_use_signup);

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPrivacy();
            }
        });

        tos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPrivacy();
            }
        });


        sessionManager = new SessionManager(getApplicationContext());

        if(!sessionManager.checkForPlayService(this)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(Constants.GOOGLE_PLAY_WARNING)
                    .setCancelable(false);



            AlertDialog alert = builder.create();

            alert.show();




        }

        else {
            if (sessionManager.checkLogin()) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }


        }



    }

    public void googleSignIn() {
        String client_id = "625045819598-dv43qn4hp3jrl9kdvnud6dqt7nvm126q.apps.googleusercontent.com";

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Log.d(TAG,"Google Init...");
        SignInButton signInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

    }

    private void signIn() {
        provider = "google";
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void faceBookSDKInitialize() {


        Log.d(TAG,"FB Init...");


        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);


        callbackManager = CallbackManager.Factory.create();


        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        // If using in a fragment

        // Other app specific specialization

        // Callback registration



        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                Log.d("Success","_SUCCESS");
                Log.d(TAG,loginResult.getAccessToken()+"_SUCCESS");

                doLogin(loginResult.getAccessToken().getToken()+"");



            }

            @Override
            public void onCancel() {
                // App code

                Log.d("Fail","_SUCCESS");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code

                exception.printStackTrace();
            }




        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("here","Here in ActivityResult");

        if (requestCode == RC_SIGN_IN) {
            provider = "google";
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else {
            provider="facebook";
            callbackManager.onActivityResult(requestCode, resultCode, data);
            Log.d(TAG, data.toString());
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess() + result.getStatus());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG,acct.getDisplayName());
            Log.d(TAG,acct.getId());
            Log.d(TAG,acct.getIdToken());
            doLogin(acct.getIdToken());

        }
    }


    @Override
    protected void onResume() {
        //apiService = ApiClient.getClient().create(ApiInterface.class);
        super.onResume();
    }

    public boolean Validation() {
        if (email.getText().length() == 0 || password.getText().length() == 0) {
            if (email.getText().length() == 0) {
                email.setError("Enter valid email");
            } else {
                password.setError("Enter valid password");
            }
            return false;
        }

        else {
            return true;
        }
    }


    private void doLogin(String token) {

        Log.d(TAG,"In login");
        Log.d(TAG,token);
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
       pd.setMessage("Loading...");
              pd.setIndeterminate(true);
            pd.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<User> call = apiInterface.doFbLogin(token,provider);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                pd.dismiss();
                if(response.code()==200 || response.code()==201) {

                    Log.d(TAG,"Successful");

                    User user = response.body();
                    remoteUser = user;
                    remoteUserUpdateRequest.setUid(user.getUid());

                    sessionManager.createLoginSession("",
                            "", response.headers(), "",user.getUid());

//
//                    if(user.getPhone()==null || user.getPhone().isEmpty()) {
//
//                        getExtraDetails();
//                    }
//                    else {

                    if(user.getEmail()!=null && user.getPhone()!=null) {

                        sessionManager.createLoginSession(user.getEmail(),
                                user.getName(), response.headers(), user.getPhone(),user.getUid());
                        sessionManager.put("image",user.getImage());



                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    }
                    else
                    getOtherDetails(user);

//                    }


                }

                if(response.code()==403) {

                    Log.d(TAG,"403 Message");


                }
                if(response.code()==400) {

                    Log.d(TAG,"400 message");

                    try {



                    }

                    catch(Exception e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                if(pd.isShowing())
                    pd.dismiss();

                t.printStackTrace();

            }
        });


    }

    private void getOtherDetails(User user) {


        if(user.getEmail()==null || user.getEmail().isEmpty()) {

            getEmail();
            Log.d(TAG,"EMAIL ::: "+enteredPassword);
        }

        else if(user.getPhone()==null || user.getPhone().isEmpty()) {

            getExtraDetails();;
        }




    }

    private  void getOtherDetails1() {

        Log.d(TAG,"EMAIL1 ::: "+enteredPassword);


    }

    private void getEmail() {



        LayoutInflater li = LayoutInflater.from(LoginActivity.this);
        View prompt = li.inflate(R.layout.user_login_extra_details_1, null);
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoginActivity.this);
        alertBuilder.setView(prompt);
        alertBuilder.setCancelable(false);

        final EditText email1 = (EditText) prompt.findViewById(R.id.email_1);
        final EditText email2 = (EditText) prompt.findViewById(R.id.email_2);

        Button button = (Button) prompt.findViewById(R.id.submitEmail);

        final AlertDialog dialog = alertBuilder.create();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG,"Clicked");

                if(email1.getText().toString().isEmpty() || email2.getText().toString().isEmpty()) {
                    email1.setError("Enter Email");

                }
                else {

                    if(email1.getText().toString().equals(email2.getText().toString())) {

                        enteredPassword = email1.getText().toString();

                        remoteUser.setEmail(enteredPassword);
                        remoteUserUpdateRequest.setEmail(enteredPassword);

                        dialog.dismiss();

                        getExtraDetails();


                    }
                    else {

                        email2.setError("Passwords don't match");
                    }

                }



            }
        });


        dialog.show();




    }

    private void getExtraDetails() {

        LayoutInflater li = LayoutInflater.from(LoginActivity.this);
        View prompt = li.inflate(R.layout.user_login_extra_details, null);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoginActivity.this);
        alertBuilder.setView(prompt);
        alertBuilder.setCancelable(false);

        AlertDialog dialog = alertBuilder.create();

        final Button otpButton = (Button)prompt.findViewById(R.id.otpButton);
        final Button verifyButton = (Button)prompt.findViewById(R.id.verifyButton);
        final EditText editText = (EditText) prompt.findViewById(R.id.verifyOtp);
        final EditText phone = (EditText) prompt.findViewById(R.id.phone_extra);

        otpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String phoneNo = phone.getText().toString();
                if(phoneNo.length()<10 || phoneNo.length()>10) {

                    Toast.makeText(LoginActivity.this,"Enter valid phone number",Toast.LENGTH_LONG).show();

                }
                else {
                    editText.setVisibility(View.VISIBLE);
                    phoneNo= "+91"+phoneNo;
                    sendOtp(phoneNo);
                    verifyButton.setVisibility(View.VISIBLE);
                }



            }
        });

        dialog.show();


        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                verifyOtp(editText.getText().toString(),"+91"+phone.getText().toString());

            }
        });




    }

    private void sendOtp(String phoneNumber) {


        Log.d(TAG,"In send otp");

        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("Sending OTP");
        pd.setIndeterminate(true);
        pd.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<Otp> call = apiInterface.getOtp(phoneNumber);
        call.enqueue(new Callback<Otp>() {
            @Override
            public void onResponse(Call<Otp> call, Response<Otp> response) {
                pd.dismiss();
                Toast.makeText(LoginActivity.this,"Sent the otp",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Otp> call, Throwable t) {

                if(pd.isShowing())
                    pd.dismiss();

            }
        });

    }

    private void verifyOtp(String otp , final String phone) {

        Log.d(TAG,"In send otp");

        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("Verifying...");
        pd.setIndeterminate(true);
        pd.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<Otp> call = apiInterface.verifyOtp(otp,phone);
        call.enqueue(new Callback<Otp>() {
            @Override
            public void onResponse(Call<Otp> call, Response<Otp> response) {
                if(response.code()==200) {

                    pd.dismiss();
                    Toast.makeText(LoginActivity.this,"Verified Successfully",Toast.LENGTH_LONG).show();

                    UserUpdateRequest req = new UserUpdateRequest();
                    remoteUserUpdateRequest.setPhone(phone);
                    req.setPhone(phone);
                    updateAndLogin(req);



                }
                else {

                    if(pd.isShowing())
                        pd.dismiss();

                    Toast.makeText(LoginActivity.this,"Wrong OTP entered,Please try again",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<Otp> call, Throwable t) {

                if(pd.isShowing())
                    pd.dismiss();

                Toast.makeText(LoginActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });



    }

    private void updateAndLogin(UserUpdateRequest request) {


        Log.d(TAG,"Updating Details...");

        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("Updating Details");
        pd.setIndeterminate(true);
        pd.show();
        ApiInterface apiInterface = ApiClient.getClientWithHeader(getApplicationContext()).create(ApiInterface.class);
        Call<User> call = apiInterface.editUserDetails(remoteUserUpdateRequest);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                pd.dismiss();
                Log.d(TAG,response.code()+" ::CODE::");
                if(response.code()==200 || response.code()==201) {

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    User user = response.body();
                    sessionManager.createLoginSession(user.getEmail(),
                            user.getName(), response.headers(), user.getPhone(),user.getUid());
                    sessionManager.put("image",user.getImage());
                    finish();




                }
//                else {
//
//                    Toast.makeText(LoginActivity.this,response.code(),Toast.LENGTH_LONG).show();
//                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                if(pd.isShowing())
                    pd.dismiss();

                Toast.makeText(LoginActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });


    }

    private void showToast() {


    }

    private void callPrivacy() {

        Intent intent = new Intent(LoginActivity.this,LegalListActivity.class);
        intent.putExtra("authenticated",false);
        startActivity(intent);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_sign_in_button:
                signIn();
                break;
//            case R.id.sign_out_button:
//                signOut();
//                break;
//            case R.id.disconnect_button:
//                revokeAccess();
//                break;
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


//    private void signIn() {
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

}

