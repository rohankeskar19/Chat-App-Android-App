package com.example.chatapplication.Helpers;

import android.util.Base64;

import com.example.chatapplication.Models.User;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class CalculationHelper {

    private static String[] split;
    public static User decodeToken(String token){
        try{
            String decodedToken = decoded(token);
            JSONObject jsonObject = new JSONObject(decodedToken);
            JSONObject user1 = jsonObject.getJSONObject("user");

            User user = new User(user1.getString("id"),user1.getString("name"),user1.getString("email"),user1.getString("username"),user1.getString("profileUrl"),"","");


            return user;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

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

}
