package com.openu.a2017_app1.models;

import android.graphics.Bitmap;

/**
 * Created by noam on 28/12/2016.
 */

public class Review extends Model {

    public static final String FIELD_PLACE = "place";
    public static final String FIELD_COMMENT = "comment";
    public static final String FIELD_SCORE = "score";
    public static final String FIELD_FACEBOOK_ID= "facebook_user_id";
    public static final String FIELD_FACEBOOK_NAME="name";
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
     * @return
     */
    public String getComment()
    {
        return (String)this.getAttribute(FIELD_COMMENT);
    }

    /**
     * @param cooment
     */
    public void setComment(String cooment)
    {
        this.setAttribute(FIELD_COMMENT,cooment);
    }

    public int getScore() {
        return (int) this.getAttribute(FIELD_SCORE);
    }

    public void setScore(int score) {
        this.setAttribute(FIELD_SCORE, score);
    }

    public  String getFacebookId(){return (String)this.getAttribute(FIELD_FACEBOOK_ID);}

    public void setFacebookId(String myFacebookId)
    {
        this.setAttribute(FIELD_FACEBOOK_ID,myFacebookId);
    }

    public String getAuthor(){return (String)this.getAttribute(FIELD_FACEBOOK_NAME);}

    public void setAuthor(String author){this.setAttribute(FIELD_FACEBOOK_NAME,author);}
}
