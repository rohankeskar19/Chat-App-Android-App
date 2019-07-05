package com.example.chatapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatapplication.Models.Message;
import com.example.chatapplication.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter {

    ArrayList<Message> messages = new ArrayList<>();
    Context context;
    String id;


    public MessageAdapter(ArrayList<Message> messages, Context context, String id) {
        this.messages = messages;
        this.context = context;
        this.id = id;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0){
            return new SenderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_message_sent,parent,false));
        }
        else{
            return new RecieverViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_message_recieved,parent,false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);

        if (message.getFromId().equals(id)){
            return 0;
        }
        else{
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (holder.getItemViewType() == 0){
            ((SenderViewHolder) holder).bind(message);
        }
        else{
            ((RecieverViewHolder) holder).bind(message);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout parentLayout;
        TextView messageBodyTextView,messageTimeTextView;



        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parentLayout);
            messageBodyTextView = itemView.findViewById(R.id.messageBodyTextView);
            messageTimeTextView = itemView.findViewById(R.id.messageTimeTextView);

        }

        void bind(Message message){
            messageBodyTextView.setText(message.getContent());
            messageTimeTextView.setText(message.getTime());



        }

    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout parentLayout;
        TextView messageBodyTextView,messageTimeTextView;



        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parentLayout);
            messageBodyTextView = itemView.findViewById(R.id.messageBodyTextView);
            messageTimeTextView = itemView.findViewById(R.id.messageTimeTextView);
        }

        void bind(Message message) {
            messageBodyTextView.setText(message.getContent());
            messageTimeTextView.setText(message.getTime());
        }
    }





}
