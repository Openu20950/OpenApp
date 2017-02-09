package com.openu.a2017_app1.data;

import com.openu.a2017_app1.models.IModel;
import com.openu.a2017_app1.models.LocationPoint;


import java.util.List;

/**
 * Created by Raz on 01/12/2016.
 */
public interface QueryBuilder<T extends IModel> {
    /**
     * Get one result.
     * @return
     */
    T get();

    /**
     * Find an object by his id.
     * @return
     */
    T find(Object id);

    /**
     * Get all results.
     * @return
     */
    List<T> getAll();

    /**
     * Filter the results by condition
     * @param field the field name
     * @param operator one of the operators "==", "!=", ">", ">=", "<", "<=". Other operator will cause to exception
     * @param value the value
     * @return this
     */
    QueryBuilder<T> where(String field, String operator, Object value);

    /**
     * Filter the results by equals condition.
     * This method is equals to {@code where(field, "==", value)}.
     * @param field the field name
     * @param value the value
     * @return this
     */
    QueryBuilder<T> where(String field, Object value);

    /**
     * Filter the results by near location condition.
     * @param field the field name
     * @param value the value
     * @param radius the radius to search in meters
     * @return this
     */
    QueryBuilder<T> whereNear(String field, LocationPoint value, double radius);

    /**
     * Filter the results by checking if a field contains a substring.
     * @param field the field name
     * @param substring the substring
     * @return this
     */
    QueryBuilder<T> whereContains(String field, String substring);

    /**
     * Controls the maximum number of results that are returned.
     * @param results
     * @return this
     */
    QueryBuilder<T> limit(int results);

    /**
     * Controls the number of results to skip before returning any results.
     * @param results
     * @return this
     */
    QueryBuilder<T> skip(int results);

    /**
     * Sorts the results in ascending order by the given field name.
     * @param field
     * @return
     */
    QueryBuilder<T> orderByAscending(String field);

    /**
     * Sorts the results in descending order by the given field name.
     * @param field
     * @return
     */
    QueryBuilder<T> orderByDescending(String field);
}
