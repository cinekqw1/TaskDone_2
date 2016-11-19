package com.example.marcin.teskdone_2;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.Snackbar;
import android.support.v4.app.*;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.List;
import java.util.Vector;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

import it.neokree.materialtabs.MaterialTabHost;

import static com.example.marcin.teskdone_2.R.id.tabHost;
import static com.example.marcin.teskdone_2.R.id.toolBar;
import static com.example.marcin.teskdone_2.R.id.viewPager;

public class MainActivity extends AppCompatActivity implements TasksListFragment.TasksListListener, TasksDetailFragment.ButtonListener, CallbackBackgroundService, MaterialTabListener{

    private JSONObject Json_object;
    private static final int MY_REQUEST_CODE = 123;
    private static String Token;
    private boolean threadRun = true;
    private String URL = "https://shopping-rails-app.herokuapp.com/api/items";
    private String URL_logout = "https://shopping-rails-app.herokuapp.com/api/logout";
    private String URL_create_item = "https://shopping-rails-app.herokuapp.com/api/createitem";
    private String URL_delete = "https://shopping-rails-app.herokuapp.com/api/destroy";
    private String URL_complete = "https://shopping-rails-app.herokuapp.com/api/complete";

    public static ArrayList<Tasks> taskLista = new ArrayList<>(0);
    public static ArrayList<Tasks> taskLista_to_do = new ArrayList<>(0);
    public static ArrayList<Tasks> taskLista_done = new ArrayList<>(0);

    List<Fragment> fragments = new Vector<Fragment>();
    List<String> fragments_title = new Vector<String>();
    MaterialTabHost tabHost;
    ViewPager viewPager;
    ViewPagerAdapter androidAdapter;
    Toolbar toolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Token = intent.getStringExtra("token");

        //android toolbar
        toolBar = (android.support.v7.widget.Toolbar) this.findViewById(R.id.toolBar);
        this.setSupportActionBar(toolBar);

        //tab host
        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        viewPager = (ViewPager) this.findViewById(R.id.viewPager);


        fragments.add(Fragment.instantiate(this, TasksListFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, TaskDoneListFragment.class.getName()));


        fragments_title.add("To do");
        fragments_title.add("Done");


