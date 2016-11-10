package com.example.marcin.teskdone_2;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TasksListFragment.TasksListListener{


    private String Token;
    private String URL = "https://shopping-rails-app.herokuapp.com/api/items";
    private String URL_logout = "https://shopping-rails-app.herokuapp.com/api/logout";


    public static ArrayList<Tasks> taskLista = new ArrayList<Tasks>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Intent intent = getIntent();
        Token = intent.getStringExtra("token");

        new MainActivity.CallServiceTask().execute(URL,Token);




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
                new MainActivity.CallServiceTask().execute(URL_logout,Token);

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

    public class CallServiceTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

        }


        protected String doInBackground(String... urls)
        {
            try {
                return push_json(urls[0],urls[1]);

            } catch (IOException e) {
                return "{\"status\":\"no connection to server\"}";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "{\"status\":\"no connection to server\"}";

        }

        private String push_json(String myurl,String token) throws IOException, JSONException {

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

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {


            if(result!=null) {


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

        }

    }

    private void logout() {
        Toast.makeText(this,"seccesfull logout",Toast.LENGTH_SHORT).show();
        finish();
    }

    private void responce_menage(JSONObject jj) throws JSONException {
        if(jj.getString("status").equals("succesfull log out")){
            logout();
        }

    }
    private void responce_manage(JSONArray jsonArray) throws JSONException {

        taskLista.clear();

        for (int i = 0; i < jsonArray.length(); i++) {
            taskLista.add(new Tasks(jsonArray.getJSONObject(i).getString("title"), jsonArray.getJSONObject(i).getString("description")));
        }

        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }



}
