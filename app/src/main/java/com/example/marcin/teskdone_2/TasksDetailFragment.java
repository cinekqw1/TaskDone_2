package com.example.marcin.teskdone_2;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class TasksDetailFragment extends Fragment implements View.OnClickListener {

    ImageButton IB_coplete;
    ImageButton IB_delete;
    private long workoutId;

    public TasksDetailFragment() {
        // Required empty public constructor
    }



    static interface ButtonListener {
        void buttonDeleteClicked(int id );
        void buttonCompleteClicked(int id);
    };

    private ButtonListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(savedInstanceState!=null){
            workoutId = savedInstanceState.getLong("workoutId");
        }



        return inflater.inflate(R.layout.fragment_tasks_detail, container, false);
    }




    public void setWorkoutId(long id){
        this.workoutId = id;
    }

    @Override
    public void onStart(){
        super.onStart();
        View view = getView();
        if (view!=null) {
            TextView title = (TextView) view.findViewById(R.id.textTitle);
            TextView description = (TextView) view.findViewById(R.id.textDescription);

            Tasks workout = MainActivity.taskLista_to_do.get((int) workoutId);
            title.setText(workout.getName());
            description.setText(workout.getDescription());

            IB_coplete = (ImageButton) getView().findViewById(R.id.imageButton_complete);
            IB_delete = (ImageButton) getView().findViewById(R.id.imageButton_delete);
            IB_coplete.setOnClickListener(this);
            IB_delete.setOnClickListener(this);
        }

    }
    public void OnSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putLong("workoutId", workoutId);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        Activity a = null;
        if(context instanceof Activity){
            a= (Activity) context;
        }
        this.listener = (ButtonListener) a;
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.imageButton_complete:{
                Tasks item = MainActivity.taskLista_to_do.get((int) workoutId);
                if(listener!=null) listener.buttonCompleteClicked(item.getId());
            }break;
            case R.id.imageButton_delete:{
                Tasks item = MainActivity.taskLista_to_do.get((int) workoutId);
                if(listener!=null) listener.buttonDeleteClicked(item.getId());
            }break;

        }
    }

}
