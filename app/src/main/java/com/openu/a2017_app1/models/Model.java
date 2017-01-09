package com.openu.a2017_app1.models;

import com.openu.a2017_app1.data.QueryBuilder;

import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by noam on 29/12/2016.
 */

public interface Model {

    public Object getAttribute(String attributeName);

    public void setAttribute(String attributeName,Object attributeValue);

    public String getPrimaryKey();

    public Map<String,Object> getAttributes();

    public void fill(Map<String,Object> attributes);

    public String getTable();

    public void save();

    public void saveAsync(final ModelSaveListener listener);

    public <T extends Model> QueryBuilder<T> getQuery();
}
