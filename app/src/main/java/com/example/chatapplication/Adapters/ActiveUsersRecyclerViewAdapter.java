package com.example.chatapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.chatapplication.ChatActivity;
import com.example.chatapplication.Models.Freind;
import com.example.chatapplication.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ActiveUsersRecyclerViewAdapter extends RecyclerView.Adapter<ActiveUsersRecyclerViewAdapter.ViewHolder>{

    private ArrayList<Freind> freinds = new ArrayList<>();
    private Context mContext;

    public ActiveUsersRecyclerViewAdapter(ArrayList<Freind> freinds, Context mContext) {
        this.freinds = freinds;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_active_freinds,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Glide.with(mContext).load(freinds.get(position).getProfileUrl()).into(holder.profileImageView);
        holder.usernameTextView.setText(freinds.get(position).getUsername());
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("freind",freinds.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return freinds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout parentLayout;
        ImageView profileImageView;
        TextView usernameTextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parentLayout);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
        }
    }


}
