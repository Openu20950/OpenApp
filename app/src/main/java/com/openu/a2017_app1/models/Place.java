package com.openu.a2017_app1.models;
import com.openu.a2017_app1.data.QueryBuilder;


/**
 * Created by noam on 28/12/2016.
 */

public class Place extends Model implements IPlace {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_LOCATION = "location";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_CATEGORY = "category";


    /**
     * default constructor
     */
    public Place()
    {
    }

    /**
     * return the name of the place
     * @return name place
     */
    public String getName()
    {
        return (String)this.getAttribute(FIELD_NAME);
    }


    /**
     * set the place name
     * @param name
     */
    public void setName(String name)
    {
        this.setAttribute(FIELD_NAME,name);
    }

    /**
     * retun the point of the place location
     * @return
     */
    public LocationPoint getLocation()
    {
        return (LocationPoint)this.getAttribute(FIELD_LOCATION);
    }

    /**
     * set the point of the place location
     * @param location
     */
    public void setLocation(LocationPoint location)
    {
        this.setAttribute(FIELD_LOCATION, location);

    }

    /**
     * return the phone number of the place
     * @return
     */
    public String getPhone()
    {
        return (String)this.getAttribute(FIELD_PHONE);
    }

    /**
     * set the phone number of the place
     * @param phoneNum
     */
    public void setPhone(String phoneNum)
    {
        this.setAttribute(FIELD_PHONE,phoneNum);
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
     * return
     * @return
     */
    public String getCategory()
    {
        return (String)this.getAttribute(FIELD_CATEGORY);
    }


    /**
     *
     * @param category
     */
    public void setCategory(String category)
    {
        this.setAttribute(FIELD_CATEGORY,category);
    }

    /**
     *
     * @return
     */
    public QueryBuilder<Review> getReviews()
    {
        return Model.getQuery(Review.class).where(getTable(), this);
    }

}

