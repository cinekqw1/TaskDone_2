package com.example.marcin.teskdone_2;

/**
 * Created by Marcin on 07.11.2016.
 */

public class Tasks {

    private String name;
    private String description;


    Tasks(String name, String description) {
        this.name = name;
        this.description = description;

    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public  String toString(){
        return this.name;
    }
}
