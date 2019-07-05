package com.example.chatapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.example.chatapplication.ChatActivity;
import com.example.chatapplication.Models.Freind;
import com.example.chatapplication.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FreindsListRecyclerViewAdapter extends RecyclerView.Adapter<FreindsListRecyclerViewAdapter.ViewHolder>{


    private static final String TAG = "FreindsListRecyclerView";
    private Context mContext;
    private ArrayList<Freind> freinds = new ArrayList<>();
    RequestQueue requestQueue;


    public FreindsListRecyclerViewAdapter(Context mContext, ArrayList<Freind> searchResult, RequestQueue requestQueue) {
        this.mContext = mContext;
        this.freinds = searchResult;
        this.requestQueue = requestQueue;

    }

    @NonNull
    @Override
    public FreindsListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_freinds,parent,false);
        return new FreindsListRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FreindsListRecyclerViewAdapter.ViewHolder holder, int position) {
        final Freind freind = freinds.get(position);



        Glide.with(mContext).load(freind.getProfileUrl()).into(holder.profileImageView);
        holder.nameTextView.setText(freind.getName());
        holder.usernameTextView.setText(freind.getUsername());


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("freind",freind);
                mContext.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return freinds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView,nameTextView;
        ImageView profileImageView;

        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parentLayout);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }


}
