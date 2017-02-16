package com.openu.a2017_app1.data;

import com.openu.a2017_app1.models.IModel;
import com.openu.a2017_app1.models.LocationPoint;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raz on 01/12/2016.
 */

class ParseQueryBuilder<T extends IModel> implements QueryBuilder<T> {

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
            return (T)ModelCreator.createModelFromParse(this.query.getFirst(), clazz);
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
            return (T)ModelCreator.createModelFromParse(this.query.get((String)id), clazz);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<T> getAll() {
        try {
            List<T> res = new ArrayList<>();
            for (ParseObject obj : query.find()) {
                res.add((T)ModelCreator.createModelFromParse(obj, clazz));
            }
            return res;
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public long count() {
        try {
            return query.count();
        }
        catch (ParseException e) {
            return -1;
        }
    }

    @Override
    public QueryBuilder<T> where(String field, String operator, Object value) {
        value = Converters.getInstance().convert(value);
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
        return where(field, "==", value);
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
}
