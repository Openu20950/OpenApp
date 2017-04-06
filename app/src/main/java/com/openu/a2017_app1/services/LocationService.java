package com.openu.a2017_app1.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.openu.a2017_app1.models.LocationPoint;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

/**
 * Created by User on 30/03/2017.
 *
 * Class that helps to get the location and location updates.
 */
public class LocationService {

    private GoogleApiClient mGoogleApiClient;
    private Context mContext;

    /**
     * Initializes a new instance of a LocationService class.
     * @param context
     * @param callbacks
     */
    public LocationService(Context context, @NonNull GoogleApiClient.ConnectionCallbacks callbacks) {
        mContext = context;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(callbacks)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Connects to the location service.
     * @return true, if the connection succeeded. false, otherwise.
     */
    public boolean connect() {
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleApiClient.connect();
            return true;
        }
        return false;
    }

    /**
     * Disconnects from the location service.
     */
    public void disconnect() {
        mGoogleApiClient.disconnect();
    }

    /**
     * Returns the last known location. It may return null if the device dose not allow the location service.
     *
     * This method needs a location permission. Be sure that the user allow the app to get the location, otherwise `SecurityException` may thrown.
     * @return the last known location, or null, if the device dose not allow the location service.
     */
    @Nullable
    public LocationPoint lastLocation() {
        //noinspection MissingPermission
        Location loc = FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (loc == null) {
            return null;
        }
        return LocationPoint.fromLocation(loc);
    }

    /**
     * Requests a locations updates from the service.
     *
     * This method needs a location permission. Be sure that the user allow the app to get the location, otherwise `SecurityException` may thrown.
     * @param callback
     */
    public void requestLocationUpdates(LocationListener callback) {
        //noinspection MissingPermission
        FusedLocationApi.requestLocationUpdates(mGoogleApiClient, LocationRequest.create(), callback);
    }

}
