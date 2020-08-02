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
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*displays the news letter to both citizens and companies*/
public class NewsLetter extends Fragment {

    private View root;

    //different title formats that can be used with each news letter
    private String[] titleFormats = {
            "A/An %s company to the rescue",
            "%s on the rise",
            "Doesn't matter if we are a %s company, we'll offer a helping hand anyway we can",
            "%s company giving back"
    };

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
        showToastNotification("Please wait...");

        //create reference to linear layout inside root
        final LinearLayout layout_citizen_requests = root.findViewById(R.id.layout_citizen_requests);

        //get all requests
        //connect to server using OkHttpClient
        OkHttpClient client = new OkHttpClient();

        //build url to connect to
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s1908676/kopano_show_newsletter.php").newBuilder();

        //convert the built link with its parameters to string
        String url = urlBuilder.build().toString();
        Log.d("NEWS_LETTER", url);

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
                    View requestItem = View.inflate(requestHolder.getContext(), R.layout.news_letter, null);

                    //create references to the textviews in the inflated item
                    TextView newsLetterTitle = requestItem.findViewById(R.id.news_letter_title);
                    TextView companyWordsTextView = requestItem.findViewById(R.id.txt_company_words);
                    TextView citizenWordsTextView = requestItem.findViewById(R.id.txt_citizen_words);
                    TextView requestDescTextView = requestItem.findViewById(R.id.txt_desc);
                    TextView requestLocTextView = requestItem.findViewById(R.id.txt_location);
                    TextView requestDateTextView = requestItem.findViewById(R.id.txt_date);
                    TextView companyNameTextView = requestItem.findViewById(R.id.txt_citizen_name);
                    TextView companyPhoneTextView = requestItem.findViewById(R.id.txt_citizen_contact);
                    TextView companyEmailTextView = requestItem.findViewById(R.id.txt_citizen_email);
                    TextView companyDescriptionTextView = requestItem.findViewById(R.id.txt_citizen_language);
                    TextView companyHiringTextView = requestItem.findViewById(R.id.txt_citizen_role);

                    //get String from the request object
                    String requestDescription = request.getString("REQUEST_DESCRIPTION");
                    String requestLocation = request.getString("REQUEST_LOCATION");
                    String requestPostDate = request.getString("REQUEST_POST_DATE").split(" ")[0];
                    String company_name = request.getString("COMPANY_NAME");
                    String company_phone = request.getString("COMPANY_PHONE_NO");
                    String company_email = request.getString("COMPANY_EMAIL_ADDRESS");
                    String company_description = request.getString("COMPANY_DESCRIPTION");
                    String company_hiring = request.getString("COMPANY_HIRING_ELIGIBIITY").trim();
                    final String company_website = request.getString("COMPANY_WEBSITE");
                    String companyWords = request.getString("ACCEPTED_REQUEST_COMPANY_TESTIMONIAL").trim();
                    String citizenWords = request.getString("ACCEPTED_REQUEST_REVIEWS_REVIEW").trim();
                    int sNumEmployees = request.getInt("COMPANY_NUMBER_OF_EMPLOYEES");

                    //build string for company type using number of employees
                    String companyType = "Well established";
                    if (sNumEmployees < 10) companyType = "Start up";
                    else if (sNumEmployees <= 100) companyType = "Up-rising";

                    //pick title format at random
                    Random random = new Random();
                    int randPos = random.nextInt(titleFormats.length);
                    String titleFormat = titleFormats[randPos];

                    //set title
                    newsLetterTitle.setText(String.format(titleFormat, companyType));

                    //set text of the textviews with the text we retrieved from the json object
                    requestDescTextView.setText(requestDescription);
                    requestLocTextView.setText(requestLocation);
                    requestDateTextView.setText(requestPostDate);
                    companyNameTextView.setText(company_name);
                    companyPhoneTextView.setText(company_phone);
                    companyEmailTextView.setText(company_email);
                    companyDescriptionTextView.setText(company_description);
                    companyHiringTextView.setText(company_hiring.equals("0") ? "No" : "Yes");
                    companyWordsTextView.setText(companyWords);
                    citizenWordsTextView.setText(citizenWords);


                    //if company has not made a review or citizen, the news letter won't be displayed
                    if (companyWords.equals("") || citizenWords.equals("")) continue;

                    //create reference to the visit website
                    Button companyWeb = requestItem.findViewById(R.id.btn_visit_web);
                    if (company_website.equals("none")) companyWeb.setVisibility(View.GONE);

                    //set click listener to companyWeb button
                    companyWeb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
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

    private void visitSite(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void showToastNotification(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
