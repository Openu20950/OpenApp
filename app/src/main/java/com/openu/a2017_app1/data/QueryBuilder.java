package com.openu.a2017_app1.data;

import com.openu.a2017_app1.models.Model;

import java.util.List;

/**
 * Created by Raz on 01/12/2016.
 */
public interface QueryBuilder<T extends Model> {
    /**
     * Get one result.
     * @return
     */
    T get();

    /**
     * Find an object by his id.
     * @return
     */
    T find(String id);

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
