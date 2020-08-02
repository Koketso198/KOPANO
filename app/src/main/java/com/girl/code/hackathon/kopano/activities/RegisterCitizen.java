package com.girl.code.hackathon.kopano.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.girl.code.hackathon.kopano.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterCitizen extends Activity {

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_register_citizen);

        //create references to the edit texts on register citizen
        final TextInputEditText edt_name = findViewById(R.id.edt_register_citizen_name);
        final TextInputEditText edt_surname = findViewById(R.id.edt_register_citizen_surname);
        final TextInputEditText edt_ph_no = findViewById(R.id.edt_register_citizen_ph_no);
        final TextInputEditText edt_email = findViewById(R.id.edt_register_citizen_email_address);
        final TextInputEditText edt_password = findViewById(R.id.edt_register_citizen_password);

        //create references to the spinner on register citizen
        final Spinner spinner_preferred_language = findViewById(R.id.spinner_register_citizen_language);
        final Spinner spinner_role_in_community = findViewById(R.id.spinner_register_citizen_role);

        //create reference to register button
        Button register_citizen = findViewById(R.id.btn_register_citizen_register);

        //set on click listener to the register button
        //the register button will validate if the user entered the required data
        //and also if they selected preferred languages and role in community
        register_citizen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make an assumption that a user entered correct data
                boolean validDataEntered = true;

                //retrieve the data the user entered
                String s_name = edt_name.getText().toString().trim();
                String s_surname = edt_surname.getText().toString().trim();
                String s_ph_no = edt_ph_no.getText().toString().trim();
                String s_email = edt_email.getText().toString().trim();
                String s_password = edt_password.getText().toString().trim();

                int preferredLanguagePos = spinner_preferred_language.getSelectedItemPosition();
                int roleInCommunityPos = spinner_role_in_community.getSelectedItemPosition();

                String preferredLanguage = "";
                String roleInCommunity = "";

                //validate the data, if the required data is not entered
                //set validDataEntered to false
                //and display errors
                if (s_name.isEmpty()){
                    edt_name.setError("Enter name");
                    validDataEntered = false;
                }
                if (s_surname.isEmpty()){
                    edt_surname.setError("Enter surname");
                    validDataEntered = false;
                }
                if (s_ph_no.isEmpty()){
                    edt_ph_no.setError("Enter phone number");
                    validDataEntered = false;
                }
                else if (!s_ph_no.matches("[0][0-9]{9}$")){
                    edt_ph_no.setError("Invalid phone number");
                    validDataEntered = false;
                }
                if (!s_email.isEmpty()){
                    if (!s_email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
                        edt_email.setError("Invalid email");
                        validDataEntered = false;
                    }
                }else{
                    s_email = "none";
                }
                if (s_password.isEmpty()){
                    edt_password.setError("Enter Password");
                    validDataEntered = false;
                }

                if (preferredLanguagePos == 0){
                    showSnackBar(view, "select preferred language");
                    validDataEntered = false;
                }
                else if (roleInCommunityPos == 0){
                    showSnackBar(view, "select role in community");
                    validDataEntered = false;
                }

                //check if the assumption we made is correct
                if (validDataEntered){
                    //get the string the user selected for
                    //preferred language
                    //and role in community
                    preferredLanguage = spinner_preferred_language.getSelectedItem().toString();
                    roleInCommunity = spinner_role_in_community.getSelectedItem().toString();

                    //register citizen
                    //connect to server using OkHttpClient
                    OkHttpClient client = new OkHttpClient();

                    //build url to connect to and pass in query parameters
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s1908676/kopano_register_citizen.php").newBuilder();
                    urlBuilder.addQueryParameter("citizen_name", s_name);
                    urlBuilder.addQueryParameter("citizen_surname", s_surname);
                    urlBuilder.addQueryParameter("citizen_preffered_communication_language", preferredLanguage);
                    urlBuilder.addQueryParameter("citizen_phone_number", s_ph_no);
                    urlBuilder.addQueryParameter("citizen_email_address", s_email);
                    urlBuilder.addQueryParameter("citizen_community_role", roleInCommunity);
                    urlBuilder.addQueryParameter("citizen_password", s_password);

                    //convert the built link with its parameters to string
                    String url = urlBuilder.build().toString();

                    //build a request object
                    Request request = new Request.Builder().url(url).build();

                    //use client to send request to server
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (response.isSuccessful()) {//server gave a response
                                //get the data the server sent
                                final String responseData = response.body().string().trim();

                                RegisterCitizen.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (responseData.equals("1")){
                                            showToastNotification("Registration success");
                                            finish();
                                        }
                                        else{
                                            showToastNotification("Registration failed");
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    //this function displays a snack bar on the bottom of the screen
    private void showSnackBar(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    private void showToastNotification(String message){
        Toast.makeText(RegisterCitizen.this, message, Toast.LENGTH_SHORT).show();
    }


}
