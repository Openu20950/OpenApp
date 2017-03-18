package com.openu.a2017_app1.models;
import com.openu.a2017_app1.data.QueryBuilder;


/**
 * Created by noam on 28/12/2016.
 */

public class Place extends Model implements IPlace {

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
        return (String)this.getAttribute("Name");
    }


    /**
     * set the place name
     * @param name
     */
    public void setName(String name)
    {
        this.setAttribute("Name",name);
    }

    /**
     * retun the point of the place location
     * @return
     */
    public LocationPoint getLocation()
    {
        return (LocationPoint)this.getAttribute("Location");
    }

    /**
     * set the point of the place location
     * @param location
     */
    public void setLocation(LocationPoint location)
    {
        this.setAttribute("Location",location);

    }

    /**
     * return the phone number of the place
     * @return
     */
    public String getPhone()
    {
        return (String)this.getAttribute("Phone");
    }

    /**
     * set the phone number of the place
     * @param phoneNum
     */
    public void setPhone(String phoneNum)
    {
        this.setAttribute("Phone",phoneNum);
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
     * return
     * @return
     */
    public String getCategory()
    {
        return (String)this.getAttribute("Category");
    }


    /**
     *
     * @param category
     */
    public void setCategory(String category)
    {
        this.setAttribute("Category",category);
    }

    /**
     *
     * @return
     */
    public QueryBuilder<Recommendation> getRecommendations()
    {
        return Model.getQuery(Recommendation.class).where("Place", this);
    }

}

