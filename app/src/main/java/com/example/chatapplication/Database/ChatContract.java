package com.example.chatapplication.Database;

import android.provider.BaseColumns;

public class ChatContract {

    private ChatContract() {}

    public static final class ConversationEntry implements BaseColumns {
        public static final String TABLE_NAME = "conversations_table";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_USER_NAME = "username";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_USER_PROFILE_URL = "user_profile_url";
        public static final String COLUMN_LATEST_MESSAGE_SENDER_ID = "latest_message_sender_id";
        public static final String COLUMN_LATEST_MESSAGE = "latest_message";
        public static final String COLUMN_LATEST_MESSAGE_TIMESTAMP = "latest_message_timestamp";
        public static final String COLUMN_LATEST_MESSAGE_DATE = "latest_message_date";
        public static final String COLUMN_CONVERSATION_OPENED = "conversation_opened";
        public static final String COLUMN_CREATED_AT = "created_at";
    }

    public static final class MessageEntry implements BaseColumns {
        public static final String TABLE_NAME = "messages_table";
        public static final String COLUMN_MESSAGE_ID = "message_id";
        public static final String COLUMN_CONVERSATION_ID = "conversation_id";
        public static final String COLUMN_FROM_ID = "from_id";
        public static final String COLUMN_TO_ID = "to_id";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_TIME = "time";
    }





}
