package com.example.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Build;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.chatapplication.Models.User;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Timer;
import java.util.regex.Pattern;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";
    
    EditText loginIdEditText,loginPasswordEditText,firstNameEditText,lastNameEditText,emailEditText,usernameEditText,password1EditText,password2EditText;

    Button ctaButton;
    TextView changeStateTextView,loginTextView;

    LinearLayout viewContainer;


    String currentState = "login";


    RelativeLayout parentLayout;
    ImageView backButton;


    RequestQueue requestQueue;



    Animation fadeInRight,fadeInLeft,fadeUp,shake,fadeOutLeft,slideUp,slideDown;

    String firstName,lastName,email,username,password,password2;

    int currentPage = 0;
    boolean backButtonAnimated = false;
    boolean errorOccured = false;
    boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("authData",0);

        String token = sharedPreferences.getString("token","null");

        if (!token.equals("null")){
            Intent intent = new Intent(this,SetProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        requestQueue = Volley.newRequestQueue(this);

        fadeInRight = AnimationUtils.loadAnimation(this,R.anim.fade_in_right);
        fadeInLeft = AnimationUtils.loadAnimation(this,R.anim.fade_in_left);
        fadeOutLeft = AnimationUtils.loadAnimation(this,R.anim.fade_out_left);
        fadeUp = AnimationUtils.loadAnimation(this,R.anim.fade_up);
        shake = AnimationUtils.loadAnimation(this,R.anim.shake);
        slideUp = AnimationUtils.loadAnimation(this,R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(this,R.anim.slide_down);

        viewContainer = findViewById(R.id.viewContainer);

        parentLayout = findViewById(R.id.parentLayout);

        backButton = findViewById(R.id.backButton);
        init();
        setupView("none");

        if (!isNetworkConnected()){
            final Snackbar snackbar = Snackbar
                    .make(parentLayout, "No Internet available", Snackbar.LENGTH_INDEFINITE).setActionTextColor(ContextCompat.getColor(this,R.color.white));

            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });

            snackbar.show();
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentState.equals("register")){
                    if (currentPage > 0){
                        currentPage--;
                        setupView("back");
                    }
                    if (currentPage == 0){
                        currentState = "login";
                        setupView("back");
                    }
                }


            }
        });




    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }





    private void setupView(String condition){
        if(currentState.equals("login")){
            if (backButtonAnimated){
                backButton.startAnimation(fadeOutLeft);
                init();
            }
            backButtonAnimated = false;
            ctaButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.GONE);
            Log.d(TAG, "setupView: Called Login");
            viewContainer.removeAllViews();


            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,70);

            loginIdEditText.setLayoutParams(layoutParams);
            loginPasswordEditText.setLayoutParams(layoutParams);

            loginIdEditText.setHint("Enter your email or username");
            loginPasswordEditText.setHint("Enter your password");
            loginPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            LinearLayout.LayoutParams ctaButtonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ctaButtonParams.setMargins(0,50,0,0);

            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textViewParams.setMargins(0,70,0,0);

            ctaButton.setBackground(getResources().getDrawable(R.drawable.button_bg));
            ctaButton.setText("Login");
            ctaButton.setLayoutParams(ctaButtonParams);


            changeStateTextView.setText("Create account");
            changeStateTextView.setTextColor(getResources().getColor(R.color.black));
            changeStateTextView.setTextSize(14);

            changeStateTextView.setLayoutParams(textViewParams);

            ctaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: " + currentState);
                    if (currentState.equals("login")){
                        Log.d(TAG, "onClick: Ola");

                        String userid = loginIdEditText.getText().toString().trim();
                        String password = loginPasswordEditText.getText().toString().trim();

                        if (userid.equals("") || password.equals("")){
                            if (userid.equals("")){
                                loginIdEditText.setError("You must enter your username or email to continue");
                            }
                            if (password.equals("")){
                                loginPasswordEditText.setError("You must enter your password to continue");
                            }

                        }
                        else{
                            String url = getString(R.string.protocol) + "://" + getString(R.string.host) + ":" + getString(R.string.API_PORT) + "/api/user/login";
                            try{
                                JSONObject jsonObject = new JSONObject();

                                if (isEmail(userid)){
                                    jsonObject.put("email",userid);
                                }
                                else{
                                    jsonObject.put("username",userid);
                                }
                                jsonObject.put("password",password);

                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try{
                                            String token = response.getString("token");
                                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("authData",0);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("token",token);
                                            editor.apply();

                                            Intent intent = new Intent(AuthActivity.this,SetProfileActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

                                        }
                                        catch (Exception e){
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        try{
                                            if (error.networkResponse.data != null){
                                                String errorString = new String(error.networkResponse.data);
                                                JSONObject errorObject = new JSONObject(errorString);
                                                String responseError = errorObject.getString("error");

                                                Snackbar.make(parentLayout,responseError,Snackbar.LENGTH_SHORT).show();
                                            }
                                        }
                                        catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                requestQueue.add(jsonObjectRequest);
                            }
                            catch (Exception e){

                            }
                        }

                    }

                }
            });


            viewContainer.addView(loginIdEditText);
            viewContainer.addView(loginPasswordEditText);
            viewContainer.addView(ctaButton);
            viewContainer.addView(changeStateTextView);


            viewContainer.startAnimation(fadeUp);




            changeStateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorOccured = false;
                    if (currentState.equals("login")){

                        currentState = "register";
                        
                        currentPage = 1;
                        setupView("forward");
                    }
                    else{
                        Log.d(TAG, "onClick: Current Page " + currentPage + " " + errorOccured);
                        switch (currentPage){
                            case 1:
                                firstName = firstNameEditText.getText().toString().trim();
                                lastName = lastNameEditText.getText().toString().trim();

                                int length = firstName.length() + lastName.length();

                                if (firstName.equals("") || lastName.equals("")){
                                    if (firstName.equals("")){
                                        firstNameEditText.setError("You must enter your first name to continue");
                                    }
                                    if (lastName.equals("")){
                                        lastNameEditText.setError("You must enter your last name to continue");
                                    }
                                    errorOccured = true;
                                }
                                else{
                                    if (length < 1 || length > 40){
                                        firstNameEditText.setError("Name must be between 1-40 characters long");
                                        errorOccured = true;
                                    }
                                }
                                break;
                            case 2:
                                email = emailEditText.getText().toString().trim();

                                if (email.equals("")){
                                    emailEditText.setError("You must enter your email to continue");
                                    errorOccured = true;
                                }
                                else if (!isEmail(email)){
                                        emailEditText.setError("Enter a valid email");
                                        errorOccured = true;

                                }
                                else{

                                    try{
                                        String url = getString(R.string.protocol) + "://" + getString(R.string.host) + ":" + getString(R.string.API_PORT) + "/api/user/email-check";
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("email",email);
                                        Log.d(TAG, "onClick: " + jsonObject.toString());
                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try{
                                                    isProcessing = false;
                                                    Log.d(TAG, "onResponse: " + response.toString());
                                                    String validation = response.getString("email");

                                                    if (validation.equals("valid")){

                                                        currentPage++;
                                                        setupView("forward");
                                                        return;
                                                    }
                                                    else{
                                                        emailEditText.setCompoundDrawables(null,null,null,null);
                                                        emailEditText.setError("Email already register try another email");
                                                        errorOccured = true;
                                                    }

                                                }
                                                catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                error.printStackTrace();
                                                errorOccured = true;
                                                isProcessing = false;
                                            }
                                        });
                                        isProcessing = true;
                                        requestQueue.add(jsonObjectRequest);


                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }

                                break;
                            case 3:
                                username = usernameEditText.getText().toString().trim();

                                if (username.equals("")){
                                    usernameEditText.setError("You must enter your username to continue");
                                    errorOccured = true;
                                }
                                else if (username.length() < 6 || username.length() > 20){
                                    usernameEditText.setError("Username must be between 6-20 characters long");
                                    errorOccured = true;
                                }
                                else{

                                    try{
                                        String url = getString(R.string.protocol) + "://" + getString(R.string.host) + ":" + getString(R.string.API_PORT) + "/api/user/username-check";
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("username",username);
                                        Log.d(TAG, "onClick: " + jsonObject.toString());
                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try{
                                                    isProcessing = false;
                                                    Log.d(TAG, "onResponse: " + response.toString());
                                                    String validation = response.getString("username");

                                                    if (validation.equals("valid")){
                                                        currentPage++;
                                                        setupView("forward");
                                                        return;
                                                    }
                                                    else{
                                                        usernameEditText.setCompoundDrawables(null,null,null,null);
                                                        usernameEditText.setError("Username already register try another username");
                                                        errorOccured = true;
                                                    }

                                                }
                                                catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                error.printStackTrace();
                                                errorOccured = true;
                                                isProcessing = false;
                                            }
                                        });
                                        isProcessing = true;
                                        requestQueue.add(jsonObjectRequest);


                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                                break;
                            case 4:
                                password = password1EditText.getText().toString().trim();
                                password2 = password2EditText.getText().toString().trim();

                                if (password.equals("") || password2.equals("")){
                                    if (password.equals("")){
                                        password1EditText.setError("You must enter your password to continue");
                                    }
                                    if (password2.equals("")){
                                        password2EditText.setError("You must enter your password to continue");
                                    }
                                    errorOccured = true;
                                }
                                else if (!password.equals(password2)){
                                        password2EditText.setError("Passwords do not match");
                                        errorOccured = true;

                                }
                                else{
                                    if (password.length() < 6 || password.length() > 30){
                                        password1EditText.setError("Password must be between 6-30 characters long");
                                        password2EditText.setError("Password must be between 6-30 characters long");
                                        errorOccured = true;
                                    }
                                }

                                break;

                        }
                        Log.d(TAG, "onClick: Hi1");
                        if (errorOccured){
                            Log.d(TAG, "onClick: Shake");
                            viewContainer.startAnimation(shake);
                        }
                        Log.d(TAG, "onClick: Outside " + errorOccured  + " " + isProcessing);
                        if (!errorOccured && !isProcessing){
                            Log.d(TAG, "onClick: Inside");
                            if (currentPage <= 5){
                                Log.d(TAG, "onClick: Smaller than 5");
                                currentPage++;
                                setupView("forward");
                            }
                        }


                    }
                }
            });


        }
        else{
            backButton.setVisibility(View.VISIBLE);
            if (!backButtonAnimated){
                backButton.startAnimation(fadeInRight);
                backButtonAnimated = true;
            }

            ctaButton.setVisibility(View.GONE);
            Log.d(TAG, "setupView: Called Register");
            switch (currentPage){
                case 1:loadPage1(condition);
                    break;
                case 2:loadPage2(condition);
                    break;
                case 3:loadPage3(condition);
                    break;
                case 4:loadPage4(condition);
                    break;
                case 5:

                    loadPage5(condition);
                    break;

            }
        }
    }

    public void loadPage1(String condition){


        firstNameEditText.setHint("Enter your first name");
        lastNameEditText.setHint("Enter your last name");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10,0,0,70);

        firstNameEditText.setLayoutParams(layoutParams);
        lastNameEditText.setLayoutParams(layoutParams);


        changeStateTextView.setText("Next");
        changeStateTextView.setTextColor(getResources().getColor(R.color.black));
        changeStateTextView.setTextSize(20);


        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.setMargins(0,50,0,0);
        changeStateTextView.setLayoutParams(textViewParams);

        viewContainer.removeAllViews();

        viewContainer.addView(firstNameEditText);
        viewContainer.addView(lastNameEditText);
        viewContainer.addView(changeStateTextView);


        if (condition.equals("forward")){
            viewContainer.startAnimation(fadeInRight);
        }
        else if (condition.equals("back")){
            viewContainer.startAnimation(fadeInLeft);
        }

    }
    public void loadPage2(String condition){



        emailEditText.setHint("Enter your email");


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,70);

        emailEditText.setLayoutParams(layoutParams);



        changeStateTextView.setText("Next");
        changeStateTextView.setTextColor(getResources().getColor(R.color.black));
        changeStateTextView.setTextSize(20);


        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.setMargins(0,50,0,0);
        changeStateTextView.setLayoutParams(textViewParams);

        viewContainer.removeAllViews();
        viewContainer.addView(emailEditText);
        viewContainer.addView(changeStateTextView);
        if (condition.equals("forward")){
            viewContainer.startAnimation(fadeInRight);
        }
        else if (condition.equals("back")){
            viewContainer.startAnimation(fadeInLeft);
        }

    }
    public void loadPage3(String condition){



        usernameEditText.setHint("Enter your username");


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,70);

        usernameEditText.setLayoutParams(layoutParams);



        changeStateTextView.setText("Next");
        changeStateTextView.setTextColor(getResources().getColor(R.color.black));
        changeStateTextView.setTextSize(20);


        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.setMargins(0,50,0,0);
        changeStateTextView.setLayoutParams(textViewParams);

        viewContainer.removeAllViews();
        viewContainer.addView(usernameEditText);
        viewContainer.addView(changeStateTextView);
        if (condition.equals("forward")){
            viewContainer.startAnimation(fadeInRight);
        }
        else if (condition.equals("back")){
            viewContainer.startAnimation(fadeInLeft);
        }

    }
    public void loadPage4(String condition){

        password1EditText.setHint("Enter Password");
        password2EditText.setHint("Confirm Password");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,70);

        password1EditText.setLayoutParams(layoutParams);
        password2EditText.setLayoutParams(layoutParams);


        password1EditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password2EditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);


        changeStateTextView.setText("Finish");
        changeStateTextView.setTextColor(getResources().getColor(R.color.black));
        changeStateTextView.setTextSize(20);


        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.setMargins(0,50,0,0);
        changeStateTextView.setLayoutParams(textViewParams);

        viewContainer.removeAllViews();
        viewContainer.addView(password1EditText);
        viewContainer.addView(password2EditText);
        viewContainer.addView(changeStateTextView);
        if (condition.equals("forward")){
            viewContainer.startAnimation(fadeInRight);
        }
        else if (condition.equals("back")){
            viewContainer.startAnimation(fadeInLeft);
        }

    }
    public void loadPage5(String condition){
        backButton.setVisibility(View.GONE);
        viewContainer.removeAllViews();




        changeStateTextView.setText("Creating account...");
        changeStateTextView.setTextColor(getResources().getColor(R.color.black));
        changeStateTextView.setTextSize(20);


        ProgressBar progressBar = new ProgressBar(this);

        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.setLayoutParams(textViewParams);

        textViewParams.setMargins(0,30,0,0);
        changeStateTextView.setLayoutParams(textViewParams);


        viewContainer.addView(progressBar);
        viewContainer.addView(changeStateTextView);

        if (condition.equals("forward")){
            viewContainer.startAnimation(fadeInRight);
        }
        else if (condition.equals("back")){
            viewContainer.startAnimation(fadeInLeft);
        }


        register();



    }

    public void register(){
        try{
            JSONObject payloadData = new JSONObject();
            String name = firstName + " " + lastName;
            payloadData.put("name",name);
            payloadData.put("email",email);
            payloadData.put("username",username);
            payloadData.put("password",password);
            payloadData.put("password2",password2);

            String url = getString(R.string.protocol) + "://" + getString(R.string.host) + ":" + getString(R.string.API_PORT) + "/api/user/register";


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, payloadData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{

                        currentState = "login";
                        currentPage = 1;
                        setupView("none");

                        Snackbar.make(parentLayout,"Account created you can now login",Snackbar.LENGTH_SHORT).show();

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    currentState = "login";
                    currentPage = 1;
                    setupView("none");
                    Toast.makeText(AuthActivity.this,"Couldn't register account try again",Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    public static boolean isEmail(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }


    private void init(){
        if (currentState.equals("login")){
            backButton.setVisibility(View.GONE);
        }

        ctaButton = new Button(this);
        ctaButton.setTextColor(getResources().getColor(R.color.white));
        ctaButton.setAllCaps(false);
        loginIdEditText = new EditText(this);
        loginPasswordEditText = new EditText(this);
        loginTextView = new TextView(this);
        changeStateTextView = new TextView(this);
        firstNameEditText = new EditText(this);
        lastNameEditText = new EditText(this);
        emailEditText = new EditText(this);
        usernameEditText = new EditText(this);
        password1EditText = new EditText(this);
        password2EditText = new EditText(this);

    }


    @Override
    public void onBackPressed() {
        if (currentState.equals("login")){
            super.onBackPressed();
        }
        else{
            if (currentState.equals("register")){
                currentPage--;
                if (currentPage == 0){
                    currentState = "login";
                    setupView("none");
                }
                else{

                    setupView("back");
                }
            }
        }


    }
}
