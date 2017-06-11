package com.maxchehab.groupfit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView email;
    private AutoCompleteTextView password;
    private Button submitButton;
    private ProgressBar loadingBar;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ApplicationController.set(this);
        SharedPreferences userDetails = this.getSharedPreferences("user", MODE_PRIVATE);
        String userID = userDetails.getString("userID", "");

        if(!userID.isEmpty()){
            Intent intent = new Intent(this, FeedActivity.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (AutoCompleteTextView) findViewById(R.id.login_email);
        password = (AutoCompleteTextView) findViewById(R.id.login_password);
        submitButton = (Button) findViewById(R.id.login_submit);
        loadingBar = (ProgressBar) findViewById(R.id.login_progress);
        registerLink = (TextView) findViewById(R.id.login_register_link);


        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean emailValid = isValidEmailAddress(email.getText().toString());
                boolean passwordValid = (password.length() > 0);

                if(passwordValid && emailValid){
                    loadingBar.setVisibility(View.VISIBLE);
                    password.setEnabled(false);
                    email.setEnabled(false);
                    submitButton.setEnabled(false);
                    submit();
                }else{
                    if(!passwordValid){
                        password.setError("Valid password is required");
                    }
                    if(!emailValid){
                        email.setError("Valid email is required");
                    }
                }
            }
        });
    }


    private void submit(){
        final String url = "http://67.204.152.242/groupfit/api/login.php";


        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JsonParser jp = new JsonParser(); //from gson
                        JsonElement root = jp.parse(response); //Convert the input stream to a json element

                        JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.

                        if(rootobj.get("userID") == null){
                            loadingBar.setVisibility(View.GONE);
                            password.setEnabled(true);
                            email.setEnabled(true);
                            submitButton.setEnabled(true);
                        }else{
                            SharedPreferences userDetails = ApplicationController.CONTEXT.getSharedPreferences("user", MODE_PRIVATE);
                            SharedPreferences.Editor edit = userDetails.edit();
                            edit.clear();
                            edit.putString("userID", rootobj.get("userID").toString().replace("\"",""));
                            edit.commit();

                            Intent intent = new Intent(ApplicationController.CONTEXT, FeedActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());

                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(postRequest);
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }




}
