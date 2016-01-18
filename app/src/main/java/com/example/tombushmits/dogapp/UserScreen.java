package com.example.tombushmits.dogapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
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
        ImageButton button = (ImageButton) findViewById(R.id.treatment_imagebutton);
        ImageButton foodbutton = (ImageButton) findViewById(R.id.food_imagebutton);
        ImageButton mapbutton = (ImageButton)findViewById(R.id.walk_imagebutton);
        ImageButton logoutbutton = (ImageButton)findViewById(R.id.logout_button);
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
        mapbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(DogHolder.getInstance().location == null )
                    Snackbar.make(findViewById(android.R.id.content),"Please enable GPS first",Snackbar.LENGTH_SHORT).show();
                else
                    map();
                }
        });
        logoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               logout();
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
        Bitmap b = dog.getPicture();
        img.setImageBitmap(getRoundedBitmap(b));

      //  RoundedImageView round  = (RoundedImageView)new RoundedImageView(this);
        //round.setImageBitmap(dog.getPicture());
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
    public void map(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public void logout(){

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}