package com.openu.a2017_app1.models;

import android.location.Location;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by noam on 01/01/2017.
 */

public class LocationPoint implements Serializable {

    private double latitude;
    private double longitude;

    public LocationPoint()
    {

    }

    /**
     *
     * @return
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     *
     * @param latitude
     */
    public void setLatitude(double latitude)
    {
        this.latitude=latitude;
    }

    /**
     *
     * @return
     */
    public double getLongitude() {
        return this.longitude;
    }

    /**
     *
     * @param longitude
     */
    public void setLongitude(double longitude)
    {
        this.longitude=longitude;
    }

    /**
     *
     * @param location
     * @return the distance between 2 location in KM
     */
    public double distanceTo(LocationPoint location)
    {
        Location location1=new Location("Point 1");
        location1.setLatitude(this.latitude);
        location1.setLongitude(this.longitude);

        Location location2=new Location("Point 2");
        location2.setLatitude(location.getLatitude());
        location2.setLongitude(location.getLongitude());

        double distance=location1.distanceTo(location2)/1000;
        return distance;
    }

    /**
     *
     * @param location
     * @return the bearing between 2 locations
     */
    public double bearingTo(LocationPoint location)
    {
        Location location1=new Location("Point 1");
        location1.setLatitude(this.latitude);
        location1.setLongitude(this.longitude);

        Location location2=new Location("Point 2");
        location2.setLatitude(location.getLatitude());
        location2.setLongitude(location.getLongitude());

        double bearing=location1.bearingTo(location2);
        return bearing;
    }

    /**
     * Convert a Location to a LocationPoint
     * @param location the location
     * @return the location as LocationPoint
     */
    public static LocationPoint fromLocation(@NonNull Location location) {
        LocationPoint lp = new LocationPoint();
        lp.setLatitude(location.getLatitude());
        lp.setLongitude(location.getLongitude());
        return lp;
    }
}
