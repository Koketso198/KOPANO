package com.girl.code.hackathon.kopano.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.girl.code.hackathon.kopano.R;
import com.girl.code.hackathon.kopano.utils.AppPrefManager;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);

        //a handler, makes the screen pause then does an action
        //we wait 3 secs then open the next activity
        //Handler waits in milliseconds, 3 secs -> 3000millis
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //get user type from sharedPreferences
                //if user has logged in before and didn't log out
                //the userType can have the values
                //0 - company
                //1 - citizen
                //if the value is non of those then it means user hasn't logged in b4 or they logged out
                int userType = new AppPrefManager(SplashScreen.this).getIntVal("USER_TYPE");

                if (userType == 0){//company
                    //open company activity
                    startActivity(new Intent(SplashScreen.this, CompanyHomeActivity.class));
                }
                else if (userType == 1){//citizen
                    //open citizen activity
                    startActivity(new Intent(SplashScreen.this, CitizenHomeActivity.class));
                }
                //open login page, user hasn't logged in
                else startActivity(new Intent(SplashScreen.this, LogInActivity.class));

                //close the current activity afterwards
                finish();
            }
        }, 3000);
    }
}
