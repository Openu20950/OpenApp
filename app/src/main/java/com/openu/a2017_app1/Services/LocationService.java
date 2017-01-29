package com.openu.a2017_app1.services;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.openu.a2017_app1.models.LocationPoint;
import com.openu.a2017_app1.screens.PostReviewActivity;

/**
 * Created by Emil on 1/5/2017.
 */

public class LocationService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int LOCATION_REQUEST = 11;
    private ImageView imgSpecimenPhoto;
    public static GoogleApiClient mGoogleApiClient;
    AppCompatActivity myActivity;
    //PostReviewActivity postact;
    public Location mLastLocation;

    public LocationPoint locpoint;

    public LocationService(AppCompatActivity activity){
        myActivity = activity;
       // postact = (PostReviewActivity)activity;
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(myActivity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }



    @Override
    public void onConnected(Bundle connectionHint) {
        if ( ContextCompat.checkSelfPermission( myActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( myActivity, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    LOCATION_REQUEST );
        }else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //if (mLastLocation != null) {
                //GetLocationPoint();
                //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            //}
        }
    }


    public LocationPoint GetLocationPoint(){
        if ( ContextCompat.checkSelfPermission( myActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( myActivity, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    LOCATION_REQUEST );
        }else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        locpoint = new LocationPoint();
        if(mLastLocation!=null) {
            locpoint.setLatitude(Double.parseDouble(String.valueOf(mLastLocation.getLatitude())));
            locpoint.setLongitude(Double.parseDouble(String.valueOf(mLastLocation.getLongitude())));
        }else{
            return null;
            //Toast.makeText(this, "Couldn't get location", Toast.LENGTH_LONG).show();
            //postact.DisplayMessage();

        }
        return locpoint;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        //Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        //Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    public LocationPoint onRequestPermissionsResultHandler(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    return GetLocationPoint();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //disable button
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
        return null;
    }

}
