package com.girl.code.hackathon.kopano.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.girl.code.hackathon.kopano.R;
import com.girl.code.hackathon.kopano.utils.AppPrefManager;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MakeRequest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_make_request);

        //create references to the edittexts
        final TextInputEditText locationRequest = findViewById(R.id.edt_request_location);
        final TextInputEditText requestDescription = findViewById(R.id.edt_request_description);

        //create reference to button
        Button sendRequest = findViewById(R.id.btn_request_send);

        //set click listener to send request button
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get entered data from the edittexts
                String sLocation = locationRequest.getText().toString().trim();
                String sDescription = requestDescription.getText().toString().trim();

                //validate the entered data
                //assume the user entered all the data
                boolean dataEntered = true;

                if (sLocation.isEmpty()){
                    //set the assumption to false and show error
                    dataEntered = false;
                    locationRequest.setError("Enter request location");
                }
                if (sDescription.isEmpty()){
                    //set the assumption to false and show error
                    dataEntered = false;
                    requestDescription.setError("Enter request description");
                }

                if (dataEntered){
                    //by default, a request will have an initial status of 0
                    String status = "0";
                    //get citizen id from shared preferences
                    String citizen_id = new AppPrefManager(MakeRequest.this).getStringVal("USER_ID");
                    //get the current data
                    //and format it to dd-MM-yyyy
                    SimpleDateFormat smdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    String currentDate = smdf.format(new Date());

                    //send request to server
                    //connect to server using OkHttpClient
                    OkHttpClient client = new OkHttpClient();

                    //build url to connect to and pass in query parameters
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s1908676/kopano_request.php").newBuilder();
                    urlBuilder.addQueryParameter("citizen_id", citizen_id);
                    urlBuilder.addQueryParameter("request_location", sLocation);
                    urlBuilder.addQueryParameter("request_description", sDescription);
                    urlBuilder.addQueryParameter("request_status", status);
                    urlBuilder.addQueryParameter("request_post_date", currentDate);

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

                                MakeRequest.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (responseData.equals("1")){
                                            showToastNotification("Request successfully sent");
                                            finish();
                                        }
                                        else{
                                            showToastNotification("failed to send request");
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

    private void showToastNotification(String message){
        Toast.makeText(MakeRequest.this, message, Toast.LENGTH_SHORT).show();
    }
}
