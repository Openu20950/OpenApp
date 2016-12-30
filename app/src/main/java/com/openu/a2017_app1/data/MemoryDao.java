package com.openu.a2017_app1.data;

import android.os.AsyncTask;

import com.openu.a2017_app1.models.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Raz on 30/12/2016.
 */

public class MemoryDao implements Dao {

    Map<String, List<Model>> db;

    public MemoryDao() {
        db = new HashMap<>();
    }

    @Override
    public boolean save(Model model) {
        int id = addToTable(model.getTable(), model);
        model.setAttribute(model.getPrimaryKey(), id);
        return true;
    }

    @Override
    public void saveAsync(final Model model, final SaveListener callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
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
    public <T extends Model> QueryBuilder query(Model model) {
        return null;
    }

    /**
     * Removes all the data from the data store.
     * The data store will be empty after this call returns.
     */
    public void clearDb() {
        db.clear();
    }

    /**
     * Returns the database object.
     * @return
     */
    /* package */ Map<String, List<Model>> getDb() {
        return db;
    }

    /**
     * Adds the model to the table.
     * @param tableName
     * @param model
     * @return
     */
    private int addToTable(String tableName, Model model) {
        List<Model> table;
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

