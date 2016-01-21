package com.example.tombushmits.dogapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateNewUser extends AppCompatActivity implements HttpInterfaceHandler  {
    private int PICK_IMAGE_REQUEST = 1;
    String imgDecodableString;
    private static String url_register = "http://46.101.229.180:8000/register";
    private static String url_write_picture = "http://10.0.0.3:5000/write_to_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);

        ImageButton importImage = (ImageButton) findViewById(R.id.image_selection);
        FloatingActionButton submit_button = (FloatingActionButton)findViewById(R.id.Submit);


        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.food_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        importImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent galleryInent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(galleryInent, RESULT_LOAD_IMAGE);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
                //TODO: need to prompot the user that registration is completed and please memorise the password selected
            }
        });

    }

    private void registerNewUser() {
//        Create a new user and register it in sever DB
            //getting all string input from EditText boxes
        Spinner spin = (Spinner)findViewById(R.id.spinner);
        Map<String, String> newDog = new HashMap<String, String>();
        EditText editText = (EditText)findViewById(R.id.dogsName);
        newDog.put("dog_name",editText.getText().toString());
         editText = (EditText)findViewById(R.id.Breed);
        newDog.put("dog_breed", editText.getText().toString());
         editText = (EditText)findViewById(R.id.age);
        newDog.put("dog_age",editText.getText().toString());

        newDog.put("dog_food", spin.getSelectedItem().toString());
         editText = (EditText)findViewById(R.id.user);
        newDog.put("username",editText.getText().toString());
        editText = (EditText)findViewById(R.id.password);
        newDog.put("password", editText.getText().toString());
                //getting the image from the imageView
        ImageView imageView = (ImageView)findViewById(R.id.image_selection);
        String image;
        if (imageView.getDrawable() != null){
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap img = drawable.getBitmap();

            image = getStringFromBitmap(Bitmap.createScaledBitmap(img,300,300,false));
        }
       else
        {
           imageView.setImageResource(R.drawable.no_picture);
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap normal = drawable.getBitmap();
            Bitmap scaled = Bitmap.createScaledBitmap(normal, 300, 300, false);
            image = getStringFromBitmap(scaled);

        }
        newDog.put("dog_picture", image);
        //TODO : need to check if user exists
        JSONObject json = new JSONObject(newDog);
        httpAsyncTask  task = (httpAsyncTask) new httpAsyncTask(CreateNewUser.this);
        task.setContext(this);
        task.execute(getString(R.string.server_url)+"/register" ,"POST",json.toString());




    }

    @Override //this activity will get an image from the image gallery and will display it on selected image view
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

//                ImageView imageView = (ImageView) findViewById(R.id.imageView);
//                imageView.setImageBitmap(bitmap);
//                imageView.setAlpha((float) 0.50);

                ImageButton button  = (ImageButton)findViewById(R.id.image_selection);
                button.setImageBitmap(bitmap);



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private String getStringFromBitmap(Bitmap bitmapPicture) {

        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }


    private Bitmap getBitmapFromString(String jsonString) {

        byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }


    @Override
    public void postHandle(String result) throws JSONException {
        JSONObject json = new JSONObject(result);
        Toast.makeText(CreateNewUser.this,"Dog created!",Toast.LENGTH_LONG).show();
        if(json.get("status").toString().equalsIgnoreCase("OK"))
        {
            //*******storing new dog locally******
            Spinner spin = (Spinner)findViewById(R.id.spinner);
            EditText editText;
            DogHolder dog =DogHolder.getInstance();
            dog.setId(json.get("_id").toString());
            editText = (EditText)findViewById(R.id.user);
            dog.setUser(editText.getText().toString());
            editText = (EditText)findViewById(R.id.password);
            dog.setPassword(editText.getText().toString());
            editText = (EditText)findViewById(R.id.dogsName);
            dog.setName(editText.getText().toString());
            editText = (EditText)findViewById(R.id.Breed);
            dog.setBreed(editText.getText().toString());
            editText = (EditText)findViewById(R.id.age);
            dog.setAge(Integer.valueOf(String.valueOf(editText.getText())));
            dog.setFood(spin.getSelectedItem().toString());
            ImageView img = (ImageView)findViewById(R.id.image_selection);
            BitmapDrawable bit = (BitmapDrawable) img.getDrawable();
            dog.setPicture(getStringFromBitmap(bit.getBitmap()));

            Intent intent =  new Intent(this, UserScreen.class);
            startActivity(intent);

        }
        if(json.get("status").toString().equalsIgnoreCase("User exists")){ //User already exists in the system , need to prompt the user
            AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.ErrorDialog);
            builder.setMessage("this user name already exists , please select a different one...");
            builder.setTitle("Something went wrong!");
            builder.setIcon(R.drawable.dog);
            builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog =builder.create();
            dialog.show();

        }

    }

    @Override
    public void getHandle(String result) {

    }

}
