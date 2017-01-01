package com.openu.a2017_app1.models;

/**
 * Created by noam on 01/01/2017.
 */

public interface LocationPoint {

    /**
     *
     * @return
     */
    public double getLatitude();

    /**
     *
     * @param latitude
     */
    public void setLatitude(double latitude);

    /**
     *
     * @return
     */
    public double getLongitude();

    /**
     *
     * @param longitude
     */
    public void setLongitude(double longitude);
}
