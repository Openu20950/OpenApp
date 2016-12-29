package com.openu.a2017_app1.data;

import com.openu.a2017_app1.models.Model;

/**
 * Created by Raz on 01/12/2016.
 */

public interface Dao {
    /**
     * Save the model
     * @param model
     * @return
     */
    boolean save(Model model);

    /**
     * Save the model asynchronously
     * @param model
     * @param callback
     */
    void saveAsync(Model model, SaveListener callback);

    /**
     * Create a query builder
     * @param model
     * @param <T>
     * @return
     */
    <T extends Model> QueryBuilder query(Model model);
}
