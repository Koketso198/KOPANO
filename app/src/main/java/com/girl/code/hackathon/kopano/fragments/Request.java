package com.girl.code.hackathon.kopano.fragments;

import android.graphics.Color;
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
import okhttp3.Response;

/*This fragment displays all the requests that were made by citizens
* the requests are displayed to companies
* and companies can accept the request and offer help*/
public class Request extends Fragment {

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

    public void initPage(){
        //create reference to linear layout inside root
        final LinearLayout layout_citizen_requests = root.findViewById(R.id.layout_citizen_requests);

        //get all requests
        //connect to server using OkHttpClient
        OkHttpClient client = new OkHttpClient();

        //build url to connect to
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s1908676/kopano_fetch_all_requests.php").newBuilder();

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
                            populateWithRequests(responseData, layout_citizen_requests);
                        }
                    });
                }
            }
        });

    }

    private void populateWithRequests(String sRequests, LinearLayout requestHolder){
        try {
            //convert the data from the server
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
                    View requestItem = View.inflate(requestHolder.getContext(), R.layout.request_view_company, null);

                    //create references to the textviews in the inflated item
                    TextView requestDescTextView = requestItem.findViewById(R.id.txt_desc);
                    TextView requestLocTextView = requestItem.findViewById(R.id.txt_location);
                    TextView requestSttsTextView = requestItem.findViewById(R.id.txt_status);
                    TextView requestDateTextView = requestItem.findViewById(R.id.txt_date);
                    TextView citizenNameTextView = requestItem.findViewById(R.id.txt_citizen_name);
                    TextView citizenPhoneTextView = requestItem.findViewById(R.id.txt_citizen_contact);
                    TextView citizenEmailTextView = requestItem.findViewById(R.id.txt_citizen_email);
                    TextView citizenCommunicationLanguageTextView = requestItem.findViewById(R.id.txt_citizen_language);
                    TextView citizenRoleTextView = requestItem.findViewById(R.id.txt_citizen_role);

                    //get String from the request object
                    String requestDescription = request.getString("REQUEST_DESCRIPTION");
                    String requestLocation = request.getString("REQUEST_LOCATION");
                    String requestStatus = request.getString("REQUEST_STATUS").trim();
                    String requestPostDate = request.getString("REQUEST_POST_DATE").split(" ")[0];
                    String citizen_name_surname = String.format("%s %s", request.getString("CITIZEN_NAME"),
                            request.getString("CITIZEN_SURNAME"));
                    String citizen_phone = request.getString("CITIZEN_PHONE_NO");
                    String citizen_email = request.getString("CITIZEN_EMAIL_ADDRESS").trim();
                    String citizen_language = request.getString("CITIZEN_PREFFERED_COMMUNICATION_LANGUAGE");
                    String citizen_role = request.getString("CITIZEN_COMMUNITY_ROLE");


                    //set text of the textviews with the text we retrieved from the json object
                    requestDescTextView.setText(requestDescription);
                    requestLocTextView.setText(requestLocation);
                    requestDateTextView.setText(requestPostDate);
                    citizenNameTextView.setText(citizen_name_surname);
                    citizenPhoneTextView.setText(citizen_phone);
                    if (citizen_email.equals("none")) citizenEmailTextView.setVisibility(View.GONE);
                    else citizenEmailTextView.setText(citizen_email);
                    citizenCommunicationLanguageTextView.setText(citizen_language);
                    citizenRoleTextView.setText(citizen_role);

                    //set the text of status and color of text for status
                    int color = 0;
                    String status = "";

                    if (requestStatus.equals("0")){
                        color = Color.BLUE;
                        status = "Open";
                    }
                    else if (requestStatus.equals("1")){
                        color =  Color.rgb(255, 165, 0);
                        status = "Partially Complete";
                    }

                    requestSttsTextView.setText(status);
                    requestSttsTextView.setTextColor(color);

                    //create reference to the accept request
                    Button acceptRequest = requestItem.findViewById(R.id.btn_accept_request);
                    //get request id, will be used to accept request
                    final String requestId = request.getString("REQUEST_ID");

                    acceptRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            acceptRequest(requestId);
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

    private void acceptRequest(String requestId) {
        showToastNotification("Please wait...");
        //get company ID from shared preference
        String company_reg = new AppPrefManager(root.getContext()).getStringVal("USER_ID");

        //get the current data
        SimpleDateFormat smdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = smdf.format(new Date());

        //send update to server
        //connect to server using OkHttpClient
        OkHttpClient client = new OkHttpClient();

        //build url to connect to and pass in query parameters
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s1908676/kopano_accepted_request.php").newBuilder();
        urlBuilder.addQueryParameter("request_id", requestId);
        urlBuilder.addQueryParameter("company_registration_no", company_reg);
        urlBuilder.addQueryParameter("accepted_request_acceptance_date", currentDate);

        //convert the built link with its parameters to string
        String url = urlBuilder.build().toString();

        Log.d("REQUEST_CO", url);

        //build a request object
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();

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
                                showToastNotification("Request accepted");
                            }
                            else if (responseData.equals("-1")){
                                showToastNotification("Request already accepted");
                            }
                            else{
                                showToastNotification("Failed to accept request");
                            }
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
