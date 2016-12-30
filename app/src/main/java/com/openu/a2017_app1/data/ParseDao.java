package com.openu.a2017_app1.data;

import com.openu.a2017_app1.models.Model;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Raz on 01/12/2016.
 */

public class ParseDao implements Dao {
    @Override
    public boolean save(Model model) {
        ParseObject object = createObjectForModel(model);
        try {
            object.save();
        } catch (ParseException e) {
            return false;
        }
        model.setAttribute(model.getPrimaryKey(), object.getObjectId());
        return true;
    }

    @Override
    public void saveAsync(final Model model, final SaveListener callback) {
        final ParseObject object = createObjectForModel(model);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                model.setAttribute(model.getPrimaryKey(), object.getObjectId());
                if (callback != null) {
                    callback.onFinish(e == null, object.getObjectId());
                }
            }
        });
    }

    @Override
    public <T extends Model> QueryBuilder<T> query(Model model) {
        return new ParseQueryBuilder(ParseQuery.getQuery(model.getTable()), model.getClass());
    }

    private ParseObject createObjectForModel(Model model) {
        ParseObject object = new ParseObject(model.getTable());
        for (Map.Entry<String, Object> attribute : model.getAttributes().entrySet()) {
            if (attribute.getKey().equals(model.getPrimaryKey())) {
                object.setObjectId((String)attribute.getValue());
            } else if (attribute.getValue() instanceof Model) {
                Model other = (Model) attribute.getValue();
                other.save();
                object.put(attribute.getKey(), other.getAttribute(other.getPrimaryKey()));
            } else if (attribute.getValue() == null) {
                object.put(attribute.getKey(), JSONObject.NULL);
            } else {
                object.put(attribute.getKey(), attribute.getValue());
            }
        }
        return object;
    }
}