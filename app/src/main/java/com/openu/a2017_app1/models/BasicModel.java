package com.openu.a2017_app1.models;


import com.openu.a2017_app1.data.DaoFactory;
import com.openu.a2017_app1.data.QueryBuilder;
import com.openu.a2017_app1.data.SaveListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by noam on 28/12/2016.
 */

public abstract class BasicModel implements Model {

    /**
     * The modle attributs
     */
    protected Map<String, Object> attributes = new HashMap<>();

    /**
     * The primary key for the model
     */
    protected String primaryKay="id";

    protected String table;


    /**
     * The attributes that are mass assignable.
     */
    protected Set<String> fillable = new HashSet<>();


    /**
     * The attributes that aren't mass assignable.
     */
    protected Set<String> guarded = new HashSet<>(Arrays.asList("*"));

    /**
     * Indicates if all mass assignment is enabled.
     */
    protected boolean unguarded = false;



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
     * Get the all attributes from the model
     * @return
     */
    public Map<String,Object> getAttributes()
    {
        return this.attributes;
    }

    public void fill(Map<String,Object> attributes)
    {
        boolean totallyGuarded = this.totallyGuarded();
        for (Map.Entry<String, Object> map : this.fillableAttributes(attributes).entrySet()) {
            String key = this.removeTableFromKey(map.getKey());
            if (this.isFillable(key)) {
                this.setAttribute(key, map.getValue());
            } else if (totallyGuarded) {
                throw new MassAssignmentException(key);
            }
        }
    }


    /**
     * Determine if the model is totally guarded.
     * @return state
     */
    public boolean totallyGuarded() {
        return this.getFillable().size() == 0 && this.getGuarded().contains("*");
    }


    /**
     * Get the guarded attributes for the model.
     * @return
     */
    public Set<String> getGuarded() {
        return guarded;
    }


    public String getTable()
    {
        return table;
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


    protected Map<String, Object> fillableAttributes(Map<String, Object> attributes) {
        if (this.getFillable().size() > 0 && !this.isUnguarded()) {
            Map<String, Object> results = new HashMap<>(attributes);
            results.keySet().retainAll(this.getFillable());
            return results;
        }
        return attributes;
    }

    /**
     * Determine if current state is "unguarded".
     * @return state
     */
    public boolean isUnguarded() {
        return unguarded;
    }

    /**
     * Get the fillable attributes for the model.
     * @return
     */
    public Set<String> getFillable() {
        return fillable;
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




    /**
     * Determine if the given attribute may be mass assigned.
     * @param key
     * @return
     */
    public boolean isFillable(String key) {
        if (this.unguarded) {
            return true;
        }
        return false;
    }


    public <T extends Model> QueryBuilder<T> getQuery()
    {
        return DaoFactory.getInstance().create().query(this);
    }

}
