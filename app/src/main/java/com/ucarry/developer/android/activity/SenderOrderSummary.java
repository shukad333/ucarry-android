package com.ucarry.developer.android.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;
import com.ucarry.developer.android.Model.Constants;
import com.ucarry.developer.android.Model.PickupOrderMapping;
import com.ucarry.developer.android.Model.Quote;
import com.ucarry.developer.android.Model.ReceiverOrderMapping;
import com.ucarry.developer.android.Model.SenderOrder;
import com.ucarry.developer.android.Model.SenderOrderItemAttributes;
import com.ucarry.developer.android.Model.SenderOrderRequest;
import com.ucarry.developer.android.Model.SenderOrderResponse;
import com.ucarry.developer.android.RetroGit.ApiClient;
import com.ucarry.developer.android.RetroGit.ApiInterface;
import com.ucarry.developer.android.Utilities.Utility;
import com.yourapp.developer.karrierbay.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SenderOrderSummary extends AppCompatActivity {

    private SenderOrder order;
    private SenderOrderItemAttributes orderItems;
    private Quote quote;
    private PickupOrderMapping pickupOrderMapping;
    private ReceiverOrderMapping receiverOrderMapping;

    private TextView fromTv;
    private TextView fromDateTV;
    private TextView toTv;
    private TextView toDateTV;
    private TextView wantToSend;
    private TextView itemWeight;
    private TextView rate;
    private TextView itemSubType;

    private TextView pickAddr1;
    private TextView pickAddr2;
    private TextView pickAddr3;

    private TextView delAddr1;
    private TextView delAddr2;
    private TextView delAddr3;

    private LinearLayout itemWeightLayout;
    private LinearLayout itemTypeLayout;
    private LinearLayout itemSubTypeLayout;
    private LinearLayout itemRateLayout;
    private LinearLayout passengerLayout;

    private boolean isPassenger;

    private ApiInterface apiService;

    private static final String TAG = "SenderOrderSV";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_order_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Order Schedule</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        order = (SenderOrder)getIntent().getSerializableExtra("SenderOrder");
        isPassenger = getIntent().getBooleanExtra("isPassenger",false);
        orderItems = order.getOrder_items().get(0);
        quote = (Quote) getIntent().getSerializableExtra("Quote");
        pickupOrderMapping = order.getPickupOrderMapping();
        receiverOrderMapping = order.getReceiverOrderMapping();
        getBindings();

        Button proceedButton = (Button) findViewById(R.id.btn_sender_next);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                apiService = ApiClient.getClientWithHeader(getApplicationContext()).create(ApiInterface.class);
                SenderOrderRequest senderOrderRequest = new SenderOrderRequest();

                final ProgressDialog pd = new ProgressDialog(SenderOrderSummary.this);
                pd.setMessage(Constants.WAIT_MESSAGE);
                pd.setIndeterminate(true);
                pd.show();

                SenderOrderItemAttributes[]  attr = new SenderOrderItemAttributes[1];
                attr[0] = orderItems;

                order.setSender_order_item_attributes(attr);
                order.setPickupOrderMapping(pickupOrderMapping);

                senderOrderRequest.setSenderOrder(order);



                Call<SenderOrderResponse> call = apiService.createSenderOrder(senderOrderRequest);

                call.enqueue(new Callback<SenderOrderResponse>() {
                    @Override
                    public void onResponse(Call<SenderOrderResponse> call, Response<SenderOrderResponse> response) {

                        Log.d(TAG,"Success");

                        pd.dismiss();

                        if(response.code()==201 || response.code()==200) {

                            final Dialog dialog = new Dialog(SenderOrderSummary.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.order_popuo);


                            TextView text = (TextView) dialog.findViewById(R.id.textView2);
                            text.setText(Constants.ORDER_CREATION_MESSAGE);

                            Button dialogButton = (Button) dialog.findViewById(R.id.btn_continue);
                            ImageView ivPop = (ImageView) dialog.findViewById(R.id.ivPop);

                            // if button is clicked, close the custom dialog
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(SenderOrderSummary.this,CarrierListActivity.class);


                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();

                            // if button is clicked, close the custom dialog
                            ivPop.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                        }

                        else {

                            try {
                                Toast.makeText(SenderOrderSummary.this, "Ooops ! " + response.body(), Toast.LENGTH_LONG).show();

                            }
                            catch (Exception e) {
                                Log.d(TAG,e.getLocalizedMessage());
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<SenderOrderResponse> call, Throwable t) {

                        if(pd.isShowing())
                            pd.dismiss();

                        try {
                            Toast.makeText(SenderOrderSummary.this, "Creation Failed ! " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            t.printStackTrace();

                        }
                        catch (Exception e) {
                            Log.d(TAG,e.getLocalizedMessage());
                        }

                        Log.d(TAG,t.getLocalizedMessage()+"");

                    }
                });

            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, SenderOrderSecondPage.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getBindings() {

        fromTv = (TextView) findViewById(R.id.sender_detail_from);
        fromDateTV = (TextView) findViewById(R.id.sender_detail_from_datetime);
        toTv = (TextView) findViewById(R.id.sender_detail_to);
        toDateTV = (TextView) findViewById(R.id.sender_detail_to_date_time);
        wantToSend = (TextView) findViewById(R.id.sender_detail_want_to_send);
        itemWeight = (TextView) findViewById(R.id.sender_detail_weight);
        rate = (TextView) findViewById(R.id.sender_detail_rate);
        itemSubType = (TextView) findViewById(R.id.sender_detail_sub_type);
        pickAddr1 = (TextView) findViewById(R.id.address_line_1);
        pickAddr2 = (TextView) findViewById(R.id.address_line_2);
        pickAddr3 = (TextView) findViewById(R.id.address_line_3);

        delAddr1 = (TextView) findViewById(R.id.del_address_line_1);
        delAddr2 = (TextView) findViewById(R.id.del_address_line_2);
        delAddr3 = (TextView) findViewById(R.id.del_address_line_3);

        itemRateLayout = (LinearLayout) findViewById(R.id.rate_layout);
        itemWeightLayout = (LinearLayout) findViewById(R.id.weight_layout);
        itemSubTypeLayout = (LinearLayout) findViewById(R.id.sub_type_layout);
        passengerLayout = (LinearLayout) findViewById(R.id.passenger_layout);

        if(!isPassenger) {


            itemWeightLayout.setVisibility(View.VISIBLE);
            itemRateLayout.setVisibility(View.VISIBLE);
            itemSubTypeLayout.setVisibility(View.VISIBLE);
            passengerLayout.setVisibility(View.GONE);
        }

        else {

            itemWeightLayout.setVisibility(View.GONE);
            itemRateLayout.setVisibility(View.GONE);
            itemSubTypeLayout.setVisibility(View.GONE);
            passengerLayout.setVisibility(View.VISIBLE);
        }


        initialize();

    }

    private void initialize() {

        fromTv.setText(order.getFrom_loc());
        fromDateTV.setText(Utility.convertToProperDate(orderItems.getStart_time()));
        toTv.setText(order.getTo_loc());
        toDateTV.setText(Utility.convertToProperDate(orderItems.getEnd_time()));
        wantToSend.setText(orderItems.getItem_type());

        if(!isPassenger) {
            rate.setText(Math.ceil(Double.parseDouble(quote.getGrand_total())) + "");
            itemWeight.setText(order.getRef_1());
            itemSubType.setText(orderItems.getItem_subtype());
        }

        pickAddr1.setText(pickupOrderMapping.getName()+","+pickupOrderMapping.getPhone_1());
        pickAddr2.setText(pickupOrderMapping.getAddress_line_1());
        pickAddr3.setText(pickupOrderMapping.getAddress_line_2());

        delAddr1.setText(receiverOrderMapping.getName()+","+receiverOrderMapping.getPhone_1());
        delAddr2.setText(receiverOrderMapping.getAddress_line_1());
        delAddr3.setText(receiverOrderMapping.getAddress_line_2());


    }

}
