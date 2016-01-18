package com.example.tombushmits.dogapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.preference.DialogPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.HttpURLConnection;

import static com.example.tombushmits.dogapp.R.style.ErrorDialog;

public class MainActivity extends AppCompatActivity implements HttpInterfaceHandler {



    private static final String URL_GET_USER = "http://46.101.229.180:8000";
    httpAsyncTask task;
    EditText user_box;
    EditText pss_box;
    TextView error_text;
    boolean userDetailRetrieval=false;
    boolean passwordRetrieval=false;
    AlertDialog passDialog;
    boolean text_changed = false;
    boolean pass_changed =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DogHolder.getInstance().setName("this is a test");
        user_box = (EditText) findViewById(R.id.user_editTxt);
        pss_box = (EditText) findViewById(R.id.password_editTxt);
        user_box.addTextChangedListener(textWatcher);
        pss_box.addTextChangedListener(passTextWatcher);

    }


    public void createNewUser(View view){
        task = (httpAsyncTask)new httpAsyncTask(MainActivity.this);
//        task.execute(URL,"POST");
        Intent intent = new Intent(this, CreateNewUser.class);
        startActivity(intent);
        finish();


    }

    public void submit(View view) {
//        String user = user_box.getText().toString();
//        String password = pss_box.getText().toString();
        if(text_changed && pass_changed) {
            userDetailRetrieval =true;
            String get_user_url = getString(R.string.server_url) + "/get_dog/" + user_box.getText().toString()+'/' + pss_box.getText().toString();
            task = (httpAsyncTask) new httpAsyncTask(MainActivity.this);
            task.setContext(this);
            task.execute(get_user_url, "GET");
        }
        else {
                  Toast.makeText(MainActivity.this, "please enter username and password...", Toast.LENGTH_LONG).show();
        }


    }

    public void forgotPassword(View view) {
        if(text_changed) {
            passwordRetrieval = true;
            task = (httpAsyncTask) new httpAsyncTask(MainActivity.this);
            String get_user_url = getString(R.string.server_url)+ "/forgot_password/" + user_box.getText().toString();
            task.setContext(this);
            task.execute(get_user_url, "GET");
        }
        else
            Toast.makeText(this, "missing username..",Toast.LENGTH_LONG).show();

    }

    public void postHandle(String result) throws JSONException {
        Log.d("","POST Handler");
        //this is called when a new dog is registered
        //now we will register the new dog/user in the DogHolder


    }
    public void getHandle(String result) throws JSONException {
        JSONObject json = new JSONObject(result);
        if (userDetailRetrieval) {
            DogHolder dogHolder = DogHolder.getInstance();
            if (json.get("status").toString().equalsIgnoreCase("user not found")) {
                EditText txt = (EditText) findViewById(R.id.user_editTxt);
                txt.setError("User does not exist!");
            } else {
                if (json.get("status").toString().equalsIgnoreCase("passwords do not match")) {
                    EditText txt = (EditText) findViewById(R.id.password_editTxt);
                    txt.setError("Wrong password!");
                }
                else {       //retrieving all dog's information to DogHandler class
                    dogHolder.setName((String) json.get("dog_name"));
                    dogHolder.setAge(Integer.valueOf(json.get("dog_age").toString()));
                    dogHolder.setBreed((String) json.get("dog_breed"));
                    dogHolder.setFood((String) json.get("dog_food"));
                    dogHolder.setUser((String) json.get("username"));
                    dogHolder.setPassword((String) json.get("password"));
                    dogHolder.setPicture((String) json.get("dog_picture"));
                    dogHolder.setId((String) json.get("_id"));
                    Log.d("", "Finished fetching new user details");
                    Intent intent = new Intent(this, UserScreen.class);
                    startActivity(intent);
                    finish();

                }
            }


//            ImageView img = (ImageView) findViewById(R.id.imageView2);
//            img.setImageBitmap(dogHolder.getPicture());
            userDetailRetrieval = false;

        }
        if(passwordRetrieval)
        {
            String status = json.get("status").toString();
            String password="";
            AlertDialog.Builder builder;

            if(status.equalsIgnoreCase("OK")) {
                password = json.get("password").toString();
                builder = new AlertDialog.Builder(this, R.style.PositiveAlertDialog);
                builder.setMessage("Your password is:" + password);
                builder.setTitle("Password reminder");
                builder.setIcon(R.drawable.logo2_small);

            }
            else {
                builder = new AlertDialog.Builder(this, ErrorDialog);
                builder.setMessage("User entered does not exist" + password);
                builder.setIcon(R.drawable.logo2_small);
                builder.setTitle("Something went wrong!");


            }

            builder.setNeutralButton("Got it!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            passDialog = builder.create();
            passDialog.show();
            passwordRetrieval = false;
        }

    }


        //this is a text watcher, it handles changes on the EditText boxes (user and password boxes)
    private TextWatcher textWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        text_changed = true;

        }
    };
    private TextWatcher passTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            pass_changed = true;

        }
    };

    @Override
    protected void onDestroy (){
        Log.d("","Finished");
       super.onDestroy();
    }
    @Override
    protected void onPause (){

        super.onPause();
        Log.d("", "Pause actiivty");

    }






}
