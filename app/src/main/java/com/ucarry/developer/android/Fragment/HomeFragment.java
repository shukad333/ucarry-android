package com.ucarry.developer.android.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ucarry.developer.android.activity.BEACARRIER;
import com.ucarry.developer.android.activity.SenderOrderFirstPage;
import com.yourapp.developer.karrierbay.R;

import java.io.File;

import com.ucarry.developer.android.Model.QuoteRequest;
import com.ucarry.developer.android.Model.SenderOrder;
import com.ucarry.developer.android.Utilities.BaseFragment;
import com.ucarry.developer.android.activity.MainActivity;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends BaseFragment {


    private Button beaCarrier, beaSender;
    SenderFragment senderFragment = new SenderFragment();
    private static final String TAG = HomeFragment.class.getName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        beaCarrier = (Button) view.findViewById(R.id.be_a_carrier);
        beaSender = (Button) view.findViewById(R.id.be_a_sender);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>CrowdCarry</font>"));
        beaCarrier.setTypeface(mTfSemiBold);
        beaSender.setTypeface(mTfSemiBold);
        //   selectImageFromGallery();
        beaCarrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ((MainActivity) getActivity()).sender = new SenderOrder();
//                ((MainActivity) getActivity()).quoteRequest = new QuoteRequest();
//                Bundle bundle = new Bundle();
//                bundle.putBoolean("isSenderFlow", false);
//                senderFragment.setArguments(bundle);
//                ((MainActivity) getActivity()).fragment(senderFragment, "senderFragment");

                Intent intent = new Intent(getActivity(), BEACARRIER.class);
                startActivity(intent);

            }
        });

        beaSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ((MainActivity) getActivity()).sender = new SenderOrder();
//                ((MainActivity) getActivity()).quoteRequest = new QuoteRequest();
//
//                Bundle bundle = new Bundle();
//                bundle.putBoolean("isSenderFlow", true);
//                senderFragment.setArguments(bundle);
//                ((MainActivity) getActivity()).fragment(senderFragment, "SenderFragment");

                Intent intent = new Intent(getActivity(), SenderOrderFirstPage.class);
                startActivity(intent);

            }
        });
    }

//    public void uploadImage(String filePath) {
//
//        File file = new File(filePath);
//
//        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
//        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");
//
//        retrofit2.Call<ImageUploadResponse> req = ((MainActivity) getActivity()).apiService.uploadFile(body, name);
//        req.enqueue(new Callback<ImageUploadResponse>() {
//            @Override
//            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
//                // Do Something
//                Toast.makeText(getActivity(), "success", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
//    }

    int PICK_IMAGE = 1;

    public void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && null != data) {

            final Uri imageUri = data.getData();
            //  uploadImage(imageUri.getPath());


        } else {
            Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }


    }


    public void uploadImage(File file) {

//        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"),file);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("picture",file.getName(),requestBody);
//
//        ApiService client = ApiClient.getClient().create(ApiService.class);
//        Call<ImageUploadRes> call = client.uploadImage(body,file.getName());
//
//        call.enqueue(new Callback<ImageUploadRes>() {
//            @Override
//            public void onResponse(Call<ImageUploadRes> call, Response<ImageUploadRes> response) {
//
//                Log.d("UPLOAD","Success:::"+response.code()+response.body().toString());
//
//                ImageUploadRes object = response.body();
//
//                Log.d("UPLOAD","Successsss:::"+object.getUrl());
//
//                Toast.makeText(getApplicationContext(),"Succesfully Uploaded",Toast.LENGTH_LONG).show();
//
//                displayImage(object.getUrl());
//
//            }
//
//            @Override
//            public void onFailure(Call<ImageUploadRes> call, Throwable t) {
//
//                Log.d("UPLOAD","Failed "+t.getMessage());
//
//            }
//        });


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG,"Inflating menu");
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.d(TAG,"On prepare options menu");
    }
}
