package com.example.marcin.teskdone_2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TasksDetailFragment extends Fragment {


    private long workoutId;

    public TasksDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(savedInstanceState!=null){
            workoutId = savedInstanceState.getLong("workoutId");
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks_detail, container, false);
    }

    public void setWorkoutId(long id){
        this.workoutId = id;
    }

    @Override
    public void onStart(){
        super.onStart();
        View view = getView();
        if (view!=null){
            TextView title = (TextView) view.findViewById(R.id.textTitle);
            Tasks workout = MainActivity.taskLista.get((int) workoutId);
            title.setText(workout.getName());
            TextView description = (TextView) view.findViewById(R.id.textDescription);
            description.setText(workout.getDescription());
        }
    }
    public void OnSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putLong("workoutId", workoutId);
    }

}
