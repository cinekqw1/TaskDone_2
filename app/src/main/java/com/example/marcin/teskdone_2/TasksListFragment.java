package com.example.marcin.teskdone_2;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TasksListFragment extends ListFragment {


    static interface TasksListListener {
        void itemClicked(long id);
    };

    private TasksListListener listener;

    public TasksListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        String[] names = new String[MainActivity.taskLista_to_do.size()];
        for (int i = 0;i<MainActivity.taskLista_to_do.size();i++){

            names[i] = MainActivity.taskLista_to_do.get(i).getName();

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                inflater.getContext(),android.R.layout.simple_list_item_1,names);

        setListAdapter(adapter);


        return super.onCreateView(inflater,container,savedInstanceState);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        Activity a = null;
        if(context instanceof Activity){
            a= (Activity) context;
        }
        this.listener = (TasksListListener) a;
    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        if (listener!=null){
            listener.itemClicked(id);
        }
    }



}
