package com.example.marcin.teskdone_2;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import android.view.inputmethod.InputMethodManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;



public class Login extends AppCompatActivity implements View.OnClickListener
{


    Button B_signin;
    EditText ET_Email;
    EditText ET_password;
    JSONObject Json_object;
    JSONObject Json_response;
    String URL = "https://shopping-rails-app.herokuapp.com/api";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        inicialize();

        if(savedInstanceState!=null)
        {
            ET_Email.setText(savedInstanceState.getString("email"));
            ET_password.setText((savedInstanceState.getString("password")));
        }
    }
    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        outState.putString("email", ET_Email.getText().toString());
        outState.putString("password",  ET_password.getText().toString());
    }




    private void inicialize() {

        ET_Email = (EditText) findViewById(R.id.editText_email);
        ET_password = (EditText) findViewById(R.id.editText_password);
        B_signin = (Button) findViewById(R.id.button_signin);
        B_signin.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.button_signin:
            {
                if(!empty_field())
                {

                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    String Email = ET_Email.getText().toString();
                    String Password = ET_password.getText().toString();
                    new CallServiceTask().execute(URL, Email, Password);
                }
                else
                {
                    Toast.makeText(this,"empty field",Toast.LENGTH_SHORT).show();
                }
            }


        }
    }



    private boolean empty_field() {

        String email_temp = ET_Email.getText().toString();
        String password_temp = ET_password.getText().toString();
        String IP_temp = ET_password.getText().toString();

        if(email_temp.matches(""))
        {
            return true;
        }
        if(password_temp.matches(""))
        {
            return true;

        }
        if(IP_temp.matches(""))
        {

            return true;
        }
        else return false;


    }


    public class CallServiceTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

        }
        @Override
        protected String doInBackground(String... urls)
        {
            try {
                return push_json(urls[0],urls[1],urls[2] );

            } catch (IOException e) {

                return "{\"status\":\"no connection to server\"}";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "{\"status\":\"no connection to server\"}";

        }

        private String push_json(String myurl,String Email, String Password) throws IOException, JSONException {

            Json_object = Json_build(Email,Password);

            InputStream is = null;

            int len = 500;

            try {
                java.net.URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestMethod("POST");
                conn.connect();

                //int response = conn.getResponseCode();
                //Log.d(String.valueOf(this), "The response is: " + response);
               // is = conn.getInputStream();

                //wysyłanie:
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(String.valueOf(Json_object));
                wr.flush();



                // Convert the InputStream into a string
                //String contentAsString = readIt(is, len);
                //return contentAsString;

                StringBuilder sb = new StringBuilder();
                int HttpResult = conn.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
                } else {
                    return conn.getResponseMessage().toString();
                }

            } finally {
                if (is != null) {
                    is.close();
                }
            }


        }



        @Override
        protected void onPostExecute(String result) {


            try {
                Json_response = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                responce_manage(Json_response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void responce_manage(JSONObject Json) throws JSONException {
        String x = Json.getString("status");
        View view = getWindow().getDecorView().getRootView();

        if(x.equals("no connection to server"))
        {
            Snackbar.make(view, "No connection to server", Snackbar.LENGTH_LONG).show();
        }
        if(x.equals("email error"))
        {
            Snackbar.make(view, "Invalid email", Snackbar.LENGTH_LONG).show();
        }
        if(x.equals("password error"))
        {
            Snackbar.make(view, "Invalid password", Snackbar.LENGTH_LONG).show();
        }
        if(x.equals("succesfull log in"))
        {
            String Token = Json.getString("token");


            Intent myIntent = new Intent(Login.this, MainActivity.class);
            myIntent.putExtra("token", Token);

            Login.this.startActivity(myIntent);
        }
    }

    private JSONObject Json_build(String Email, String Password) throws JSONException {

        JSONObject jo = new JSONObject();
        JSONObject jo_final = new JSONObject();
        jo.put("email", Email);
        jo.put("password", Password);

        jo_final.put("log_in", jo);


        return jo_final;
    }


}
