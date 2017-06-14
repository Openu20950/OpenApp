package com.openu.a2017_app1.data;

import android.os.AsyncTask;

import com.openu.a2017_app1.models.IModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Raz on 30/12/2016.
 */

public class MemoryDao implements Dao {

    private static final Map<String, List<IModel>> db;

    static {
        db = new HashMap<>();
    }

    /* package */ MemoryDao() {
    }

    @Override
    public boolean save(IModel model) {
        if (model.getAttribute(model.getPrimaryKey()) != null) {
            return true;
        }
        int id = addToTable(model.getTable(), model);
        model.setAttribute(model.getPrimaryKey(), id);
        return true;
    }

    @Override
    public void saveAsync(final IModel model, final SaveListener callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (model.getAttribute(model.getPrimaryKey()) != null) {
                    if (callback != null) {
                        callback.onFinish(true, model.getAttribute(model.getPrimaryKey()));
                    }
                    return null;
                }
                int id = addToTable(model.getTable(), model);
                model.setAttribute(model.getPrimaryKey(), id);
                if (callback != null) {
                    callback.onFinish(true, id);
                }
                return null;
            }
        };
    }

    @Override
    public <T extends IModel> QueryBuilder query(IModel model) {
        List<IModel> table = db.get(model.getTable());
        if (table == null) {
            db.put(model.getTable(), table = new ArrayList<>());
        }
        return new MemoryQueryBuilder<>(table);
    }

    /**
     * Removes all the data from the data store.
     * The data store will be empty after this call returns.
     */
    public static void clearDb() {
        db.clear();
    }

    /**
     * Adds the model to the table.
     * @param tableName
     * @param model
     * @return
     */
    private int addToTable(String tableName, IModel model) {
        List<IModel> table;
        if (db.containsKey(tableName)) {
            table = db.get(tableName);
        } else {
            table = new ArrayList<>();
            db.put(tableName, table);
        }
        table.add(model);
        return table.size() - 1;
    }

}

