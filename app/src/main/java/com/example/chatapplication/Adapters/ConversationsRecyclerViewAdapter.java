package com.example.chatapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.chatapplication.ChatActivity;
import com.example.chatapplication.Database.ChatDBHelper;
import com.example.chatapplication.Models.Conversation;
import com.example.chatapplication.Models.Freind;
import com.example.chatapplication.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ConversationsRecyclerViewAdapter extends RecyclerView.Adapter<ConversationsRecyclerViewAdapter.ViewHolder> {


    private static final String TAG = "ConversationsRecyclerVi";
    private List<Conversation> conversations = new ArrayList<>();
    private Context mContext;
    String id;

    public ConversationsRecyclerViewAdapter(List<Conversation> conversations, Context mContext, String id) {
        this.conversations = conversations;
        this.mContext = mContext;
        this.id = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_conversation,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        try{
            final Conversation conversation = conversations.get(position);


            if (conversation.getConversationOpened().equals("true")){
                holder.sticker.setVisibility(View.GONE);
                holder.lastMessageTimeTextView.setVisibility(View.VISIBLE);
                holder.lastMessageDateTextView.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                int dpValue = 15; // margin in dips
                float d = mContext.getResources().getDisplayMetrics().density;
                int margin = (int)(dpValue * d);
                layoutParams.topMargin = margin;

                holder.messageHolderLayout.setLayoutParams(layoutParams);

            }
            else{
                holder.sticker.setVisibility(View.VISIBLE);
                holder.lastMessageTimeTextView.setVisibility(View.GONE);
                holder.lastMessageDateTextView.setVisibility(View.GONE);
            }

            String stringDate = conversation.getLastMessageDate();

            String[] splittedDate = stringDate.split(" ");

            Log.d(TAG, "onBindViewHolder: " + stringDate);
            Log.d(TAG, "onBindViewHolder: " + Arrays.toString(splittedDate));


            holder.freindsNameTextView.setText(conversation.getName());
            Log.d(TAG, "onBindViewHolder: lastMessageSender " + conversation.getLastMessageSenderId() + " Id: " + id);
            if (conversation.getLastMessageSenderId().equals(id)){
                holder.lastMessageTextView.setText("You: " + conversation.getLastMessage());
            }
            else{
                holder.lastMessageTextView.setText(conversation.getLastMessage());
            }



            String[] dateArray = splittedDate[0].split("-");

            Calendar c = Calendar.getInstance();


            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            Date today = c.getTime();

            int year = Integer.parseInt(dateArray[0]);
            int month = Integer.parseInt(dateArray[1]);
            int dayOfMonth = Integer.parseInt(dateArray[2]);


            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);


            Date dateSpecified = c.getTime();


            if (dateSpecified.before(today)) {
                holder.lastMessageDateTextView.setText(splittedDate[0].replace("-","/"));
            } else {
                holder.lastMessageDateTextView.setText("Today");
            }


            String[] splittedTime = splittedDate[1].split(":");

            holder.lastMessageTimeTextView.setText(splittedTime[0] + ":" + splittedTime[1]);


            Glide.with(mContext).load(conversation.getUserProfileUrl()).into(holder.freindProfileImageView);

            holder.parentlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Freind freind = new Freind(conversation.getUserId(),conversation.getUsername(),conversation.getUserProfileUrl(),conversation.getName());

                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("freind",freind);

                    ChatDBHelper dbHelper = new ChatDBHelper(mContext);
                    dbHelper.setConversationOpened(conversation.getConversationId(),"true");
                    conversations.get(position).setConversationOpened("true");

                    mContext.startActivity(intent);
                }
            });

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout parentlayout,sticker,messageHolderLayout;
        TextView freindsNameTextView,lastMessageTextView,lastMessageDateTextView,lastMessageTimeTextView;
        ImageView freindProfileImageView;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            messageHolderLayout = itemView.findViewById(R.id.messageHolderLayout);
            sticker = itemView.findViewById(R.id.messageStatusSticker);
            parentlayout = itemView.findViewById(R.id.parentLayout);
            freindProfileImageView = itemView.findViewById(R.id.freindProfileImageView);
            freindsNameTextView = itemView.findViewById(R.id.freindNameTextView);
            lastMessageTextView = itemView.findViewById(R.id.lastMessageTextView);
            lastMessageDateTextView = itemView.findViewById(R.id.lastMessageDateTextView);
            lastMessageTimeTextView = itemView.findViewById(R.id.lastMessageTimeTextView);

        }
    }


}
