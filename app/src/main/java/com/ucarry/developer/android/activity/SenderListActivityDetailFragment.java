package com.ucarry.developer.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.ucarry.developer.android.Model.Constants;
import com.ucarry.developer.android.Model.User;
import com.yourapp.developer.karrierbay.R;


import com.ucarry.developer.android.Model.AcceptOrderResponse;

import com.ucarry.developer.android.RetroGit.ApiClient;
import com.ucarry.developer.android.RetroGit.ApiInterface;
import com.ucarry.developer.android.Utilities.CircleTransform;
import com.ucarry.developer.android.Utilities.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a single SenderListActivity detail screen.
 * This fragment is either contained in a {@link SenderListActivityListActivity}
 * in two-pane mode (on tablets) or a {@link SenderListActivityDetailActivity}
 * on handsets.
 */
public class SenderListActivityDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ORDER_ID = "order_id";
    public static final String USER_NAME = "name";
    public static final String ADDRESS = "address";
    public static final String IMAGE = "image_url";
    public static final String FROM_ADDRESS = "from_address";
    public static final String TO_ADDRESS = "to_address";
    public static final String CATEGORY = "category";
    public static final String SUB_CATEGORY = "sub_category";
    public static final String ITEM_WEIGHT = "item_weight";
    public static final String ITEM_VALUE = "item_value";
    public static final String RATE = "rate";
    public static final String USER_OBJ = "user_obj";
    //public static final String IMAGE = "image_url";
    /**
     * The dummy content this fragment is presenting.
     */


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SenderListActivityDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SENDER_DETAIL","OnCreate");
        if (getArguments().containsKey(USER_NAME)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.


            Activity activity = this.getActivity();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.senderlistactivity_detail, container, false);

        Log.d("SENDER_DETAIL","OnViewCreated");

        // Show the dummy content as text in a TextView.

        String image_url = getArguments().getString(IMAGE);
        ImageView iv = (ImageView) rootView.findViewById(R.id.sender_detail_image);


        Picasso.Builder picBuilder = new Picasso.Builder(rootView.getContext());
        picBuilder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });
        picBuilder.build().load(Utility.getAwsUrl(image_url)).placeholder(R.drawable.carrier).transform(new CircleTransform()).error(R.drawable.carrier).into(iv);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ProfileVIewActivity.class);
                User user = (User) getArguments().getSerializable(USER_OBJ);
                intent.putExtra(USER_OBJ,user);
                intent.putExtra("IS_SENDER",true);
                startActivity(intent);
            }
        });

        ((TextView) rootView.findViewById(R.id.sender_detail_name)).setText(getArguments().getString(USER_NAME));
        ((TextView) rootView.findViewById(R.id.sender_detail_address)).setText(getArguments().getString(ADDRESS));
        ((TextView) rootView.findViewById(R.id.sender_detail_from)).setText(getArguments().getString(FROM_ADDRESS));
        ((TextView) rootView.findViewById(R.id.sender_detail_to)).setText(getArguments().getString(TO_ADDRESS));
        ((TextView) rootView.findViewById(R.id.sender_detail_item_rate)).setText(getArguments().getString(RATE));
        ((TextView) rootView.findViewById(R.id.sender_detail_category)).setText(getArguments().getString(CATEGORY));
        ((TextView) rootView.findViewById(R.id.sender_detail_sub_category)).setText(getArguments().getString(SUB_CATEGORY));
        ((TextView) rootView.findViewById(R.id.sender_detail_item_weight)).setText(getArguments().getString(ITEM_WEIGHT));

        Button acceptButton = (Button) rootView.findViewById(R.id.sender_trip_accept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("DETAIL","CLICKED:::"+getArguments().getString(ORDER_ID));
                //Toast.makeText(view.getContext(),"Clicked",Toast.LENGTH_SHORT);

                ApiInterface apiInterface = ApiClient.getClientWithHeader(rootView.getContext()).create(ApiInterface.class);


                Call<AcceptOrderResponse> call = apiInterface.acceptOrder(getArguments().getString(ORDER_ID));

                final ProgressDialog pd = new ProgressDialog(getContext());
                pd.setMessage(Constants.LOADING_MESSAGE);
                pd.setIndeterminate(true);
                pd.show();

                call.enqueue(new Callback<AcceptOrderResponse>() {
                    @Override
                    public void onResponse(Call<AcceptOrderResponse> call, Response<AcceptOrderResponse> response) {

                        pd.dismiss();
                        Log.d("ACCEPT_RESPONSE",""+response.code());
                        if(response.code()==200)
                            Log.d("ACCEPT_RESPONSE",""+response.code()+" accepted");
                        Toast.makeText(rootView.getContext(),"Accepted",Toast.LENGTH_LONG);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(Constants.ACCEPT_MESSAGE)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);

                                    }
                                });

                        AlertDialog alert = builder.create();
                        alert.show();


                    }

                    @Override
                    public void onFailure(Call<AcceptOrderResponse> call, Throwable t) {

                        Log.d("ACCEPT_ERROR",t.getLocalizedMessage());
                        if(pd.isShowing())
                            pd.dismiss();
                    }
                });


            }
        });

        return rootView;
    }
}