        androidAdapter = new ViewPagerAdapter(getSupportFragmentManager(),fragments,fragments_title);
        viewPager.setAdapter(androidAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int tabposition) {
                tabHost.setSelectedNavigationItem(tabposition);
            }
        });

        for (int i = 0; i < androidAdapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(androidAdapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddItem();
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {

            @Override
            public void run() {
                if(threadRun) {
                    new MainActivity.CallServiceTask().execute("items", URL, Token);
                }

                handler.postDelayed( this, 3 * 1000 );
            }
        }, 3 * 1000 );

    }


    @Override
    public void onBackPressed()
    {
        new MainActivity.CallServiceTask().execute("logout",URL_logout,Token);
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {

        viewPager.setCurrentItem(materialTab.getPosition());
    }

    //tab on reselected
    @Override
    public void onTabReselected(MaterialTab materialTab) {

    }

    //tab on unselected
    @Override
    public void onTabUnselected(MaterialTab materialTab) {

    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mFragments;
        private List<String> mTitles;


        public ViewPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments, List<String> titles) {
            super(fragmentManager);
            mFragments = fragments;
            mTitles = titles;
        }

        public Fragment getItem(int num) {
            return mFragments.get(num);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int tabposition) {
            return mTitles.get(tabposition);
            //return "Tab " + tabposition;
        }
    }

    public static String getToken(){
        return Token;
    }

    public ArrayList<Tasks> getListTask(){
        return taskLista;
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
    public void onResume(){
        super.onResume();
        threadRun = true;

    }

    @Override
    public void onPause(){
        super.onPause();
        threadRun = false;
    }
    @Override
    public void onStop(){
        super.onStop();
        threadRun = false;
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
    public void buttonDeleteClicked(int id) {
        new BackgroundService((CallbackBackgroundService) this).execute("delete",URL_delete,Token,String.valueOf(id));
    }

    @Override
    public void buttonCompleteClicked(int id) {
        new BackgroundService((CallbackBackgroundService) this).execute("complete",URL_complete,Token,String.valueOf(id));
    }

    @Override
    public void callbackBackgroundService(String result) {
        View view = getWindow().getDecorView().getRootView();
        Snackbar.make(view, result, Snackbar.LENGTH_LONG).show();
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

        View view = getWindow().getDecorView().getRootView();

        if(jj.getString("status").equals("succesfull log out")){
            threadRun = false;
            Toast.makeText(this,"Successful logout",Toast.LENGTH_SHORT).show();
            finish();
        }
        if(jj.getString("status").equals("item created")){
            Snackbar.make(view, "The item has created", Snackbar.LENGTH_LONG).show();

        }
        if(jj.getString("status").equals("item created error")){
            Toast.makeText(this,"Item created error",Toast.LENGTH_SHORT).show();
        }
        if(jj.getString("status").equals("no connection to server")){

            threadRun = false;
            Snackbar snackbar = Snackbar
                    .make(view, "connection lost", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Refresh", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            refresh();
                        }
                    });

            snackbar.show();
        }
        if (jj.getString("status").equals("item deleted")){
            Toast.makeText(this,"Item deleted",Toast.LENGTH_SHORT).show();

        }
        if (jj.getString("status").equals("item deleted error")){
            Toast.makeText(this,"Item deleted error",Toast.LENGTH_SHORT).show();
        }
        if (jj.getString("status").equals("token expired error")){
            Toast.makeText(this,"Session expired. Login again",Toast.LENGTH_SHORT).show();
            finish();
        }
        if (jj.getString("status").equals("invalid token")){
            Toast.makeText(this,"Token session error. Contact to admin",Toast.LENGTH_SHORT).show();
            finish();
        }
        if (jj.getString("status").equals("marked succesfull")){
            Toast.makeText(this,"checked!",Toast.LENGTH_SHORT).show();

        }
        if (jj.getString("status").equals("marked error")){
            Toast.makeText(this,"checked error",Toast.LENGTH_SHORT).show();
        }


    }

    private void refresh() {
        threadRun = true;
    }

    private void responce_manage(JSONArray jsonArray) throws JSONException {

        ArrayList<Tasks> taskLista_temp = new ArrayList<>(0);


        for (int i = 0; i < jsonArray.length(); i++) {

            if(!jsonArray.getJSONObject(i).isNull("completed_at")) {
                taskLista_temp.add(new Tasks(jsonArray.getJSONObject(i).getInt("id"), jsonArray.getJSONObject(i).getString("title"), jsonArray.getJSONObject(i).getString("description"), jsonArray.getJSONObject(i).getString("completed_at")));
            }else{
                taskLista_temp.add(new Tasks(jsonArray.getJSONObject(i).getInt("id"), jsonArray.getJSONObject(i).getString("title"), jsonArray.getJSONObject(i).getString("description"),  "x"));

            }
        }

        for (int i = 0; i < taskLista_temp.size(); i++) System.out.println(taskLista_temp.get(i).getName() + " " +taskLista_temp.get(i).getCompleted_at() );
        for (int i = 0; i < taskLista.size(); i++) System.out.println(taskLista.get(i).getName() + " " +taskLista.get(i).getCompleted_at() );
        if (ifDiffrent(taskLista_temp,taskLista)){
            taskLista=taskLista_temp;
            if(isActivityVisible()){


                taskLista_to_do.clear();
                taskLista_done.clear();
                for (int i = 0; i < taskLista.size(); i++) {

                    if(!taskLista.get(i).getCompleted_at().equals("x")) {
                        taskLista_done.add(new Tasks(jsonArray.getJSONObject(i).getInt("id"), jsonArray.getJSONObject(i).getString("title"), jsonArray.getJSONObject(i).getString("description"), jsonArray.getJSONObject(i).getString("completed_at")));
                    }else{
                        taskLista_to_do.add(new Tasks(jsonArray.getJSONObject(i).getInt("id"), jsonArray.getJSONObject(i).getString("title"), jsonArray.getJSONObject(i).getString("description"),  "x"));

                    }
                }

                /*
                TasksListFragment details = new TasksListFragment();
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container_list, details);
                ft.commit();


                TaskDoneListFragment details2 = new TaskDoneListFragment();
                android.support.v4.app.FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                ft2.replace(R.id.fragment_container_list_done, details2);
                ft2.commit();
                */
                androidAdapter = new ViewPagerAdapter(getSupportFragmentManager(),fragments,fragments_title);
                viewPager.setAdapter(androidAdapter);
                viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int tabposition) {
                        tabHost.setSelectedNavigationItem(tabposition);
                    }
                });

                for (int i = 0; i < androidAdapter.getCount(); i++) {
                    tabHost.addTab(
                            tabHost.newTab()
                                    .setText(androidAdapter.getPageTitle(i))
                                    .setTabListener(this)
                    );
                }

            }

        }

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
    protected boolean isActivityVisible() {
        if (this != null) {
            Class klass = this.getClass();
            while (klass != null) {
                try {
                    Field field = klass.getDeclaredField("mResumed");
                    field.setAccessible(true);
                    Object obj = field.get(this);
                    return (Boolean)obj;
                } catch (NoSuchFieldException exception1) {
//                Log.e(TAG, exception1.toString());
                } catch (IllegalAccessException exception2) {
//                Log.e(TAG, exception2.toString());
                }
                klass = klass.getSuperclass();
            }
        }
        return false;
    }

    public boolean ifDiffrent(ArrayList<Tasks> one, ArrayList<Tasks> two){

        if (one.size()!=two.size()){
            return true;
        }
        else {
            for (int i = 0; i < one.size(); i++) {
                if (one.get(i).getId() != two.get(i).getId()) return true;
            }
            for (int i = 0; i < one.size(); i++) {
                if (!one.get(i).getCompleted_at().equals(two.get(i).getCompleted_at())) return true;
            }

            return false;
        }
    }

}
