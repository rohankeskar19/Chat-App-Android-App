package com.example.chatapplication.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.chatapplication.DraggingPanel;
import com.example.chatapplication.Adapters.NotificationsRecyclerViewAdapter;
import com.example.chatapplication.Adapters.SearchRecyclerViewAdapter;
import com.example.chatapplication.Models.User;
import com.example.chatapplication.Models.UserSearch;
import com.example.chatapplication.R;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AddFreindsFragment extends Fragment {


    private static final String TAG = "AddFreindsFragment";

    User user;
    String token;

    View view;

    public AddFreindsFragment() {
    }


    RecyclerView recyclerView,notificationsRecyclerView;

    ArrayList<UserSearch> users = new ArrayList<>();
    ArrayList<com.example.chatapplication.Models.Request> requests = new ArrayList<>();


    private LinearLayout mQueen;

    private DraggingPanel mDraggingPanel;
    private RelativeLayout mMainLayout;

    EditText searchEditText;

    SwipeRefreshLayout swipeRefreshLayout;

    RequestQueue requestQueue;

    long delay = 1000;
    long lastTextEdit = 0;
    Handler handler = new Handler(Looper.getMainLooper());

    String searchField;

    private Runnable searchFinishChecker = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() > (lastTextEdit + delay - 500)){
                try{
                    makeRequests(searchField);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            Log.d(TAG, "instance initializer: Hi");
            final SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("authData",0);

            token = sharedPreferences.getString("token","null");



            String decodedToken = decoded(token);

            JSONObject jsonObject = new JSONObject(decodedToken);
            JSONObject user1 = jsonObject.getJSONObject("user");

            user = new User(user1.getString("id"),user1.getString("name"),user1.getString("email"),user1.getString("username"),user1.getString("profileUrl"),"","");




        }
        catch (Exception e){
            e.printStackTrace();
        }







    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_freinds,container,false);

        searchEditText = view.findViewById(R.id.searchUsersEditText);


        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutAddFreinds);

        mDraggingPanel = view.findViewById(R.id.outer_layout);
        mMainLayout = view.findViewById(R.id.main_layout);


        notificationsRecyclerView = view.findViewById(R.id.notificationsRecyclerView);
        recyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        mQueen = view.findViewById(R.id.queen_button);



        mMainLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mDraggingPanel.isMoving()) {

                    v.setTop(oldTop);
                    v.setBottom(oldBottom);
                    v.setLeft(oldLeft);
                    v.setRight(oldRight);
                }
                
            }
        });



        try{
            Field field = swipeRefreshLayout.getClass().getDeclaredField("mCircleView");
            field.setAccessible(true);

            ImageView imageView = (ImageView) field.get(swipeRefreshLayout);
            Glide.with(getContext()).load(R.drawable.spinner).into(imageView);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setupNotifications();
            }
        });




        requestQueue = Volley.newRequestQueue(getContext());

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {

                searchField = s.toString();

                handler.postDelayed(searchFinishChecker,delay);



            }
        });

        setupNotifications();

        return view;

    }

    private void makeRequests(final String s){
        String url = getContext().getString(R.string.protocol) + "://" + getContext().getString(R.string.host) + ":" + getContext().getString(R.string.API_PORT) + "/api/user/users";

        if (s.trim().equals("")){
            users.clear();
            setupRecyclerView();
        }
        else {
            try {
                JSONObject payloadData = new JSONObject();
                payloadData.put("username", s.trim());

                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, payloadData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        users.clear();

                        try {
                            JSONArray jsonUsers = response.getJSONArray("responseData");
                            Log.d(TAG, "onResponse: " + jsonUsers);
                            for (int i = 0; i < jsonUsers.length(); i++) {
                                JSONObject jsonObject = jsonUsers.getJSONObject(i);

                                String id = jsonObject.getString("id");
                                String username = jsonObject.getString("username");
                                String email = jsonObject.getString("email");
                                String profileUrl = jsonObject.getString("profileUrl");
                                boolean requestSent = jsonObject.getBoolean("requestSent");


                                UserSearch userToAdd = new UserSearch(id,username,email,profileUrl,requestSent);


                                users.add(userToAdd);

                            }

                            setupRecyclerView();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
                    public Map getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();

                        headers.put("authorization", token);
                        return headers;
                    }


                };

                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

    private void setupRecyclerView(){


        SearchRecyclerViewAdapter searchRecyclerViewAdapter = new SearchRecyclerViewAdapter(getContext(),users,requestQueue,token);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(searchRecyclerViewAdapter);
    }

    private void setupNotifications(){
        requests.clear();
        String url = getString(R.string.protocol) + "://" + getString(R.string.host) + ":" + getString(R.string.API_PORT) + "/api/user/request-self";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    Log.d(TAG, "onResponse: " + response);
                    JSONArray jsonArray = response.getJSONArray("requests");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        JSONObject toObject = jsonObject.getJSONObject("from");

                        String id = jsonObject.getString("_id");
                        String fromId = toObject.getString("id");
                        String fromUsername = toObject.getString("username");
                        String profileUrl = toObject.getString("profileUrl");

                        com.example.chatapplication.Models.Request request = new com.example.chatapplication.Models.Request(id,fromId,fromUsername,profileUrl);



                        requests.add(request);

                    }

                    setupNotificationsRecyclerView();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("authorization",token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void setupNotificationsRecyclerView(){
            if (swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }

            NotificationsRecyclerViewAdapter notificationsRecyclerViewAdapter = new NotificationsRecyclerViewAdapter(getContext(),requests,requestQueue,token);

            notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            notificationsRecyclerView.setAdapter(notificationsRecyclerViewAdapter);



    }


}
