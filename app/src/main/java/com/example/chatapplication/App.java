package com.example.chatapplication;

import android.app.Application;
import android.util.Log;

import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;


public class App extends Application {
    private static final String TAG = "App";
    @Override
    public void onCreate() {
        super.onCreate();
        EmojiManager.install(new GoogleEmojiProvider());
    }


}
