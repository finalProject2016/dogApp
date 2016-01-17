package com.example.tombushmits.dogapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;

import java.util.HashMap;

public class Food extends AppCompatActivity implements HttpInterfaceHandler
{
    private WebView mWebview;
    public String default_food;// = getString(R.string.defaultFood);
    private ImageButton ImageButtons[];
    final Context context = this;
    public String current_food;
    private HashMap<String,String> foodToUrl = new HashMap<String,String>();
    private static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        buildHM();
        default_food = DogHolder.getInstance().getFood();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("default_food", default_food);
        editor.putString("current_food", default_food);

        editor.commit();


        String test = sharedpreferences.getString("default_food","");

        ImageButton ImageButtons[] = {(ImageButton)findViewById(R.id.food6),(ImageButton)findViewById(R.id.food7),
                (ImageButton)findViewById(R.id.food8),(ImageButton)findViewById(R.id.food9),
                (ImageButton)findViewById(R.id.food10)};

        mWebview  = (WebView) findViewById(R.id.webview);
        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        final Activity activity = this;

        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        mWebview.loadUrl(foodToUrl.get(default_food));


        View.OnLongClickListener listener = new View.OnLongClickListener()
        {
              @Override
              public boolean onLongClick(View v)
              {
                  default_food = (String)v.getTag();

                  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                  // set title
                 // alertDialogBuilder.setTitle("Your Title");

                  // set dialog message
                  alertDialogBuilder
                          .setMessage("Set this food to Default?")
                          .setCancelable(false)
                          .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog,int id)
                              {
                                  //updating Dog object food

                                  DogHolder.getInstance().setFood(default_food);
                                  //default_food = foodToUrl.get(current_food);
                                  SharedPreferences.Editor editor = sharedpreferences.edit();
                                  editor.putString("default_food", default_food);
                                  editor.commit();

                                  //updating the server
                                  httpAsyncTask task = (httpAsyncTask) new httpAsyncTask(Food.this);
                                  task.setContext(Food.this);
                                  String url = getString(R.string.server_url) + "/updateFood/"+DogHolder.getInstance().getUser()+"/"+default_food;
                                  task.execute(url,"GET");



                                  // if this button is clicked, close
                                  // current activity
                                  AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(context);
                                  // set dialog message
                                  alertDialogBuilder2
                                          .setIcon(R.drawable.greenfoot)
                                          .setTitle("Food saved!")
                                          .setCancelable(true);



                                  // create alert dialog
                                  AlertDialog alertDialog2 = alertDialogBuilder2.create();

                                  // show it
                                  alertDialog2.show();

                              }
                          })
                          .setNegativeButton("No",new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog,int id) {
                                  // if this button is clicked, just close
                                  // the dialog box and do nothing
                                  dialog.cancel();
                              }
                          });

                  // create alert dialog
                  AlertDialog alertDialog = alertDialogBuilder.create();

                  // show it
                  alertDialog.show();

                  return true;
              }
        };

        for(int i=0; i<5; i++)
        {
            ImageButtons[i].setOnLongClickListener(listener);
        }


    }
    //"OnClick" function of food horizontal scroller
    public void Launch_web(View v)
    {
        current_food = ((String) v.getTag());
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("current_food", current_food);
        editor.commit();
        mWebview.loadUrl(foodToUrl.get(current_food));

    }

    //building HashMap array: <food name, zap URL>
    public void buildHM()
    {
        foodToUrl.put("Pro_Plan","http://www.zap.co.il/model.aspx?modelid=887534");
        foodToUrl.put("Royal_Canin","http://www.zap.co.il/model.aspx?modelid=719463");
        foodToUrl.put("Bonzo","http://www.zap.co.il/model.aspx?modelid=749784");
        foodToUrl.put("Taste_of_the_world", "http://www.zap.co.il/model.aspx?modelid=852659");
        foodToUrl.put("Go", "http://www.zap.co.il/model.aspx?modelid=846394");

    }

    @Override
    protected void onResume()
    {
        //sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        current_food = sharedpreferences.getString("current_food","");
        Log.w("onResume",current_food+"!!!");
        super.onResume();
        mWebview.loadUrl(foodToUrl.get(current_food));
    }

    @Override
    public void postHandle(String result) throws JSONException {

    }

    @Override
    public void getHandle(String result) throws JSONException {

    }
}
