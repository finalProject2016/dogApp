package com.example.tombushmits.dogapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by tombushmits on 1/2/16.
 */
public class httpAsyncTask extends AsyncTask<String, Integer, String> {

    HttpInterfaceHandler httpInterface;
    String requestType;
    HttpURLConnection http;
    ProgressDialog dialog;
    private Context context;


    public httpAsyncTask(HttpInterfaceHandler interFace) {
        //We set the interface here
        this.httpInterface = interFace;
    }

    @Override
    protected String doInBackground(String... params) {
        this.requestType = params[1]; //POST/GET
        if(requestType == "GET")
            dialog.setMessage("Fetching user details , please wait....");
        if(requestType == "POST")
            dialog.setMessage("Creating new user , please wait....");
        String json;




        try {
            if(requestType == "POST") {
                URL url = new URL(params[0]);
                json = params[2];
                http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                http.setDoInput(true);
                http.setRequestProperty("Content-Type", "application/json");
                http.setRequestProperty("Host", "android.schoolportal.gr");
                http.setRequestProperty("utf-8", "charset");
//            http.connect();

                DataOutputStream printout = new DataOutputStream(http.getOutputStream());
                printout.writeBytes(json);
                printout.flush();
                printout.close();
            }
            if(requestType == "GET")
            {
                URL url = new URL(params[0]);
                http= (HttpURLConnection)url.openConnection();
            }


            try {
                int filelength = http.getContentLength();

                BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream())); //download the file stream
                StringBuilder builder = new StringBuilder();
                char[] buf = new char[8000];
                int l=0;
                int count = 0;
                while(l>=0){
                    builder.append(buf,0,l);
                    l=br.read(buf);
                    count+=l;
                    if(l <0)
                        count++;
                    publishProgress((int) (count * 100 / filelength));
                }
//                return builder.toString();






                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {


                    sb.append(line + "\n");

                }
                br.close();

                return builder.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } finally {
                //Close the http connection when done.
                http.disconnect();
            }
            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No response";






    }

    @Override
    protected void onPostExecute(String result) {
        if(dialog.isShowing())
            dialog.dismiss();

        if(requestType == "GET")
        try {
            httpInterface.getHandle(result) ;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(requestType == "POST")
            try {
                httpInterface.postHandle(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    @Override
    protected void onPreExecute() {
//        this.dialog.setMessage("Working...");
//        this.dialog.show();
        super.onPreExecute();
        //if(requestType == "GET") {
            dialog = new ProgressDialog(this.context);
            dialog.setTitle("DOGM8");


        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIcon(R.drawable.dog);
            dialog.setCancelable(false);
            dialog.show();
        //}



    }
    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        dialog.setIndeterminate(false);
        dialog.setMax(100);
        dialog.setProgress(progress[0]);
    }
    public void setContext(Context context){this.context = context;}
}
