package com.openu.a2017_app1.data;

import android.location.Location;

import com.openu.a2017_app1.data.parse.Converters;
import com.openu.a2017_app1.models.LocationPoint;
import com.openu.a2017_app1.models.Model;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Raz on 01/12/2016.
 */

class ParseQueryBuilder<T extends Model> implements QueryBuilder<T> {

    private static final int METERS_TO_KILOMETERS = 100;

    private ParseQuery<ParseObject> query;
    private Class<T> clazz;

    public ParseQueryBuilder(ParseQuery<ParseObject> query, Class<T> clazz) {
        this.query = query;
        this.clazz = clazz;
    }

    @Override
    public T get() {
        try {
            return createModelFor(this.query.getFirst());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public T find(Object id) {
        if (!(id instanceof String)) {
            throw new IllegalArgumentException("The id must be a string");
        }
        try {
            return createModelFor(this.query.get((String)id));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<T> getAll() {
        try {
            List<T> res = new ArrayList<>();
            for (ParseObject obj : query.find()) {
                res.add(createModelFor(obj));
            }
            return res;
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public QueryBuilder<T> where(String field, String operator, Object value) {
        switch (operator) {
            case "==":
                query.whereEqualTo(field, value);
                break;
            case "!=":
                query.whereNotEqualTo(field, value);
                break;
            case ">":
                query.whereGreaterThan(field, value);
                break;
            case ">=":
                query.whereGreaterThanOrEqualTo(field, value);
                break;
            case "<":
                query.whereLessThan(field, value);
                break;
            case "<=":
                query.whereLessThanOrEqualTo(field, value);
                break;
            default:
                new IllegalArgumentException("operator \"" + operator + "\" is not supported!");
        }
        return this;
    }

    @Override
    public QueryBuilder<T> where(String field, Object value) {
        query.whereEqualTo(field, value);
        return this;
    }

    @Override
    public QueryBuilder<T> whereNear(String field, LocationPoint value, double radius) {
        query.whereWithinKilometers(field,
                new ParseGeoPoint(value.getLatitude(), value.getLongitude()),
                radius / METERS_TO_KILOMETERS);
        return this;
    }

    @Override
    public QueryBuilder<T> whereContains(String field, String substring) {
        query.whereContains(field, substring);
        return this;
    }

    @Override
    public QueryBuilder<T> limit(int results) {
        query.setLimit(results);
        return this;
    }

    @Override
    public QueryBuilder<T> skip(int results) {
        query.setSkip(results);
        return this;
    }

    @Override
    public QueryBuilder<T> orderByAscending(String field) {
        query.orderByAscending(field);
        return this;
    }

    @Override
    public QueryBuilder<T> orderByDescending(String field) {
        query.orderByDescending(field);
        return this;
    }

    private T createModelFor(ParseObject obj) {
        try {
            T model = clazz.newInstance();
            fillModel(model, obj);
            return model;
        } catch (Exception e) {
            return null;
        }
    }

    private void fillModel(T model, ParseObject obj) {
        Map<String, Object> attributes = getAttributes(model, obj);
        attributes.put(model.getPrimaryKey(), obj.getObjectId());
        attributes.put(ParseDao.PARSE_OBJECT_ATTRIBUTE, obj);
        model.fill(attributes);
    }

    private Map<String,Object> getAttributes(T model, ParseObject obj) {
        Map<String, Object> res = new HashMap<>();
        Object value;
        Converters converters = Converters.getInstance();
        for (String key : obj.keySet()) {
            value = obj.get(key);
            res.put(key, converters.convertBack(value));
        }
        return res;
    }
}
