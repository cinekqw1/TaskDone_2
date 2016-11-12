package com.example.marcin.teskdone_2;

/**
 * Created by Marcin on 07.11.2016.
 */

public class Tasks {

    private int id;
    private String name;
    private String description;
    private String completed_at;


    Tasks(int id, String name, String description, String completed_at) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.completed_at = completed_at;

    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getCompleted_at() {
        return completed_at;
    }

    public  String toString(){
        return this.name;
    }
}
