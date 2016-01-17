package com.example.tombushmits.dogapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GPSTracker extends Service implements LocationListener{

	private final Context context;

	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;

	Location location;

	double latitude;
	double longitude;

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;
	private static final long MIN_TIME_BW_UPDATES = 1000 * 30 * 1;

	protected LocationManager locationManager;

	public GPSTracker(Context context) {
		this.context = context;
	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {

			} else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {

					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

						if (location != null) {

							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}

				}

				if (isGPSEnabled) {
					if (location == null) {
							locationManager.requestLocationUpdates(
									LocationManager.GPS_PROVIDER,
									MIN_TIME_BW_UPDATES,
									MIN_DISTANCE_CHANGE_FOR_UPDATES, this);



						if (locationManager != null) {
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}


	@TargetApi(Build.VERSION_CODES.M)
	public void stopUsingGPS() {
		if (locationManager != null) {

			locationManager.removeUpdates(GPSTracker.this);

			Toast.makeText(context,"GPS OFF",Toast.LENGTH_LONG).show();
		}

	}

	public double getLatitude() {
		if(location != null) {
			latitude = location.getLatitude();
		}
		return latitude;
	}
	
	public double getLongitude() {
		if(location != null) {
			longitude = location.getLongitude();
		}
		
		return longitude;
	}
	
	public boolean canGetLocation() {
		return this.canGetLocation;
	}
	
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		
		alertDialog.setTitle("GPS is settings");
		
		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
		
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(intent);
			}
		});
		
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		alertDialog.show();
	}
	
	@Override
	public void onLocationChanged(Location arg0) {
		UpdateLocation task = (UpdateLocation)new UpdateLocation().execute("http://46.101.229.180:8000" + "/setCoordinates/" + DogHolder.getInstance().getUser() + "/" + Double.toString(arg0.getLatitude()) + "/" + Double.toString(arg0.getLongitude()));

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	public class UpdateLocation extends AsyncTask<String, Void, String> {
		private Context mContext;
		private String mUrl;
		HttpURLConnection http;

		public UpdateLocation() {

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... params) {
			//TODO: advance background methods -check
			try {
				URL url = new URL(params[0]);
				http = (HttpURLConnection) url.openConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}


			http.disconnect();
			return "no response";
		}

		@Override
		protected void onPostExecute(String strings) {
			super.onPostExecute(strings);

		}

	}

	public void startGps() {
		getLocation();
		Toast.makeText(context , "GPS ON!",Toast.LENGTH_LONG).show();

	}
}
