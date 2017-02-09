package com.openu.a2017_app1.data;

import com.openu.a2017_app1.models.IModel;


/**
 * Created by Raz on 01/12/2016.
 */

public interface Dao {
    /**
     * Save the model
     * @param model
     * @return
     */
    boolean save(IModel model);

    /**
     * Save the model asynchronously
     * @param model
     * @param callback
     */
    void saveAsync(IModel model, SaveListener callback);

    /**
     * Create a query builder
     * @param model
     * @param <T>
     * @return
     */
    <T extends IModel> QueryBuilder<T> query(IModel model);
}
