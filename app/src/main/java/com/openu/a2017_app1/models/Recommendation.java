package com.openu.a2017_app1.models;

import android.graphics.Bitmap;

/**
 * Created by noam on 29/12/2016.
 */

public interface Recommendation {


    /**
     *
     * @return
     */
    public String getTitle();

    /**
     *
     * @param title
     */
    public void setTitle(String title);

    /**
     *
     * @return
     */
    public Place getPlace();

    /**
     *
     * @param place
     */
    public void setPlace(Place place);

    /**
     * return the description of the place
     * @return
     */
    public String getDescription();

    /**
     * set the description of the place
     * @param description
     */
    public void setDescription(String description);

    /**
     * return photo of the place from the recommendation
     * @return
     */
    public Bitmap getPhoto();

    /**
     * set a photo of the place to the recommendation
     * @param photo
     */
    public void setPhoto(Bitmap photo);
}
