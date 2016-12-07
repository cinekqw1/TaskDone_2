package com.example.marcin.teskdone_2;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by Marcin on 12.11.2016.
 */




public class BackgroundService extends AsyncTask<String, Void, String> {

    JSONObject jsonObject;



    private CallbackBackgroundService delegate ;

    public BackgroundService(CallbackBackgroundService delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {

    }



    protected String doInBackground(String... urls)
    {
        try {
            if(urls[0].equals("delete")){
                return push_json_delete_item(urls[1],urls[2],urls[3]);
            }
            if(urls[0].equals("complete")){
                return push_json_delete_item(urls[1],urls[2],urls[3]);
            }

        } catch (IOException e) {
            return "{\"status\":\"no connection to server\"}";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "{\"status\":\"no connection to server\"}";

    }


    private String  push_json_delete_item(String myurl,String token,String id) throws IOException, JSONException {



        JSONObject jo = new JSONObject();
        JSONObject Json_object = new JSONObject();
        jo.put("id", Integer.parseInt(id));
        Json_object.put("item", jo);


        InputStream is = null;


        try {
            java.net.URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.addRequestProperty ("Authorization" , "Token token="+token );
            conn.setRequestMethod("POST");
            conn.connect();



            //wysyłanie:
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(String.valueOf(Json_object));
            wr.flush();



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



            JSONObject jo = null;

            try {
                jo = new JSONObject(result);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            try {
                responce_menage(jo);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

    }



    private void responce_menage(JSONObject jj) throws JSONException {


        if(delegate!=null) delegate.callbackBackgroundService(jj.getString("status"));


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