package com.example.chatapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.chatapplication.Models.Request;
import com.example.chatapplication.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationsRecyclerViewAdapter extends RecyclerView.Adapter<NotificationsRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "NotificationsRecyclerVi";
    private Context mContext;
    private ArrayList<Request> mRequests = new ArrayList<>();
    private RequestQueue requestQueue;
    private String token;

    public NotificationsRecyclerViewAdapter(Context mContext, ArrayList<Request> mRequests, RequestQueue requestQueue,String token) {
        this.mContext = mContext;
        this.mRequests = mRequests;
        this.requestQueue = requestQueue;
        this.token = token;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Request request = mRequests.get(position);

        String msg = request.getFromUsername() + " has sent you a freind request";

        holder.username.setText(msg);
        Glide.with(mContext).load(request.getFromProfileUrl()).into(holder.profileImage);

        final String url = mContext.getString(R.string.protocol) + "://" + mContext.getString(R.string.host) + ":" + mContext.getString(R.string.API_PORT) + "/api/user/request-transaction";

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    JSONObject payloadData = new JSONObject();
                    payloadData.put("id",request.getId());
                    payloadData.put("status","accepted");

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, payloadData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            mRequests.remove(request);
                            notifyDataSetChanged();

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
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    JSONObject payloadData = new JSONObject();
                    payloadData.put("id",request.getId());
                    payloadData.put("status","rejected");

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, payloadData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            mRequests.remove(request);
                            notifyDataSetChanged();

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
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout parentLayout;
        TextView username;
        ImageView profileImage;
        ImageButton acceptButton,rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            username = itemView.findViewById(R.id.usernameTextView);
            profileImage = itemView.findViewById(R.id.profileImageView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }


}
