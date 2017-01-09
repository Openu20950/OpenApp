package com.openu.a2017_app1.data;

import android.support.annotation.NonNull;

import com.openu.a2017_app1.data.parse.Converters;
import com.openu.a2017_app1.models.Model;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Map;

/**
 * Created by Raz on 01/12/2016.
 */

public class ParseDao implements Dao {

    /* package */ static final String PARSE_OBJECT_ATTRIBUTE = "_parse";


    /* package */ ParseDao() {

    }

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
        ParseObject object = getParseObject(model);
        Converters converters = Converters.getInstance();
        for (Map.Entry<String, Object> attribute : model.getAttributes().entrySet()) {
            if (attribute.getKey().equals(model.getPrimaryKey())) {
                object.setObjectId((String)attribute.getValue());
            } else {
                object.put(attribute.getKey(), converters.convert(attribute.getValue()));
            }
        }
        return object;
    }

    @NonNull
    private ParseObject getParseObject(Model model) {
        Object parse = model.getAttribute(PARSE_OBJECT_ATTRIBUTE);
        return parse != null ? (ParseObject)parse : new ParseObject(model.getTable());
    }
}
