package com.example.tombushmits.dogapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    GPSTracker gps;
    Boolean partnersReady =false;
    Double latitude;
    Double longitude;
    ArrayList<PartnerInfo> walkers = new ArrayList<PartnerInfo>();
    LatLng curr2;
    LatLng curr3;
    BitmapDescriptor pic;
    View frag_view;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getLocationAsyncTask task = (getLocationAsyncTask)new getLocationAsyncTask(this);
        task.execute(getString(R.string.server_url) + "/getCoordinates/" + DogHolder.getInstance()._id);



        /*pic = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.dog));

        curr2 = new LatLng(32.077437,34.777649);
        curr3 = new LatLng(32.077546,34.779859);



        gps = new GPSTracker(MapsActivity.this);

        if (gps.canGetLocation())
        {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            //TODO: get my location from DogHolder:
            //DogHolder.getInstance().location.setLatitude(latitude);
            //DogHolder.getInstance().location.setLongitude(longitude);

        } else {
            gps.showSettingsAlert();
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;



        /*// Add a marker in Sydney and move the camera
        LatLng current = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(current).title("You are HERE bro"));
        mMap.addMarker(new MarkerOptions().position(curr2).title("curr2"));
        mMap.addMarker(new MarkerOptions().position(curr3).title("curr3").icon(pic));*/

        LatLng current = new LatLng(DogHolder.getInstance().location.getLatitude(), DogHolder.getInstance().location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
        mMap.animateCamera(zoom);

        for(int i=0; i<walkers.size(); i++)
        {
            if(i==walkers.size()-1)
                mMap.addMarker(new MarkerOptions().position(walkers.get(i).getCoordination()).title(walkers.get(i).getName()));
            else {
                mMap.addMarker(new MarkerOptions().position(walkers.get(i).getCoordination()).title(walkers.get(i).getName()).icon(walkers.get(i).getPic()));

            }
        }

    }

    public void startMapFragment()
    {
        //adding myself to array list of walkers:
        PartnerInfo toAdd = new PartnerInfo();
        toAdd.setLatitude(DogHolder.getInstance().location.getLatitude());
        toAdd.setLongitude(DogHolder.getInstance().location.getLongitude());
        toAdd.setLatLong();
        toAdd.setName(DogHolder.getInstance().name);
        Bitmap bitmap = getRoundedBitmap(DogHolder.getInstance().picture);
        toAdd.setPic(bitmap);
        walkers.add(toAdd);

        //starting map activity:
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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


    public class getLocationAsyncTask extends AsyncTask<String,Void,String>{
        HttpURLConnection connection;

        Context context;

        public getLocationAsyncTask(Context context){this.context = context;}
        @Override
        protected String doInBackground(String... params) {
            URL url = null;

            try {
                url = new URL(params[0]);

                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                        br.close();
                        return sb.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "No response";
        }

        @Override
        protected void onPostExecute(String result){
            JSONObject json = null;
            try {
                json = new JSONObject(result);
                PartnerInfo toAdd = null;
                JSONArray array = json.getJSONArray("Dogs"); //gettting top key from the json array
                int size = array.length(); //iterate over each value (=each treatment) and parse data
                for(int i=0; i< array.length(); i++)
                {
                    JSONObject obj = array.getJSONObject(i);
                    String name = obj.get("dog_name").toString();
                    String pic = obj.get("dog_picture").toString();
                    String lat = obj.get("latitude").toString();
                    String longi = obj.get("longitude").toString();
                    toAdd = new PartnerInfo(name, pic, lat, longi);
                    toAdd.setLatLong();
                    walkers.add(toAdd);
                }
                startMapFragment();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            }



    }


}

