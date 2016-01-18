package com.example.tombushmits.dogapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

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

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,HttpInterfaceHandler
{

    private GoogleMap mMap;
    GPSTracker gps;
    Double latitude;
    Double longitude;
    ArrayList<PartnerInfo> walkers = new ArrayList<PartnerInfo>();
    LatLng curr2;
    LatLng curr3;
    BitmapDescriptor pic;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        httpAsyncTask task = (httpAsyncTask) new httpAsyncTask(MapsActivity.this);
        task.setContext(this);
        task.execute(getString(R.string.server_url) + "/getCoordinates/" + DogHolder.getInstance()._id, "GET");




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

        for(int i=0; i<walkers.size(); i++)
        {
            if(i==walkers.size()-1)
                mMap.addMarker(new MarkerOptions().position(walkers.get(i).getCoordination()).title(walkers.get(i).getName()));
            else {
                mMap.addMarker(new MarkerOptions().position(walkers.get(i).getCoordination()).title(walkers.get(i).getName()).icon(walkers.get(i).getPic()));

            }
        }

        /*// Add a marker in Sydney and move the camera
        LatLng current = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(current).title("You are HERE bro"));
        mMap.addMarker(new MarkerOptions().position(curr2).title("curr2"));
        mMap.addMarker(new MarkerOptions().position(curr3).title("curr3").icon(pic));*/

        LatLng current = new LatLng(DogHolder.getInstance().location.getLatitude(), DogHolder.getInstance().location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
        mMap.animateCamera(zoom);
    }

    @Override
    public void postHandle(String result) throws JSONException {

    }
    @Override
    public void getHandle(String result) throws JSONException
    {
        //get all treatment here
        JSONObject json = new JSONObject(result);
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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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

}

