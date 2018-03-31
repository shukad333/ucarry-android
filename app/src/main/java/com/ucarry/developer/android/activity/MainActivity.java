package com.ucarry.developer.android.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.facebook.login.LoginManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.ucarry.developer.android.Utilities.MyFirebaseInstanceIDService;
import com.ucarry.developer.android.Utilities.PrefManager;
import com.yourapp.developer.karrierbay.R;

import java.util.Calendar;
import java.util.HashMap;

import com.ucarry.developer.android.Fragment.ContactFragment;
import com.ucarry.developer.android.Fragment.HomeFragment;
import com.ucarry.developer.android.Fragment.ProfileFragment;
import com.ucarry.developer.android.Fragment.*;
import com.ucarry.developer.android.Model.Constants;
import com.ucarry.developer.android.Model.QuoteRequest;
import com.ucarry.developer.android.Model.SenderOrder;
import com.ucarry.developer.android.Model.User;
import com.ucarry.developer.android.RetroGit.ApiClient;
import com.ucarry.developer.android.RetroGit.ApiInterface;
import com.ucarry.developer.android.Utilities.CircleTransform;
import com.ucarry.developer.android.Utilities.SessionManager;
import com.ucarry.developer.android.Utilities.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ucarry.developer.android.Utilities.Utility.hideKeyboard;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = MainActivity.class.getName();
    private SessionManager sessionManager;
    private NavigationView navigationView;
    private String tag;
    private HashMap<String, String> user;
    private TextView emailHeader,nameHeader;
    public ApiInterface apiService;
    public SenderOrder sender = new SenderOrder();
    public QuoteRequest quoteRequest = new QuoteRequest();
    private static int NOTIFICATION_ID = 1;
    private boolean exit = false;


    @Override
    protected void onResumeFragments() {
        if(sessionManager==null) {
            sessionManager = new SessionManager(getApplicationContext());

        }
        if(sessionManager.checkLogin()) {
            getAndUpdateUserDetails();
        }
        else {
            gotoLogin(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG,"Checking for FCM");

        if(!Utility.isOnline(getApplicationContext())) {



        }


        Intent intent = new Intent(this, MyFirebaseInstanceIDService.class);
        startService(intent);
        sessionManager = new SessionManager(getApplicationContext());

        try {
        String uid = sessionManager.getUserDetails().get(SessionManager.KEY_UID);
           if(null==uid || uid.isEmpty()) {
                Log.d(TAG,"No Login information available , Henxece giunf ti login screen");
               gotoLogin(this);

           }

           else {

               boolean isPermission = isStoragePermissionGranted();

               Log.d(TAG, isPermission + " Permission Available");

               //createNotification();
               //String refreshToken = FirebaseInstanceId.getInstance().getToken();
               //Log.d("TOKEN", "Refreshed token: " + refreshToken);
               getAndUpdateUserDetails();
               Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
               DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
               navigationView = (NavigationView) findViewById(R.id.nav_view);
               navigationView.setNavigationItemSelectedListener(this);




               setSupportActionBar(toolbar);

               getSupportActionBar().setDisplayHomeAsUpEnabled(true);
               getSupportActionBar().setDisplayShowHomeEnabled(true);
               getSupportActionBar().setDisplayShowTitleEnabled(true);
               getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

               getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>CrowdCarry</font>"));

               View hView = navigationView.getHeaderView(0);
               emailHeader = (TextView) hView.findViewById(R.id.email_header);
               nameHeader = (TextView) hView.findViewById(R.id.name_header);
               user = sessionManager.getUserDetails();

               ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                       this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                   public void onDrawerClosed(View view) {
                       super.onDrawerClosed(view);
                       // Do whatever you want here

                       Log.d(TAG, "Drawer Closed:::" + user.get(SessionManager.KEY_NAME));
                   }

                   /**
                    * Called when a drawer has settled in a completely open state.
                    */
                   public void onDrawerOpened(View drawerView) {
                       super.onDrawerOpened(drawerView);
                       // Do whatever you want here

                       Log.d(TAG, "Drawer Opened:::" + user.get(SessionManager.KEY_NAME));
                       emailHeader.setText(user.get(SessionManager.KEY_EMAIL));
                       nameHeader.setText(user.get(SessionManager.KEY_NAME));

                   }
               };
               drawer.setDrawerListener(toggle);
               toggle.syncState();


               emailHeader.setText(user.get(SessionManager.KEY_EMAIL));
               nameHeader.setText(user.get(SessionManager.KEY_NAME));

               String token = sessionManager.getvalStr(SessionManager.FCM_REG_ID);


               if (token != null) {

                   Log.d("FCM TOKEN:::", token);

                   MyFirebaseInstanceIDService.updateFCMRegId(token, getApplicationContext());

               }


               hView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {



                       fragment(new ProfileFragment(), "ProfileFragment");
                       DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                       drawer.closeDrawer(GravityCompat.START);
                   }
               });

               Log.d(TAG, "IMAGE IS CHECKED");
               String image = sessionManager.getvalStr("image");
               if (image != null) {

                   Log.d(TAG, "IMAGE IS NOT NULL");
                   displayImage(hView, image);

               } else {

                   Log.d(TAG, "IMAGE IS NULL");
               }

               fragment(new HomeFragment(), "MainFragment");
           }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public void createNotification() {


        Log.d("MAIN_ACTIVITY","CREATE NOTIFICATION");
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        int color = 0xff123456;
        builder.setSmallIcon(R.mipmap.ic_kb_notify)
                .setContentTitle("KarrierBay")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentText("Enthello")
                .setColor(color);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID,builder.build());

    }

    public void getAndUpdateUserDetails() {

        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        //Log.d(TAG,MyFirebaseInstanceIDService.getToken());
        String token = MyFirebaseInstanceIDService.getToken();
        if(token!=null) {
            MyFirebaseInstanceIDService.updateFCMRegId(token, MainActivity.this);
            pd.setMessage("Loading...");
            pd.setIndeterminate(true);
            pd.show();
            ApiInterface apiInterface = ApiClient.getClientWithHeader(getApplicationContext()).create(ApiInterface.class);
            Call<User> call = apiInterface.getUserDetails();
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    pd.dismiss();
                    if (response.code() == 200) {

                        Log.d(TAG, "Got 200!");
                        User user = response.body();
                        if (user.getImage() != null)
                            sessionManager.put(SessionManager.KEY_IMAGE, user.getImage());
                        if (user.getAadhar_link() != null)
                            sessionManager.put(SessionManager.KEY_AADHAR, user.getAadhar_link());

                        if (user.getAddress() != null) {
                            sessionManager.put(SessionManager.KEY_ADDRESS, user.getAddress());
                        }

                        sessionManager.put(SessionManager.KEY_VERIFIED, user.getVerified());

                        HashMap<String, String> u = sessionManager.getUserDetails();
                        Log.d(TAG, "Verify User");
                        if (u.get(SessionManager.KEY_IMAGE) != null)
                            Log.d(TAG, u.get("image"));
                        if (u.get(SessionManager.KEY_AADHAR) != null)
                            Log.d(TAG, u.get(SessionManager.KEY_AADHAR));

                        //  pd.dismiss();

                    }

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                    if (pd.isShowing())
                        pd.dismiss();


                }
            });

        }
    }

    public void displayImage(View view ,final String url) {


        final ProgressDialog pd = new ProgressDialog(view.getContext());
        pd.setIndeterminate(true);
        pd.setMessage("Downloading Image...");
        pd.show();

        String uri = Utility.getAwsUrl(url);
        ImageView iv = (ImageView) view.findViewById(R.id.profile_avatar);
        Picasso.with(view.getContext()).load(uri).transform(new CircleTransform()).into(iv);
        Log.d(TAG,uri);

        pd.dismiss();

    }
    @Override
    protected void onResume() {
        apiService = ApiClient.getClientWithHeader(this).create(ApiInterface.class);

        super.onResume();
    }
    @Override
    public void onBackPressed() {

        hideKeyboard(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            //Toast.makeText(this,tag,Toast.LENGTH_LONG).show();

            if (tag.equals("MainFragment")) {


                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                startActivity(intent);
                finish();
                System.exit(0);

            } else if (tag.equals(Constants.DETAILSFRAGMENT)) {
                FragmentManager fm = getSupportFragmentManager();
                for (int i = 1; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                sender = new SenderOrder();
                quoteRequest = new QuoteRequest();
                fragment(new HomeFragment(), "MainFragment");
            }

            //super.onBackPressed();
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
                //finish();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideKeyboard(this);
        int id = item.getItemId();
       /* if (id == R.id.action_notification)
        {
            fragment(new NotificationFragment(),"NotificationFragment");
            //item.setVisible(false);
        }*/
        if (id == R.id.action_home) {
//            sender = new SenderOrder();
//            quoteRequest = new QuoteRequest();
//            fragment(new HomeFragment(), "MainFragment");

            Intent intent = new Intent(this,WallListActivity.class);
            startActivity(intent);


        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        hideKeyboard(this);
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if(id==R.id.nav_home) {
            fragment(new HomeFragment(),"HomeFragment");
        }
        if(id==R.id.nav_my_bay)
        {
            fragment(new MyBayFragment(), "MyBayFragment");
            //fragment(new MapActivity(),"MapActivity");

//            Intent intent = new Intent(this,MyBayActivity.class);
//            startActivity(intent);

        }

        if(id==R.id.nav_legal) {

            Intent intent = new Intent(this,LegalListActivity.class);
            startActivity(intent);
        }
        if(id==R.id.carrier_list) {

//            Bundle bundle = new Bundle();
//            bundle.putBoolean("isCarrierFlow", true);
//            bundle.putBoolean("isSenderFlow", false);
//
//            CarrierListFragment clf = new CarrierListFragment();
//            clf.setArguments(bundle);
//            fragment(clf,"CarrierListFragment");

            Intent intent = new Intent(this,CarrierListActivity.class);
            startActivity(intent);

        }

        if(id==R.id.sender_list) {
//            this.sender = new SenderOrder();

          //  Bundle bundle = new Bundle();
//            bundle.putBoolean("isSenderFlow", true);
//            bundle.putBoolean("isCarrierFlow", false);
//            SenderFragment senderFragment = new SenderFragment();
//
//            CarrierListFragment clf = new CarrierListFragment();
//            SenderListFragment slf = new SenderListFragment();
//            clf.setArguments(bundle);
//            fragment(slf,"SenderListFragment");

           // Log.d("Firing sender","Sender List");
          //  fragment(new SenderListFragment(),"SenderListFragement");

            Intent intent = new Intent(this,SenderListActivityListActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_contact_us) {
            Intent intent = new Intent(this,ContactActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_rate_app) {
            //final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            final String appPackageName = "com.ucarry.developer.android";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
        if (id == R.id.nav_logout) {
            LoginManager.getInstance().logOut();
            sessionManager.logoutUser();
            finish();
            //Toast.makeText(getApplicationContext(), "User Login Status: " + sessionManager.checkLogin(), Toast.LENGTH_LONG).show();
        }

        if(id == R.id.nav_settings) {

            Intent intent = new Intent(this,Settings.class);
            startActivity(intent);
        }
        return false;
    }

    public void fragment(Fragment fragment, String transaction) {
        tag = transaction;
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_transaction, fragment, transaction);
        fragmentTransaction.addToBackStack(transaction);
        fragmentTransaction.commit();
        Log.d("backFragment", tag);
    }


    public void dateClick(View view) {
        final EditText et = (EditText) view;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this,
                new
                        DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker arg0,
                                                  int arg1, int arg2, int arg3) {

                                et.setText(arg3 + "-" + (arg2 + 1) + "-" + arg1);
                            }
                        }, year, month, day).show();
    }



    public void timeClick(View view) {
        final EditText et = (EditText) view;
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                et.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void gotoLogin(Context ctx) {
        Intent intent = new Intent(ctx,LoginActivity.class);
        startActivity(intent);
    }

}