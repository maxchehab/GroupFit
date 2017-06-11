package com.maxchehab.groupfit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private AutoCompleteTextView username;
    private AutoCompleteTextView email;
    private AutoCompleteTextView password;
    private AutoCompleteTextView confirmPassword;
    private ProgressBar progressBar;
    private Button submitButton;
    private TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ApplicationController.set(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (AutoCompleteTextView) findViewById(R.id.register_username);
        email = (AutoCompleteTextView) findViewById(R.id.register_email);
        password = (AutoCompleteTextView) findViewById(R.id.register_password);
        confirmPassword = (AutoCompleteTextView) findViewById(R.id.register_confirm_password);
        progressBar = (ProgressBar) findViewById(R.id.register_progress);
        submitButton = (Button) findViewById(R.id.register_submit);
        loginLink = (TextView) findViewById(R.id.register_login_link);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean emailValid = isValidEmailAddress(email.getText().toString());
                boolean passwordValid = (password.length() > 0);
                boolean passwordMatch = (password.getText().toString().equals(confirmPassword.getText().toString()));
                boolean usernameValid = (username.length() > 0);

                if(passwordValid && emailValid && passwordMatch && usernameValid){
                    progressBar.setVisibility(View.VISIBLE);
                    password.setEnabled(false);
                    confirmPassword.setEnabled(false);
                    username.setEnabled(false);
                    email.setEnabled(false);
                    submitButton.setEnabled(false);
                    loginLink.setEnabled(false);
                    submit();
                }else{
                    if(!passwordValid){
                        password.setError("Valid password is required");
                    }
                    if(!passwordMatch){
                        confirmPassword.setError("Passwords must match");
                    }
                    if(!usernameValid){
                        username.setError("Valid username is required");
                    }
                    if(!emailValid){
                        email.setError("Valid email is required");
                    }
                }
            }
        });
    }

    private void submit(){
        final String url = "http://67.204.152.242/groupfit/api/register.php";


        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response", response);


                        JsonParser jp = new JsonParser(); //from gson
                        JsonElement root = jp.parse(response); //Convert the input stream to a json element

                        JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.


                        if(rootobj.get("userID") == null){
                            progressBar.setVisibility(View.GONE);
                            password.setEnabled(true);
                            confirmPassword.setEnabled(true);
                            username.setEnabled(true);
                            email.setEnabled(true);
                            submitButton.setEnabled(true);
                            loginLink.setEnabled(true);

                            if(rootobj.get("usernameTaken") != null){
                                username.setError("This username is already taken");
                            }
                            if(rootobj.get("emailTaken") != null){
                                email.setError("This email is already taken");
                            }
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
                params.put("username", username.getText().toString());
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
