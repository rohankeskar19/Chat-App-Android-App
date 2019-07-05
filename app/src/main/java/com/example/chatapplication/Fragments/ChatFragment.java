package com.example.chatapplication.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapplication.Adapters.ActiveUsersRecyclerViewAdapter;
import com.example.chatapplication.Adapters.ConversationsRecyclerViewAdapter;
import com.example.chatapplication.Database.ChatDBHelper;
import com.example.chatapplication.Models.Conversation;
import com.example.chatapplication.Models.Freind;
import com.example.chatapplication.Models.Message;
import com.example.chatapplication.Models.User;
import com.example.chatapplication.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment {

    View view;

    User user;

    ArrayList<Freind> activeFreinds = new ArrayList<>();

    RecyclerView activeUsersRecyclerView,conversationsRecyclerView;

    private static final String TAG = "ChatFragment";

    public ChatFragment() {

    }

    String token;

    Socket mSocket;

    private List<Conversation> conversationsList = new ArrayList<>();
    ConversationsRecyclerViewAdapter conversationsRecyclerViewAdapter;

    ChatDBHelper chatDBHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Hi");

        try{
            chatDBHelper = new ChatDBHelper(getContext());

            Log.d(TAG, "instance initializer: Hi");
            SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("authData",0);

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

    /*
    * @TODO: Fix buggy recyclerview (Recycler view is not scmooth scrolling)
    * */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chats,container,false);

        activeUsersRecyclerView = view.findViewById(R.id.recyclerViewActiveUsers);
        conversationsRecyclerView = view.findViewById(R.id.recyclerViewConversations);



        try{



            String url = getString(R.string.protocol) + "://" + getString(R.string.host) + ":" + getString(R.string.SOCKET_PORT);



            conversationsList = chatDBHelper.getAllConversation();
            setupConversationsRecyclerView();



            mSocket = IO.socket(url);


            mSocket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Transport transport = (Transport) args[0];
                    transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {

                            Map<String, List<String>> headers = (Map<String, List<String>>)args[0];
                            headers.put("authorization", Arrays.asList(token));


                        }
                    });
                }
            });

            mSocket.connect();



        }
        catch (Exception e){
            e.printStackTrace();
        }



        mSocket.on("activeUsers",activeUsers);
        mSocket.on("offline",offline);
        mSocket.on("online",online);
        mSocket.on("newMessage",newMessage);
        mSocket.on("newConversation",newConversation);

        return view;

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
        Log.d(TAG, "setupRecyclerView: " + activeFreinds);
        ActiveUsersRecyclerViewAdapter activeUsersRecyclerViewAdapter = new ActiveUsersRecyclerViewAdapter(activeFreinds,getContext());
        activeUsersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        activeUsersRecyclerView.setAdapter(activeUsersRecyclerViewAdapter);

    }



    /*
    * ----------------------------------------------------------------------SOCKET.IO-------------------------------------------------------------------------------------------------
    * */

    private Emitter.Listener newConversation = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try{
                JSONObject jsonObject = (JSONObject) args[0];
                Log.d(TAG, "run: new Conversation " + jsonObject.toString());
                String createdAt = jsonObject.getString("createdAt");
                String conversationId = jsonObject.getString("_id");

                Conversation conversation1 = chatDBHelper.checkIfConversationExistsById(conversationId);
                Log.d(TAG, "call: Conversation from database " + conversation1);
                if (conversation1 == null){
                    Log.d(TAG, "call: Conversation does not exists");
                    JSONArray usersArray = jsonObject.getJSONArray("Users");

                    for (int i = 0; i < usersArray.length(); i++){
                        JSONObject userObject = usersArray.getJSONObject(i);

                        String id = userObject.getString("user_id");

                        if (!id.equals(user.getId())){
                            String username = userObject.getString("username");
                            String name = userObject.getString("name");
                            String profileUrl = userObject.getString("profileUrl");

                            Conversation conversation = new Conversation(conversationId,id,username,name,profileUrl,"","","","","false",createdAt);

                            chatDBHelper.addConversation(conversation);

                        }

                    }
                    Log.d(TAG, "call: Activity " + getActivity());
                   if (getActivity() != null){
                       getActivity().runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               try{
                                   Log.d(TAG, "run: I ran runonuithread");
                                   conversationsList = chatDBHelper.getAllConversation();
                                   if(conversationsRecyclerViewAdapter != null && conversationsList.size() != 0){
                                       conversationsRecyclerViewAdapter.notifyDataSetChanged();
                                   }
                                   else{
                                       setupConversationsRecyclerView();
                                   }
                               }
                               catch (Exception e){
                                   e.printStackTrace();
                               }
                           }
                       });
                   }



                }else{
                    conversationsList.add(conversation1);
                    if (getActivity() != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Log.d(TAG, "run: I ran runonuithread2");
                                    conversationsList = chatDBHelper.getAllConversation();
                                    if(conversationsRecyclerViewAdapter != null && conversationsList.size() != 0){
                                        conversationsRecyclerViewAdapter.notifyDataSetChanged();
                                    }
                                    else{
                                        setupConversationsRecyclerView();
                                    }
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }




            }
            catch (Exception e){
                e.printStackTrace();
            }


        }
    };


    private Emitter.Listener newMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

                    try{
                        JSONObject jsonObject = (JSONObject) args[0];
                        Log.d(TAG, "run: newMessage" + jsonObject.toString());

                        String id = jsonObject.getString("_id");

                        final Message newMessage = chatDBHelper.checkIfMessageExists(id);

                        if (newMessage == null){
                            String conversationId = jsonObject.getString("conversationID");
                            String fromId = jsonObject.getString("fromId");
                            String toId = jsonObject.getString("toId");
                            String content = jsonObject.getString("content");
                            String createdAt = jsonObject.getString("createdAt");


                            Date date = new Timestamp(Long.parseLong(createdAt));

                            String[] splittedArray = date.toString().split(" ");

                            String[] splittedTime = splittedArray[1].split(":");

                            String time = splittedTime[0] + ":" + splittedTime[1];

                            Message message = new Message(id,conversationId,fromId,toId,content,createdAt,time);

                            chatDBHelper.addMessage(message);

                            Date date1 = new Timestamp(Long.parseLong(message.getCreatedAt()));

                            chatDBHelper.updateConversation(conversationId,content,createdAt,date1.toString(),fromId,"false");

                            if (getActivity() != null){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            conversationsList = chatDBHelper.getAllConversation();

                                            if(conversationsRecyclerViewAdapter != null){
                                                conversationsRecyclerViewAdapter.notifyDataSetChanged();
                                            }
                                            else{
                                                setupConversationsRecyclerView();
                                            }
                                        }
                                        catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                            }
                            else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Date date1 = new Timestamp(Long.parseLong(newMessage.getCreatedAt()));
                                        chatDBHelper.updateConversation(newMessage.getConversationId(),newMessage.getContent(),newMessage.getCreatedAt(),date1.toString(),newMessage.getFromId(),"false");
                                        conversationsList = chatDBHelper.getAllConversation();
                                        if(conversationsRecyclerViewAdapter != null){
                                            conversationsRecyclerViewAdapter.notifyDataSetChanged();
                                        }
                                        else{
                                            setupConversationsRecyclerView();
                                        }
                                    }
                                });
                            }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }




        }
    };


    private Emitter.Listener activeUsers = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            activeFreinds.clear();
                            Log.d(TAG, "run: Online");
                            JSONObject jsonObject = (JSONObject) args[0];
                            JSONArray jsonArray = (JSONArray) jsonObject.getJSONArray("activeUsers");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String id = jsonObject1.getString("user_id");
                                String username = jsonObject1.getString("username");
                                String profileUrl = jsonObject1.getString("profileUrl");
                                String name = jsonObject1.getString("name");



                                Freind freind = new Freind(id, username, profileUrl, name);

                                activeFreinds.add(freind);

                            }

                            setupRecyclerView();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        }
    };


    private Emitter.Listener offline = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            Log.d(TAG, "run: Offline");
                            JSONObject jsonObject = (JSONObject) args[0];

                            String id = jsonObject.getString("id");

                            for (Freind freind : activeFreinds){
                                if (freind.getId().equals(id)){
                                    activeFreinds.remove(freind);
                                }
                            }

                            setupRecyclerView();

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }



                    }
                });
            }

        }
    };

    private Emitter.Listener online = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try{

                            JSONObject jsonObject = (JSONObject) args[0];
                            JSONObject userObject = jsonObject.getJSONObject("user");

                            String id = userObject.getString("id");
                            String username = userObject.getString("username");
                            String profileUrl = userObject.getString("profileUrl");
                            String name = userObject.getString("name");



                            Freind freind = new Freind(id, username, profileUrl, name);

                            activeFreinds.add(freind);

                            setupRecyclerView();

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }



                    }
                });
            }

        }
    };







    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Started");

        conversationsList = chatDBHelper.getAllConversation();
        if(conversationsRecyclerViewAdapter != null){
            conversationsRecyclerViewAdapter.notifyDataSetChanged();
        }
        else{
            setupConversationsRecyclerView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        conversationsList = chatDBHelper.getAllConversation();
        if(conversationsRecyclerViewAdapter != null){
            conversationsRecyclerViewAdapter.notifyDataSetChanged();
        }
        else{
            setupConversationsRecyclerView();
        }
    }

    private void setupConversationsRecyclerView(){
        Log.d(TAG, "setupConversationsRecyclerView: " + Arrays.toString(conversationsList.toArray()));
        ConversationsRecyclerViewAdapter conversationsRecyclerViewAdapter = new ConversationsRecyclerViewAdapter(conversationsList,getContext(),user.getId());
        conversationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        conversationsRecyclerView.setAdapter(conversationsRecyclerViewAdapter);

    }


}
