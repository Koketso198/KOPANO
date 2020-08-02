package com.girl.code.hackathon.kopano.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.girl.code.hackathon.kopano.R;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterCompany extends Activity {

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_register_company);

        //create references to the edittexts
        final TextInputEditText edt_reg_no = findViewById(R.id.edt_register_company_reg_no);
        final TextInputEditText edt_name = findViewById(R.id.edt_register_company_name);
        final TextInputEditText edt_ph_no = findViewById(R.id.edt_register_company_ph_no);
        final TextInputEditText edt_email = findViewById(R.id.edt_register_company_email_address);
        final TextInputEditText edt_password = findViewById(R.id.edt_register_company_password);
        final TextInputEditText edt_web = findViewById(R.id.edt_register_company_website);
        final TextInputEditText edt_employees = findViewById(R.id.edt_register_company_no_employee);
        final TextInputEditText edt_desc = findViewById(R.id.edt_register_company_description);

        //create reference to the checkbox
        final CheckBox company_hiring = findViewById(R.id.chckbox_register_company_hiring);

        //create reference to the register button
        Button register_company = findViewById(R.id.btn_register_company_register);

        //set on click listener to the register button
        //the register button will validate if the user entered the required data
        register_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make an assumption that a user entered correct data
                boolean validDataEntered = true;

                //retrieve the data the user entered
                String s_reg_no = edt_reg_no.getText().toString().trim();
                String s_name = edt_name.getText().toString().trim();
                String s_ph_no = edt_ph_no.getText().toString().trim();
                String s_email = edt_email.getText().toString().trim();
                String s_password = edt_password.getText().toString().trim();
                String s_web = edt_web.getText().toString().trim();
                String s_employees = edt_employees.getText().toString().trim();
                String s_desc = edt_desc.getText().toString().trim();

                String companyHiring = company_hiring.isChecked() ? "1" : "0";

                //validate the data, if the required data is not entered
                //set validDataEntered to false
                //and display errors
                if (s_reg_no.isEmpty()){
                    edt_reg_no.setError("Enter company registration number");
                    validDataEntered = false;
                }
                if (s_name.isEmpty()){
                    edt_name.setError("Enter name");
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
                if (s_email.isEmpty()){
                    edt_email.setError("Enter email");
                    validDataEntered = false;
                }
                else if (!s_email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
                    edt_email.setError("Invalid email");
                    validDataEntered = false;
                }

                if (s_password.isEmpty()){
                    edt_password.setError("Enter Password");
                    validDataEntered = false;
                }

                if (!s_web.isEmpty()){
                    if (!URLUtil.isValidUrl(s_web)){
                        edt_web.setError("Invalid website");
                        validDataEntered = false;
                    }
                }
                else{
                    s_web = "none";
                }

                if (s_employees.isEmpty()){
                    edt_employees.setError("Enter no of employees");
                    validDataEntered = false;
                }

                if (s_desc.isEmpty()){
                    edt_desc.setError("Enter company description");
                    validDataEntered = false;
                }


                if (validDataEntered){
                    //register company
                    //connect to server using OkHttpClient
                    OkHttpClient client = new OkHttpClient();

                    //build url to connect to and pass in query parameters
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s1908676/kopano_register_company.php").newBuilder();
                    urlBuilder.addQueryParameter("company_registration_number", s_reg_no);
                    urlBuilder.addQueryParameter("company_description", s_desc);
                    urlBuilder.addQueryParameter("company_name", s_name);
                    urlBuilder.addQueryParameter("company_phone_number", s_ph_no);
                    urlBuilder.addQueryParameter("company_email_address", s_email);
                    urlBuilder.addQueryParameter("company_website", s_web);
                    urlBuilder.addQueryParameter("company_number_of_employees", s_employees);
                    urlBuilder.addQueryParameter("company_hiring_eligibility", companyHiring);
                    urlBuilder.addQueryParameter("company_password", s_password);

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
                            if (response.isSuccessful()){//server gave a response
                                //get the data the server sent
                                final String responseData = response.body().string().trim();

                                Log.d("REG_COMP", responseData);

                                RegisterCompany.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (responseData.equals("1")){
                                            showToastNotification("Company successfully registered");
                                            finish();
                                        }
                                        else{
                                            showToastNotification("Company registration failed");
                                        }
                                    }
                                });

                            }
                            else showToastNotification("An error occurred");
                        }
                    });
                }
            }
        });
    }

    private void showToastNotification(String message){
        Toast.makeText(RegisterCompany.this, message, Toast.LENGTH_SHORT).show();
    }
}
