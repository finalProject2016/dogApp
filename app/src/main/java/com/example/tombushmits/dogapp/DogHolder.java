package com.example.tombushmits.dogapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Base64;

import java.util.ArrayList;

/**
 * Created by tombushmits on 12/31/15.
 */
public class DogHolder {
    String name;
    String breed;
    int age;
    String dayOfBirth;
    Bitmap picture;
    String food;
    ArrayList<Treatment> treatments;
    String user;
    String password;
    String _id;
    Location location;


            //getters
    public String getName(){return this.name;}
    public String getId(){return this._id;}
    public String getBreed(){return this.breed;}
    public int getAge(){return this.age;}
    public String getDayOfBirth(){return this.dayOfBirth;}
    public String getFood(){return this.food;}
    public ArrayList<Treatment> getTreatments(){return this.treatments;}
    public Bitmap getPicture(){return this.picture;}
    public String getUser(){return this.user;}
    public String getPassword(){return this.password;}

            //setters
    public void setName(String name){this.name=name;}
    public void setBreed(String breed){this.breed = breed;}
    public void setAge(int age){this.age = age;}
    public void setDayOfBirth(String dayOfBirth){this.dayOfBirth = dayOfBirth;}
    public void setFood(String food){this.food = food;};
    public void setTreatment(ArrayList<Treatment> list){this.treatments = list;}
    public void setUser(String user){this.user = user;}
    public void setId(String id){this._id=id;}
    public void setPassword(String password){this.password = password;}
    public void setPicture(String picture){
        byte[] imageAsBytes = Base64.decode(picture.getBytes(), Base64.DEFAULT);
        this.picture = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
            //define the singleton
    private static DogHolder dogHolder = new DogHolder();
    public static DogHolder getInstance(){return dogHolder;}


}


