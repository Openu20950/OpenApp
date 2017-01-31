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
        return (String)this.getAttribute("Title");
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title)
    {
        this.setAttribute("Title",title);
    }

    /**
     *
     * @return
     */
    public Place getPlace()
    {
        return (Place)this.getAttribute("Place");
    }

    /**
     *
     * @param place
     */
    public void setPlace(Place place)
    {
        this.setAttribute("Place",place);
    }

    /**
     * return the description of the place
     * @return
     */
    public String getDescription()
    {
        return (String)this.getAttribute("Description");
    }

    /**
     * set the description of the place
     * @param description
     */
    public void setDescription(String description)
    {
        this.setAttribute("Description",description);
    }

    /**
     * return photo of the place from the recommendation
     * @return
     */
    public Bitmap getPhoto()
    {
        return (Bitmap)this.getAttribute("Photo");
    }

    /**
     * set a photo of the place to the recommendation
     * @param photo
     */
    public void setPhoto(Bitmap photo)
    {
        this.setAttribute("Photo",photo);
    }
}
