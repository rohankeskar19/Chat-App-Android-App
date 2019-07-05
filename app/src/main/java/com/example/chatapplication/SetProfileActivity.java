package com.example.chatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.chatapplication.Helpers.CalculationHelper;
import com.example.chatapplication.Helpers.VolleyMultipartRequest;
import com.example.chatapplication.Models.User;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SetProfileActivity extends AppCompatActivity {


    String token;
    User user;

    ImageView profileImageView;

    Button submitButton;

    private static final int IMAGE_REQUEST = 1;
    Bitmap bitmap;
    boolean imageSelected = false;

    RequestQueue requestQueue;

    private static final String TAG = "SetProfileActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        profileImageView = findViewById(R.id.profileImageView);

        submitButton = findViewById(R.id.buttonSubmit);


        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("authData",0);
        token = sharedPreferences.getString("token","null");

        requestQueue = Volley.newRequestQueue(this);


        user = CalculationHelper.decodeToken(token);

        Log.d(TAG, "onCreate: " + user.getProfileUrl());
        if (!user.getProfileUrl().equals(getString(R.string.placeholderImageUrl))){
            Log.d(TAG, "onCreate: Hiiii");
            Intent intent = new Intent(this,HomeActivity.class);
            intent.putExtra("user",user);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }



        Glide.with(this).load(user.getProfileUrl()).into(profileImageView);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageSelected){
                    uploadImage();

                }


            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelected = false;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,IMAGE_REQUEST);
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null){
            try{
                Uri path = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                Glide.with(this).load(bitmap).into(profileImageView);
                imageSelected = true;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(){
        String url = getString(R.string.protocol) + "://" + getString(R.string.host) + ":" + getString(R.string.API_PORT) + "/api/user/profile-upload";




        try{
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    try{
                        byte[] responseData = response.data;
                        String dataToOutput = new String(responseData);
                        JSONObject jsonObject = new JSONObject(dataToOutput);

                        String token = jsonObject.getString("token");

                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("authData",0).edit();

                        editor.putString("token",token);
                        editor.apply();

                        Intent intent = new Intent(SetProfileActivity.this,HomeActivity.class);
                        user = CalculationHelper.decodeToken(token);
                        intent.putExtra("user",user);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    byte[] data = error.networkResponse.data;

                    String errorData = new String(data);
                    Log.d(TAG, "onErrorResponse: " + errorData);
                }
            }){
                 @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String,String> headers = new HashMap<>();
                    headers.put("authorization",token);
                    return headers;
                }

                @Override
                protected Map<String, DataPart> getByteData() throws AuthFailureError {
                    HashMap<String,DataPart> params = new HashMap<>();
                    params.put("image",new DataPart("image",imageToString(bitmap),"image/jpeg"));
                    return params;

                }
            };
            requestQueue.add(volleyMultipartRequest);


        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private byte[] imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();

    }


}
