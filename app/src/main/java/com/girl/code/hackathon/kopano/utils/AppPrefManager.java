package com.girl.code.hackathon.kopano.utils;

import android.content.Context;
import android.content.SharedPreferences;

//this class manages the shared preferences
//saves and retrieves data into and from shared preferences
public class AppPrefManager {

    private final SharedPreferences sharedPreferences;

    //constructor takes in a context
    public AppPrefManager(Context context){
        sharedPreferences = context.getSharedPreferences("KopanoSharedPreference", Context.MODE_PRIVATE);
    }

    //this function saves any string value
    //to shared preferences
    //it takes in a key and a value
    //and stores it in the shared preferences
    public void saveStringVal(String key, String val){
        sharedPreferences.edit().putString(key, val).apply();
    }

    //this function saves any int value
    //to shared preferences
    //it takes in a key and a value
    //and stores it in the shared preferences
    public void saveIntVal(String key, int val){
        sharedPreferences.edit().putInt(key, val).apply();
    }

    //this function retrieves any int from sharedPreferences
    //it takes in a key of the value we want
    public int getIntVal(String key){
        return sharedPreferences.getInt(key, -1);
    }

    //this function retrieves any string from sharedPreferences
    //it takes in a key of the value we want
    public String getStringVal(String key){
        return sharedPreferences.getString(key, "");
    }

    //clears(deletes) the whole shared preferences data
    public void clearAllValues(){
        sharedPreferences.edit().clear().apply();
    }
}
