package com.example.tombushmits.dogapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class UserScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_screen);
        getAllDogData();
        Button button = (Button) findViewById(R.id.goToTreatments);
        Button foodbutton = (Button) findViewById(R.id.goToFood);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                treatments();
            }
        });
        foodbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                food();
            }
        });
        final GPSTracker gps = new GPSTracker(this);

        ToggleButton gpsButton = (ToggleButton) findViewById(R.id.toggleButton);
        gpsButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//gps one
                    gps.startGps();
                } else {       //gps of
                    gps.stopUsingGPS();
                }

            }
        });


    }


    void getAllDogData() {

        ImageView img = (ImageView) findViewById(R.id.dogPic);
        DogHolder dog = DogHolder.getInstance();
        TextView dog_name = (TextView) findViewById(R.id.Dog_name);


//        img.setImageBitmap(dog.getPicture());

        RoundedImageView round  = (RoundedImageView)new RoundedImageView(this);
        round.setImageBitmap(dog.getPicture());
        dog_name.setText(dog.getName());
        Typeface type = Typeface.createFromAsset(getResources().getAssets(), "fonts/scrap.ttf");
        dog_name.setTypeface(type);

    }

    public void treatments() {
        Intent intent = new Intent(this, Treatments.class);
        startActivity(intent);
        Log.d("", "starting activity");

    }

    public void food() {
        Intent intent = new Intent(this, Food.class);
        startActivity(intent);
    }

}