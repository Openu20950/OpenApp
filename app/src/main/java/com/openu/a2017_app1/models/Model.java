package com.openu.a2017_app1.models;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by noam on 29/12/2016.
 */

public interface Model extends Serializable {

    public Object getAttribute(String attributeName);

    public void setAttribute(String attributeName,Object attributeValue);

    public String getPrimaryKey();

    public Map<String,Object> getAttributes();

    public void fill(Map<String,Object> attributes);

    public String getTable();

    public void save();

    public void saveAsync(final ModelSaveListener listener);


}
