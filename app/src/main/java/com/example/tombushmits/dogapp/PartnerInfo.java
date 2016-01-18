package com.example.tombushmits.dogapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by AvshalomP on 1/14/2016.
 */
public class PartnerInfo
{
    String name;
    BitmapDescriptor pic;
    Double latitude;
    Double longitude;
    LatLng coordination;
    public PartnerInfo(){}

    public PartnerInfo(String name, String pic, String latitude, String longitude)
    {
        this.name=name;
        this.latitude = Double.valueOf(latitude);
        this.longitude = Double.valueOf(longitude);
        byte[] imageAsBytes = Base64.decode(pic.getBytes(), Base64.DEFAULT);
        Bitmap img = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        this.pic = BitmapDescriptorFactory.fromBitmap(getRoundedBitmap(Bitmap.createScaledBitmap(img,150,150,false)));
        this.coordination = new LatLng(this.latitude, this.longitude);

    }

    //getters
    public String getName(){return this.name;}
    public BitmapDescriptor getPic(){return this.pic;}
    public Double getLatitude(){return this.latitude;}
    public Double getLongitude(){return this.longitude;}
    public LatLng getCoordination(){return this.coordination;}

    //setters
    public void setName(String name){this.name=name;}
    public void setPic(Bitmap pic){
        this.pic = BitmapDescriptorFactory.fromBitmap(pic);}
    public void setLatitude(Double latitude){this.latitude=latitude;}
    public void setLongitude(Double longitude){this.longitude=longitude;}

    public void setLatLong() {
        this.coordination = new LatLng(this.latitude, this.longitude);
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
