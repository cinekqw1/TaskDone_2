package com.example.marcin.teskdone_2;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity implements TasksDetailFragment.ButtonListener, CallbackBackgroundService {

    public static final String EXTRA_WORKOUT_ID = "id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TasksDetailFragment workoutDetailFragment = (TasksDetailFragment) getSupportFragmentManager().findFragmentById(R.id.detail_frag);
        int workoutId = (int) getIntent().getExtras().get(EXTRA_WORKOUT_ID);
        workoutDetailFragment.setWorkoutId(workoutId);
    }



    @Override
    public void buttonDeleteClicked(int id) {


         new BackgroundService(this).execute("delete","https://shopping-rails-app.herokuapp.com/api/destroy",MainActivity.getToken(),String.valueOf(id));
    }

    @Override
    public void buttonCompleteClicked(int id) {
        new BackgroundService(this).execute("complete","https://shopping-rails-app.herokuapp.com/api/complete",MainActivity.getToken(),String.valueOf(id));

    }

    @Override
    public void callbackBackgroundService(String result) {

        if(result.equals("item deleted")){

            finish();
        }
        if(result.equals("item deleted error")){
            View view = getWindow().getDecorView().getRootView();
            Snackbar.make(view, "item deleted error", Snackbar.LENGTH_LONG).show();
        }
        if(result.equals("marked succesfull")){

            finish();
        }
    }
}
