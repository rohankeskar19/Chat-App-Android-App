package com.example.chatapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.chatapplication.Models.UserSearch;
import com.example.chatapplication.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "SearchRecyclerViewAdapt";
    private Context mContext;
    private ArrayList<UserSearch> searchResult = new ArrayList<>();
    RequestQueue requestQueue;
    String token;

    public SearchRecyclerViewAdapter(Context mContext, ArrayList<UserSearch> searchResult, RequestQueue requestQueue, String token) {
        this.mContext = mContext;
        this.searchResult = searchResult;
        this.requestQueue = requestQueue;
        this.token = token;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search_result,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final UserSearch user = searchResult.get(position);

        final String url = mContext.getString(R.string.protocol) + "://" + mContext.getString(R.string.host) + ":" + mContext.getString(R.string.API_PORT) + "/api/user/request";

        holder.usernameTextView.setText(user.getUsername());
        Glide.with(mContext).load(user.getProfileUrl()).into(holder.profileImageView);

        if (user.isRequestSent()){
            Glide.with(mContext).load(R.drawable.check_mark).into(holder.sendRequestButton);
        }
        else{
            holder.sendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    Glide.with(mContext).load(R.drawable.spinner).into(holder.sendRequestButton);

                    JSONObject payloadData = new JSONObject();

                    try {
                        payloadData.put("id", user.getId());
                        payloadData.put("username", user.getUsername());
                        payloadData.put("profileUrl", user.getProfileUrl());

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, payloadData, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Glide.with(mContext).load(R.drawable.animated_check).into(holder.sendRequestButton);
                                holder.sendRequestButton.setOnClickListener(null);
                                Toast.makeText(mContext, "Request Sent", Toast.LENGTH_SHORT).show();
                            }
                        },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
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
            });
        }



    }

    @Override
    public int getItemCount() {
        return searchResult.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        ImageView profileImageView;
        ImageButton sendRequestButton;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parentLayout);
            usernameTextView = itemView.findViewById(R.id.searchUsername);
            profileImageView = itemView.findViewById(R.id.searchProfile);
            sendRequestButton = itemView.findViewById(R.id.sendRequestButton);
        }
    }


}
