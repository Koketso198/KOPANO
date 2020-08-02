package com.girl.code.hackathon.kopano.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.girl.code.hackathon.kopano.R;
import com.girl.code.hackathon.kopano.utils.AppPrefManager;
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

public class LogInActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_login);

        //create references to the edit texts on login page
        final TextInputEditText edt_ph_no = findViewById(R.id.edt_login_ph_no);
        final TextInputEditText edt_password = findViewById(R.id.edt_login_password);

        //create reference to the radio buttons on login page
        final RadioButton radio_citizen = findViewById(R.id.radio_login_citizen);
        final RadioButton radio_company = findViewById(R.id.radio_login_company);

        //create reference to login button
        Button login = findViewById(R.id.btn_login_login);

        //create reference to the create account textview
        TextView createAcc = findViewById(R.id.txt_login_create_acc);

        //set on click listener to the login button
        //the login button will validate if the user entered the required data
        //and also check if user selected the user type
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make an assumption that a user entered the correct data
                boolean validDataEntered = true;

                //retrieve the data user entered
                String s_ph_no = edt_ph_no.getText().toString().trim();
                String s_password = edt_password.getText().toString().trim();
                final boolean isCitizen = radio_citizen.isChecked();
                boolean isCompany = radio_company.isChecked();

                //validate the data, if the required data is not entered
                //set validDataEntered to false
                //and display errors
                if (s_ph_no.isEmpty()){
                    edt_ph_no.setError("Enter phone number");
                    validDataEntered = false;
                }
                else if (!s_ph_no.matches("[0][0-9]{9}$")){
                    edt_ph_no.setError("Invalid phone number");
                    validDataEntered = false;
                }
                if (s_password.isEmpty()){
                    edt_password.setError("Enter Password");
                    validDataEntered = false;
                }

                if (!isCitizen && !isCompany){
                    showSnackBar(view, "Select the login type");
                    validDataEntered = false;
                }

                if (validDataEntered){
                    //login in user
                    //connect to server using OkHttpClient
                    OkHttpClient client = new OkHttpClient();

                    //build query according the selected login type (citizen or company)
                    String link = String.format("https://lamp.ms.wits.ac.za/home/s1908676/%s",
                            isCitizen ? "kopano_login_citizen.php" : "kopano_login_company.php");
                    //build query parameters according to the selected login type
                    String phoneKey = String.format("%s_phone_number", isCitizen ? "citizen" : "company");
                    String passwordKey = String.format("%s_password", isCitizen ? "citizen" : "company");

                    //build url to connect to and pass in query parameters
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(link).newBuilder();
                    urlBuilder.addQueryParameter(phoneKey, s_ph_no);
                    urlBuilder.addQueryParameter(passwordKey, s_password);

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

                                LogInActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (responseData.equals("0")){
                                            showToastNotification("Login Failed");
                                        }
                                        else{
                                            showToastNotification("Login Success");
                                            //create instance of AppPrefManager
                                            AppPrefManager appPrefManager = new AppPrefManager(LogInActivity.this);
                                            //save user id (will be used in other activities i.e. Make request) and
                                            //user type (1 - citizen, 0 - Company)
                                            appPrefManager.saveStringVal("USER_ID", responseData);
                                            appPrefManager.saveIntVal("USER_TYPE", isCitizen ? 1 : 0);

                                            //open citizenHomeActivity if logging in as citizen
                                            //open companyHomeActivity if logging in as company
                                            startActivity(new Intent(LogInActivity.this, isCitizen ?
                                                    CitizenHomeActivity.class : CompanyHomeActivity.class));

                                            //close the current activity
                                            finish();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        //set on click listener to the create account textview
        //the create account text view will open an alert dialog
        //dialog will request user to specify if they registering as
        //a company or citizen
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserTypeSelectionDialog();
            }
        });
    }

    //this function creates and shows an alert dialog
    //which asks the user if they registering as citizen
    //or a company
    public void showUserTypeSelectionDialog(){
        new AlertDialog.Builder(LogInActivity.this) //create alert dialog
                .setIcon(R.mipmap.ic_launcher) //add icon to alert dialog
                .setTitle("Register") //add title to the alert dialog
                .setMessage("Your registering as?") //alert dialog message
                .setPositiveButton("Citizen", new DialogInterface.OnClickListener() { //adds a
                    // positive button (appears on the right) with label citizen
                    // and adds on click listener, to the button, once pressed
                    //it takes us to RegisterCitizen activity
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(LogInActivity.this, RegisterCitizen.class));
                    }
                })
                .setNegativeButton("Company", new DialogInterface.OnClickListener() {//adds a
                    //negative button (appears on the left) with label company
                    // and adds on click listener, to the button, once pressed
                    //it takes us to RegisterCompany activity
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(LogInActivity.this, RegisterCompany.class));
                    }
                })
                .create().show();
    }

    //this function displays a snack bar on the bottom of the screen
    private void showSnackBar(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    //this function takes in a message
    //and displays it in a toast notification
    private void showToastNotification(String message){
        Toast.makeText(LogInActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
