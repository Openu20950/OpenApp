package com.openu.a2017_app1.models;
import java.util.List;

/**
 * Created by noam on 29/12/2016.
 */

public interface Place extends Model {


    /**
     * return the name of the place
     * @return name place
     */
    public String getName();


    /**
     * set the place name
     * @param name
     */
    public void setName(String name);

    /**
     * retun the point of the place location
     * @return
     */
    public LocationPoint getLocation();

    /**
     * set the point of the place location
     * @param location
     */
    public void setLocation(LocationPoint location);

    /**
     * return the phone number of the place
     * @return
     */
    public String getPhone();

    /**
     * set the phone number of the place
     * @param phoneNum
     */
    public void setPhone(String phoneNum);


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
     * return
     * @return
     */
    public String getCategory();


    /**
     *
     * @param category
     */
    public void setCategory(String category);

    /**
     *
     * @return
     */
    public List<Recommendation> getRecommendations();

    /**
     *
     * @param recommendation
     */
    public void addRecommendation(Recommendation recommendation);

    //public boolean removeRecommendation(Recommendation recommendation);
}
