package com.openu.a2017_app1.models;

import android.graphics.Bitmap;

/**
 * Created by noam on 28/12/2016.
 */

public class Review extends Model implements IReview {

    public static final String FIELD_TITLE = "title";
    public static final String FIELD_PLACE = "place";
    public static final String FIELD_DESCRIPTION = "Description";
    public static final String FIELD_PHOTO = "photo";
    public static final String FIELD_CONCLUSION = "conclusion";

    /**
     * default constructor
     */
    public Review()
    {

    }

    /**
     *
     * @return
     */
    public String getTitle()
    {
        return (String)this.getAttribute(FIELD_TITLE);
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title)
    {
        this.setAttribute(FIELD_TITLE,title);
    }

    /**
     *
     * @return
     */
    public Place getPlace()
    {
        return (Place)this.getAttribute(FIELD_PLACE);
    }

    /**
     *
     * @param place
     */
    public void setPlace(Place place)
    {
        this.setAttribute(FIELD_PLACE,place);
    }

    /**
     * return the description of the place
     * @return
     */
    public String getDescription()
    {
        return (String)this.getAttribute(FIELD_DESCRIPTION);
    }

    /**
     * set the description of the place
     * @param description
     */
    public void setDescription(String description)
    {
        this.setAttribute(FIELD_DESCRIPTION,description);
    }

    /**
     * return photo of the place from the recommendation
     * @return
     */
    public Bitmap getPhoto()
    {
        return (Bitmap)this.getAttribute(FIELD_PHOTO);
    }

    /**
     * set a photo of the place to the recommendation
     * @param photo
     */
    public void setPhoto(Bitmap photo)
    {
        this.setAttribute(FIELD_PHOTO,photo);
    }

    @Override
    public ReviewConclusion getConclusion() {
        return ReviewConclusion.valueOf((String) this.getAttribute(FIELD_CONCLUSION));
    }

    @Override
    public void setConclusion(ReviewConclusion conclusion) {
        this.setAttribute(FIELD_CONCLUSION, conclusion.name());
    }
}
