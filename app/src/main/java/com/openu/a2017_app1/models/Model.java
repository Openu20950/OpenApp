package com.openu.a2017_app1.models;


import android.support.annotation.Nullable;

import com.openu.a2017_app1.data.DaoFactory;
import com.openu.a2017_app1.data.QueryBuilder;
import com.openu.a2017_app1.data.SaveListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by noam on 28/12/2016.
 */

public abstract class Model implements IModel {

    private static final String FIELD_CREATED_AT = "created_at";
    private static final String FIELD_UPDATED_AT = "updated_at";
    /**
     * The modle attributs
     */
    protected Map<String, Object> attributes = new HashMap<>();

    /**
     * The primary key for the model
     */
    protected String primaryKay="id";

    /**
     * Get an attribute from the model.
     * @param attributeName
     * @return
     */
    public Object getAttribute(String attributeName)
    {
        if(this.attributes.containsKey(attributeName))
        {
            return this.attributes.get(attributeName);
        }
        return null;
    }

    /**
     * Set a given attribute on the model.
     * @param attributeName
     * @param attributeValue
     */
    public void setAttribute(String attributeName,Object attributeValue)
    {
        this.attributes.put(attributeName,attributeValue);
    }

    /**
     * Get the primary key of the model
     * @return
     */
    public String getPrimaryKey()
    {
        return this.primaryKay;
    }

    /**
     * Get the primary key of the model
     * @return
     */
    public String getId()
    {
        Object id = this.getAttribute(this.getPrimaryKey());
        return id == null ? null : id.toString();
    }

    /**
     * Get the create time of the object
     * @return
     */
    public Date getCreatedAt()
    {
        return (Date) this.getAttribute(FIELD_CREATED_AT);
    }

    /**
     * Get the last update time of the object
     * @return
     */
    public Date getUpdatedAt()
    {
        return (Date) this.getAttribute(FIELD_UPDATED_AT);
    }

    /**
     * Get the all attributes from the model
     * @return
     */
    public Map<String,Object> getAttributes()
    {
        return this.attributes;
    }

    public void fill(Map<String,Object> attributes)
    {
        for (Map.Entry<String, Object> map : attributes.entrySet()) {
            String key = this.removeTableFromKey(map.getKey());
            this.setAttribute(key, map.getValue());
        }
    }

    public String getTable()
    {
        return getClass().getSimpleName();
    }

    public void save()
    {
        DaoFactory.getInstance().create().save(this);
    }

    public void saveAsync(final ModelSaveListener listener)
    {
        DaoFactory.getInstance().create().saveAsync(this, new SaveListener() {
            @Override
            public void onFinish(boolean succeeded, Object id) {
                if(listener!=null)
                {
                    listener.onSave(succeeded,id);
                }
            }
        });
    }

    /**
     * Remove the table name from a given key.
     * @param key
     * @return key
     */
    private String removeTableFromKey(String key) {
        if (!key.contains(".")) {
            return key;
        }
        String[] parts = key.split("\\.");
        return parts[parts.length - 1];
    }

    @Nullable
    public static <T extends IModel> QueryBuilder<T> getQuery(Class<T> clas)
    {
        T model = null;
        try {
            model = clas.newInstance();
            return DaoFactory.getInstance().create().query(model);
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }

    }

}
