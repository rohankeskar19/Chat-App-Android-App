package com.example.chatapplication;

import android.content.Intent;
import android.content.SharedPreferences;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.chatapplication.Fragments.AddFreindsFragment;
import com.example.chatapplication.Fragments.ChatFragment;
import com.example.chatapplication.Fragments.FreindsFragment;
import com.example.chatapplication.Adapters.ViewPagerAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    String token;



    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("authData",0);

        token = sharedPreferences.getString("token","null");


        if (token.equals("null")){
            Intent intent = new Intent(this,AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }





        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),1);

        viewPagerAdapter.addFragment(new FreindsFragment(),"");
        viewPagerAdapter.addFragment(new ChatFragment(),"");
        viewPagerAdapter.addFragment(new AddFreindsFragment(),"");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_person);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_chat_bubble);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_group_add);

        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.select();

        viewPager.setOffscreenPageLimit(2);
       
    }

    




    
}

    
