package com.openu.a2017_app1.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by noam on 28/12/2016.
 */

public class PlaceImp extends BasicModel implements Place {
    /**
     * default constructor
     */
    public PlaceImp()
    {
        this.table="Place";
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
    public List<Recommendation> getRecommendations()
    {
        List<Recommendation> recommendationList = (List<Recommendation>)this.getAttribute("Recommendations");
        if (recommendationList == null) {
            return Collections.unmodifiableList(new ArrayList<Recommendation>());
        }
        return Collections.unmodifiableList(recommendationList);
    }

    /**
     *
     * @param recommendation
     */
    public void addRecommendation(Recommendation recommendation)
    {
        if (recommendation == null) {
            return;
        }

        List<Recommendation> recommendationList = (List<Recommendation>)this.getAttribute("Recommendations");

        if (recommendationList == null) {
            recommendationList = new ArrayList<>();
            setAttribute("Recommendations", recommendationList);
        }

        recommendationList.add(recommendation);
    }


}
