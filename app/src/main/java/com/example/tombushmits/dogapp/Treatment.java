package com.example.tombushmits.dogapp;

/**
 * Created by tombushmits on 12/31/15.
 */
public class Treatment {
    String treatmentName;
    String doctorName;
    String date;
    String comments;
    String _id;

    public Treatment(String doctor,String treatment, String date, String comments){
        this.treatmentName =treatment;
        this.doctorName = doctor;
        this.date = date;
        this.comments = comments;
        this._id="";
    }
    public Treatment(){
        super();
    }

    public void setId(String id){
        this._id = id;
    }

    public String getName(){return this.treatmentName;}
    public String getDate(){return this.date;}
    public String getDetails(){return this.comments;}
    public String getDoctorName(){return this.doctorName;}
    public String getId(){return this._id;}

}

