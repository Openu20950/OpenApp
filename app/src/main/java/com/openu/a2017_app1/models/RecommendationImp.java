package com.openu.a2017_app1.models;

import android.graphics.Bitmap;

import java.io.Serializable;


/**
 * Created by noam on 28/12/2016.
 */

public class RecommendationImp extends BasicModel implements Recommendation,Serializable {


    private String title;
    private Place place;
    private String description;
    private Bitmap photo;

    /**
     * default constructor
     */
    public RecommendationImp()
    {
        this.table="Recommendation";
        this.title="";
        this.place=new PlaceImp();
        this.description="";
        this.photo=null;
    }


    /**
     *
     * @return
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title)
    {
        this.title=title;
    }

    /**
     *
     * @return
     */
    public Place getPlace()
    {
        return this.place;
    }

    /**
     *
     * @param place
     */
    public void setPlace(Place place)
    {
        this.place=place;
    }

    /**
     * return the description of the place
     * @return
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * set the description of the place
     * @param description
     */
    public void setDescription(String description)
    {
        this.description=description;
    }

    /**
     * return photo of the place from the recommendation
     * @return
     */
    public Bitmap getPhoto()
    {
        return this.photo;
    }

    /**
     * set a photo of the place to the recommendation
     * @param photo
     */
    public void setPhoto(Bitmap photo)
    {
        this.photo=photo;
    }
}
