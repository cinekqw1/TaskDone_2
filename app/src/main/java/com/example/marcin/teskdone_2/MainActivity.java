package com.example.marcin.teskdone_2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TasksListFragment.TasksListListener, TasksDetailFragment.ButtonListener{

    private JSONObject Json_object;
    private static final int MY_REQUEST_CODE = 123;
    private String Token;
    private String URL = "https://shopping-rails-app.herokuapp.com/api/items";
    private String URL_logout = "https://shopping-rails-app.herokuapp.com/api/logout";
    private String URL_create_item = "https://shopping-rails-app.herokuapp.com/api/createitem";

    public static ArrayList<Tasks> taskLista = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Token = intent.getStringExtra("token");

        new MainActivity.CallServiceTask().execute("items",URL,Token);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.log_out:
            {
                new MainActivity.CallServiceTask().execute("logout",URL_logout,Token);

            }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void itemClicked(long id) {
        View fragmentContainer = findViewById(R.id.fragment_container);
        if(fragmentContainer!=null) {
            TasksDetailFragment details = new TasksDetailFragment();
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            details.setWorkoutId(id);
            ft.replace(R.id.fragment_container, details);
            ft.addToBackStack(null);
            ft.commit();
        }else{

            Intent intent = new Intent(this,DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_WORKOUT_ID,(int)id);
            startActivity(intent);
        }
    }

    @Override
    public void buttonClicked(long id, String button) {
        Toast.makeText(this, "click",Toast.LENGTH_SHORT).show();
    }


    public class CallServiceTask extends AsyncTask<String, Void, String> {



        @Override
        protected void onPreExecute() {

        }


        protected String doInBackground(String... urls)
        {
            try {
                if(urls[0].equals("items")) {
                    return push_json_items(urls[1], urls[2]);
                }
                if(urls[0].equals("create_item")){
                    return push_json_create_item(urls[1], urls[2],urls[3],urls[4]);
                }
                if(urls[0].equals("logout")){
                    return push_json_items(urls[1], urls[2]);
                }
            } catch (IOException e) {
                return "{\"status\":\"no connection to server\"}";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "{\"status\":\"no connection to server\"}";

        }

        private String push_json_items(String myurl,String token) throws IOException, JSONException {


            InputStream is = null;

            int len = 500;

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

                Map<String, List<String>> map = conn.getHeaderFields();

                System.out.println("Printing Response Header...\n");

                for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                    System.out.println("Key : " + entry.getKey()
                            + " ,Value : " + entry.getValue());
                }


                //wysyłanie:
                //OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                //wr.write(String.valueOf(Json_object));
                //wr.flush();





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

        private String  push_json_create_item(String myurl,String token,String title, String description) throws IOException, JSONException {


            Json_object = Json_build(title,description);


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


                try {
                    JSONArray jsonArray = new JSONArray(result);
                    responce_manage(jsonArray);
                    
                } catch (JSONException e) {

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


        }

        private JSONObject Json_build(String title, String description) throws JSONException {

            JSONObject jo = new JSONObject();
            JSONObject jo_final = new JSONObject();
            jo.put("title", title);
            jo.put("description", description);

            jo_final.put("item", jo);

            return jo_final;
        }

    }



    private void responce_menage(JSONObject jj) throws JSONException {
        if(jj.getString("status").equals("succesfull log out")){
            Toast.makeText(this,"successful logout",Toast.LENGTH_SHORT).show();
            finish();
        }
        if(jj.getString("status").equals("item created")){
            Toast.makeText(this,"item created",Toast.LENGTH_SHORT).show();
        }
        if(jj.getString("status").equals("item created error")){
            Toast.makeText(this,"item created error",Toast.LENGTH_SHORT).show();
        }
        if(jj.getString("status").equals("no connection to server")){
            Toast.makeText(this,"no connection to server",Toast.LENGTH_SHORT).show();
        }


    }
    private void responce_manage(JSONArray jsonArray) throws JSONException {

        taskLista.clear();

        for (int i = 0; i < jsonArray.length(); i++) {

            if(!jsonArray.getJSONObject(i).isNull("completed_at")) {
                taskLista.add(new Tasks(jsonArray.getJSONObject(i).getInt("id"), jsonArray.getJSONObject(i).getString("title"), jsonArray.getJSONObject(i).getString("description"), jsonArray.getJSONObject(i).getString("completed_at")));
            }else{
                taskLista.add(new Tasks(jsonArray.getJSONObject(i).getInt("id"), jsonArray.getJSONObject(i).getString("title"), jsonArray.getJSONObject(i).getString("description"),  "x"));
            }
        }
        for(int i =0; i<taskLista.size();i++) System.out.println(taskLista.get(i).getName()+ " "+ taskLista.get(i).getId() + " "+  taskLista.get(i).getCompleted_at()+ " ");

        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startAddItem();
            }
        });
    }

    private void startAddItem() {
        Intent intent = new Intent(this, AddItem.class);
        startActivityForResult(intent,MY_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent pData){
        if(requestCode == MY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                final String title_respond = pData.getStringExtra("title");
                final String description_respond = pData.getStringExtra("description");

                new MainActivity.CallServiceTask().execute("create_item",URL_create_item,Token,title_respond,description_respond);
            }
        }

    }




}
