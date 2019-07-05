package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.chatapplication.Adapters.MessageAdapter;
import com.example.chatapplication.Database.ChatDBHelper;
import com.example.chatapplication.Models.Conversation;
import com.example.chatapplication.Models.Freind;
import com.example.chatapplication.Models.Message;
import com.example.chatapplication.Models.User;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;


import org.json.JSONArray;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {

    Freind freind;
    String token;

    String eventMessageFromId;

    ImageView freindProfileImageView,sendButtonImageView;
    TextView freindNameTextView,freindStatusTextView;

    EditText editText;

    Socket socket;

    User user;

    int counter = 0;

    boolean isOnline;

    private static final String TAG = "ChatActivity";

    ArrayList<Message> messages = new ArrayList<>();

    long delay = 1000;
    long lastTextEdit = 0;
    Handler handler = new Handler(Looper.getMainLooper());

    Context context;

    RecyclerView messagesRecyclerView;

    RequestQueue requestQueue;

    ChatDBHelper dbHelper;

    MessageAdapter messageAdapter;

    Animation fadeInLeft;

    private Runnable inputFinishChecker = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() > (lastTextEdit + delay - 500)){
                try{
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("toId",freind.getId());
                    socket.emit("stop-typing",jsonObject);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        context = ChatActivity.this;

        dbHelper = new ChatDBHelper(context);



        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);

        requestQueue = Volley.newRequestQueue(context);

        String url = getString(R.string.protocol) + "://" + getString(R.string.host) + ":" + getString(R.string.SOCKET_PORT);

        try {
            socket = IO.socket(url);





            fadeInLeft = AnimationUtils.loadAnimation(this,R.anim.fade_in_left);

            editText = findViewById(R.id.messageEditText);
            freindProfileImageView = findViewById(R.id.freindProfileImageView);
            freindNameTextView = findViewById(R.id.freindNameTextView);
            freindStatusTextView = findViewById(R.id.freindStatusTextView);
            sendButtonImageView = findViewById(R.id.sendButton);

            Intent intent = getIntent();

            if (intent.hasExtra("freind")){
                freind = (Freind) intent.getSerializableExtra("freind");
            }

            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("authData",0);



            token = sharedPreferences.getString("token","null");

            if (token.equals("null")){
                finish();
            }

            String decodedToken = decoded(token);

            final JSONObject jsonObject = new JSONObject(decodedToken);
            JSONObject user1 = jsonObject.getJSONObject("user");

            user = new User(user1.getString("id"),user1.getString("name"),user1.getString("email"),user1.getString("username"),user1.getString("profileUrl"),"","");




            Conversation conversation = dbHelper.checkIfConversationExists(freind.getId());

            if (conversation != null){
                messages = dbHelper.getAllMessages(conversation.getConversationId());
                setupMessagesRecyclerView();
                messagesRecyclerView.smoothScrollToPosition(messagesRecyclerView.getAdapter().getItemCount());
            }
            //@TODO message id not given if message is from user add that functionality when message delivered,read is implemented
            sendButtonImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String message = editText.getText().toString().trim();

                    if (message.equals("")){

                    }
                    else{
                        editText.setText("");

                        try{


                            Conversation conversation = dbHelper.checkIfConversationExists(freind.getId());

                            if (conversation != null){
                                String messageUrl = getString(R.string.protocol) + "://" + getString(R.string.host) + ":" + getString(R.string.API_PORT) + "/api/user/message";

                                String timeStamp = Long.toString(System.currentTimeMillis());

                                Date date = new Timestamp(Long.parseLong(timeStamp));

                                String[] splittedArray = date.toString().split(" ");

                                String[] splittedTime = splittedArray[1].split(":");

                                String time = splittedTime[0] + ":" + splittedTime[1];

                                final Message message1 = new Message("",conversation.getConversationId(),user.getId(),freind.getId(),message,timeStamp,time);

                                messages.add(message1);
                                if (messageAdapter == null){
                                    setupMessagesRecyclerView();
                                    messagesRecyclerView.smoothScrollToPosition(messagesRecyclerView.getAdapter().getItemCount());
                                }
                                else{
                                    messageAdapter.notifyDataSetChanged();
                                    messagesRecyclerView.smoothScrollToPosition(messagesRecyclerView.getAdapter().getItemCount());
                                }
                                dbHelper.addMessage(message1);

                                Log.d(TAG, "onClick: Conversation exists " + messages.toString());

                                conversation.setLastMessageSenderId(message1.getFromId());
                                conversation.setLastMessage(message);
                                conversation.setLastMessageTimeStamp(timeStamp);


                                dbHelper.updateConversation(conversation.getConversationId(),conversation.getLastMessage(),conversation.getLastMessageTimeStamp(),conversation.getLastMessageDate(),conversation.getLastMessageSenderId(),"true");

                                JSONObject jsonObject1 = new JSONObject();

                                jsonObject1.put("conversationID",conversation.getConversationId());
                                jsonObject1.put("fromId",user.getId());
                                jsonObject1.put("toId",freind.getId());
                                jsonObject1.put("content",message);
                                jsonObject1.put("timeStamp",timeStamp);

                                JSONObject messageObject = new JSONObject();

                                messageObject.put("message",jsonObject1);

                                JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, messageUrl, messageObject, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {


                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        error.printStackTrace();
                                    }
                                }){
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        HashMap<String, String> headers = new HashMap<>();
                                        headers.put("authorization",token);
                                        return headers;
                                    }
                                };
                                requestQueue.add(jsonObjectRequest1);
                            }
                            else{
                                try {
                                    JSONArray jsonArray = new JSONArray();

                                    JSONObject freindObject = new JSONObject();

                                    freindObject.put("user_id",freind.getId());
                                    freindObject.put("username",freind.getUsername());
                                    freindObject.put("name",freind.getName());
                                    freindObject.put("profileUrl",freind.getProfileUrl());

                                    JSONObject userObject = new JSONObject();

                                    userObject.put("user_id",user.getId());
                                    userObject.put("username",user.getUsername());
                                    userObject.put("name",user.getName());
                                    userObject.put("profileUrl",user.getProfileUrl());

                                    jsonArray.put(userObject);
                                    jsonArray.put(freindObject);


                                    JSONObject users = new JSONObject();
                                    users.put("users",jsonArray);

                                    String url = getString(R.string.protocol) + "://" + getString(R.string.host) + ":" + getString(R.string.API_PORT) + "/api/user/conversation";


                                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, users, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try{
                                                JSONArray jsonArray1 = response.getJSONArray("Users");
                                                String conversationId = response.getString("_id");
                                                String createdAt = response.getString("createdAt");

                                                String currentTimeMillis = Long.toString(System.currentTimeMillis());

                                                for (int i = 0; i < jsonArray1.length(); i++){
                                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(i);

                                                    String id = jsonObject1.getString("user_id");

                                                    if (!id.equals(user.getId())){

                                                        String userName = jsonObject1.getString("username");
                                                        String name = jsonObject1.getString("name");
                                                        String profileUrl = jsonObject1.getString("profileUrl");





                                                        Conversation conversation1 = new Conversation(conversationId,id,userName,name,profileUrl,"",message,currentTimeMillis,getDateFromTimeStamp(currentTimeMillis),"true",createdAt);


                                                        dbHelper.addConversation(conversation1);
                                                    }

                                                }



                                                Date date = new Timestamp(Long.parseLong(currentTimeMillis));

                                                String[] splittedArray = date.toString().split(" ");

                                                String[] splittedTime = splittedArray[1].split(":");

                                                String time = splittedTime[0] + ":" + splittedTime[1];

                                                final Message message1 = new Message("",conversationId,user.getId(),freind.getId(),message,currentTimeMillis,time);

                                                dbHelper.addMessage(message1);
                                                messages.add(message1);
                                                if (messageAdapter == null){
                                                    setupMessagesRecyclerView();
                                                    messagesRecyclerView.smoothScrollToPosition(messagesRecyclerView.getAdapter().getItemCount());
                                                }
                                                else{
                                                    messageAdapter.notifyDataSetChanged();
                                                    messagesRecyclerView.smoothScrollToPosition(messagesRecyclerView.getAdapter().getItemCount());
                                                }



                                                String messageUrl = getString(R.string.protocol) + "://" + getString(R.string.host) + ":" + getString(R.string.API_PORT) + "/api/user/message";

                                                JSONObject jsonObject1 = new JSONObject();

                                                jsonObject1.put("conversationID",conversationId);
                                                jsonObject1.put("fromId",user.getId());
                                                jsonObject1.put("toId",freind.getId());
                                                jsonObject1.put("content",message);
                                                jsonObject1.put("timeStamp",Long.toString(System.currentTimeMillis()));

                                                JSONObject messageObject = new JSONObject();

                                                messageObject.put("message",jsonObject1);

                                                Log.d(TAG, "onClick: Conversation created " + messages.toString());

                                                JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, messageUrl, messageObject, new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {


                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        error.printStackTrace();
                                                    }
                                                }){
                                                    @Override
                                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                                        HashMap<String, String> headers = new HashMap<>();
                                                        headers.put("authorization",token);
                                                        return headers;
                                                    }
                                                };
                                                requestQueue.add(jsonObjectRequest1);
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
                                            HashMap<String, String> headers = new HashMap<>();

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
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });

            init();

            EmojiManager.install(new GoogleEmojiProvider());
        }
        catch (Exception e){
            e.printStackTrace();
        }




    }



    private void init(){
        Glide.with(this).load(freind.getProfileUrl()).into(freindProfileImageView);
        freindNameTextView.setText(freind.getName());

        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",freind.getId());
            socket.emit("isOnline",jsonObject);

            socket.on("online", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    if (context != null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject jsonObject = (JSONObject) args[0];

                                    JSONObject userObject = jsonObject.getJSONObject("user");
                                    final String id = userObject.getString("id");
                                    if (id.equals(freind.getId())){
                                        freindStatusTextView.setText("Online");
                                        freindStatusTextView.setTextColor(ContextCompat.getColor(ChatActivity.this,R.color.gray));
                                        freindStatusTextView.startAnimation(fadeInLeft);
                                    }
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                }
            });


            socket.on("isOnline", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                JSONObject jsonObject = (JSONObject) args[0];

                                String id = jsonObject.getString("id");
                                if (id.equals(freind.getId())){
                                    freindStatusTextView.setText("Online");
                                    freindStatusTextView.setTextColor(ContextCompat.getColor(ChatActivity.this,R.color.gray));
                                    isOnline = true;
                                    freindStatusTextView.startAnimation(fadeInLeft);
                                }

                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });


                }
            });

            socket.on("isOffline", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                JSONObject jsonObject = (JSONObject) args[0];

                                String id = jsonObject.getString("id");
                                if (id.equals(freind.getId())){
                                    freindStatusTextView.setText("Offline");
                                    freindStatusTextView.setTextColor(ContextCompat.getColor(ChatActivity.this,R.color.gray));
                                    isOnline = false;
                                    freindStatusTextView.startAnimation(fadeInLeft);
                                }

                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });


                }
            });

            socket.on("offline", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = (JSONObject) args[0];

                                String id = jsonObject.getString("id");
                                if (id.equals(freind.getId())){
                                    freindStatusTextView.setText("Offline");
                                    freindStatusTextView.setTextColor(ContextCompat.getColor(ChatActivity.this,R.color.gray));
                                    isOnline = false;
                                    freindStatusTextView.startAnimation(fadeInLeft);
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });


            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (s.length() > 0){

                            final JSONObject jsonObject = new JSONObject();
                            jsonObject.put("toId",freind.getId());
                            socket.emit("typing",jsonObject);
                            lastTextEdit = System.currentTimeMillis();
                            handler.postDelayed(inputFinishChecker,delay);
                        }



                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });


            socket.on("newConversation", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try{
                        JSONObject jsonObject = (JSONObject) args[0];
                        Log.d(TAG, "run: new Conversation " + jsonObject.toString());
                        String createdAt = jsonObject.getString("createdAt");
                        String conversationId = jsonObject.getString("_id");

                        Conversation conversation1 = dbHelper.checkIfConversationExistsById(conversationId);

                        if (conversation1 == null){
                            JSONArray usersArray = jsonObject.getJSONArray("Users");

                            for (int i = 0; i < usersArray.length(); i++){
                                JSONObject userObject = usersArray.getJSONObject(i);

                                String id = userObject.getString("user_id");

                                if (!id.equals(user.getId())){
                                    String username = userObject.getString("username");
                                    String name = userObject.getString("name");
                                    String profileUrl = userObject.getString("profileUrl");

                                    Conversation conversation = new Conversation(conversationId,id,username,name,profileUrl,"","","","","false",createdAt);

                                    dbHelper.addConversation(conversation);

                                }

                            }
                        }


                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            socket.on("newMessage", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try{

                        counter++;
                        Log.d(TAG, "call: new message" + counter);
                        JSONObject jsonObject = (JSONObject) args[0];
                        Log.d(TAG, "run: newMessage" + jsonObject.toString());


                        String id = jsonObject.getString("_id");



                        final Message newMessage = dbHelper.checkIfMessageExists(id);


                        Log.d(TAG, "call: Outside if " + newMessage);
                        if (newMessage == null){
                            Log.d(TAG, "call: Inside if " + newMessage);
                            String conversationId = jsonObject.getString("conversationID");
                            String fromId = jsonObject.getString("fromId");
                            eventMessageFromId = fromId;
                            String toId = jsonObject.getString("toId");
                            String content = jsonObject.getString("content");
                            String createdAt = jsonObject.getString("createdAt");

                            Date date = new Timestamp(Long.parseLong(createdAt));

                            String[] splittedArray = date.toString().split(" ");

                            String[] splittedTime = splittedArray[1].split(":");

                            String time = splittedTime[0] + ":" + splittedTime[1];

                            Message message = new Message(id,conversationId,fromId,toId,content,createdAt,time);

                            dbHelper.addMessage(message);

                            Date date1 = new Timestamp(Long.parseLong(message.getCreatedAt()));

                            dbHelper.updateConversation(conversationId,content,createdAt,date1.toString(),fromId,"false");
                            if (eventMessageFromId.equals(freind.getId())){
                                Log.d(TAG, "run: Message from this freind");
                                if (messageAdapter != null){
                                    messageAdapter.notifyDataSetChanged();

                                }
                                else{
                                    setupMessagesRecyclerView();
                                }
                                messagesRecyclerView.smoothScrollToPosition(messagesRecyclerView.getAdapter().getItemCount());
                            }




                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (newMessage.getFromId().equals(freind.getId())){
                                        messages.add(newMessage);
                                        if (messageAdapter != null){
                                            messageAdapter.notifyDataSetChanged();
                                        }
                                        else{
                                            setupMessagesRecyclerView();
                                        }
                                        messagesRecyclerView.smoothScrollToPosition(messagesRecyclerView.getAdapter().getItemCount());
                                    }
                                }
                            });



                        }


                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }



                }
            });



            socket.on("typing", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{


                                JSONObject jsonObject = (JSONObject) args[0];
                                String id = jsonObject.getString("id");

                                if (id.equals(freind.getId())){
                                    freindStatusTextView.setText("typing...");
                                    freindStatusTextView.setTextColor(ContextCompat.getColor(ChatActivity.this,R.color.green));

                                }

                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });

            socket.on("stop-typing", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{


                                JSONObject jsonObject = (JSONObject) args[0];
                                String id = jsonObject.getString("id");

                                if (id.equals(freind.getId())){

                                    if (isOnline){
                                        freindStatusTextView.setText("Online");
                                        freindStatusTextView.setTextColor(ContextCompat.getColor(ChatActivity.this,R.color.gray));

                                    }
                                    else{
                                        freindStatusTextView.setText("Online");
                                        freindStatusTextView.setTextColor(ContextCompat.getColor(ChatActivity.this,R.color.gray));

                                    }
                                }

                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });


                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
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

    private String getDateFromTimeStamp(String timeStamp1){
        try{
            Timestamp timestamp = new Timestamp(Long.parseLong(timeStamp1));

            Date dateObject = timestamp;

            String stringDate = dateObject.toString();

            stringDate = stringDate.substring(0, stringDate.length() - 3);



            return stringDate;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return timeStamp1;

    }

    private void setupMessagesRecyclerView(){
        Log.d(TAG, "setupMessagesRecyclerView: Size " + messages.size());
        messageAdapter = new MessageAdapter(messages,this,user.getId());
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messageAdapter);
    }


}

