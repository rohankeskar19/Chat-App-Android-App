package com.example.chatapplication.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.chatapplication.AuthActivity;
import com.example.chatapplication.Adapters.FreindsListRecyclerViewAdapter;
import com.example.chatapplication.Database.ChatDBHelper;
import com.example.chatapplication.Models.Freind;
import com.example.chatapplication.Models.User;
import com.example.chatapplication.R;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FreindsFragment extends Fragment {


    View view;
    private static final String TAG = "FreindsFragment";

    User user;
    String token;

    public FreindsFragment(){

    }


    ArrayList<Freind> freinds = new ArrayList<>();

    ImageView profileImageView;

    TextView nameTextView,usernameTextView,bioTextView;

    TextView signOutTextView;
    RecyclerView freindsList;

    RequestQueue requestQueue;


    SwipeRefreshLayout swipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_freinds,container,false);

        profileImageView = view.findViewById(R.id.profileImageView);
        nameTextView = view.findViewById(R.id.nameTextView);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        bioTextView = view.findViewById(R.id.bioTextView);

        signOutTextView = view.findViewById(R.id.signOutTextView);
        freindsList = view.findViewById(R.id.freindsList);

        requestQueue = Volley.newRequestQueue(getContext());

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutFreinds);

        init();
        getUserInfo();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserInfo();
            }
        });


        return view;

    }

    private void init(){
        try{

            Field field = swipeRefreshLayout.getClass().getDeclaredField("mCircleView");
            field.setAccessible(true);

            ImageView imageView = (ImageView) field.get(swipeRefreshLayout);
            Glide.with(getContext()).load(R.drawable.spinner).into(imageView);



            Log.d(TAG, "instance initializer: Hi");
            final SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("authData",0);

            token = sharedPreferences.getString("token","null");


            String decodedToken = decoded(token);

            JSONObject jsonObject = new JSONObject(decodedToken);
            JSONObject user1 = jsonObject.getJSONObject("user");

            user = new User(user1.getString("id"),user1.getString("name"),user1.getString("email"),user1.getString("username"),user1.getString("profileUrl"),"","");

            

            Log.d(TAG, "instance initializer: " + user);

            String[] tokens = token.split(" ");
            tokens[0] += "%20";

            String urlToken = "";

            for(int i = 0; i < tokens.length; i++){
                urlToken += tokens[i];
            }

            Glide.with(getContext()).load(user.getProfileUrl()).into(profileImageView);

            nameTextView.setText(user.getName());
            usernameTextView.setText(user.getUsername());
            bioTextView.setText("");
            Log.d(TAG, "init: Token " + token + "\nUser " + user);
            signOutTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        String socketUrl = getString(R.string.protocol) + "://" + getString(R.string.host) + ":" + getString(R.string.SOCKET_PORT);
                        Socket socket = IO.socket(socketUrl);
                        socket.disconnect();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        ChatDBHelper chatDBHelper = new ChatDBHelper(getContext());
                        chatDBHelper.dropTables();


                        Intent intent = new Intent(getContext(), AuthActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });


        }
        catch (Exception e){
            e.printStackTrace();
        }





    }

    private void getUserInfo(){

        String url = getString(R.string.protocol) + "://" + getString(R.string.host) + ":" + getString(R.string.API_PORT) + "/api/user/details";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    freinds.clear();
                    Log.d(TAG, "onResponse: " + response.toString());
                    String id = response.getString("_id");
                    String name = response.getString("name");
                    String username = response.getString("username");
                    String email = response.getString("email");
                    String profileUrl = response.getString("profileUrl");
                    String createdAt = response.getString("createdAt");

                    user = new User(id,name,email,username,profileUrl,"",createdAt);


                    JSONArray jsonArray = response.getJSONArray("freinds");

                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);


                        String id1 = jsonObject.getString("user_id");
                        String username1 = jsonObject.getString("username");
                        String profileUrl1 = jsonObject.getString("profileUrl");
                        String name1 = jsonObject.getString("name");

                        Freind freind = new Freind(id1,username1,profileUrl1,name1);

                        freinds.add(freind);

                    }
                    setupRecyclerView();

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("authorization",token);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }


    private void setupRecyclerView(){
        Log.d(TAG, "setupRecyclerView: " + freinds);
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }

        FreindsListRecyclerViewAdapter freindsListRecyclerViewAdapter = new FreindsListRecyclerViewAdapter(getContext(),freinds,requestQueue);
        freindsList.setLayoutManager(new LinearLayoutManager(getContext()));
        freindsList.setAdapter(freindsListRecyclerViewAdapter);
    }

    private static String[] split;

    public static String decoded(String JWTEncoded) throws Exception {
        try {
            split = JWTEncoded.split("\\.");

        } catch (Exception e) {
            //Error
        }
        return getJson(split[1]);
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }



}
