package com.openu.a2017_app1.data;

import android.support.annotation.NonNull;

import com.openu.a2017_app1.models.IModel;

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
    public boolean save(IModel model) {
        ParseObject object = ModelCreator.createParseFromModel(model);
        try {
            object.save();
        } catch (ParseException e) {
            return false;
        }
        model.setAttribute(model.getPrimaryKey(), object.getObjectId());
        return true;
    }

    @Override
    public void saveAsync(final IModel model, final SaveListener callback) {
        final ParseObject object = ModelCreator.createParseFromModel(model);
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
    public <T extends IModel> QueryBuilder<T> query(IModel model) {
        return new ParseQueryBuilder(ParseQuery.getQuery(model.getTable()), model.getClass());
    }

    @NonNull
    public static ParseObject getParseObject(IModel model) {
        Object parse = model.getAttribute(PARSE_OBJECT_ATTRIBUTE);
        if (parse == null) {
            parse = new ParseObject(model.getTable());
            model.setAttribute(PARSE_OBJECT_ATTRIBUTE, parse);
        }
        return (ParseObject)parse;
    }
}
