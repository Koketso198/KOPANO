package com.girl.code.hackathon.kopano.fragments;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.girl.code.hackathon.kopano.R;
import com.girl.code.hackathon.kopano.activities.MakeRequest;
import com.girl.code.hackathon.kopano.activities.RegisterCitizen;
import com.girl.code.hackathon.kopano.utils.AppPrefManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
* This displays the requests the citizen made*/
public class CitizenRequests extends Fragment {

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

    private void initPage() {
        showToastNotification("Fetching Requests");

        //create reference to linear layout inside root
        final LinearLayout layout_citizen_requests = root.findViewById(R.id.layout_citizen_requests);

        //get citizen id from shared preferences
        String citizen_id = new AppPrefManager(root.getContext()).getStringVal("USER_ID");

        //get users requests from server
        //connect to server using OkHttpClient
        OkHttpClient client = new OkHttpClient();

        //build url to connect to and pass in query parameters
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s1908676/kopano_show_citizen_requests.php").newBuilder();
        urlBuilder.addQueryParameter("citizen_id", citizen_id);

        //convert the built link with its parameters to string
        String url = urlBuilder.build().toString();

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
                    View requestItem = View.inflate(requestHolder.getContext(), R.layout.request_item, null);

                    //create references to the textviews in the inflated item
                    TextView requestDescTextView = requestItem.findViewById(R.id.txt_desc);
                    TextView requestLocTextView = requestItem.findViewById(R.id.txt_location);
                    TextView requestSttsTextView = requestItem.findViewById(R.id.txt_status);
                    TextView requestDateTextView = requestItem.findViewById(R.id.txt_date);

                    //get String from the request object
                    String requestDescription = request.getString("REQUEST_DESCRIPTION");
                    String requestLocation = request.getString("REQUEST_LOCATION");
                    String requestStatus = request.getString("REQUEST_STATUS").trim();
                    String requestPostDate = request.getString("REQUEST_POST_DATE").split(" ")[0];

                    //set text of the textviews with the text we retrieved from the json object
                    requestDescTextView.setText(requestDescription);
                    requestLocTextView.setText(requestLocation);
                    requestDateTextView.setText(requestPostDate);

                    //set the text of status and color of text for status
                    int color;
                    String status;

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
                    else {
                        color = Color.GREEN;
                        status = "Complete";
                    }

                    requestSttsTextView.setText(status);
                    requestSttsTextView.setTextColor(color);

                    //create reference to the update stts button
                    Button updateStatusBtn = requestItem.findViewById(R.id.btn_update_status);
                    //get request id, will be used to update status
                    final String requestId = request.getString("REQUEST_ID");

                    updateStatusBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateStatus(requestId);
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

    private void updateStatus(final String requestId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Update Request Status")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("Update status to?")
                .setPositiveButton("Complete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateStatus(requestId, "2");
                    }
                })
                .setNegativeButton("Partially Complete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateStatus(requestId, "1");
                    }
                })
                .create().show();
    }

    private void updateStatus(String requestId, String status) {
        showToastNotification("Please wait...");
        //send update to server
        //connect to server using OkHttpClient
        OkHttpClient client = new OkHttpClient();

        //build url to connect to and pass in query parameters
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s1908676/kopano_request_status_update.php").newBuilder();
        urlBuilder.addQueryParameter("request_id", requestId);
        urlBuilder.addQueryParameter("request_status", status);

        //convert the built link with its parameters to string
        String url = urlBuilder.build().toString();

        //build a request object
        okhttp3.Request request = new Request.Builder().url(url).build();

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
                                showToastNotification("Status update success");
                                //refresh the page after a successful status update
                                initPage();
                            }
                            else{
                                showToastNotification("Failed to update status");
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
