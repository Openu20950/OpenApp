package com.openu.a2017_app1.data;

import com.openu.a2017_app1.data.parse.Converters;
import com.openu.a2017_app1.models.Model;
import com.parse.ParseException;
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
