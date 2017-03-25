package com.openu.a2017_app1.models;

import android.graphics.Bitmap;

/**
 * Created by noam on 29/12/2016.
 */

public interface IReview extends IModel {


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
     * return photo of the place from the review
     * @return
     */
    public Bitmap getPhoto();

    /**
     * set a photo of the place to the review
     * @param photo
     */
    public void setPhoto(Bitmap photo);

    /**
     * return the conclusion of the review
     * @return
     */
    public ReviewConclusion getConclusion();

    /**
     * set a conclusion for the review
     * @param conclusion
     */
    public void setConclusion(ReviewConclusion conclusion);
}
