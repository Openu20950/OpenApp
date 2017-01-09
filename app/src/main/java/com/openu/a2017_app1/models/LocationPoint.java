package com.openu.a2017_app1.models;

/**
 * Created by noam on 01/01/2017.
 */

public class LocationPoint  {

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
}
