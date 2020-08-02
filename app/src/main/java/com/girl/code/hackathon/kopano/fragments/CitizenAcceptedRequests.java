package com.girl.code.hackathon.kopano.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.girl.code.hackathon.kopano.R;
import com.girl.code.hackathon.kopano.utils.AppPrefManager;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

/*
* This fragment displays the citizens requests that were accepted by a company
* displays the requests to the citizen*/
public class CitizenAcceptedRequests extends Fragment {

    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_citizen_requests, container, false);
        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        initPage();
    }

    /*this function fetches requests from the server and displays them on the screen*/
    public void initPage(){
        //create reference to linear layout inside root
        final LinearLayout layout_citizen_requests = root.findViewById(R.id.layout_citizen_requests);

        //get all requests
        //connect to server using OkHttpClient
        OkHttpClient client = new OkHttpClient();

        //get citizen id from shared preferences
        String citizen_id = new AppPrefManager(getContext()).getStringVal("USER_ID");
        //build url to connect to and pass in the params
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s1908676/kopano_show_citizen_accepted_request.php").newBuilder();
        urlBuilder.addQueryParameter("citizen_id", citizen_id);

        //convert the built link with its parameters to string
        String url = urlBuilder.build().toString();

        //build a request object
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();

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

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //call a function that will populate the screen with the data
                            populateWithRequests(responseData, layout_citizen_requests);
                        }
                    });
                }
            }
        });

    }

    /*This function takes in a String (data from server) and linearLayout (displays the data)*/
    private void populateWithRequests(String sRequests, LinearLayout requestHolder){
        try {
            //convert the data(sRequest) from the server
            //to a json array
            JSONArray requests = new JSONArray(sRequests);
            if (!sRequests.isEmpty() && requests.length() != 0){//there's data from server
                requestHolder.removeAllViews();//remove all the views that were in the linear layout
                //iterate the json array
                //and display the data
                for (int i = 0; i < requests.length(); i++){
                    //get json object from the json array
                    JSONObject request = requests.getJSONObject(i);

                    //inflate the view that will display the request data
                    View requestItem = View.inflate(requestHolder.getContext(), R.layout.request_view_citizen_accepted, null);

                    //create references to the textviews in the inflated item
                    TextView requestDescTextView = requestItem.findViewById(R.id.txt_desc);
                    TextView requestLocTextView = requestItem.findViewById(R.id.txt_location);
                    TextView requestSttsTextView = requestItem.findViewById(R.id.txt_status);
                    TextView requestDateTextView = requestItem.findViewById(R.id.txt_date);
                    TextView companyNameTextView = requestItem.findViewById(R.id.txt_citizen_name);
                    TextView companyPhoneTextView = requestItem.findViewById(R.id.txt_citizen_contact);
                    TextView companyEmailTextView = requestItem.findViewById(R.id.txt_citizen_email);
                    TextView companyDescriptionTextView = requestItem.findViewById(R.id.txt_citizen_language);
                    TextView companyHiringTextView = requestItem.findViewById(R.id.txt_citizen_role);

                    //create reference to the edittexts
                    final TextInputEditText experience = requestItem.findViewById(R.id.edt_feedback);

                    //get String from the request object
                    String requestDescription = request.getString("REQUEST_DESCRIPTION");
                    String requestLocation = request.getString("REQUEST_LOCATION");
                    String requestStatus = request.getString("REQUEST_STATUS").trim();//remove possible empty spaces
                    String requestPostDate = request.getString("REQUEST_POST_DATE").split(" ")[0];//get date only, the date from server comes back as dd-MM-yyyy hh:mm:ss and don't know how to fix it
                    String company_name = request.getString("COMPANY_NAME");
                    String company_phone = request.getString("COMPANY_PHONE_NO");
                    String company_email = request.getString("COMPANY_EMAIL_ADDRESS");
                    String company_description = request.getString("COMPANY_DESCRIPTION");
                    String company_hiring = request.getString("COMPANY_HIRING_ELIGIBIITY").trim();
                    final String company_website = request.getString("COMPANY_WEBSITE");


                    //set text of the textviews with the text we retrieved from the json object
                    requestDescTextView.setText(requestDescription);
                    requestLocTextView.setText(requestLocation);
                    requestDateTextView.setText(requestPostDate);
                    companyNameTextView.setText(company_name);
                    companyPhoneTextView.setText(company_phone);
                    companyEmailTextView.setText(company_email);
                    companyDescriptionTextView.setText(company_description);
                    companyHiringTextView.setText(company_hiring.equals("0") ? "No" : "Yes"); //if company_hiring is equal to 0, display No, else Yes

                    //set the text of status and color of text for status
                    //based on the requestStatus(hence we trimed it because we gonna use it to compare)
                    int color = 0;
                    String status = "";

                    //requestStatus has 3 possible values
                    //0 - Open
                    //1 - Partially Complete
                    //2 - Complete
                    if (requestStatus.equals("0")){
                        color = Color.BLUE;
                        status = "Open";
                    }
                    else if (requestStatus.equals("1")){
                        color =  Color.rgb(255, 165, 0);
                        status = "Partially Complete";
                    }

                    //set the status text and color
                    requestSttsTextView.setText(status);
                    requestSttsTextView.setTextColor(color);

                    //create reference to the send feedback and visit website button
                    final Button sendFeedback = requestItem.findViewById(R.id.btn_send_feedback);
                    Button companyWeb = requestItem.findViewById(R.id.btn_visit_web);

                    //get accepted_request_id
                    final String acceptedRequestId = request.getString("ACCEPTED_REQUEST_ID");

                    sendFeedback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //get text from editText
                            String sExperience = experience.getText().toString().trim();
                            if (sExperience.isEmpty()){
                                experience.setError("Input required");
                            }
                            else{
                                //send the feedback to the server
                                //feedback that the citizen made about the
                                //companys contribution to their request
                                sendFeedback(acceptedRequestId, experience);
                            }
                        }
                    });

                    //if company doesn't have a website, then hide the visit company button
                    if (company_website.equals("none")) companyWeb.setVisibility(View.GONE);

                    //set click listener to companyWeb button
                    companyWeb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //this function takes in a website link and opens a browser
                            visitSite(company_website);
                        }
                    });

                    //add inflated view to the linear layout
                    //create layout params
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 5, 0, 5);
                    requestHolder.addView(requestItem, params);
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //this function request a browser to open the url
    private void visitSite(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void sendFeedback(String acceptedRequestId, final TextInputEditText experience) {
        //get text the citizen entered in the experience edittext
        String sExperience = experience.getText().toString().trim();

        //get the current data
        SimpleDateFormat smdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = smdf.format(new Date());

        //connect to server using OkHttpClient
        OkHttpClient client = new OkHttpClient();

        //build url to connect to and pass in query parameters
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s1908676/kopano_accepted_request_reviews.php").newBuilder();
        urlBuilder.addQueryParameter("accepted_request_id", acceptedRequestId);
        urlBuilder.addQueryParameter("accepted_request_reviews_review", sExperience);
        urlBuilder.addQueryParameter("accepted_request_reviews_date", currentDate);

        //convert the built link with its parameters to string
        String url = urlBuilder.build().toString();

        Log.d("FEED", url);

        //build a request object
        okhttp3.Request request = new Request.Builder().url(url).build();

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

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseData.equals("1")){
                                showToastNotification("Feedback sent successfully");
                                //clear the users text because it has been sent to the server
                                experience.setText("");
                            }
                            else showToastNotification("Failed to send feedback");
                        }
                    });
                }
            }
        });
    }

    private void showToastNotification(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
