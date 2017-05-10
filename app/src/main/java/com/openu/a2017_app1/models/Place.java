package com.openu.a2017_app1.models;
import android.graphics.Bitmap;

import com.openu.a2017_app1.data.QueryBuilder;


/**
 * Created by noam on 28/12/2016.
 */

public class Place extends Model {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_LOCATION = "location";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_PHOTO = "photo";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_FACEBOOK_ID= "facebook_user_id";

    /**
     * default constructor
     */
    public Place()
    {

    }

    public String getName()
    {
        return (String)this.getAttribute(FIELD_NAME);
    }


    public void setName(String name)
    {
        this.setAttribute(FIELD_NAME,name);
    }

    public LocationPoint getLocation()
    {
        return (LocationPoint)this.getAttribute(FIELD_LOCATION);
    }

    public void setLocation(LocationPoint location)
    {
        this.setAttribute(FIELD_LOCATION, location);
    }

    public String getPhone()
    {
        return (String)this.getAttribute(FIELD_PHONE);
    }

    public void setPhone(String phoneNum)
    {
        this.setAttribute(FIELD_PHONE,phoneNum);
    }

    public Bitmap getPhoto()
    {
        return (Bitmap) this.getAttribute(FIELD_PHOTO);
    }

    public void setPhoto(Bitmap photo)
    {
        this.setAttribute(FIELD_PHOTO, photo);
    }

    public String getDescription()
    {
        return (String)this.getAttribute(FIELD_DESCRIPTION);
    }

    public void setDescription(String description) {this.setAttribute(FIELD_DESCRIPTION,description);}

    public String getCategory()
    {
        return (String)this.getAttribute(FIELD_CATEGORY);
    }

    public void setCategory(String category)
    {
        this.setAttribute(FIELD_CATEGORY,category);
    }

    public  String getFacebookId(){return (String)this.getAttribute(FIELD_FACEBOOK_ID);}

    public void setFacebookId(String myFacebookId) {this.setAttribute(FIELD_FACEBOOK_ID,myFacebookId);}

    public QueryBuilder<Review> getReviews()
    {
        return Model.getQuery(Review.class).where(Review.FIELD_PLACE, this);
    }

}

