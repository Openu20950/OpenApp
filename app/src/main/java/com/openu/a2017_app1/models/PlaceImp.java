package com.openu.a2017_app1.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by noam on 28/12/2016.
 */

public class PlaceImp extends BasicModel implements Place {

    private String name;
    private LocationPoint placeLocation;
    private String phone;
    private String description;
    private String category;
    private List<Recommendation> recommendation;


    /**
     * default constructor
     */
    public PlaceImp()
    {
        this.name="";
        this.placeLocation=new LocationPoint();
        this.phone="";
        this.description="";
        this.category="";
        this.recommendation=new ArrayList<Recommendation>();
    }

    /**
     * return the name of the place
     * @return name place
     */
    public String getName()
    {
        return this.name;
    }


    /**
     * set the place name
     * @param name
     */
    public void setName(String name)
    {
        this.name=name;
    }

    /**
     * retun the point of the place location
     * @return
     */
    public LocationPoint getLocation()
    {
        return this.placeLocation;
    }

    /**
     * set the point of the place location
     * @param location
     */
    public void setLocation(LocationPoint location)
    {
        this.placeLocation=location;

    }

    /**
     * return the phone number of the place
     * @return
     */
    public String getPhone()
    {
        return this.phone;
    }

    /**
     * set the phone number of the place
     * @param phoneNum
     */
    public void setPhone(String phoneNum)
    {
        this.phone=phoneNum;
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
     * return
     * @return
     */
    public String getCategory()
    {
        return this.category;
    }


    /**
     *
     * @param category
     */
    public void setCategory(String category)
    {
        this.category=category;
    }

    /**
     *
     * @return
     */
    public List<Recommendation> getRecommendations()
    {
        return Collections.unmodifiableList(recommendation);
    }

    /**
     *
     * @param recommendation
     */
    public void addRecommendation(Recommendation recommendation)
    {
        if(this.recommendation==null)
        {
            this.recommendation=new ArrayList<Recommendation>();
            this.recommendation.add(recommendation);
        }else{
            this.recommendation.add(recommendation);
        }
    }


}