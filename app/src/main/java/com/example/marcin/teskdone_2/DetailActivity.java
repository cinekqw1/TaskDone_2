package com.example.marcin.teskdone_2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity implements TasksDetailFragment.ButtonListener {

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
    public void buttonClicked(long id, String button) {
        Toast.makeText(this,button+" "+id,Toast.LENGTH_SHORT).show();
    }
}
