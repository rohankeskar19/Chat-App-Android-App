package com.example.chatapplication.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.chatapplication.Database.ChatContract.*;
import com.example.chatapplication.Models.Conversation;
import com.example.chatapplication.Models.Message;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class ChatDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "chat.db";
    public static final int DATABASE_VERSION = 1;

    private static final String TAG = "ChatDBHelper";

    public ChatDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQLITE_CREATE_CONVERSATIONS_TABLE = "CREATE TABLE " + ConversationEntry.TABLE_NAME + " (" + ConversationEntry.COLUMN_ID + " VARCHAR(25) NOT NULL, " + ConversationEntry.COLUMN_USER_ID + " VARCHAR(25) NOT NULL, " + ConversationEntry.COLUMN_USER_NAME + " VARCHAR(20) NOT NULL, " + ConversationEntry.COLUMN_NAME + " VARCHAR(40) NOT NULL, " +  ConversationEntry.COLUMN_USER_PROFILE_URL + " VARCHAR(500) NOT NULL, " +  ConversationEntry.COLUMN_LATEST_MESSAGE_SENDER_ID + " VARCHAR(20) NOT NULL, " + ConversationEntry.COLUMN_LATEST_MESSAGE + " VARCHAR(65535) NOT NULL, " + ConversationEntry.COLUMN_LATEST_MESSAGE_TIMESTAMP + " VARCHAR(25) NOT NULL, " + ConversationEntry.COLUMN_LATEST_MESSAGE_DATE + " VARCHAR(10) NOT NULL, " + ConversationEntry.COLUMN_CONVERSATION_OPENED + " VARCHAR(10) NOT NULL, " + ConversationEntry.COLUMN_CREATED_AT + " VARCHAR(40) NOT NULL);";
        final String SQLITE_CREATE_MESSAGES_TABLE = "CREATE TABLE " + MessageEntry.TABLE_NAME + " ("  + MessageEntry.COLUMN_MESSAGE_ID + " VARCHAR(30) NOT NULL, " + MessageEntry.COLUMN_CONVERSATION_ID + " VARCHAR(25) NOT NULL, " + MessageEntry.COLUMN_FROM_ID + " VARCHAR(20) NOT NULL, " + MessageEntry.COLUMN_TO_ID + " VARCHAR(20) NOT NULL, " + MessageEntry.COLUMN_CONTENT + " VARCHAR(65535) NOT NULL, "  + MessageEntry.COLUMN_CREATED_AT + " VARCHAR(30) NOT NULL, " + MessageEntry.COLUMN_TIME + " VARCHAR(10) NOT NULL);";

        db.execSQL(SQLITE_CREATE_CONVERSATIONS_TABLE);
        db.execSQL(SQLITE_CREATE_MESSAGES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ConversationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MessageEntry.TABLE_NAME);
        onCreate(db);
    }

    public void addConversation(Conversation conversation){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "addConversation: " + conversation.toString());
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConversationEntry.COLUMN_ID,conversation.getConversationId());
        contentValues.put(ConversationEntry.COLUMN_USER_ID,conversation.getUserId());
        contentValues.put(ConversationEntry.COLUMN_USER_NAME,conversation.getUsername());
        contentValues.put(ConversationEntry.COLUMN_NAME,conversation.getName());
        contentValues.put(ConversationEntry.COLUMN_USER_PROFILE_URL,conversation.getUserProfileUrl());
        contentValues.put(ConversationEntry.COLUMN_LATEST_MESSAGE_SENDER_ID, conversation.getLastMessageSenderId());
        contentValues.put(ConversationEntry.COLUMN_LATEST_MESSAGE,conversation.getLastMessage());
        contentValues.put(ConversationEntry.COLUMN_LATEST_MESSAGE_TIMESTAMP,conversation.getLastMessageTimeStamp());
        contentValues.put(ConversationEntry.COLUMN_LATEST_MESSAGE_DATE,conversation.getLastMessageDate());
        contentValues.put(ConversationEntry.COLUMN_CONVERSATION_OPENED,conversation.getConversationOpened());
        contentValues.put(ConversationEntry.COLUMN_CREATED_AT,conversation.getCreatedAt());

        db.insert(ConversationEntry.TABLE_NAME,null,contentValues);

        db.close();

    }

    public Conversation checkIfConversationExists(String user_id){
        Log.d(TAG, "checkIfConversationExists: Called");
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(ConversationEntry.TABLE_NAME, new String[]
                                {ConversationEntry.COLUMN_ID, ConversationEntry.COLUMN_USER_ID, ConversationEntry.COLUMN_USER_NAME, ConversationEntry.COLUMN_NAME, ConversationEntry.COLUMN_USER_PROFILE_URL,ConversationEntry.COLUMN_LATEST_MESSAGE_SENDER_ID,ConversationEntry.COLUMN_LATEST_MESSAGE, ConversationEntry.COLUMN_LATEST_MESSAGE_TIMESTAMP, ConversationEntry.COLUMN_LATEST_MESSAGE_DATE, ConversationEntry.COLUMN_CONVERSATION_OPENED,ConversationEntry.COLUMN_CREATED_AT},
                        ConversationEntry.COLUMN_USER_ID + "=?", new String[] {user_id},null, null, null, null);
        if (cursor != null && cursor.moveToFirst()){
            cursor.moveToFirst();
            Log.d(TAG, "checkIfConversationExists: " + cursor.getString(0));
            Log.d(TAG, "checkIfConversationExists: " + cursor.getString(1));
            Log.d(TAG, "checkIfConversationExists: " + cursor.getString(2));
            Log.d(TAG, "checkIfConversationExists: " + cursor.getString(3));
            Log.d(TAG, "checkIfConversationExists: " + cursor.getString(4));
            Log.d(TAG, "checkIfConversationExists: " + cursor.getString(5));
            Log.d(TAG, "checkIfConversationExists: " + cursor.getString(6));
            Log.d(TAG, "checkIfConversationExists: " + cursor.getString(7));

            Conversation conversation = new Conversation(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10));
            cursor.close();
            return conversation;
        }

        return null;

    }

    public Conversation checkIfConversationExistsById(String id){
        Log.d(TAG, "checkIfConversationExists: Called");
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(ConversationEntry.TABLE_NAME, new String[]
                        {ConversationEntry.COLUMN_ID, ConversationEntry.COLUMN_USER_ID, ConversationEntry.COLUMN_USER_NAME, ConversationEntry.COLUMN_NAME, ConversationEntry.COLUMN_USER_PROFILE_URL,ConversationEntry.COLUMN_LATEST_MESSAGE, ConversationEntry.COLUMN_LATEST_MESSAGE_TIMESTAMP, ConversationEntry.COLUMN_LATEST_MESSAGE_DATE, ConversationEntry.COLUMN_CREATED_AT},
                ConversationEntry.COLUMN_ID + "=?", new String[] {id},null, null, null, null);
        if (cursor != null && cursor.moveToFirst()){
            cursor.moveToFirst();
            Log.d(TAG, "checkIfConversationExists: " + cursor.getString(0));
            Log.d(TAG, "checkIfConversationExists: " + cursor.getString(1));
            Log.d(TAG, "checkIfConversationExists: " + cursor.getString(2));
            Log.d(TAG, "checkIfConversationExists: " + cursor.getString(3));
            Log.d(TAG, "checkIfConversationExists: " + cursor.getString(4));
            Log.d(TAG, "checkIfConversationExists: " + cursor.getString(5));
            Log.d(TAG, "checkIfConversationExists: " + cursor.getString(6));
            Log.d(TAG, "checkIfConversationExists: " + cursor.getString(7));

            Conversation conversation = new Conversation(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10));
            cursor.close();
            return conversation;
        }

        return null;

    }

    public List<Conversation> getAllConversation(){
        Log.d(TAG, "getAllConversation: Called");
        List<Conversation> conversationList = new ArrayList<>();

        String query = "SELECT * FROM " + ConversationEntry.TABLE_NAME + " ORDER BY " + ConversationEntry.COLUMN_LATEST_MESSAGE_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if (cursor.moveToFirst()){
            do{
                Log.d(TAG, "checkIfConversationExists: " + cursor.getString(0));
                Log.d(TAG, "checkIfConversationExists: " + cursor.getString(1));
                Log.d(TAG, "checkIfConversationExists: " + cursor.getString(2));
                Log.d(TAG, "checkIfConversationExists: " + cursor.getString(3));
                Log.d(TAG, "checkIfConversationExists: " + cursor.getString(4));
                Log.d(TAG, "checkIfConversationExists: " + cursor.getString(5));
                Log.d(TAG, "checkIfConversationExists: " + cursor.getString(6));
                Log.d(TAG, "checkIfConversationExists: " + cursor.getString(7));
                Log.d(TAG, "checkIfConversationExists: " + cursor.getString(8));
                Conversation conversation = new Conversation(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10));

                conversationList.add(conversation);
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        return conversationList;
    }

    public void updateConversation(String id,String lastMessage,String lastMessageTimeStamp,String lastMessageDate,String lastMessageSenderId,String opened){
        Log.d(TAG, "updateConversation: Called");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(ConversationEntry.COLUMN_LATEST_MESSAGE_SENDER_ID,lastMessageSenderId);
        contentValues.put(ConversationEntry.COLUMN_LATEST_MESSAGE,lastMessage);
        contentValues.put(ConversationEntry.COLUMN_LATEST_MESSAGE_TIMESTAMP,lastMessageTimeStamp);
        contentValues.put(ConversationEntry.COLUMN_LATEST_MESSAGE_DATE,lastMessageDate);
        contentValues.put(ConversationEntry.COLUMN_CONVERSATION_OPENED,opened);

        db.update(ConversationEntry.TABLE_NAME,contentValues,ConversationEntry.COLUMN_ID + "=?",new String[]{id});

    }

    public void setConversationOpened(String id,String opened){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ConversationEntry.COLUMN_CONVERSATION_OPENED,opened);

        db.update(ConversationEntry.TABLE_NAME,contentValues,ConversationEntry.COLUMN_ID + "=?",new String[]{id});
    }


    public Message checkIfMessageExists(String id){
        SQLiteDatabase db = this.getReadableDatabase();



        Cursor cursor = db.query(MessageEntry.TABLE_NAME,new String[] {MessageEntry.COLUMN_MESSAGE_ID, MessageEntry.COLUMN_CONVERSATION_ID, MessageEntry.COLUMN_FROM_ID, MessageEntry.COLUMN_TO_ID, MessageEntry.COLUMN_CONTENT, MessageEntry.COLUMN_CREATED_AT, MessageEntry.COLUMN_TIME},MessageEntry.COLUMN_MESSAGE_ID + "=?",new String[]{id},null, null, null, null);

        if (cursor != null && cursor.moveToFirst()){
            cursor.moveToFirst();

            Message message = new Message(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
            cursor.close();
            return message;
        }

        return null;
    }


    public void addMessage(Message message){
        Log.d(TAG, "addMessage: Called");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MessageEntry.COLUMN_MESSAGE_ID, message.getId());
        contentValues.put(MessageEntry.COLUMN_CONVERSATION_ID,message.getConversationId());
        contentValues.put(MessageEntry.COLUMN_FROM_ID,message.getFromId());
        contentValues.put(MessageEntry.COLUMN_TO_ID,message.getToId());
        contentValues.put(MessageEntry.COLUMN_CONTENT,message.getContent());
        contentValues.put(MessageEntry.COLUMN_CREATED_AT,message.getCreatedAt());
        contentValues.put(MessageEntry.COLUMN_TIME,message.getTime());

        db.insert(MessageEntry.TABLE_NAME,null,contentValues);

        db.close();
    }

    public ArrayList<Message> getAllMessages(String conversationID){
        Log.d(TAG, "getAllMessages: Called");
        ArrayList<Message> messageList = new ArrayList<>();

        String query = "SELECT * FROM " + MessageEntry.TABLE_NAME + " WHERE " + MessageEntry.COLUMN_CONVERSATION_ID + "= '" + conversationID + "'";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if (cursor.moveToFirst()){
            do{
                Message message = new Message(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));

                messageList.add(message);
            }
            while (cursor.moveToNext());
        }

        cursor.close();


        return messageList;
    }

    public void dropTables(){
        Log.d(TAG, "dropTables: Called");
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + ConversationEntry.TABLE_NAME);
        db.execSQL("DELETE FROM " + MessageEntry.TABLE_NAME);
    }


}
