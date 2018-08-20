package edu.illinois.finalproject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by alexandregeubelle on 11/8/17.
 */

//Based off youtube tutorial found at: https://www.youtube.com/watch?v=h7LUNCC0U1U

public class GPSTracker extends Service implements LocationListener{

    private final Context context;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    //Variables holding the location.
    Location location;
    double latitude;
    double longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; //1000ms * 60s * 1min

    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.context = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            //Check permission to use the user's location.
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                showSettingsAlert();
                return null;
            }

            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(isGPSEnabled || isNetworkEnabled) {
                this.canGetLocation = true;

                // Set up request of user location through the network. Will fill values
                // of the location, latitude and longitude.
                if(isNetworkEnabled) {
                    setUpRequestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES);
                }

                // If the location is still null, and the GPS is enabled.
                if(isGPSEnabled && location == null) {
                    setUpRequestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES);
                }
            } else {
                canGetLocation = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(location == null) {
            canGetLocation = false;
        }
        return location;
    }

    private void setUpRequestLocationUpdates(String provider, long minTimeBetweenUpdates,
                                                 long minDistanceBetweenUpdates) {
        try {
            locationManager.requestLocationUpdates(provider,
                    minTimeBetweenUpdates,
                    minDistanceBetweenUpdates,
                    this);

            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(provider);

                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        } catch (SecurityException e) {
            showSettingsAlert();
            e.printStackTrace();
        }

    }

    public void stopUsingGPS() {
        if(locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
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

    //Alert Dialog documentation: https://developer.android.com/reference/android/app/AlertDialog.html
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        final String title = context.getResources().getString(R.string.GPS_alert_dialog_title);
        final String message = context.getResources().getString(R.string.GPS_alert_dialog_text);
        final String positiveButtonText = context.getResources().getString(R.string.GPS_alert_positive_button_text);
        final String negativeButtonText = context.getResources().getString(R.string.GPS_alert_negative_button_text);

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        alertDialog.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
