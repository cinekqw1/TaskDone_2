package com.example.marcin.teskdone_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class AddItem extends AppCompatActivity implements View.OnClickListener {

    private EditText ET_title;
    private EditText ET_description;
    private Button B_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        ET_title = (EditText) findViewById(R.id.editText_title);
        ET_description = (EditText) findViewById(R.id.editText_description);
        B_ok = (Button) findViewById(R.id.button_ok);
        B_ok.setOnClickListener(this);
    }

    private void pullBack(){

        Intent iData = new Intent();
        iData.putExtra("title",ET_title.getText().toString());
        iData.putExtra("description",ET_description.getText().toString());

        setResult(Activity.RESULT_OK,iData);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.button_ok:{

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if(!empty_field()){
                    pullBack();
                }else{
                    View view = getWindow().getDecorView().getRootView();
                    Snackbar.make(view, "Empty fields", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean empty_field() {

        String email_temp = ET_title.getText().toString();
        String password_temp = ET_description.getText().toString();


        if(email_temp.matches(""))
        {
            return true;
        }
        if(password_temp.matches(""))
        {
            return true;

        }

        else return false;


    }
}


